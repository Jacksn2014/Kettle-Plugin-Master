package com.agileq.pentaho.kafka.consumer;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @createDate 2019年7月23日 下午5:15:12
 * @version v0.1
 * @describe Kafka Consumer step processor
 */
public class KafkaConsumerStep extends BaseStep implements StepInterface {
	public static final String CONSUMER_TIMEOUT_KEY = "consumer.timeout.ms";

	public KafkaConsumerStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		super.init(smi, sdi);

		KafkaConsumerMeta meta = (KafkaConsumerMeta) smi;
		KafkaConsumerData data = (KafkaConsumerData) sdi;

		Properties properties = meta.getKafkaProperties();
		Properties substProperties = new Properties();
		for (Entry<Object, Object> e : properties.entrySet()) {
			substProperties.put(e.getKey(), environmentSubstitute(e.getValue()
					.toString()));
		}
		if (meta.isStopOnEmptyTopic()) {

			// If there isn't already a provided value, set a default of 1s
			if (!substProperties.containsKey(CONSUMER_TIMEOUT_KEY)) {
				substProperties.put(CONSUMER_TIMEOUT_KEY, "1000");
			}
		} else {
			if (substProperties.containsKey(CONSUMER_TIMEOUT_KEY)) {
				logError(Messages
						.getString("KafkaConsumerStep.WarnConsumerTimeout"));
			}
		}

		String groupId = environmentSubstitute(meta.getGroupId());
		if (groupId != null) {
			substProperties.put("group.id", groupId);
		}
		Thread.currentThread().setContextClassLoader(null);
		data.kafkaConsumer = new KafkaConsumer<>(substProperties);
		logBasic(Messages
				.getString("KafkaConsumerStep.CreateKafkaConsumer.Message"));

		String topic = environmentSubstitute(meta.getTopic());
		// String timeout = environmentSubstitute(meta.getTimeout());
		String assignPartition = environmentSubstitute(meta
				.getAssignPartition());
		String offset = environmentSubstitute(meta.getOffsetVal());
		logBasic("Assigned partition is {0}, its offset is {1}.",
				assignPartition, offset);

		TopicPartition tp = new TopicPartition(topic,
				Integer.valueOf(assignPartition));
		// 指定消费topic的那个分区
		data.kafkaConsumer.assign(Arrays.asList(tp));
		// 指定从topic的分区的某个offset开始消费
		data.kafkaConsumer.seek(tp, "0".equals(offset) ? Long.valueOf(offset)
				: Long.valueOf(offset) + 1);

		data.processed = new AtomicInteger(0);

		return true;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		/*KafkaConsumerData data = (KafkaConsumerData) sdi;
		if (data.kafkaConsumer != null) {
			data.kafkaConsumer.close();
		}*/
		super.dispose(smi, sdi);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {
		Object[] r = getRow();
		if (r == null) {
			/*
			 * If we have no input rows, make sure we at least run once to
			 * produce output rows. This allows us to consume without requiring
			 * an input step.
			 */
			if (!first) {
				setOutputDone();
				return false;
			}
			r = new Object[0];
		} else {
			incrementLinesRead();
		}

		final Object[] inputRow = r;

		KafkaConsumerMeta meta = (KafkaConsumerMeta) smi;
		final KafkaConsumerData data = (KafkaConsumerData) sdi;

		if (first) {
			first = false;
			data.inputRowMeta = getInputRowMeta();
			// No input rows means we just dummy data
			if (data.inputRowMeta == null) {
				data.outputRowMeta = new RowMeta();
				data.inputRowMeta = new RowMeta();
			} else {
				data.outputRowMeta = getInputRowMeta().clone();
			}
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
		}

		try {

			long timeout = getTimeout(meta);
			long limit = getLimit(meta);

			long processed = 0l;
			long now = System.currentTimeMillis();
			boolean isLooped = true;
			while (isLooped && !data.canceled) {

				ConsumerRecords<String, String> records = data.kafkaConsumer
						.poll(100);

				long interval = System.currentTimeMillis() - now;
				if (timeout > 0 && (interval > timeout)) {
					isLooped = false;
					break;
				}

				for (ConsumerRecord<String, String> record : records) {

					if (record != null && !data.canceled
							&& (limit <= 0 || processed < limit)) {

						Object[] newRow = RowDataUtil.addRowData(
								inputRow.clone(),
								data.inputRowMeta.size(),
								new Object[] { record.value().getBytes(),
										record.key().getBytes(),
										Long.valueOf(record.partition()),
										record.offset() });
						putRow(data.outputRowMeta, newRow);

						++processed;
						data.processed.getAndIncrement();

						if (isRowLevel()) {
							logRowlevel(Messages.getString(
									"KafkaConsumerStep.Log.OutputRow",
									Long.toString(getLinesWritten()),
									data.outputRowMeta.getString(newRow)));
						}
					} else {
						isLooped = false;
						break;
					}
				}
			}

			//data.kafkaConsumer.commitSync();
			setOutputDone();
		} catch (KettleException e) {
			if (!getStepMeta().isDoingErrorHandling()) {
				logError(Messages.getString(
						"KafkaConsumerStep.ErrorInStepRunning", e.getMessage()));
				setErrors(1);
				stopAll();
				setOutputDone();
				return false;
			}
			putError(getInputRowMeta(), r, 1, e.toString(), null, getStepname());
		}finally{
			if (data.kafkaConsumer != null) {
				data.kafkaConsumer.close();
			}
		}
		return true;
	}

	private long getLimit(KafkaConsumerMeta meta) throws KettleException {

		long limit;
		String strLimit = meta.getLimit();

		try {
			limit = KafkaConsumerMeta.isEmpty(strLimit) ? 0 : Long
					.parseLong(environmentSubstitute(strLimit));
		} catch (NumberFormatException e) {
			throw new KettleException(
					"Unable to parse messages limit parameter", e);
		}

		if (limit > 0) {
			logDebug("Collecting up to " + limit + " messages");
		} else {
			logDebug("Collecting unlimited messages");
		}
		return limit;
	}

	private long getTimeout(KafkaConsumerMeta meta) throws KettleException {

		long timeout;
		String strTimeout = meta.getTimeout();

		try {
			timeout = KafkaConsumerMeta.isEmpty(strTimeout) ? 0 : Long
					.parseLong(environmentSubstitute(strTimeout));
		} catch (NumberFormatException e) {
			throw new KettleException("Unable to parse step timeout value", e);
		}

		logDebug("Starting message consumption with overall timeout of "
				+ timeout + "ms");
		return timeout;
	}

	public void stopRunning(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {

		KafkaConsumerData data = (KafkaConsumerData) sdi;
		//data.kafkaConsumer.close();
		data.canceled = true;

		super.stopRunning(smi, sdi);
	}

}
