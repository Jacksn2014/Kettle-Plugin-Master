package com.agileq.kettle.splunk.connection;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;

import com.splunk.SSLSecurityProtocol;
import com.splunk.Service;
import com.splunk.ServiceArgs;

public class SplunkConnection extends Variables {

	private String name;

	private String hostname;

	private String port;

	private String username;

	private String password;

	public SplunkConnection() {
		super();
	}

	public SplunkConnection(VariableSpace parent) {
		super.initializeVariablesFrom(parent);
	}

	public SplunkConnection(VariableSpace parent, SplunkConnection source) {
		super.initializeVariablesFrom(parent);
		this.name = source.name;
		this.hostname = source.hostname;
		this.port = source.port;
		this.username = source.username;
		this.password = source.password;
	}

	public SplunkConnection(String name, String hostname, String port,
			String username, String password) {
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public void test() throws KettleException {
		try {
			Service.connect(getServiceArgs());
		} catch (Exception e) {
			throw new KettleException(
					"Error connecting to Splunk connection '" + name
							+ "' on host '" + getRealHostname()
							+ "' and port '" + getRealPort() + "' with user '"
							+ getRealUsername() + "'", e);
		}
	}

	public ServiceArgs getServiceArgs() {
		ServiceArgs args = new ServiceArgs();
		args.setUsername(getRealUsername());
		args.setPassword(getRealPassword());
		args.setHost(getRealHostname());
		args.setPort(Const.toInt(getRealPort(), 8089));
		args.setSSLSecurityProtocol(SSLSecurityProtocol.TLSv1_2);
		return args;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SplunkConnection that = (SplunkConnection) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	@Override
	public String toString() {
		return name == null ? super.toString() : name;
	}

	public String getRealHostname() {
		return environmentSubstitute(hostname);
	}

	public String getRealPort() {
		return environmentSubstitute(port);
	}

	public String getRealUsername() {
		return environmentSubstitute(username);
	}

	public String getRealPassword() {
		return environmentSubstitute(password);
	}

	/**
	 * Gets name
	 *
	 * @return value of name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets hostname
	 *
	 * @return value of hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname
	 *            The hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Gets port
	 *
	 * @return value of port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port
	 *            The port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * Gets username
	 *
	 * @return value of username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            The username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets password
	 *
	 * @return value of password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            The password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
