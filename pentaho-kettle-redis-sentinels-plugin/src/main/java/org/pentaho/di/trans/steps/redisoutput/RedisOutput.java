package org.pentaho.di.trans.steps.redisoutput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pentaho.di.core.exception.KettleException;
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
 * The Redis Output step insert into value objects, by the given key names, from
 * Redis server(s).
 * 
 * @author minjie.qu
 * @createDate 2019年1月6日 下午5:04:25
 * @version v0.1
 * @describe 往Redis插入数据，初始化Redis连接配置
 */
public class RedisOutput extends BaseStep implements StepInterface {
	public static Class<?> PKG = RedisOutputMeta.class; // for i18n purposes, needed by Translator2!! $NON-NLS-1$

	protected RedisOutputMeta meta;
	protected RedisOutputData data;
	String keyfieldname;
	String valuefieldname;
	String ttlfieldname;
	ArrayList<Object> rowkey = new ArrayList<Object>();

	public RedisOutput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	//pool设置为static变量，多组件共享，会存在某一个组件使用完连接池，导致其他组件pool不可用
	//private static JedisSentinelPool pool = null;
	private  JedisSentinelPool pool = null;
	ExecutorService service = Executors.newFixedThreadPool(12);

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		long start = System.currentTimeMillis();
		if (super.init(smi, sdi)) {
			try {
				// Create client and connect to redis server(s)
				Set<Map<String, String>> jedisClusterNodes = ((RedisOutputMeta) smi).getServers();
				// 建立连接池配置参数
				JedisPoolConfig config = new JedisPoolConfig();
				// 设置最大连接数
				config.setMaxTotal(10000);
				// 设置最大阻塞时间，记住是毫秒数milliseconds
				config.setMaxWaitMillis(10000);
				// 设置最大空闲连接数
				config.setMaxIdle(300);
				// jedis实例是否可用
				config.setTestOnBorrow(true);
				// return 一个jedis实例给pool时，是否检查连接可用性（ping()）
				// config.setTestOnReturn(true);
			    // 设置jedis实例空闲检查连接可用性
				config.setTestWhileIdle(true);
				// 创建连接池
				// 获取redis密码
				String password = null;
				int timeout = 3000;
				String masterName = ((RedisOutputMeta) smi).getMasterName();
				Set<String> sentinels = new HashSet<String>();
				Iterator<Map<String, String>> it = jedisClusterNodes.iterator();
				while (it.hasNext()) {
					Map<String, String> hostAndPort = it.next();
					password = hostAndPort.get("auth");
					sentinels.add(hostAndPort.get("hostname") + ":" + hostAndPort.get("port"));
				}
				pool = new JedisSentinelPool(masterName, sentinels, config, timeout, password);
				long end = System.currentTimeMillis();
				logBasic("建立连接池 毫秒：" + (end - start));

				return true;
			} catch (Exception e) {
				logError(BaseMessages.getString(PKG, "RedisInput.Error.ConnectError"), e);
				return false;
			}
		} else {
			return false;
		}

	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (RedisOutputMeta) smi;
		data = (RedisOutputData) sdi;

		// TODO Auto-generated method stub
		Jedis jedis = pool.getResource();
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
			valuefieldname = meta.getValueFieldName();
			ttlfieldname = meta.getTtlFieldName();
			logBasic("keyfieldname:" + keyfieldname);
			logBasic("valuefieldname:" + valuefieldname);
			logBasic("ttlfieldname:" + ttlfieldname);
		}

		RedisOutputThread thread = new RedisOutputThread(this, jedis, r);
		service.submit(thread);

		if (checkFeedback(getLinesRead())) {
			if (log.isBasic()) {
				logBasic(BaseMessages.getString(PKG, "RedisOutput.Log.LineNumber") + getLinesRead());
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
		service.shutdown();
	}
}
