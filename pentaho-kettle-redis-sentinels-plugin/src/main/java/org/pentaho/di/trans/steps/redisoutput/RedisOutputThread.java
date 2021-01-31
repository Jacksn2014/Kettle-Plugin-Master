package org.pentaho.di.trans.steps.redisoutput;

import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.util.StringUtil;

import redis.clients.jedis.Jedis;

/**
 * The Redis Output step insert into value objects, by the given key names, from
 * Redis server(s).
 * 
 * @author minjie.qu
 * @createDate 2019年1月7日 上午9:43:38
 * @version v0.1
 * @describe 往Redis插入数据，多线程处理类RedisOutputThread
 */
public class RedisOutputThread implements Runnable {
	private RedisOutput redisOutput;
	private Jedis jedis;
	private Object[] r;

	public RedisOutputThread(RedisOutput redisOutput, Jedis jedis, Object[] r) {
		this.redisOutput = redisOutput;
		this.jedis = jedis;
		this.r = r;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();

		// Get value from redis, don't cast now, be lazy. TODO change this?
		int keyFieldIndex = redisOutput.getInputRowMeta().indexOfValue(
				redisOutput.meta.getKeyFieldName());
		Object key = r[keyFieldIndex];

		int valueFieldIndex = redisOutput.getInputRowMeta().indexOfValue(
				redisOutput.meta.getValueFieldName());
		Object value = r[valueFieldIndex];

		int ttlFieldIndex = redisOutput.getInputRowMeta().indexOfValue(
				redisOutput.meta.getTtlFieldName());
		Object ttl = r[ttlFieldIndex];

		String keyString = key.toString();
		String valueString = value.toString();
		String ttlString = ttl.toString();

		if (keyString != null && !StringUtil.isEmpty(keyString)) {
			String getKeyValue = jedis.get(keyString);
			boolean existsKey = jedis.exists(keyString);
			// 如果key已存在,就只更新value数据；如果key不存在，更新数据同时设置key过期时间(单位：秒)
			if (existsKey) {
				jedis.set(keyString, valueString);
				redisOutput
						.logBasic(" This key already exists, so only the corresponding value= "
								+ getKeyValue + " is updated.");
			} else {
				if (ttlString != null && !StringUtil.isEmpty(ttlString)) {
					jedis.set(keyString, valueString, "NX", "EX",
							Integer.parseInt(ttlString));
				} else {
					jedis.set(keyString, valueString, "NX", "EX",
							2 * 24 * 60 * 60);
				}
			}
		}

		try {
			redisOutput.putRow(redisOutput.data.outputRowMeta, r);
			// redisOutput.rowkey.add(r[idFieldIndex]);
		} catch (KettleStepException e) {
			e.printStackTrace();
		}

		jedis.close();
		long end = System.currentTimeMillis();
		redisOutput.logBasic("Redis_Key:" + keyString + " ,Redis_Value:"
				+ valueString + " ,Redis_TTL:" + ttlString + " ,processRow "
				+ (end - start) + "  milliseconds .");
	}

}
