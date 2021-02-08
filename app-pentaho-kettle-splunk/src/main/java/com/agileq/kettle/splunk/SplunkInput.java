package com.agileq.kettle.splunk;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.agileq.kettle.splunk.connection.SplunkConnection;
import com.splunk.JobResultsArgs;
import com.splunk.ResultsReaderXml;
import com.splunk.Service;

/**
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @ClassName: SplunkInput
 * @Description: Holds data processed by this step
 * @date: 2019年8月18日 下午2:33:48
 */
public class SplunkInput extends BaseStep implements StepInterface {

	private SplunkInputMeta meta;
	private SplunkInputData data;

	public SplunkInput(StepMeta stepMeta, StepDataInterface stepDataInterface,
			int copyNr, TransMeta transMeta, Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {

		meta = (SplunkInputMeta) smi;
		data = (SplunkInputData) sdi;

		// Is the step getting input?
		// List<StepMeta> steps = getTransMeta().findPreviousSteps(
		// getStepMeta() );

		// Connect to Neo4j
		if (StringUtils.isEmpty(meta.getHost())) {
			log.logError("You need to specify a Splunk connection Host to use in this step");
			return false;
		}
		if (StringUtils.isEmpty(meta.getPort())) {
			log.logError("You need to specify a Splunk connection Port to use in this step");
			return false;
		}
		if (StringUtils.isEmpty(meta.getUsername())) {
			log.logError("You need to specify a Splunk connection Username to use in this step");
			return false;
		}
		if (StringUtils.isEmpty(meta.getPassword())) {
			log.logError("You need to specify a Splunk connection Password to use in this step");
			return false;
		}
		// To correct lazy programmers who built certain PDI steps...
		//System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3");
		
		//Security.setProperty("jdk.tls.disabledAlgorithms","SSLv3, RC4, MD5withRSA, DH keySize < 768");

		 /* Overriding the static method setSslSecurityProtocol to implement the security protocol of choice */
        // HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
        /* end comment for overriding the method setSslSecurityProtocol */
		
		data.splunkConnection = new SplunkConnection(meta.getName(),
				meta.getHost(), meta.getPort(), meta.getUsername(),
				meta.getPassword());
		data.splunkConnection.initializeVariablesFrom(this);

		try {

			data.serviceArgs = data.splunkConnection.getServiceArgs();
			
			data.service = Service.connect(data.serviceArgs);

		} catch (Exception e) {
			log.logError(
					"Unable to get or create Neo4j database driver for database '"
							+ data.splunkConnection.getName() + "'", e);
			return false;
		}

		return super.init(smi, sdi);
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {

		meta = (SplunkInputMeta) smi;
		data = (SplunkInputData) sdi;

		super.dispose(smi, sdi);
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi)
			throws KettleException {

		meta = (SplunkInputMeta) smi;
		data = (SplunkInputData) sdi;

		if (first) {
			first = false;

			// get the output fields...
			data.outputRowMeta = new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null,
					getStepMeta(), this, repository, data.metaStore);

			// Run a one shot search in blocking mode
			JobResultsArgs args = new JobResultsArgs();
			args.setCount(0);
			args.setOutputMode(JobResultsArgs.OutputMode.XML);

			data.eventsStream = data.service.oneshotSearch(getTransMeta()
					.environmentSubstitute(meta.getQuery()), args);

		}

		try {
			ResultsReaderXml resultsReader = new ResultsReaderXml(
					data.eventsStream);
			HashMap<String, String> event;
			while ((event = resultsReader.getNextEvent()) != null) {

				Object[] outputRow = RowDataUtil
						.allocateRowData(data.outputRowMeta.size());

				for (int i = 0; i < meta.getReturnValues().size(); i++) {
					ReturnValue returnValue = meta.getReturnValues().get(i);
					String value = event.get(returnValue.getSplunkName());
					outputRow[i] = value;
				}

				incrementLinesInput();
				putRow(data.outputRowMeta, outputRow);
			}

		} catch (Exception e) {
			throw new KettleException(
					"Error reading from Splunk events stream", e);
		} finally {
			try {
				data.eventsStream.close();
			} catch (IOException e) {
				throw new KettleException("Unable to close events stream", e);
			}
		}

		setOutputDone();
		return false;
	}

}
