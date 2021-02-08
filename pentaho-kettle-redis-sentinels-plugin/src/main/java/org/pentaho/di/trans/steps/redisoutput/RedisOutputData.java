package org.pentaho.di.trans.steps.redisoutput;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;


/**
 * The Redis Output step insert into value objects, by the given key names, from Redis server(s).
 * @author 微信公众号"以数据之名"
 * @createDate 2019年1月7日 上午9:40:41
 * @version v0.1
 * @describe 往Redis插入数据，定义kettle插件的RowMetaInterface
 */
public class RedisOutputData extends BaseStepData implements StepDataInterface {

    public RowMetaInterface outputRowMeta;

    /**
     *
     */
    public RedisOutputData() {
        super();
    }

}
