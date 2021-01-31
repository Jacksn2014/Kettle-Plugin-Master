package com.agileq.kettle.mts;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MqToSqlTest {
	
	public static final String BILL_BATCH_RELATIONAL_OPERATOR_COMMA_SEPERATOR = "\\,";
	public static final String BILL_BATCH_RELATIONAL_OPERATOR_SEMICOLON_SEPERATOR = "\\;";
	public static final String BILL_BATCH_RELATIONAL_OPERATOR_DELIVERY_SEPERATOR = "\\%";

	public static final String BILL_BATCH_RELATIONAL_OPERATOR_COMMA = ",";
	public static final String BILL_BATCH_RELATIONAL_OPERATOR_SEMICOLON = ";";
	public static final String BILL_BATCH_RELATIONAL_OPERATOR_DELIVERY = "%";
	
	public static final String REPORT_SHEET_SQL_OPERATOR_AND_SEPERATOR = "\\&&&&&&";
	
	public static final String REPORT_SHEET_SQL_OPERATOR_AND = "&&&&&&";

	public static void main(String[] args) {

		String json = "{\"TRADE_ID\":49,\"CREATE_TIME\":\"2016-12-31 00:00:00\",\"FUNCTION_CODE\":\"1r\",\"d\":\"8\",\"TRADE_AMOUNT\":140.98,\"REF_CTRL_ID\":\"1111111111\"}";
		String jsonKey = "{\"TRADE_ID\":\"TRADE_ID\",\"FUNCTION_CODE\":\"TRADE_AMT\",\"MERCHANT_CODE\":\"MERCHANT_CODE\",\"BILL_ORDER_NO\":\"BILL_ORDER_NO\",\"TRADE_AMOUNT\":\"FUNCTION_CODE\",\"TRADE_STATUS\":\"TRADE_STATUS\",\"ERROR_CODE\":\"ERROR_CODE\",\"ERROR_INFO\":\"ERROR_INFO\",\"MEMBER_CODE\":\"MEMBER_CODE\",\"CHANNEL_TYPE\":\"CHANNEL_TYPE\",\"PAY_MODE\":\"PAY_MODE\",\"CURRENCY_CODE\":\"CURRENCY\",\"TRADE_START_TIME\":\"TRADE_START_TIME\",\"TRADE_END_TIME\":\"TRADE_END_TIME\",\"BANK_ACCT_ID\":\"BANK_ACCT_ID\",\"BANK_ACCT_TYPE\":\"BANK_ACCT_TYPE\",\"BANK_ACCT_NAME\":\"BANK_ACCT_NAME\",\"BILL_ORDER_ID\":\"BILL_ORDER_ID\",\"STL_MERCHANT_CODE\":\"STL_MERCHANT_CODE\",\"REF_ID_CTRL\":\"REF_ID_CTRL\",\"MEMBER_BANK_ID\":\"MEMBER_BANK_ID\",\"MAIN_MERCHANT_ID\":\"MAIN_MERCHANT_ID\",\"SHARE_AMOUNT\":\"SHARE_AMT\",\"NET_AMOUNT\":\"NET_AMT\",\"CREATE_TIME\":\"CREATE_TIME\",\"MODIFY_TIME\":\"UPDATE_TIME\",\"ORIG_TRADE_ID\":\"ORIG_TRADE_ID\",\"EQUITY_CODE\":\"EQUITY_CODE\",\"EQUITY_AMOUNT\":\"EQUITY_AMT\",\"VERSION\":\"VERSION\"}";
		String jsonValue = "{\"TRADE_AMOUNT\":\"TRADE_AMOUNT*10\",\"NET_AMOUNT\":\"NET_AMOUNT*10\",\"EQUITY_AMOUNT\":\"EQUITY_AMOUNT*10\"}";
		String jsonDefStr = "ods_last_update_date = sysdate(6)";

		long l1 = System.currentTimeMillis();
		System.out.println(JsonToSqlUtil.jsonToMapSql(json,
				JSONObject.parseObject(jsonKey),
				JSONObject.parseObject(jsonValue), "U",
				"tf_evt_qq_txn", jsonDefStr,
				isPrimaryKey("TRADE_ID,CREATE_TIME")));
		System.out.println("sql解析update耗时" + (System.currentTimeMillis() - l1));

		json = "{\"FUNCTION_CODE\":20,\"ORDER_STATUS\":0,\"CREATE_TIME\":\"2017-05-05 16:36:04.000000\",\"REFUND_AMOUNT\":10,\"TXN_TIME_EXPIRE\":\"2017-05-06 16:36:04.000000\",\"OUT_TRADE_NO\":\"1705051636101744\",\"CHANNEL_TYPE\":22,\"VERSION\":0,\"MASTER_ID\":117052086,\"STAGE_INFO\":\"{\\\"D\\\":[{\\\"N\\\":3,\\\"R\\\":0.01,\\\"F\\\":84.85,\\\"P\\\":2913.1833,\\\"TF\\\":254.55,\\\"T\\\":8739.55},{\\\"N\\\":6,\\\"R\\\":0.009,\\\"F\\\":76.365,\\\"P\\\":1490.5317,\\\"TF\\\":458.19,\\\"T\\\":8943.19},{\\\"N\\\":12,\\\"R\\\":0.008,\\\"F\\\":67.88,\\\"P\\\":774.9633,\\\"TF\\\":814.56,\\\"T\\\":9299.56}]}\",\"SUB_MERCHANT_NAME\":\"蝶巢精致生活平台\",\"UPDATE_TIME\":\"2017-05-05 16:36:04.000000\",\"PRODUCT_DESC\":\"微软笔记本  Surface Pro 4 12.3英寸（i58G256G）\",\"EXT2\":\"7965CD06D6B3B979B50A002C65844EDDBA1BFEF85092117A99F80C5B676C7A8544BA05283BCC743C09B602478517E880A2D56621FEE887ED68D69F8AB5C60BE95CBE2EACB0F4992E5D8FF7CB6EF55660162967DE4DCB42BB25DCE51EE28BDBCE61D6050310DAD833E5962E51A36A031B57B0CDAED045FBF48EE6888555CB161A5D06DFFFAC8C513FF7D5FB339F680EDE\",\"BG_URL\":\"http://www.99-ss.com/billstore/public/index.php/wap/payback.html\",\"SECURE_TYPE\":0,\"APP_ID\":\"102\",\"MEMBER_CODE\":\"10078762596\",\"TXN_TIME_START\":\"2017-05-05 16:05:04.000000\",\"BILL_ORDER_NO\":\"920170505163611705208643766\",\"MAIN_MERCHANT_ID\":\"812310053111688\",\"PAY_AMOUNT\":0,\"ORDER_TYPE\":180001,\"ORDER_AMOUNT\":848500,\"BIZ_EXT\":\"{\\\"ateVersion\\\":\\\"3.0\\\"}\",\"PAY_STATUS\":0,\"PRODUCT_TAG\":\"[{\\\"id\\\":1257,\\\"quantity\\\":1,\\\"price\\\":848500}]\",\"EQUITY_AMOUNT\":0,\"CURRENCY_CODE\":\"CNY\",\"MERCHANT_CODE\":\"10073243176\"}";
		jsonKey = "{\"MASTER_ID\":\"MASTER_ID\",\"BILL_ORDER_NO\":\"BILL_ORDER_NO\",\"OUT_TRADE_NO\":\"OUT_TRADE_NO\",\"MERCHANT_CODE\":\"MERCHANT_CODE\",\"APP_ID\":\"APP_ID\",\"CHANNEL_TYPE\":\"CHANNEL_TYPE\",\"ORDER_TYPE\":\"ORDER_TYPE\",\"FUNCTION_CODE\":\"FUNCTION_CODE\",\"TXN_TIME_START\":\"TXN_TIME_START\",\"ORDER_AMOUNT\":\"ORDER_AMT\",\"TRADE_ID\":\"TRADE_ID\",\"TXN_END_TIME\":\"TXN_END_TIME\",\"TXN_TIME_EXPIRE\":\"TXN_TIME_EXPIRE\",\"ORDER_STATUS\":\"ORDER_STATUS\",\"PAY_STATUS\":\"PAY_STATUS\",\"PAY_AMOUNT\":\"PAY_AMT\",\"EQUITY_FLAG\":\"EQUITY_FLAG\",\"EQUITY_CODE\":\"EQUITY_CODE\",\"EQUITY_AMOUNT\":\"EQUITY_AMT\",\"EQUITY_DESC\":\"EQUITY_DESC\",\"PAY_MEDIA\":\"PAY_MEDIA\",\"AUTH_CODE\":\"AUTH_CODE\",\"PAY_MODE\":\"PAY_MODE\",\"MEMBER_BANK_ID\":\"MEMBER_BANK_ID\",\"CURRENCY_CODE\":\"CURRENCY_CODE\",\"BANK_ACCT_ID\":\"BANK_ACCT_ID\",\"BANK_ACCT_TYPE\":\"BANK_ACCT_TYPE\",\"BANK_ACCT_NAME\":\"BANK_ACCT_NAME\",\"SECURE_TYPE\":\"SECURE_TYPE\",\"MEMBER_CODE\":\"MEMBER_CODE\",\"MEMBER_MARK\":\"MEMBER_MARK\",\"PRODUCT_TAG\":\"PROD_TAG\",\"PRODUCT_DESC\":\"PROD_DESC\",\"ORDER_COUNT\":\"ORDER_COUNT\",\"SUB_MERCHANT_NAME\":\"SUB_MERCHANT_NAME\",\"STL_MERCHANT_CODE\":\"STL_MERCHANT_CODE\",\"MAIN_MERCHANT_ID\":\"MAIN_MERCHANT_ID\",\"BILL_ORDER_ID\":\"BILL_ORDER_ID\",\"TXN_SEND_IP\":\"TXN_SEND_IP\",\"TERMINAL_ID\":\"TERMINAL_ID\",\"DEVICE_INFO\":\"DEVICE_INFO\",\"MEMO\":\"MEMO\",\"ERROR_CODE\":\"ERROR_CODE\",\"ERROR_INFO\":\"ERROR_INFO\",\"REFUND_AMOUNT\":\"REFUND_AMT\",\"TXN_REVERSE_TIME\":\"TXN_REVERSE_TIME\",\"STAGE_INFO\":\"STAGE_INFO\",\"SIGN_PATH\":\"SIGN_PATH\",\"CREATE_TIME\":\"CREATE_TIME\",\"UPDATE_TIME\":\"UPDATE_TIME\",\"EXT1\":\"EXT1\",\"EXT2\":\"EXT2\",\"EXT3\":\"EXT3\",\"VERSION\":\"VERSION\",\"BG_URL\":\"BG_URL\"}";
		jsonValue = "{\"ORDER_AMOUNT\":\"ORDER_AMOUNT*10\",\"PAY_AMOUNT\":\"PAY_AMOUNT*10\",\"EQUITY_AMOUNT\":\"EQUITY_AMOUNT*10\",\"REFUND_AMOUNT\":\"REFUND_AMOUNT*10\",\"MEMBER_CODE\":\"MEMBER_CODE.substring(0,11)\"}";
		jsonDefStr = "ods_eff_from_date=sysdate(6),ods_eff_to_date='2099-12-31 00:00:00',ods_last_update_date=sysdate(6),ods_last_update_staff='ETL'";

		l1 = System.currentTimeMillis();
		System.out.println(JsonToSqlUtil.jsonToMapSql(json,
				JSONObject.parseObject(jsonKey),
				JSONObject.parseObject(jsonValue), "I",
				"tf_evt_qq_txn", jsonDefStr,
				isPrimaryKey("MASTER_ID")));
		System.out.println("sql解析insert耗时" + (System.currentTimeMillis() - l1));

		json = "{\"MASTER_ID\":9999,\"CREATE_TIME\":\"2099-12-31 00:00:00\",\"OUT_TRADE_NO\":\"2017-05-05 16:36:04.000000\",\"MERCHANT_CODE\":null}";
		jsonKey = "{\"MASTER_ID\":\"MASTER_ID\",\"BILL_ORDER_NO\":\"BILL_ORDER_NO\",\"OUT_TRADE_NO\":\"OUT_TRADE_NO\",\"MERCHANT_CODE\":\"MERCHANT_CODE\",\"APP_ID\":\"APP_ID\",\"CHANNEL_TYPE\":\"CHANNEL_TYPE\",\"ORDER_TYPE\":\"ORDER_TYPE\",\"FUNCTION_CODE\":\"FUNCTION_CODE\",\"TXN_TIME_START\":\"TXN_TIME_START\",\"ORDER_AMOUNT\":\"ORDER_AMT\",\"TRADE_ID\":\"TRADE_ID\",\"TXN_END_TIME\":\"TXN_END_TIME\",\"TXN_TIME_EXPIRE\":\"TXN_TIME_EXPIRE\",\"ORDER_STATUS\":\"ORDER_STATUS\",\"PAY_STATUS\":\"PAY_STATUS\",\"PAY_AMOUNT\":\"PAY_AMT\",\"EQUITY_FLAG\":\"EQUITY_FLAG\",\"EQUITY_CODE\":\"EQUITY_CODE\",\"EQUITY_AMOUNT\":\"EQUITY_AMT\",\"EQUITY_DESC\":\"EQUITY_DESC\",\"PAY_MEDIA\":\"PAY_MEDIA\",\"AUTH_CODE\":\"AUTH_CODE\",\"PAY_MODE\":\"PAY_MODE\",\"MEMBER_BANK_ID\":\"MEMBER_BANK_ID\",\"CURRENCY_CODE\":\"CURRENCY_CODE\",\"BANK_ACCT_ID\":\"BANK_ACCT_ID\",\"BANK_ACCT_TYPE\":\"BANK_ACCT_TYPE\",\"BANK_ACCT_NAME\":\"BANK_ACCT_NAME\",\"SECURE_TYPE\":\"SECURE_TYPE\",\"MEMBER_CODE\":\"MEMBER_CODE\",\"MEMBER_MARK\":\"MEMBER_MARK\",\"PRODUCT_TAG\":\"PROD_TAG\",\"PRODUCT_DESC\":\"PROD_DESC\",\"ORDER_COUNT\":\"ORDER_COUNT\",\"SUB_MERCHANT_NAME\":\"SUB_MERCHANT_NAME\",\"STL_MERCHANT_CODE\":\"STL_MERCHANT_CODE\",\"MAIN_MERCHANT_ID\":\"MAIN_MERCHANT_ID\",\"BILL_ORDER_ID\":\"BILL_ORDER_ID\",\"TXN_SEND_IP\":\"TXN_SEND_IP\",\"TERMINAL_ID\":\"TERMINAL_ID\",\"DEVICE_INFO\":\"DEVICE_INFO\",\"MEMO\":\"MEMO\",\"ERROR_CODE\":\"ERROR_CODE\",\"ERROR_INFO\":\"ERROR_INFO\",\"REFUND_AMOUNT\":\"REFUND_AMT\",\"TXN_REVERSE_TIME\":\"TXN_REVERSE_TIME\",\"STAGE_INFO\":\"STAGE_INFO\",\"SIGN_PATH\":\"SIGN_PATH\",\"CREATE_TIME\":\"CREATE_TIME\",\"UPDATE_TIME\":\"UPDATE_TIME\",\"EXT1\":\"EXT1\",\"EXT2\":\"EXT2\",\"EXT3\":\"EXT3\",\"VERSION\":\"VERSION\",\"BG_URL\":\"BG_URL\"}";
		jsonValue = "{\"ORDER_AMOUNT\":\"ORDER_AMOUNT*10\",\"PAY_AMOUNT\":\"PAY_AMOUNT*10\",\"EQUITY_AMOUNT\":\"EQUITY_AMOUNT*10\",\"REFUND_AMOUNT\":\"REFUND_AMOUNT*10\",\"MEMBER_CODE\":\"MEMBER_CODE.substring(0,11)\"}";
		jsonDefStr = "ods_eff_from_date=sysdate(6),ods_eff_to_date='2099-12-31 00:00:00',ods_last_update_date=sysdate(6),ods_last_update_staff='ETL'";

		System.out.println("tttttttttttttttt--------"
				+ new MqToSqlTest().isJson(jsonKey));
		JSONObject tstObj = JSONObject.parseObject(json);
		System.out.println("9999999999999999999999" + tstObj.toJSONString());
		System.out.println("9999999999999999999999"
				+ JSONObject.toJSONString(tstObj,
						SerializerFeature.WriteMapNullValue));

		l1 = System.currentTimeMillis();
		System.out.println(JsonToSqlUtil.jsonToMapSql(json,
				JSONObject.parseObject(jsonKey),
				JSONObject.parseObject(jsonValue), "U",
				"tf_evt_qq_txn", jsonDefStr,
				isPrimaryKey("MASTER_ID,CREATE_TIME")));
		System.out.println("sql解析insert耗时" + (System.currentTimeMillis() - l1));

		json = "{\"CREATE_TIME\":\"2020-06-12 16:13:31.000000\",\"ACCOUNT_ID\":\"33001942006\",\"AMT\":933.00,\"DIG_TYPE\":null,\"SETTLE_FEE\":null,\"TRADE_TIME\":\"2020-06-12 16:13:31.000000\",\"ORIGIN_OUT_TRACE_NO\":null,\"VERSION\":0,\"OUT_TRACE_NO\":\"T0000002010398007\",\"REFUND_AMT\":null,\"ID\":90000002010397007,\"FO_STATUS\":null,\"RSP_MESSAGE\":null,\"EXT_DATA\":\"{\\\"x_location\\\":\\\"108.9705637916058\\\",\\\"y_location\\\":\\\"34.26850233527618\\\",\\\"isRoutingJdbc\\\":\\\"1\\\"}\",\"UPDATE_TIME\":\"2020-06-12 16:13:31.000000\",\"CHANNEL\":\"1\",\"INNER_ORDER_NO\":\"T0000002010398007\",\"FO_AMT\":null,\"APP_TYPE\":null,\"TERM_TRACE_NO\":null,\"RT_TYPE\":null,\"SN_CODE\":\"00000303QJNL01143121\",\"EXT1\":null,\"EXT2\":null,\"BUSINESS_MEMBER_CODE\":null,\"EXT3\":null,\"TRADE_TERMINAL_ID\":null,\"ID_TXN_CTRL\":null,\"VIP\":\"1\",\"SMALL_FREE\":null,\"PRODUCT_CODE\":null,\"STATUS\":\"0\",\"TRADE_MERCHANT_ID\":null,\"SETTLE_STATUS\":null,\"TRADE_MERCHANT_NAME\":null,\"FO_SERIAL_ID\":null,\"EXP_TIME\":null,\"TERM_BATCH_NO\":null,\"ORDER_TYPE\":0,\"CITY_CODE\":\"610100\",\"CARD_TYPE\":null,\"BIZ_TYPE\":0,\"ID_TXN\":null,\"RSP_CODE\":null,\"RISK_INFO\":\"{\\\"xProvince\\\":\\\"陕西省\\\",\\\"xCity\\\":\\\"西安市\\\",\\\"merchantCode\\\":\\\"10210738348\\\",\\\"deviceId\\\":\\\"A0E3C098-771E-4453-A215-51A3B4509927\\\",\\\"xCountry\\\":\\\"中国\\\"}\",\"DIG_FLAG\":null,\"FO_FEE\":null,\"USER_RT_TYPE\":null,\"TRADE_FEE\":null,\"DEVICE_TYPE\":null,\"TRADE_TYPE\":\"5\",\"TERM_INVOICE_NO\":null,\"MERCHANT_ID\":\"812610048214807\",\"TERMINAL_ID\":\"91293843\"}";
		jsonKey = "{\"ID\":\"ID\",\"TRADE_MERCHANT_ID\":\"TRADE_MERCHANT_ID\",\"TRADE_MERCHANT_NAME\":\"TRADE_MERCHANT_NAME\",\"TRADE_TERMINAL_ID\":\"TRADE_TERMINAL_ID\",\"MERCHANT_ID\":\"MERCHANT_ID\",\"ACCOUNT_ID\":\"ACCOUNT_ID\",\"BUSINESS_MEMBER_CODE\":\"BUSINESS_MEMBER_CODE\",\"TERMINAL_ID\":\"TERMINAL_ID\",\"TERM_TRACE_NO\":\"TERM_TRACE_NO\",\"TERM_BATCH_NO\":\"TERM_BATCH_NO\",\"TERM_INVOICE_NO\":\"TERM_INVOICE_NO\",\"SN_CODE\":\"SN_CODE\",\"TRADE_TYPE\":\"TRADE_TYPE\",\"AMT\":\"AMT\",\"TRADE_FEE\":\"TRADE_FEE\",\"OUT_TRACE_NO\":\"OUT_TRACE_NO\",\"INNER_ORDER_NO\":\"INNER_ORDER_NO\",\"ORIGIN_OUT_TRACE_NO\":\"ORIGIN_OUT_TRACE_NO\",\"STATUS\":\"STATUS\",\"RSP_CODE\":\"RSP_CODE\",\"RSP_MESSAGE\":\"RSP_MESSAGE\",\"TRADE_TIME\":\"TRADE_TIME\",\"CREATE_TIME\":\"CREATE_TIME\",\"UPDATE_TIME\":\"UPDATE_TIME\",\"CHANNEL\":\"CHANNEL\",\"VIP\":\"VIP\",\"RISK_INFO\":\"RISK_INFO\",\"CITY_CODE\":\"CITY_CODE\",\"ID_TXN\":\"ID_TXN\",\"ID_TXN_CTRL\":\"ID_TXN_CTRL\",\"RT_TYPE\":\"RT_TYPE\",\"USER_RT_TYPE\":\"USER_RT_TYPE\",\"CARD_TYPE\":\"CARD_TYPE\",\"SMALL_FREE\":\"SMALL_FREE\",\"EXT_DATA\":\"EXT_DATA\",\"DIG_FLAG\":\"DIG_FLAG\",\"DIG_TYPE\":\"DIG_TYPE\",\"FO_SERIAL_ID\":\"FO_SERIAL_ID\",\"FO_STATUS\":\"FO_STATUS\",\"FO_AMT\":\"FO_AMT\",\"FO_FEE\":\"FO_FEE\",\"SETTLE_STATUS\":\"SETTLE_STATUS\",\"SETTLE_FEE\":\"SETTLE_FEE\",\"EXP_TIME\":\"EXP_TIME\",\"VERSION\":\"VERSION\",\"BIZ_TYPE\":\"BIZ_TYPE\",\"ORDER_TYPE\":\"ORDER_TYPE\",\"REFUND_AMT\":\"REFUND_AMT\",\"ODS_EFF_FROM_DATE\":\"ODS_EFF_FROM_DATE\",\"ODS_EFF_TO_DATE\":\"ODS_EFF_TO_DATE\",\"ODS_LAST_UPDATE_DATE\":\"ODS_LAST_UPDATE_DATE\",\"ODS_LAST_UPDATE_STAFF\":\"ODS_LAST_UPDATE_STAFF\"}";
		jsonValue = "{\"AMT\":\"AMT*1000\",\"TRADE_FEE\":\"TRADE_FEE*1000\",\"FO_AMT\":\"FO_AMT*1000\",\"FO_FEE\":\"FO_FEE*1000\",\"SETTLE_FEE\":\"SETTLE_FEE*1000\",\"REFUND_AMT\":\"REFUND_AMT*1000\"}";
		jsonDefStr = "ods_eff_from_date=sysdate(6),ods_eff_to_date='2099-12-31 00:00:00',ods_last_update_date=sysdate(6),ods_last_update_staff='ETL'";

		System.out.println(JsonToSqlUtil.jsonToMapSql(json,
				JSONObject.parseObject(jsonKey),
				JSONObject.parseObject(jsonValue), "I", "tf_evt_qq_txn",
				jsonDefStr, isPrimaryKey("ID,TRADE_TIME")));

		json = "{\"table\": \"myoggtest.qq_txn\",\"op_type\": \"U\",\"op_ts\": \"2017-03-22 06:17:45.812871\",\"current_ts\": \"2017-03-22T14:17:52.164000\",\"pos\": \"00000000230000005478\",\"before\": {\"trade_id\": 7,\"inner_trade_no\": \"4444449999999999999999\",\"create_time\": \"0000-00-00\"},\"after\": {\"trade_id\": 7,\"inner_trade_no\": \"44444499999999\",\"create_time\": null}}";
		System.out.println("json格式化前：" + json);
		System.out.println("json格式化后：" + JSONObject.parseObject(json));
		JSONObject jsb = JSONObject.parseObject(json);
		System.out.println(jsb.get("after"));
		System.out.println("keys：" + jsb.getJSONObject("after").toJSONString());
		String str = JSON.toJSONString(jsb.getJSONObject("after"),
				SerializerFeature.WRITE_MAP_NULL_FEATURES);
		System.out.println("json格式化后：" + str);

		ObjectMapper omJson = new ObjectMapper();
		omJson.setSerializationInclusion(Include.NON_NULL);
		String outJson = null;
		try {
			outJson = omJson.writeValueAsString(jsb.get("after"));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(outJson);
		JSONObject jsbout = JSONObject.parseObject(outJson);
		System.out.println(jsbout.get("create_time"));
		System.out.println(jsbout.get("inner_trade_no"));
		
	}

	public static Map<String, String> isPrimaryKey(String pramKey) {
		String[] keys = pramKey.split("[,]");
		Map<String, String> primaryKeys = new HashMap<String, String>();
		if (keys != null) {
			for (String key : keys) {
				primaryKeys.put(key, "");
			}
		}
		return primaryKeys;
	}

	public boolean isJson(String content) {

		try {
			JSONObject jsonStr = JSONObject.parseObject(content);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
