package com.agileq.pentaho.kafka.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.producer.ProducerConfig;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
 * 微信公众号（游走在数据之间）
 * 
 * @ClassName: KafkaProducerMeta
 * @Description: Kafka Producer step definitions and serializer to/from XML and
 *               to/from Kettle repository.
 * @author: AgileQ
 * @date: 2019年8月10日 下午3:33:48
 */
public class KafkaProducerMeta extends BaseStepMeta implements
		StepMetaInterface {
	public static final String[] KAFKA_PROPERTIES_NAMES = {
			"bootstrap.servers", "acks", "producer.type", "retries",
			"batch.size", "linger.ms", "buffer.memory", "compression.type",
			"serializer.class", "key.serializer", "value.serializer",
			"request.timeout.ms", "client.id" };
	public static final Map<String, String> KAFKA_PROPERTIES_DEFAULTS = new HashMap<String, String>();

	static {
		KAFKA_PROPERTIES_DEFAULTS.put("bootstrap.servers", "localhost:9092");
		KAFKA_PROPERTIES_DEFAULTS.put("acks", "1");
		KAFKA_PROPERTIES_DEFAULTS.put("producer.type", "sync");
		KAFKA_PROPERTIES_DEFAULTS.put("compression.type", "none"); // none,gzip,snappy
		KAFKA_PROPERTIES_DEFAULTS.put("serializer.class",
				"kafka.serializer.DefaultEncoder");
		KAFKA_PROPERTIES_DEFAULTS.put("key.serializer",
				"org.apache.kafka.common.serialization.ByteArraySerializer");
		KAFKA_PROPERTIES_DEFAULTS.put("value.serializer",
				"org.apache.kafka.common.serialization.ByteArraySerializer");
	}

	private Properties kafkaProperties = new Properties();
	private String topic;
	private String messageField;
	private String keyField;

	Properties getKafkaProperties() {
		return this.kafkaProperties;
	}

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getKeyField() {
		return this.keyField;
	}

	public void setKeyField(String field) {
		this.keyField = field;
	}

	public String getMessageField() {
		return this.messageField;
	}

	public void setMessageField(String field) {
		this.messageField = field;
	}

	public void check(List<CheckResultInterface> remarks, TransMeta transMeta,
			StepMeta stepMeta, RowMetaInterface prev, String[] input,
			String[] output, RowMetaInterface info) {
		if ((this.topic == null) || (Const.isEmpty(this.topic))) {
			remarks.add(new CheckResult(4, Messages
					.getString("KafkaProducerMeta.Check.InvalidTopic"),
					stepMeta));
		}
		if ((this.messageField == null) || (Const.isEmpty(this.messageField))) {
			remarks.add(new CheckResult(4, Messages
					.getString("KafkaProducerMeta.Check.InvalidMessageField"),
					stepMeta));
		}
		try {
			new ProducerConfig(this.kafkaProperties);
		} catch (IllegalArgumentException e) {
			remarks.add(new CheckResult(4, e.getMessage(), stepMeta));
		}
	}

	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans trans) {
		return new KafkaProducerStep(stepMeta, stepDataInterface, cnr,
				transMeta, trans);
	}

	public StepDataInterface getStepData() {
		return new KafkaProducerData();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases,
			Map<String, Counter> counters) throws KettleXMLException {
		try {
			this.topic = XMLHandler.getTagValue(stepnode, "TOPIC");
			this.messageField = XMLHandler.getTagValue(stepnode, "FIELD");
			this.keyField = XMLHandler.getTagValue(stepnode, "KEYFIELD");
			Node kafkaNode = XMLHandler.getSubNode(stepnode, "KAFKA");
			for (String name : KAFKA_PROPERTIES_NAMES) {
				String value = XMLHandler.getTagValue(kafkaNode, name);
				if (value != null) {
					this.kafkaProperties.put(name, value);
				}
			}
		} catch (Exception e) {
			throw new KettleXMLException(
					Messages.getString("KafkaProducerMeta.Exception.loadXml"),
					e);
		}
	}

	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder();
		if (this.topic != null) {
			retval.append("    ").append(
					XMLHandler.addTagValue("TOPIC", this.topic));
		}
		if (this.messageField != null) {
			retval.append("    ").append(
					XMLHandler.addTagValue("FIELD", this.messageField));
		}
		if (this.keyField != null) {
			retval.append("    ").append(
					XMLHandler.addTagValue("KEYFIELD", this.keyField));
		}
		retval.append("    ").append(XMLHandler.openTag("KAFKA"))
				.append(Const.CR);
		for (String name : KAFKA_PROPERTIES_NAMES) {
			String value = this.kafkaProperties.getProperty(name);
			if (value != null) {
				retval.append("      " + XMLHandler.addTagValue(name, value));
			}
		}
		retval.append("    ").append(XMLHandler.closeTag("KAFKA"))
				.append(Const.CR);
		return retval.toString();
	}

	public void readRep(Repository rep, ObjectId stepId,
			List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		try {
			this.topic = rep.getStepAttributeString(stepId, "TOPIC");
			this.messageField = rep.getStepAttributeString(stepId, "FIELD");
			this.keyField = rep.getStepAttributeString(stepId, "KEYFIELD");
			for (String name : KAFKA_PROPERTIES_NAMES) {
				String value = rep.getStepAttributeString(stepId, name);
				if (value != null) {
					this.kafkaProperties.put(name, value);
				}
			}
		} catch (Exception e) {
			throw new KettleException("KafkaProducerMeta.Exception.loadRep", e);
		}
	}

	public void saveRep(Repository rep, ObjectId transformationId,
			ObjectId stepId) throws KettleException {
		try {
			if (this.topic != null) {
				rep.saveStepAttribute(transformationId, stepId, "TOPIC",
						this.topic);
			}
			if (this.messageField != null) {
				rep.saveStepAttribute(transformationId, stepId, "FIELD",
						this.messageField);
			}
			if (this.keyField != null) {
				rep.saveStepAttribute(transformationId, stepId, "KEYFIELD",
						this.keyField);
			}
			for (String name : KAFKA_PROPERTIES_NAMES) {
				String value = this.kafkaProperties.getProperty(name);
				if (value != null) {
					rep.saveStepAttribute(transformationId, stepId, name, value);
				}
			}
		} catch (Exception e) {
			throw new KettleException("KafkaProducerMeta.Exception.saveRep", e);
		}
	}

	public void setDefault() {
	}
}
