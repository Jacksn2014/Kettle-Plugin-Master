package com.agileq.kettle.mts.plugin;

import java.util.HashMap;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.alibaba.fastjson.JSONObject;
import com.agileq.kettle.mts.JsonToSqlUtil;

/**
 * (微信公众号：游走在数据之间)
 * @Title: MQ To SQL
 * @ClassName MqToSqlStep
 * @Description: 步骤类
 * @author AgileQ
 * @date 2017-04-12 下午07:10:26
 * @version V1.0
 */

public class MqToSqlStep extends BaseStep implements StepInterface {

	private MqToSqlData data;
	private MqToSqlMeta meta;

	public MqToSqlStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
		super(s, stepDataInterface, c, t, dis);
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (MqToSqlMeta) smi;
		data = (MqToSqlData) sdi;

		Object[] r = getRow(); // get row, blocks when needed!
		if (r == null) // no more input to be expected...
		{
			setOutputDone();
			return false;
		}

		RowMetaInterface inputRowMeta = getInputRowMeta();
		if (first) {
			first = false;

			data.outputRowMeta = getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			int numErrors = 0;

			String jsonStr = environmentSubstitute(meta.getJsonStr());

			if (isEmpty(jsonStr)) {
				logError(Messages.getString("MqToSqlStep.Log.JsonStrIsNull", jsonStr)); //$NON-NLS-1$
				numErrors++;
			}
			data.jsonStrNr = inputRowMeta.indexOfValue(jsonStr);

			if (data.jsonStrNr < 0) {
				logError(Messages.getString("MqToSqlStep.Log.CouldntFindField", jsonStr)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.jsonStrNr).isBinary()
					&& !inputRowMeta.getValueMeta(data.jsonStrNr).isString()) {
				logError(Messages.getString("MqToSqlStep.Log.FieldNotValid", jsonStr)); //$NON-NLS-1$
				numErrors++;
			}
			data.jsonStrMeta = inputRowMeta.getValueMeta(data.jsonStrNr);

			String tableName = environmentSubstitute(meta.getTableName());
			if (!isEmpty(tableName)) {
				logBasic(Messages.getString("MqToSqlStep.Log.TableName", tableName));
			}

			String operType = environmentSubstitute(meta.getOperType());
			if (isEmpty(operType)) {
				logError(Messages.getString("MqToSqlStep.Log.operTypeIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.operTypeNr = inputRowMeta.indexOfValue(operType);

			if (data.operTypeNr < 0) {
				logError(Messages.getString("MqToSqlStep.Log.CouldntFindField", operType)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.operTypeNr).isBinary()
					&& !inputRowMeta.getValueMeta(data.operTypeNr).isString()) {
				logError(Messages.getString("MqToSqlStep.Log.FieldNotValid", operType)); //$NON-NLS-1$
				numErrors++;
			}
			data.operTypeMeta = inputRowMeta.getValueMeta(data.operTypeNr);

			String jsonDefaultstr = environmentSubstitute(meta.getJsonDefaultStr());
			if (isEmpty(jsonDefaultstr)) {
				logError(Messages.getString("MqToSqlStep.Log.JsonDefaultstrIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.jsonDefaultStrNr = inputRowMeta.indexOfValue(jsonDefaultstr);

			if (data.jsonDefaultStrNr < 0) {
				logError(Messages.getString("MqToSqlStep.Log.CouldntFindField", jsonDefaultstr)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.jsonDefaultStrNr).isBinary()
					&& !inputRowMeta.getValueMeta(data.jsonDefaultStrNr).isString()) {
				logError(Messages.getString("MqToSqlStep.Log.FieldNotValid", jsonDefaultstr)); //$NON-NLS-1$
				numErrors++;
			}
			data.jsonDefaultStrMeta = inputRowMeta.getValueMeta(data.jsonDefaultStrNr);

			if (numErrors > 0) {
				setErrors(numErrors);
				stopAll();
				return false;
			}

			logBasic("template step initialized successfully");

		}

		int jsonStrIndex = getInputRowMeta().indexOfValue(meta.getJsonStr());
		int operTypeIndex = getInputRowMeta().indexOfValue(meta.getOperType());
		int jsonDefaultStrIndex = getInputRowMeta().indexOfValue(meta.getJsonDefaultStr());

		String sbSql = null;
		try {
			sbSql = JsonToSqlUtil.jsonToMapSql(r[jsonStrIndex].toString(),
				data.jsonKeyStrObj, data.jsonValueStrObj,
				r[operTypeIndex].toString(), meta.getTableName(),
				r[jsonDefaultStrIndex].toString(), data.primaryKeys);
		} catch (Exception e) {
			logError("Failed to process row[" +  r[jsonStrIndex].toString() + "].", e);
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		Object[] outputRow = RowDataUtil.addValueData(r, data.outputRowMeta.size() - 1, sbSql);

		putRow(data.outputRowMeta, outputRow);

		incrementLinesOutput();

		if (checkFeedback(getLinesRead())) {
			logBasic("Linenr " + getLinesRead()); // Some basic logging
		}

		return true;
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (MqToSqlMeta) smi;
		data = (MqToSqlData) sdi;

		String jsonKeyStr = environmentSubstitute(meta.getJsonKeyStr());
		if (!isEmpty(jsonKeyStr)) {
			logBasic(Messages.getString("MqToSqlStep.Log.JsonKeyStr", jsonKeyStr));
			data.jsonKeyStrObj = JSONObject.parseObject(jsonKeyStr);
		}

		String jsonValueStr = environmentSubstitute(meta.getJsonValueStr());
		if (!isEmpty(jsonValueStr)) {
			logBasic(Messages.getString("MqToSqlStep.Log.JsonValueStr", jsonValueStr));
			data.jsonValueStrObj = JSONObject.parseObject(jsonValueStr);
		}

		String primaryKey = environmentSubstitute(meta.getPrimaryKey());
		if (!isEmpty(primaryKey)) {
			logBasic(Messages.getString("MqToSqlStep.Log.PrimaryKey", primaryKey));
			String[] keys = primaryKey.split("[,]");
			data.primaryKeys = new HashMap<String, String>();
			if (keys != null) {
				for (String key : keys) {
					data.primaryKeys.put(key, "");
				}
			}
		}

		return super.init(smi, sdi);
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (MqToSqlMeta) smi;
		data = (MqToSqlData) sdi;

		super.dispose(smi, sdi);
	}

	//
	// Run is were the action happens!
	public void run() {
		logBasic("Starting to run...");
		try {
			while (processRow(meta, data) && !isStopped())
				;
		} catch (Exception e) {
			logError("Unexpected error : " + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1);
			stopAll();
		} finally {
			dispose(meta, data);
			logBasic("Finished, processing " + getLinesRead() + " rows");
			markStop();
		}
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

}
