package com.agileq.kettle.mts.plugin;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
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
 * 微信公众号"以数据之名"
 * @Title: MQ To SQL
 * @ClassName: MqToSqlMeta
 * @Description: 元数据类
 * @author: itbigbird
 * @date：2017-04-12 下午07:10:26
 * @version：V1.0
 */
@SuppressWarnings("deprecation")
public class MqToSqlMeta extends BaseStepMeta implements StepMetaInterface {

	private static Class<?> PKG = MqToSqlMeta.class; // for i18n purposes

	private String jsonStr;
	private String jsonKeyStr;
	private String jsonValueStr;
	private String tableName;
	private String primaryKey;
	private String operType;
	private String jsonDefaultStr;

	private String outputDML = "execDML";

	public MqToSqlMeta() {
		super();
	}

	public String getOutputDML() {
		return outputDML;
	}

	public void setOutputDML(String outputDML) {
		this.outputDML = outputDML;
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	public String getJsonKeyStr() {
		return jsonKeyStr;
	}

	public void setJsonKeyStr(String jsonKeyStr) {
		this.jsonKeyStr = jsonKeyStr;
	}

	public String getJsonValueStr() {
		return jsonValueStr;
	}

	public void setJsonValueStr(String jsonValueStr) {
		this.jsonValueStr = jsonValueStr;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getJsonDefaultStr() {
		return jsonDefaultStr;
	}

	public void setJsonDefaultStr(String jsonDefaultStr) {
		this.jsonDefaultStr = jsonDefaultStr;
	}

	public String getXML() throws KettleValueException {
		StringBuilder retval = new StringBuilder();
		retval.append("<values>").append(Const.CR);
		retval.append("    ")
				.append(XMLHandler.addTagValue("jsonStr", jsonStr));
		retval.append("    ").append(
				XMLHandler.addTagValue("jsonKeyStr", jsonKeyStr));
		retval.append("    ").append(
				XMLHandler.addTagValue("jsonValueStr", jsonValueStr));
		retval.append("    ").append(
				XMLHandler.addTagValue("tableName", tableName));
		retval.append("    ").append(
				XMLHandler.addTagValue("primaryKey", primaryKey));
		retval.append("    ").append(
				XMLHandler.addTagValue("operType", operType));
		retval.append("    ").append(
				XMLHandler.addTagValue("jsonDefaultStr", jsonDefaultStr));
		retval.append("</values>").append(Const.CR);
		return retval.toString();
	}

	public Object clone() {
		return super.clone();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases,
			Map<String, Counter> counters) throws KettleXMLException {
		Node valnode = XMLHandler.getSubNode(stepnode, "values", "jsonStr");
		if (null != valnode) {
			jsonStr = valnode.getTextContent();
		}
		valnode = XMLHandler.getSubNode(stepnode, "values", "jsonKeyStr");
		if (null != valnode) {
			jsonKeyStr = valnode.getTextContent();
		}
		valnode = XMLHandler.getSubNode(stepnode, "values", "jsonValueStr");
		if (null != valnode) {
			jsonValueStr = valnode.getTextContent();
		}
		valnode = XMLHandler.getSubNode(stepnode, "values", "tableName");
		if (null != valnode) {
			tableName = valnode.getTextContent();
		}
		valnode = XMLHandler.getSubNode(stepnode, "values", "primaryKey");
		if (null != valnode) {
			primaryKey = valnode.getTextContent();
		}
		valnode = XMLHandler.getSubNode(stepnode, "values", "operType");
		if (null != valnode) {
			operType = valnode.getTextContent();
		}
		valnode = XMLHandler.getSubNode(stepnode, "values", "jsonDefaultStr");
		if (null != valnode) {
			jsonDefaultStr = valnode.getTextContent();
		}

	}

	public void getFields(RowMetaInterface r, String origin,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {

		// append the outputField to the output
		ValueMetaInterface v = new ValueMeta();
		v.setName(outputDML);
		v.setType(ValueMeta.TYPE_STRING);
		v.setTrimType(ValueMeta.TRIM_TYPE_BOTH);
		v.setOrigin(origin);

		r.addValueMeta(v);

	}

	public void setDefault() {
		this.jsonKeyStr = "{\"trade_amount\":\"trade_amt\",\"currency_code\":\"currency\",\"ref_id_txn\":\"ref_txn_id\",\"ref_id_ctrl\":\"ref_ctrl_id\",\"net_amount\":\"net_amt\",\"equity_amount\":\"equity_amt\"}";
		this.jsonValueStr = "{\"trade_amount\":\"trade_amount*10\",\"net_amount\":\"net_amount*10\",\"equity_amount\":\"equity_amount*10\"}";
		this.tableName = "tf_evt_qq_txn";
		this.primaryKey = "trade_id,create_time";
	}

	public void check(List<CheckResultInterface> remarks, TransMeta transmeta,
			StepMeta stepMeta, RowMetaInterface prev, String input[],
			String output[], RowMetaInterface info) {

		if (MqToSqlStep.isEmpty(jsonStr)) {

			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidJsonStr"),
					stepMeta));
		} else if (MqToSqlStep.isEmpty(jsonKeyStr)) {
			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidJsonKeyStr"),
					stepMeta));
		} else if (MqToSqlStep.isEmpty(jsonValueStr)) {
			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidJsonValueStr"),
					stepMeta));
		} else if (MqToSqlStep.isEmpty(tableName)) {
			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidTableName"),
					stepMeta));
		} else if (MqToSqlStep.isEmpty(primaryKey)) {
			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidPrimaryKey"),
					stepMeta));
		} else if (MqToSqlStep.isEmpty(operType)) {
			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidOperType"),
					stepMeta));
		} else if (MqToSqlStep.isEmpty(jsonDefaultStr)) {
			remarks.add(new CheckResult(
					CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.InvalidJsonDefaultStr"),
					stepMeta));
		} else {
			remarks.add(new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR,
					Messages.getString("MqToSqlMeta.Check.Invalid"),
					stepMeta));
		}

	}

	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta,
			TransMeta transMeta, String name) {
		return new MqToSqlDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans disp) {
		return new MqToSqlStep(stepMeta, stepDataInterface, cnr,
				transMeta, disp);
	}

	public StepDataInterface getStepData() {
		return new MqToSqlData();
	}

	public void readRep(Repository rep, ObjectId id_step,
			List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException {
		try {
			jsonStr = rep.getStepAttributeString(id_step, "jsonStr"); //$NON-NLS-1$
			jsonKeyStr = rep.getStepAttributeString(id_step, "jsonKeyStr");
			jsonValueStr = rep.getStepAttributeString(id_step, "jsonValueStr");
			tableName = rep.getStepAttributeString(id_step, "tableName");
			primaryKey = rep.getStepAttributeString(id_step, "primaryKey");
			operType = rep.getStepAttributeString(id_step, "operType");
			jsonDefaultStr = rep.getStepAttributeString(id_step,
					"jsonDefaultStr");
			outputDML = rep.getStepAttributeString(id_step, "outputDML"); //$NON-NLS-1$
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages
							.getString(PKG,
									"MqToSqlMeta.Exception.UnexpectedErrorInReadingStepInfo"),
					e);
		}
	}

	public void saveRep(Repository rep, ObjectId id_transformation,
			ObjectId id_step) throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "jsonStr",
					jsonStr);
			rep.saveStepAttribute(id_transformation, id_step, "jsonKeyStr",
					jsonKeyStr);
			rep.saveStepAttribute(id_transformation, id_step, "jsonValueStr",
					jsonValueStr);
			rep.saveStepAttribute(id_transformation, id_step, "tableName",
					tableName);
			rep.saveStepAttribute(id_transformation, id_step, "primaryKey",
					primaryKey);
			rep.saveStepAttribute(id_transformation, id_step, "operType",
					operType);
			rep.saveStepAttribute(id_transformation, id_step, "jsonDefaultStr",
					jsonDefaultStr);
			rep.saveStepAttribute(id_transformation, id_step, "outputDML",
					outputDML);
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG,
							"MqToSqlMeta.Exception.UnableToSaveStepInfoToRepository")
							+ id_step, e);
		}
	}
}
