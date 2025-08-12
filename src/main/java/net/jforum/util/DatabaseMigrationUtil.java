package net.jforum.util;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Database migration utility using Liquibase
 * This class provides programmatic access to Liquibase migration functionality
 * 
 * @author jforum
 */
public class DatabaseMigrationUtil {
    
    private static final String DEFAULT_CHANGELOG = "db/changelog/db.changelog-master.xml";
    
    /**
     * Perform database migration update
     * 
     * @param jdbcUrl Database JDBC URL
     * @param username Database username
     * @param password Database password
     * @param changelogFile Optional custom changelog file (uses default if null)
     * @throws Exception if migration fails
     */
    public static void updateDatabase(String jdbcUrl, String username, String password, String changelogFile) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            String changelog = changelogFile != null ? changelogFile : DEFAULT_CHANGELOG;
            try (Liquibase liquibase = new Liquibase(changelog, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        }
    }
    
    /**
     * Check database migration status
     * 
     * @param jdbcUrl Database JDBC URL
     * @param username Database username 
     * @param password Database password
     * @return Number of unrun changesets
     * @throws Exception if status check fails
     */
    public static int checkMigrationStatus(String jdbcUrl, String username, String password) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            try (Liquibase liquibase = new Liquibase(DEFAULT_CHANGELOG, new ClassLoaderResourceAccessor(), database)) {
                return liquibase.listUnrunChangeSets(new Contexts(), new LabelExpression()).size();
            }
        }
    }
    
    /**
     * Rollback database changes
     * 
     * @param jdbcUrl Database JDBC URL
     * @param username Database username
     * @param password Database password
     * @param rollbackCount Number of changesets to rollback
     * @throws Exception if rollback fails
     */
    public static void rollbackDatabase(String jdbcUrl, String username, String password, int rollbackCount) throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            
            try (Liquibase liquibase = new Liquibase(DEFAULT_CHANGELOG, new ClassLoaderResourceAccessor(), database)) {
                liquibase.rollback(rollbackCount, new Contexts(), new LabelExpression());
            }
        }
    }
    
    /**
     * Initialize database migration from properties
     * Reads configuration from system properties or provided properties object
     * 
     * @param properties Configuration properties (can be null to use system properties)
     * @throws Exception if migration fails
     */
    public static void migrateFromProperties(Properties properties) throws Exception {
        Properties config = properties != null ? properties : System.getProperties();
        
        String url = config.getProperty("liquibase.url");
        String username = config.getProperty("liquibase.username");
        String password = config.getProperty("liquibase.password");
        String changelogFile = config.getProperty("liquibase.changeLogFile");
        
        if (url == null || username == null) {
            throw new IllegalArgumentException("Database URL and username must be provided");
        }
        
        updateDatabase(url, username, password != null ? password : "", changelogFile);
    }
    
    /**
     * Utility method to validate database connection
     * 
     * @param jdbcUrl Database JDBC URL
     * @param username Database username
     * @param password Database password
     * @return true if connection is successful
     */
    public static boolean testConnection(String jdbcUrl, String username, String password) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            return connection.isValid(5); // 5 second timeout
        } catch (Exception e) {
            return false;
        }
    }
}