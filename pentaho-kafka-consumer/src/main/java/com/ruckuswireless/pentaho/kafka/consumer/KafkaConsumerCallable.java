package com.ruckuswireless.pentaho.kafka.consumer;

import java.util.concurrent.Callable;

import org.pentaho.di.core.exception.KettleException;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

/**
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @ClassName: KafkaConsumerCallable
 * @Description: Kafka reader callable
 * @date: 2017年7月4日 下午4:33:48
 */
public abstract class KafkaConsumerCallable implements Callable<Object> {

	private KafkaConsumerMeta meta;
	private KafkaConsumerData data;
	private KafkaConsumerStep step;
	private KafkaStream<byte[], byte[]> stream;

	public KafkaConsumerCallable(KafkaConsumerMeta meta,
			KafkaConsumerData data, KafkaConsumerStep step,
			KafkaStream<byte[], byte[]> stream) {
		this.meta = meta;
		this.data = data;
		this.step = step;
		this.stream = stream;
	}

	/**
	 * Called when new message arrives from Kafka stream
	 *
	 * @param message
	 *            Kafka message
	 * @param key
	 *            Kafka key
	 * @param partition
	 *            Kafka partition
	 * @param offset
	 *            Kafka offset
	 */
	protected abstract void messageReceived(byte[] key, byte[] message,
			int partition, long offset) throws KettleException;

	public Object call() throws KettleException {

		long processed = 0;
		try {
			long limit;
			String strData = meta.getLimit();

			try {
				limit = KafkaConsumerMeta.isEmpty(strData) ? 0 : Long
						.parseLong(step.environmentSubstitute(strData));
			} catch (NumberFormatException e) {
				throw new KettleException(
						"Unable to parse messages limit parameter", e);
			}
			if (limit > 0) {
				step.logDebug("Collecting up to " + limit + " messages");
			} else {
				step.logDebug("Collecting unlimited messages");
			}

			ConsumerIterator<byte[], byte[]> itr = stream.iterator();
			while (itr.hasNext() && !data.canceled
					&& (limit <= 0 || processed < limit)) {
				MessageAndMetadata<byte[], byte[]> messageAndMetadata = itr
						.next();
				messageReceived(messageAndMetadata.key(),
						messageAndMetadata.message(),
						messageAndMetadata.partition(),
						messageAndMetadata.offset());
				++processed;
				data.processed.getAndIncrement();
			}
		} catch (ConsumerTimeoutException cte) {
			step.logDebug("Received a consumer timeout after " + processed
					+ " messages");
			if (!meta.isStopOnEmptyTopic()) {
				// Because we're not set to stop on empty, this is an abnormal
				// timeout
				throw new KettleException("Unexpected consumer timeout!", cte);
			}
		}
		// Notify that all messages were read successfully
		// data.consumer.commitOffsets();
		return null;
	}
}
