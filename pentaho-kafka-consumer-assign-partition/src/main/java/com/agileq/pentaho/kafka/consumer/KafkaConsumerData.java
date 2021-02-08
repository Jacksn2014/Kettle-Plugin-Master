package com.agileq.pentaho.kafka.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @createDate 2019年7月23日 下午5:01:23
 * @version v0.1
 * @describe KafkaConsumerData
 */
public class KafkaConsumerData extends BaseStepData
		implements
			StepDataInterface {

	KafkaConsumer<String, String> kafkaConsumer;
	RowMetaInterface outputRowMeta;
	RowMetaInterface inputRowMeta;
	boolean canceled;
	AtomicInteger processed;
}
