package com.agileq.kettle.splunk;

import java.io.InputStream;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.metastore.api.IMetaStore;

import com.agileq.kettle.splunk.connection.SplunkConnection;
import com.splunk.Service;
import com.splunk.ServiceArgs;

public class SplunkInputData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface outputRowMeta;
	public SplunkConnection splunkConnection;
	public int[] fieldIndexes;
	public String query;
	public IMetaStore metaStore;

	public ServiceArgs serviceArgs;
	public Service service;
	public InputStream eventsStream;
}
