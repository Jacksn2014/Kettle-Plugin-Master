package com.agileq.kettle.mts;

import com.alibaba.fastjson.JSONObject;
import com.greenpineyu.fel.FelEngine;
/**
 * (微信公众号：游走在数据之间)
 * @Title: MQ To SQL
 * @ClassName: JsonFormat 
 * @Description: Json消息格式化
 * @author: AgileQ
 * @date: 2017-04-12 下午07:10:26
 * @version V1.0
 */
public class JsonFormat {

	private static FelEngine fel = FelEngine.instance;

	public static String jsonKeyFormat(JSONObject jsonObject, String keyObj) {
		return jsonObject.getString(keyObj);
	}

	public static Object jsonValueFormat(JSONObject jsonObject, String keyObj,
			Object valueObj) {

		Object jsonValue = valueObj;
		String value = jsonObject.getString(keyObj);
		if (value != null) {
			if (valueObj instanceof String) {
				jsonValue = fel.eval(value.replace(keyObj, "'"
						+ (String) valueObj + "'"));
			} else {
				jsonValue = valueObj != null ? fel.eval(value.replace(keyObj,
						valueObj.toString())) : null;
			}
		}
		return jsonValue;
	}
}
