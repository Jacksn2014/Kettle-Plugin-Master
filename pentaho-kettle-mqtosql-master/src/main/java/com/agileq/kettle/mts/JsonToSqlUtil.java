package com.agileq.kettle.mts;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * (微信公众号：游走在数据之间)
 * @Title: MQ To SQL
 * @ClassName: JsonToSqlUtil
 * @Description: json转换成可执行mysql的DML语句sql
 * @author AgileQ
 * @date: 2017-04-12 下午07:10:26
 * @version V1.0
 */
@SuppressWarnings("deprecation")
public class JsonToSqlUtil {

	/**
	 * json转换成Map,根据操作类型等参数输出要执行的sql语句
	 * 
	 * @param jsonString
	 * @param jsonKeyString
	 * @param jsonValueString
	 * @param changeOper
	 * @param tableName
	 * @param prmKeyName
	 * @return
	 * @throws JSONException
	 */
	public static String jsonToMapSql(String jsonString,
			JSONObject jsonKeyObject, JSONObject jsonValueObject,
			String changeOper, String tableName, String jsonDefString,
			Map<String, String> pramKeys) throws JSONException {

		JSONObject jsonObject = JSONObject.parseObject(jsonString);

		Map<String, Object> resultMapSet = new HashMap<String, Object>();
		Map<String, Object> resultMapWhere = new HashMap<String, Object>();
		Map<String, Object> resultMapOnDuplicate = new HashMap<String, Object>();

		String jsonToSql = null;
		String jsonToSqlWhere = null;
		String jsonToSqlOnDuplicate = null;
		if (null != jsonObject) {
			Iterator<String> iter = jsonObject.keySet().iterator();
			while (iter.hasNext()) {

				String key = iter.next();
				Object value = jsonObject.get(key);
				// 标准化value输出
				if (null != jsonValueObject) {
					value = JsonFormat.jsonValueFormat(jsonValueObject, key,
							value);
				}
				// 标准化key输出
				if (null != jsonKeyObject) {
					key = JsonFormat.jsonKeyFormat(jsonKeyObject, key);
				}
				if (null != key && !"".equals(key)) {

					if ("U".equals(changeOper) || "D".equals(changeOper)) {
						if (isPrimaryKey(pramKeys, key)) {
							putResultMap(resultMapWhere, key, value);
						} else {
							putResultMap(resultMapSet, key, value);
						}
					} else {
						putResultMap(resultMapSet, key, value);
						if (!isPrimaryKey(pramKeys, key)) {
							putResultMap(resultMapOnDuplicate, key, value);
						}
					}
				}
			}

			jsonToSql = resultMapSet.toString();
			jsonToSqlOnDuplicate = resultMapOnDuplicate.toString();
			jsonToSqlWhere = resultMapWhere.toString();
			jsonToSql = jsonToSql.substring(1, jsonToSql.length() - 1);
			jsonToSqlOnDuplicate = jsonToSqlOnDuplicate.substring(1,
					jsonToSqlOnDuplicate.length() - 1);
			jsonToSqlWhere = jsonToSqlWhere.substring(1,
					jsonToSqlWhere.length() - 1).replaceAll(",", " and ");
		}

		if (null == jsonToSqlWhere || ("").equals(jsonToSqlWhere)) {
			jsonToSqlWhere = "0=1";
		}

		if (("U").equals(changeOper)) {
			StringBuilder builder = new StringBuilder("update ")
					.append(tableName).append(" set ").append(jsonToSql);
			if (jsonToSql != null && !jsonToSql.equals("")) {
				builder.append(", ");
			}
			if (jsonDefString != null && !jsonDefString.equals("")) {
				builder.append(jsonDefString);
			}
			jsonToSql = builder.append(" where ").append(jsonToSqlWhere)
					.append(";").toString();
		} else if (("I").equals(changeOper)) {
			StringBuilder builder = new StringBuilder("insert into ")
					.append(tableName).append(" set ").append(jsonToSql);
			if (jsonDefString != null && jsonDefString != "") {
				builder.append(", ").append(jsonDefString);
			}
			builder.append(" on duplicate key update ").append(
					jsonToSqlOnDuplicate);
			if (jsonDefString != null && jsonDefString != "") {
				builder.append(", ").append(jsonDefString);
			}
			jsonToSql = builder.append(";").toString();

		} else if (("D").equals(changeOper)) {
			jsonToSql = new StringBuilder("delete from ").append(tableName)
					.append(" where ").append(jsonToSqlWhere).append(";")
					.toString();
		}

		return jsonToSql;
	}

	/**
	 * 当做更新操作时，判断key是否为主键
	 * 
	 * @param keyArray
	 * @param keyStr
	 * @return
	 */
	public static boolean isPrimaryKey(Map<String, String> keyArray,
			String keyStr) {
		return keyArray.containsKey(keyStr);

	}

	/**
	 * 标准化map输出
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static void putResultMap(Map<String, Object> resultMap, String key,
			Object value) {
		if (value instanceof String) {
			resultMap.put(
					key,
					("'"
							+ StringEscapeUtils.unescapeJava(StringUtils
									.replace(filterOffUtf8Mb4((String) value),
											"'", "''")) + "'"));
		} else {
			resultMap.put(key, value);
		}
	}

	/**
	 * 源数据中文乱码转换
	 * 
	 * @param messyCode
	 * @return
	 */
	public static String filterOffUtf8Mb4(String messyCode) {
		if (null != messyCode && !"".equals(messyCode)) {
			try {
				byte[] bytes = messyCode.getBytes("utf-8");
				byte[] tmp = { '?' };
				ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
				int i = 0;
				while (i < bytes.length) {
					short b = bytes[i];
					if (b > 0) {
						buffer.put(bytes[i++]);
						continue;
					}

					b += 256; // 去掉符号位

					if (((b >> 5) ^ 0x6) == 0) {
						buffer.put(bytes, i, 2);
						i += 2;
					} else if (((b >> 4) ^ 0xE) == 0) {
						buffer.put(bytes, i, 3);
						i += 3;
					} else if (((b >> 3) ^ 0x1E) == 0) {
						buffer.put(tmp);
						i += 4;
					} else if (((b >> 2) ^ 0x3E) == 0) {
						buffer.put(tmp);
						i += 5;
					} else if (((b >> 1) ^ 0x7E) == 0) {
						buffer.put(tmp);
						i += 6;
					} else {
						buffer.put(bytes[i++]);
					}
				}
				buffer.flip();
				return new String(buffer.array(), "utf-8");
			} catch (UnsupportedEncodingException e) {

			}
		}
		return messyCode;
	}
}
