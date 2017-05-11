package io.snappydata.cloudfoundry.servicebroker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


/**
 * Utility class for manipulating a SnappyData cluster.
 */
@Service
public class SnappyDataAdminService {

	private Logger logger = LoggerFactory.getLogger(SnappyDataAdminService.class);

	@Value("${snappydata.host:localhost}")
	private String host;
	
	@Value("${snappydata.port:1527}")
	private String port;

    @Value("${snappydata.jobserver:localhost}")
    private String jobserver;

    @Value("${snappydata.jobPort:8090}")
    private String jobPort;

    @Value("${snappydata.user:snappyadmin}")
	private String user;

	@Value("${snappydata.password:snappyadmin123}")
	private String password;

	@Value("${snappydata.properties:}")
	private String properties;

	private Random random = new Random();

	private Connection conn;

	private Map<String, String> bindings = new HashMap<String, String>();

	private Set<String> instances = new HashSet<String>();

	private static final String ALPHA_NUMERIC_STRING = "01234ABCDEFGHIJKLMNOPQRSTUVWXYZ56789abcdifghijklmnopqrstuvwxyz";

	public String[] createUser(String bindingId) {
		int random = this.random.nextInt(9999);
		while (random < 0) {
			random = this.random.nextInt(9999);
		}
		String user = "SNAPPYUSER" + random;
		String pass = SnappyDataAdminService.randomAlphaNumeric(16);

		// this.executeStatement("CALL SYS.CREATE_USER('" + user + "', '" + pass + "')");
		// stmt.execute("GRANT EXECUTE ON PROCEDURE SYS.CREATE_ALL_BUCKETS TO " + newUsername);
		this.bindings.put(bindingId, user);
		return new String[]{user, pass};
	}

	public void deleteUser(String bindingId) {
		String username = this.bindings.get(bindingId);
		if (username == null) {
			return;
		}
		// this.executeStatement("CALL SYS.DELETE_USER('" + username + "')");
		// this.executeStatement("CALL SYS.CHANGE_PASSWORD('" + username + "', '', '" + randomAlphaNumeric(10) + "')");
		this.bindings.remove(bindingId);
	}

	private void executeStatement(String sql) {
		try {
			Class.forName("io.snappydata.jdbc.ClientDriver").newInstance();
			conn = DriverManager.getConnection(getConnectionString(), user, password);
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException sqle) {
			logger.warn("Could not get a connection to SnappyData Service", sqle);
		} catch (Throwable t) {
			logger.warn("Could not load driver class ", t);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException sqle) {
					logger.warn("Could not close the connection to SnappyData Service", sqle);
				}
			}
		}
	}

	public Boolean containsBinding(String bindingId) {
		return this.bindings.containsKey(bindingId);
	}

	public Boolean containsInstance(String instanceId) {
		return this.instances.contains(instanceId);
	}

	public Boolean addInstance(String instanceId) {
		return this.instances.add(instanceId);
	}

	public Boolean removeInstance(String instanceId) {
		return this.instances.remove(instanceId);
	}

	public String getConnectionString() {
		return new StringBuilder()
				.append("jdbc:snappydata://")
				.append(getLocatorAddress())
				.toString();
	}

	public String getLocatorAddress() {
		Map<String, String> map = System.getenv();
		for (Map.Entry<String, String> e : map.entrySet()) {
			if ("SNAPPYDATA_LOCATOR_HOST".equalsIgnoreCase(e.getKey())) {
				this.host = e.getValue();
			} else if ("SNAPPYDATA_LOCATOR_CLIENT_PORT".equalsIgnoreCase(e.getKey())) {
				this.port = e.getValue();
			}
		}
		return new StringBuilder(this.host + ":" + this.port).toString();
	}

    public String getJobServerUrl() {
		Map<String, String> map = System.getenv();
		for (Map.Entry<String, String> e : map.entrySet()) {
			if ("SNAPPYDATA_LEAD_HOST".equalsIgnoreCase(e.getKey())) {
				this.jobserver = e.getValue();
			} else if ("SNAPPYDATA_JOB_PORT".equalsIgnoreCase(e.getKey())) {
				this.jobPort = e.getValue();
			}
		}
        return new StringBuilder(this.jobserver + ":" + this.jobPort).toString();
    }

	public String getProperties() {
		Map<String, String> map = System.getenv();
		for (Map.Entry<String, String> e : map.entrySet()) {
			if ("SNAPPYDATA_CONNECTION_PROPS".equalsIgnoreCase(e.getKey())) {
				this.properties = e.getValue();
			}
		}
		return this.properties;
	}

    public static String randomAlphaNumeric(int count) {
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

}
