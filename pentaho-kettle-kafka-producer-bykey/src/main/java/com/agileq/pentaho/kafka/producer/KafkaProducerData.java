package com.agileq.pentaho.kafka.producer;

import org.apache.kafka.clients.producer.Producer;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * 微信公众号"以数据之名"
 * 
 * @ClassName: KafkaProducerData
 * @Description: Holds data processed by this step
 * @author: itbigbird
 * @date: 2019年8月10日 下午3:33:48
 */
public class KafkaProducerData extends BaseStepData implements
		StepDataInterface {
	Producer<Object, Object> producer;
	RowMetaInterface outputRowMeta;
	int messageFieldNr;
	int keyFieldNr;
	boolean messageIsString;
	boolean keyIsString;
	ValueMetaInterface messageFieldMeta;
	ValueMetaInterface keyFieldMeta;
}
