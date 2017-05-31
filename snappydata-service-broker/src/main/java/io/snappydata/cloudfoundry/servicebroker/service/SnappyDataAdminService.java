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

    // Environment variables for managed service
    private static final String MANAGED_LOCATOR = "SNAPPYDATA_SERVICE_SNAPPYDATA_LOCATOR_HOST";

    private static final String MANAGED_LOCATORS = MANAGED_LOCATOR + "S";

    private static final String MANAGED_LEAD = "SNAPPYDATA_SERVICE_SNAPPYDATA_LEAD_HOST";

    private static final String MANAGED_LEADS = MANAGED_LEAD + "S";

    // Environment variables for brokered service
    private static final String EXTERNAL_LOCATOR = "SNAPPYDATA_LOCATOR_HOST";

    private static final String EXTERNAL_LOCATOR_PORT = "SNAPPYDATA_LOCATOR_CLIENT_PORT";

    private static final String EXTERNAL_LEAD = "SNAPPYDATA_LEAD_HOST";

    private static final String EXTERNAL_LEAD_PORT = "SNAPPYDATA_JOB_PORT";

    // Common
    private static final String CONN_PROPS = "SNAPPYDATA_CONNECTION_PROPS";

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
                .append(getFirstLocator())
                .toString();
    }

    public String getFirstLocator() {
        String host = System.getenv(MANAGED_LOCATOR);
        if (host == null || host.isEmpty()) {
            host = System.getenv(EXTERNAL_LOCATOR);
        }
        String port = System.getenv(EXTERNAL_LOCATOR_PORT);

        if (host != null && !host.isEmpty()) {
            this.host = host;
        }
        if (port != null && !port.isEmpty()) {
            this.port = port;
        }
        return new StringBuilder(this.host + ":" + this.port).toString();
    }

    public String getFirstLead() {
        String host = System.getenv(MANAGED_LEAD);
        if (host == null || host.isEmpty()) {
            host = System.getenv(EXTERNAL_LEAD);
        }
        String port = System.getenv(EXTERNAL_LEAD_PORT);

        if (host != null && !host.isEmpty()) {
            this.jobserver = host;
        }
        if (port != null && !port.isEmpty()) {
            this.jobPort = port;
        }
        return new StringBuilder(this.jobserver + ":" + this.jobPort).toString();
    }

    public String getLocators() {
        String host = buildLocatorAddresses(10334);
        if (host != null && !host.isEmpty()) {
            return host;
        }
        host = System.getenv(EXTERNAL_LOCATOR);
        String port = System.getenv(EXTERNAL_LOCATOR_PORT);

        if (host != null && !host.isEmpty()) {
            this.host = host;
        }
        if (port != null && !port.isEmpty()) {
            this.port = port;
        }
        return new StringBuilder(this.host + ":" + this.port).toString();
    }

    public String getLeads() {
        String host = buildLeadAddresses(8090);
        if (host != null && !host.isEmpty()) {
            return host;
        }
        host = System.getenv(EXTERNAL_LEAD);
        String port = System.getenv(EXTERNAL_LEAD_PORT);

        if (host != null && !host.isEmpty()) {
            this.jobserver = host;
        }
        if (port != null && !port.isEmpty()) {
            this.jobPort = port;
        }
        return new StringBuilder(this.jobserver + ":" + this.jobPort).toString();
    }

    private String buildLocatorAddresses(int port) {
        return buildProcessAddresses(MANAGED_LOCATORS, port);
    }

    private String buildLeadAddresses(int port) {
        return buildProcessAddresses(MANAGED_LEADS, port);
    }

    private String buildProcessAddresses(String env, int port) {
        // Build a semicolon delimited string
        // e.g. From ["192.168.1.11", "192.168.1.22"] to "192.168.1.11:10334,192.168.1.22:10334"
        StringBuilder sb = new StringBuilder("");
        String hosts = System.getenv(env);
        if (hosts != null && !hosts.isEmpty()) {
            // Create a tokenizer after removing the enclosing square '[' brackets
            StringTokenizer st = new StringTokenizer(hosts.substring(1, hosts.length() - 1), ",");
            while (st.hasMoreTokens()) {
                // Delimit with comma ','
                sb.append(sb.toString().isEmpty() ? "" : ",");
                String s = st.nextToken().trim();
                // Remove enclosing double quotes and append port
                sb.append(s.substring(1, s.length() - 1) + ":" + port);
            }
        }
        return sb.toString();
    }

    public String getProperties() {
        String props = System.getenv(CONN_PROPS);
        if (props != null) {
            this.properties = props;
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
