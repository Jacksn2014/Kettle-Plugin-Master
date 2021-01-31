package org.pentaho.di.trans.steps.redisinput;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;


/**
 * The Redis Input step looks up value objects, from the given key names, from Redis server(s).
 * @author minjie.qu
 * @createDate 2019年1月6日 下午4:18:34
 * @version v0.1
 * @describe 从Redis读取数据，定义kettle插件的RowMetaInterface
 */
public class RedisInputData extends BaseStepData implements StepDataInterface {

    public RowMetaInterface outputRowMeta;

    /**
     *
     */
    public RedisInputData() {
        super();
    }

}
