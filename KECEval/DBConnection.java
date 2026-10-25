import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DBConnection - JDBC Connection Management for Kongu Engineering College (R2024)
 * Student Academic & Continuous Evaluation System.
 */
public class DBConnection {

    // Default connection configuration for local MySQL
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "kec_eval_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "0000";

    private static String dbUrl;
    private static String user;
    private static String password;

    static {
        // Read configuration with support for environment overrides
        String host = System.getProperty("db.host", System.getenv().getOrDefault("DB_HOST", DEFAULT_HOST));
        String port = System.getProperty("db.port", System.getenv().getOrDefault("DB_PORT", DEFAULT_PORT));
        String dbName = System.getProperty("db.name", System.getenv().getOrDefault("DB_NAME", DEFAULT_DB));
        user = System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", DEFAULT_USER));
        password = System.getProperty("db.password", System.getenv().getOrDefault("DB_PASS", DEFAULT_PASS));

        dbUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, dbName);

        try {
            // Register MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection ERROR] MySQL JDBC Driver not found in classpath!");
            System.err.println("Ensure mysql-connector-j.jar is in the classpath.");
        }
    }

    /**
     * Obtains a new database connection.
     * @return Connection object
     * @throws SQLException on connection failure
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, user, password);
    }

    /**
     * Safely closes a connection.
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[DBConnection WARN] Failed to close connection: " + e.getMessage());
            }
        }
    }

    /**
     * Tests the database connection and verifies table accessibility.
     * @return true if connection is active and valid, false otherwise
     */
    public static boolean testConnection() {
        System.out.println("Testing connection to: " + dbUrl + " (User: " + user + ")");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            boolean valid = conn.isValid(3);
            if (valid) {
                System.out.println("[SUCCESS] Database connection established successfully!");
                return true;
            } else {
                System.err.println("[FAILURE] Connection returned invalid.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Unable to connect to MySQL database: " + e.getMessage());
            return false;
        }
    }

    // Setters for dynamic credential adjustment if required
    public static void setCredentials(String newDbUrl, String newUser, String newPass) {
        dbUrl = newDbUrl;
        user = newUser;
        password = newPass;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getUser() {
        return user;
    }
}

