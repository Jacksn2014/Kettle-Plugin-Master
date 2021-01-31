package com.ruckuswireless.pentaho.kafka.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import kafka.common.OffsetAndMetadata;
import kafka.common.OffsetMetadata;
import kafka.common.TopicAndPartition;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;

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
 * 微信公众号（游走在数据之间）
 * 
 * @ClassName: KafkaConsumerStep
 * @Description: Kafka Consumer step processor
 * @author: AgileQ
 * @date: 2017年7月4日 下午4:33:48
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
		ConsumerConfig consumerConfig = new ConsumerConfig(substProperties);

		logBasic(Messages.getString(
				"KafkaConsumerStep.CreateKafkaConsumer.Message",
				consumerConfig.zkConnect()));
		data.consumer = Consumer.createJavaConsumerConnector(consumerConfig);
		String topic = environmentSubstitute(meta.getTopic());
		String offset = environmentSubstitute(meta.getOffsetVal());
		String[] offsets = offset.split("[,]");
		logBasic("Kafka partition length is {0}.", offsets.length);

		int partitions = offsets.length;
		Map<TopicAndPartition, OffsetAndMetadata> partitionOffset = new HashMap<TopicAndPartition, OffsetAndMetadata>();
		for (int i = 0; i < partitions; i++) {
			TopicAndPartition tap = new TopicAndPartition(topic, i);
			if (offsets[i] == null || offsets[i].equals("")) {
				offsets[i] = "0";
			}
			OffsetMetadata om = new OffsetMetadata(
					Long.valueOf(offsets[i]) + 1, null);
			OffsetAndMetadata oam = new OffsetAndMetadata(
					om,
					org.apache.kafka.common.requests.OffsetCommitRequest.DEFAULT_TIMESTAMP,
					org.apache.kafka.common.requests.OffsetCommitRequest.DEFAULT_TIMESTAMP);
			partitionOffset.put(tap, oam);
		}
		data.consumer.commitOffsets(partitionOffset, true);

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, partitions);
		Map<String, List<KafkaStream<byte[], byte[]>>> streamsMap = data.consumer
				.createMessageStreams(topicCountMap);
		logDebug("Received streams map: " + streamsMap);
		data.kafkaStreams = streamsMap.get(topic);
		data.numThreads = partitions;
		data.executor = Executors.newFixedThreadPool(partitions);
		data.processed = new AtomicInteger(0);

		return true;
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		KafkaConsumerData data = (KafkaConsumerData) sdi;
		if (data.consumer != null) {
			data.consumer.shutdown();
		}
		if (data.executor != null) {
			data.executor.shutdown();
		}
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
			long timeout;
			String strData = meta.getTimeout();

			try {
				timeout = KafkaConsumerMeta.isEmpty(strData) ? 0 : Long
						.parseLong(environmentSubstitute(strData));
			} catch (NumberFormatException e) {
				throw new KettleException("Unable to parse step timeout value",
						e);
			}

			logDebug("Starting message consumption with overall timeout of "
					+ timeout + "ms");

			List<Future<?>> futures = new ArrayList<Future<?>>();
			for (int i = 0; i < data.numThreads; i++) {
				KafkaConsumerCallable kafkaConsumer = new KafkaConsumerCallable(
						meta, data, this, data.kafkaStreams.get(i)) {
					protected void messageReceived(byte[] key, byte[] message,
							int partition, long offset) throws KettleException {
						Object[] newRow = RowDataUtil.addRowData(
								inputRow.clone(),
								data.inputRowMeta.size(),
								new Object[] { message, key,
										Long.valueOf(partition), offset });
						putRow(data.outputRowMeta, newRow);

						if (isRowLevel()) {
							logRowlevel(Messages.getString(
									"KafkaConsumerStep.Log.OutputRow",
									Long.toString(getLinesWritten()),
									data.outputRowMeta.getString(newRow)));
						}
					}
				};
				Future<?> future = data.executor.submit(kafkaConsumer);
				futures.add(future);
			}
			if (timeout > 0) {
				logDebug("Starting timed consumption");
				try {
					for (int i = 0; i < futures.size(); i++) {
						try {
							futures.get(i).get(timeout, TimeUnit.MILLISECONDS);
						} catch (TimeoutException e) {
						} catch (Exception e) {
							throw new KettleException(e);
						}
					}
				} finally {
					data.executor.shutdown();
				}
			} else {
				try {
					for (int i = 0; i < futures.size(); i++) {
						try {
							futures.get(i).get();
						} catch (Exception e) {
							throw new KettleException(e);
						}
					}
				} finally {
					data.executor.shutdown();
				}
			}

			data.consumer.commitOffsets();
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
		}
		return true;
	}

	public void stopRunning(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {

		KafkaConsumerData data = (KafkaConsumerData) sdi;
		data.consumer.shutdown();
		data.executor.shutdown();
		data.canceled = true;

		super.stopRunning(smi, sdi);
	}

}
