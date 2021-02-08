package com.agileq.kettle.mts.plugin;

import java.util.Map;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.alibaba.fastjson.JSONObject;
 
 /**
 * 微信公众号"以数据之名"
 * @Title: 数据类 
 * @Package plugin.template 
 * @Description: MqToSqlData
 * @author: itbigbird   
 * @date: 2017-04-12 下午07:10:26 
 * @version: V1.0   
 */
public class MqToSqlData extends BaseStepData implements StepDataInterface {

	RowMetaInterface outputRowMeta;
	int jsonStrNr;
	int jsonKeyStrNr;
	int jsonValueStrNr;
	int tableNameNr;
	int primaryKeyNr;
	int operTypeNr;
	int jsonDefaultStrNr;
	ValueMetaInterface jsonStrMeta;
	ValueMetaInterface jsonKeyStrMeta;
	ValueMetaInterface jsonValueStrMeta;
	ValueMetaInterface tableNameMeta;
	ValueMetaInterface primaryKeyMeta;
	ValueMetaInterface operTypeMeta;
	ValueMetaInterface jsonDefaultStrMeta;

	JSONObject jsonKeyStrObj;
	JSONObject jsonValueStrObj;
	Map<String, String> primaryKeys;

	public MqToSqlData() {
		super();
	}
}
