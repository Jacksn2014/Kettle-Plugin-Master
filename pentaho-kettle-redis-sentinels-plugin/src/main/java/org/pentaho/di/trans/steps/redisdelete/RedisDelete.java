package org.pentaho.di.trans.steps.redisdelete;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

/**
 * The Redis Delete step delete value objects, by the given key names, from
 * Redis server(s).
 * 
 * @author 微信公众号"以数据之名"
 * @createDate 2019年1月6日 下午4:26:40
 * @version v0.1
 * @describe 从Redis删除Key，初始化Redis连接配置
 */
public class RedisDelete extends BaseStep implements StepInterface {
	private static Class<?> PKG = RedisDeleteMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

	protected RedisDeleteMeta meta;
	protected RedisDeleteData data;
	String mastername;
	String keyfieldname;
	String valuefieldname;
	String valuetype;

	public RedisDelete(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	// pool设置为static变量，多组件共享，会存在某一个组件使用完连接池，导致其他组件pool不可用
	// private static JedisSentinelPool pool = null;
	private JedisSentinelPool pool = null;

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		if (super.init(smi, sdi)) {
			try {
				// Create client and connect to redis server(s)
				Set<Map<String, String>> jedisClusterNodes = ((RedisDeleteMeta) smi).getServers();
				// 建立连接池配置参数
				JedisPoolConfig config = new JedisPoolConfig();
				// 设置最大连接数
				config.setMaxTotal(2000);
				// 设置最大阻塞时间，记住是毫秒数milliseconds
				config.setMaxWaitMillis(1000);
				// 设置最大空闲连接数
				config.setMaxIdle(30);
				// jedis实例是否可用
				config.setTestOnBorrow(true);
				// 设置jedis实例空闲检查连接可用性
				config.setTestWhileIdle(true);
				// 创建连接池
				// 获取redis密码
				String password = null;
				int timeout = 3000;
				String masterName = ((RedisDeleteMeta) smi).getMasterName();
				Set<String> sentinels = new HashSet<String>();
				Iterator<Map<String, String>> it = jedisClusterNodes.iterator();
				while (it.hasNext()) {
					Map<String, String> hostAndPort = it.next();
					password = hostAndPort.get("auth");
					sentinels.add(hostAndPort.get("hostname") + ":" + hostAndPort.get("port"));
				}
				pool = new JedisSentinelPool(masterName, sentinels, config, timeout, password);

				return true;
			} catch (Exception e) {
				logError(BaseMessages.getString(PKG, "RedisDelete.Error.ConnectError"), e);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (RedisDeleteMeta) smi;
		data = (RedisDeleteData) sdi;
		Object[] r = getRow(); // get row, set busy!
		// If no more input to be expected, stop
		if (r == null) {
			setOutputDone();
			return false;
		}
		if (first) {
			first = false;
			// clone input row meta for now, we will change it (add or set inline) later
			data.outputRowMeta = getInputRowMeta().clone();
			// Get output field types
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			keyfieldname = meta.getKeyFieldName();
			logBasic("keyfieldname:" + keyfieldname);
			valuefieldname = meta.getValueFieldName();
			logBasic("valueFieldName:" + valuefieldname);
			valuetype = meta.getValueTypeName();
			logBasic("valuetype:" + valuetype);
			mastername = meta.getMasterName();
			logBasic("mastername:" + mastername);
		}

		// Get value from redis, don't cast now, be lazy. TODO change this?
		int keyFieldIndex = getInputRowMeta().indexOfValue(meta.getKeyFieldName());
		if (keyFieldIndex < 0) {
			throw new KettleException(BaseMessages.getString(PKG, "RedisDeletetMeta.Exception.KeyFieldNameNotFound"));
		}

		Jedis jedis = pool.getResource();
		String outPutValue = "";
		String[] delKeys=r[keyFieldIndex].toString().split(",");
		try {
			Long delete_flag = jedis.del(delKeys);
			outPutValue = String.valueOf(delete_flag);
		} finally {
			jedis.close();
		}

		// logBasic("output:" + output);
		Object[] outputRowData = r;
		int valueFieldIndex = getInputRowMeta().indexOfValue(meta.getValueFieldName());
		if (valueFieldIndex < 0 || valueFieldIndex > outputRowData.length) {
			// Not found so add it
			outputRowData = RowDataUtil.addValueData(r, getInputRowMeta().size(), outPutValue);
		} else {
			// Update value in place
			outputRowData[valueFieldIndex] = outPutValue;
		}

		putRow(data.outputRowMeta, outputRowData); // copy row to possible alternate rowset(s).

		if (checkFeedback(getLinesRead())) {
			if (log.isBasic()) {
				logBasic(BaseMessages.getString(PKG, "RedisDelete.Log.LineNumber") + getLinesRead());
			}
		}
		return true;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		super.dispose(smi, sdi);
		if (pool != null) {
			pool.close();
			pool.destroy();
		}
	}
}
