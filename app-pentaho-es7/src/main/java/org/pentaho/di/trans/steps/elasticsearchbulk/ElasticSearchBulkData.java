/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.elasticsearchbulk;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * 
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @createDate 2019年7月24日 下午2:16:24
 * @version v0.1
 * @describe ElasticSearchBulkData
 */
public class ElasticSearchBulkData extends BaseStepData implements
		StepDataInterface {
	public RowMetaInterface inputRowMeta;
	public RowMetaInterface outputRowMeta;

	public int nextBufferRowIdx;

	public Object[][] inputRowBuffer;

	public ElasticSearchBulkData() {
		super();

		nextBufferRowIdx = 0;
	}

}
