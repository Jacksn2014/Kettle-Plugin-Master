package org.pentaho.di.trans.steps.redisdelete;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
 * The Redis Delete step delete value objects, by the given key names, from
 * Redis server(s).
 * 
 * @author 微信公众号"以数据之名"
 * @createDate 2019年1月6日 下午4:37:40
 * @version v0.1
 * @describe 从Redis删除Key，根据配置执行redis操作，并定义kettle插件显示类
 */
@Step(id = "RedisDelete", image = "image/redis.svg", name = "Redis Delete", description = "利用Redis做缓存，找到Key并删除数据", categoryDescription = "DTS")
public class RedisDeleteMeta extends BaseStepMeta implements StepMetaInterface {
	private static Class<?> PKG = RedisDeleteMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

	private String keyFieldName;
	private String valueFieldName;
	private String valueTypeName;
	private String masterName;
	private Set<Map<String, String>> servers;

	public Map<String, String> getJedisServer() {
		return jedisServer;
	}

	public void setJedisServer(Map<String, String> jedisServer) {
		this.jedisServer = jedisServer;
	}

	private Map<String, String> jedisServer;

	public RedisDeleteMeta() {
		super(); // allocate BaseStepMeta
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databaseMetas, Map<String, Counter> stringCounterMap)
			throws KettleXMLException {
		readData(stepnode);
	}

	public Object clone() {
		RedisDeleteMeta retval = (RedisDeleteMeta) super.clone();
		retval.setKeyFieldName(this.keyFieldName);
		retval.setValueFieldName(this.valueFieldName);
        retval.setValueTypeName(this.valueTypeName);
		retval.setMasterName(this.masterName);
		retval.setServers(this.servers);
		return retval;
	}

	public void allocate(int nrfields) {
		servers = new HashSet<Map<String, String>>();
	}

	public void setDefault() {
		this.keyFieldName = null;
		this.valueFieldName = null;
        this.valueTypeName = null;
		this.masterName = null;
		allocate(0);
	}

	public void getFields(RowMetaInterface inputRowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space) throws KettleStepException {
		if (!Const.isEmpty(this.valueFieldName)) {
			// Add value field meta if not found, else set it
			ValueMetaInterface v = new ValueMeta();
			v.setName(this.valueFieldName);
			v.setType(ValueMeta.getType(this.valueTypeName));// String
			v.setOrigin(origin);
			int valueFieldIndex = inputRowMeta.indexOfValue(this.valueFieldName);
			if (valueFieldIndex < 0) {
				inputRowMeta.addValueMeta(v);
			} else {
				inputRowMeta.setValueMeta(valueFieldIndex, v);
			}
		} else {
			throw new KettleStepException(
					BaseMessages.getString(PKG, "RedisDeleteMeta.Exception.ValueFieldNameNotFound"));
		}
		if (Const.isEmpty(space.environmentSubstitute(this.keyFieldName))) {
			throw new KettleStepException(
					BaseMessages.getString(PKG, "RedisDeleteMeta.Exception.KeyFieldNameNotFound"));
		}
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta tr,
			Trans trans) {
		return new RedisDelete(stepMeta, stepDataInterface, cnr, tr, trans);
	}

	public StepDataInterface getStepData() {
		return new RedisDeleteData();
	}

	public String getKeyFieldName() {
		return keyFieldName;
	}

	public void setKeyFieldName(String keyFieldName) {
		this.keyFieldName = keyFieldName;
	}

	public String getValueFieldName() {
		return valueFieldName;
	}

	public void setValueFieldName(String valueFieldName) {
		this.valueFieldName = valueFieldName;
	}

	public String getValueTypeName() {
		return valueTypeName;
	}

	public void setValueTypeName(String valueTypeName) {
		this.valueTypeName = valueTypeName;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	@Override
	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder();
		retval.append("    " + XMLHandler.addTagValue("keyfieldname", this.getKeyFieldName()));
		retval.append("    " + XMLHandler.addTagValue("valuefieldname", this.getValueFieldName()));
        retval.append("    " + XMLHandler.addTagValue("valuetype", this.getValueTypeName()));
		retval.append("    " + XMLHandler.addTagValue("mastername", this.getMasterName()));
		retval.append("    <servers>").append(Const.CR);
		Set<Map<String, String>> servers = this.getServers();
		if (servers != null) {
			Iterator<Map<String, String>> iterator = servers.iterator();
			while (iterator.hasNext()) {
				Map<String, String> addr = iterator.next();
				retval.append("      <server>").append(Const.CR);
				retval.append("        ").append(XMLHandler.addTagValue("hostname", addr.get("hostname")));
				retval.append("        ").append(XMLHandler.addTagValue("port", addr.get("port")));
				retval.append("        ").append(XMLHandler.addTagValue("auth", addr.get("auth")));
				retval.append("      </server>").append(Const.CR);
			}
		}
		retval.append("    </servers>").append(Const.CR);

		return retval.toString();
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info) {
		CheckResult cr;
		if (prev == null || prev.size() == 0) {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_WARNING,
					BaseMessages.getString(PKG, "RedisDeleteMeta.CheckResult.NotReceivingFields"), stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK,
					BaseMessages.getString(PKG, "RedisDeleteMeta.CheckResult.StepRecevingData", prev.size() + ""),
					stepMeta);
			remarks.add(cr);
		}

