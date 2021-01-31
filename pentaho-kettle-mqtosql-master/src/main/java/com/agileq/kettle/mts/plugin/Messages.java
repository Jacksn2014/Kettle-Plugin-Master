package com.agileq.kettle.mts.plugin;

import org.pentaho.di.i18n.BaseMessages;

/**
 * (微信公众号：游走在数据之间)
 * @Title: MQ To SQL
 * @ClassName: Messages 
 * @Description: 消息提示类
 * @author: AgileQ
 * @date: 2017-04-12 下午07:10:26
 * @version V1.0
 */
public class Messages {
	public static final Class<?> clazz = Messages.class;

	public static String getString(String key) {
		return BaseMessages.getString(clazz, key);
	}

	public static String getString(String key, String param1) {
		return BaseMessages.getString(clazz, key, param1);
	}

	public static String getString(String key, String param1, String param2) {
		return BaseMessages.getString(clazz, key, param1, param2);
	}

	public static String getString(String key, String param1, String param2,
			String param3) {
		return BaseMessages.getString(clazz, key, param1, param2, param3);
	}

	public static String getString(String key, String param1, String param2,
			String param3, String param4) {
		return BaseMessages.getString(clazz, key, param1, param2, param3,
				param4);
	}

	public static String getString(String key, String param1, String param2,
			String param3, String param4, String param5) {
		return BaseMessages.getString(clazz, key, param1, param2, param3,
				param4, param5);
	}

	public static String getString(String key, String param1, String param2,
			String param3, String param4, String param5, String param6) {
		return BaseMessages.getString(clazz, key, param1, param2, param3,
				param4, param5, param6);
	}
}