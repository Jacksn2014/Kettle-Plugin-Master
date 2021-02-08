package com.agileq.kettle.splunk;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionDeep;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * 微信公众号"以数据之名"
 * @author: itbigbird
 * @ClassName: SplunkInputMeta
 * @Description: define plugin meta 
 * @date: 2019年8月18日 下午2:33:48
 */
@Step(id = "KettleSplunkInput", name = "Splunk Input", description = "Read data from Splunk", image = "splunk.svg", categoryDescription = "Input")
@InjectionSupported(localizationPrefix = "Cypher.Injection.", groups = {
		"PARAMETERS", "RETURNS" })
public class SplunkInputMeta extends BaseStepMeta implements StepMetaInterface {

	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String QUERY = "query";
	public static final String RETURNS = "returns";
	public static final String RETURN = "return";
	public static final String RETURN_NAME = "return_name";
	public static final String RETURN_SPLUNK_NAME = "return_splunk_name";
	public static final String RETURN_TYPE = "return_type";
	public static final String RETURN_LENGTH = "return_length";
	public static final String RETURN_FORMAT = "return_format";

	@Injection(name = HOST)
	private String host;

	@Injection(name = PORT)
	private String port;

	@Injection(name = USERNAME)
	private String username;

	@Injection(name = PASSWORD)
	private String password;

	@Injection(name = QUERY)
	private String query;

	@InjectionDeep
	private List<ReturnValue> returnValues;

	public SplunkInputMeta() {
		super();
		returnValues = new ArrayList<>();
	}

