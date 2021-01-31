package com.agileq.kettle.mts.plugin;

import java.util.Map;

/**   
 * @Title: 数据类 
 * @Package plugin.template 
 * @Description: TODO(JSON TO SQL) 
 * @author minjie.qu  
 * @date 2017-04-12 下午07:10:26 
 * @version V1.0   
 */

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.alibaba.fastjson.JSONObject;
/**
 * (微信公众号：游走在数据之间)
 * @Title: MQ To SQL
 * @ClassName: MqToSqlData 
 * @Description: 数据类
 * @author: AgileQ
 * @date: 2021年1月24日 下午7:54:24
 * @version V1.0
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
