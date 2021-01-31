package org.pentaho.di.trans.steps.redisdelete;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;


/**
 * The Redis Delete step delete value objects, by the given key names, from Redis server(s).
 * @author minjie.qu
 * @createDate 2019年1月6日 下午4:31:18
 * @version v0.1
 * @describe 从Redis删除Key，定义kettle插件的RowMetaInterface
 */
public class RedisDeleteData extends BaseStepData implements StepDataInterface {

    public RowMetaInterface outputRowMeta;
    
    /**
    *
    */
   public RedisDeleteData() {
       super();
   }

}