	@Override
	public void setDefault() {
		host = "127.0.0.1";
		port = "8089";
		username = "query";
		password = "query";
		query = "search * | head 100";
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta,
			StepDataInterface stepDataInterface, int i, TransMeta transMeta,
			Trans trans) {
		return new SplunkInput(stepMeta, stepDataInterface, i, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new SplunkInputData();
	}

	@Override
	public String getDialogClassName() {
		return SplunkInputDialog.class.getName();
	}

	@Override
	public void getFields(RowMetaInterface rowMeta, String name,
			RowMetaInterface[] info, StepMeta nextStep, VariableSpace space,
			Repository repository, IMetaStore metaStore)
			throws KettleStepException {

		for (ReturnValue returnValue : returnValues) {
			try {
				int type = ValueMetaFactory.getIdForValueMeta(returnValue
						.getType());
				ValueMetaInterface valueMeta = ValueMetaFactory
						.createValueMeta(returnValue.getName(), type);
				valueMeta.setLength(returnValue.getLength());
				valueMeta.setOrigin(name);
				rowMeta.addValueMeta(valueMeta);
			} catch (KettlePluginException e) {
				throw new KettleStepException("Unknown data type '"
						+ returnValue.getType() + "' for value named '"
						+ returnValue.getName() + "'");
			}
		}

	}

	@Override
	public String getXML() {
		StringBuilder xml = new StringBuilder();
		xml.append(XMLHandler.addTagValue(HOST, host));
		xml.append(XMLHandler.addTagValue(PORT, port));
		xml.append(XMLHandler.addTagValue(USERNAME, username));
		xml.append(XMLHandler.addTagValue(PASSWORD, password));
		xml.append(XMLHandler.addTagValue(QUERY, query));

		xml.append(XMLHandler.openTag(RETURNS));
		for (ReturnValue returnValue : returnValues) {
			xml.append(XMLHandler.openTag(RETURN));
			xml.append(XMLHandler.addTagValue(RETURN_NAME,
					returnValue.getName()));
			xml.append(XMLHandler.addTagValue(RETURN_SPLUNK_NAME,
					returnValue.getSplunkName()));
			xml.append(XMLHandler.addTagValue(RETURN_TYPE,
					returnValue.getType()));
			xml.append(XMLHandler.addTagValue(RETURN_LENGTH,
					returnValue.getLength()));
			xml.append(XMLHandler.addTagValue(RETURN_FORMAT,
					returnValue.getFormat()));
			xml.append(XMLHandler.closeTag(RETURN));
		}
		xml.append(XMLHandler.closeTag(RETURNS));

		return xml.toString();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases,
			IMetaStore metaStore) throws KettleXMLException {
		host = XMLHandler.getTagValue(stepnode, HOST);
		port = XMLHandler.getTagValue(stepnode, PORT);
		username = XMLHandler.getTagValue(stepnode, USERNAME);
		password = XMLHandler.getTagValue(stepnode, PASSWORD);
		query = XMLHandler.getTagValue(stepnode, QUERY);

		// Parse return values
		//
		Node returnsNode = XMLHandler.getSubNode(stepnode, RETURNS);
		List<Node> returnNodes = XMLHandler.getNodes(returnsNode, RETURN);
		returnValues = new ArrayList<>();
		for (Node returnNode : returnNodes) {
			String name = XMLHandler.getTagValue(returnNode, RETURN_NAME);
			String splunkName = XMLHandler.getTagValue(returnNode,
					RETURN_SPLUNK_NAME);
			String type = XMLHandler.getTagValue(returnNode, RETURN_TYPE);
			int length = Const.toInt(
					XMLHandler.getTagValue(returnNode, RETURN_LENGTH), -1);
			String format = XMLHandler.getTagValue(returnNode, RETURN_FORMAT);
			returnValues.add(new ReturnValue(name, splunkName, type, length,
					format));
		}

		super.loadXML(stepnode, databases, metaStore);
	}

	@Override
	public void saveRep(Repository rep, IMetaStore metaStore,
			ObjectId transformationId, ObjectId stepId) throws KettleException {
		rep.saveStepAttribute(transformationId, stepId, HOST, host);
		rep.saveStepAttribute(transformationId, stepId, PORT, port);
		rep.saveStepAttribute(transformationId, stepId, USERNAME, username);
		rep.saveStepAttribute(transformationId, stepId, PASSWORD, password);
		rep.saveStepAttribute(transformationId, stepId, QUERY, query);

		for (int i = 0; i < returnValues.size(); i++) {
			ReturnValue returnValue = returnValues.get(i);
			rep.saveStepAttribute(transformationId, stepId, i, RETURN_NAME,
					returnValue.getName());
			rep.saveStepAttribute(transformationId, stepId, i,
					RETURN_SPLUNK_NAME, returnValue.getSplunkName());
			rep.saveStepAttribute(transformationId, stepId, i, RETURN_TYPE,
					returnValue.getType());
			rep.saveStepAttribute(transformationId, stepId, i, RETURN_LENGTH,
					returnValue.getLength());
			rep.saveStepAttribute(transformationId, stepId, i, RETURN_FORMAT,
					returnValue.getFormat());
		}
	}

	@Override
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId stepId,
			List<DatabaseMeta> databases) throws KettleException {
		host = rep.getStepAttributeString(stepId, HOST);
		port = rep.getStepAttributeString(stepId, PORT);
		username = rep.getStepAttributeString(stepId, USERNAME);
		password = rep.getStepAttributeString(stepId, PASSWORD);
		query = rep.getStepAttributeString(stepId, QUERY);

		returnValues = new ArrayList<>();
		int nrReturns = rep.countNrStepAttributes(stepId, RETURN_NAME);
		for (int i = 0; i < nrReturns; i++) {
			String name = rep.getStepAttributeString(stepId, i, RETURN_NAME);
			String splunkName = rep.getStepAttributeString(stepId, i,
					RETURN_SPLUNK_NAME);
			String type = rep.getStepAttributeString(stepId, i, RETURN_TYPE);
			int length = (int) rep.getStepAttributeInteger(stepId, i,
					RETURN_LENGTH);
			String format = rep
					.getStepAttributeString(stepId, i, RETURN_FORMAT);
			returnValues.add(new ReturnValue(name, splunkName, type, length,
					format));
		}

	}

	/**
	 * Gets host
	 *
	 * @return value of host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            The host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets query
	 *
	 * @return value of query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            The query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Gets returnValues
	 *
	 * @return value of returnValues
	 */
	public List<ReturnValue> getReturnValues() {
		return returnValues;
	}

	/**
	 * @param returnValues
	 *            The returnValues to set
	 */
	public void setReturnValues(List<ReturnValue> returnValues) {
		this.returnValues = returnValues;
	}
}