		// See if we have input streams leading to this step!
		if (input.length > 0) {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK,
					BaseMessages.getString(PKG, "RedisDeleteMeta.CheckResult.StepRecevingData2"), stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "RedisDeleteMeta.CheckResult.NoInputReceivedFromOtherSteps"), stepMeta);
			remarks.add(cr);
		}
	}

	private void readData(Node stepnode) throws KettleXMLException {
		try {
			this.keyFieldName = XMLHandler.getTagValue(stepnode, "keyfieldname");
			this.valueFieldName = XMLHandler.getTagValue(stepnode, "valuefieldname");
            this.valueTypeName = XMLHandler.getTagValue(stepnode, "valuetype");
			this.masterName = XMLHandler.getTagValue(stepnode, "mastername");
			Node serverNodes = XMLHandler.getSubNode(stepnode, "servers");
			int nrservers = XMLHandler.countNodes(serverNodes, "server");
			allocate(nrservers);

			for (int i = 0; i < nrservers; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(serverNodes, "server", i);
				Map<String, String> hostAndPort = new HashMap<String, String>();
				hostAndPort.put("hostname", XMLHandler.getTagValue(fnode, "hostname"));
				hostAndPort.put("port", XMLHandler.getTagValue(fnode, "port"));
				hostAndPort.put("auth", XMLHandler.getTagValue(fnode, "auth"));
				if (i == 0) {
					setJedisServer(hostAndPort);
				}
				servers.add(hostAndPort);
			}
		} catch (Exception e) {
			throw new KettleXMLException(BaseMessages.getString(PKG, "RedisDeleteMeta.Exception.UnableToReadStepInfo"),
					e);
		}
	}

	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		try {
			this.keyFieldName = rep.getStepAttributeString(id_step, "keyfieldname");
			this.valueFieldName = rep.getStepAttributeString(id_step, "valuefieldname");
            this.valueTypeName = rep.getStepAttributeString(id_step, "valuetype");
			this.masterName = rep.getStepAttributeString(id_step, "mastername");
			int nrservers = rep.countNrStepAttributes(id_step, "server");
			allocate(nrservers);
			for (int i = 0; i < nrservers; i++) {
				Map<String, String> nrserversmap = new HashMap<String, String>();
				nrserversmap.put("hostname", rep.getStepAttributeString(id_step, i, "hostname"));
				nrserversmap.put("port", rep.getStepAttributeString(id_step, i, "port"));
				nrserversmap.put("auth", rep.getStepAttributeString(id_step, i, "auth"));
				servers.add(nrserversmap);
			}

		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG, "RedisDeleteMeta.Exception.UnexpectedErrorReadingStepInfo"), e);
		}
	}

	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
		return new RedisDeleteDialog(shell, meta, transMeta, name);
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "keyfieldname", this.keyFieldName);
			rep.saveStepAttribute(id_transformation, id_step, "valuefieldname", this.valueFieldName);
            rep.saveStepAttribute(id_transformation, id_step, "valuetype", this.valueTypeName);
			rep.saveStepAttribute(id_transformation, id_step, "mastername", this.masterName);
			int i = 0;
			Set<Map<String, String>> servers = this.getServers();
			if (servers != null) {
				Iterator<Map<String, String>> iterator = servers.iterator();
				while (iterator.hasNext()) {
					Map<String, String> addr = iterator.next();
					rep.saveStepAttribute(id_transformation, id_step, i++, "hostname", addr.get("hostname"));
					rep.saveStepAttribute(id_transformation, id_step, i++, "port", addr.get("port"));
					rep.saveStepAttribute(id_transformation, id_step, i++, "auth", addr.get("auth"));
				}
			}
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG, "RedisDeleteMeta.Exception.UnexpectedErrorSavingStepInfo"), e);
		}
	}

	public Set<Map<String, String>> getServers() {
		return servers;
	}

	public void setServers(Set<Map<String, String>> servers) {
		this.servers = servers;
	}

}
