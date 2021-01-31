package com.ruckuswireless.pentaho.kafka.consumer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * 微信公众号（游走在数据之间）
 * 
 * @ClassName: KafkaConsumerData
 * @Description: Holds data processed by this step
 * @author: Agile.Q
 * @date: 2017年7月4日 下午4:33:48
 */
public class KafkaConsumerData extends BaseStepData implements
		StepDataInterface {

	ConsumerConnector consumer;
	List<KafkaStream<byte[], byte[]>> kafkaStreams;
	RowMetaInterface outputRowMeta;
	RowMetaInterface inputRowMeta;
	boolean canceled;
	AtomicInteger processed;
	int numThreads;
	ExecutorService executor;
}
