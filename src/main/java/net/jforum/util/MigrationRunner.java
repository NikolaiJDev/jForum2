package net.jforum.util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Command-line database migration tool
 * Provides a simple way to run migrations from command line
 * 
 * Usage: java -cp jforum.jar net.jforum.util.MigrationRunner [action] [options]
 * 
 * Actions:
 *   update - Apply all pending migrations
 *   status - Check migration status  
 *   rollback [count] - Rollback specified number of changesets
 *   test-connection - Test database connectivity
 * 
 * Options:
 *   -config [file] - Use specified properties file instead of liquibase.properties
 *   -url [url] - Database URL
 *   -username [user] - Database username
 *   -password [pass] - Database password
 * 
 * @author jforum
 */
public class MigrationRunner {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }
        
        String action = args[0];
        Properties config = new Properties();
        
        try {
            // Load default properties from classpath
            try (var inputStream = MigrationRunner.class.getClassLoader().getResourceAsStream("liquibase.properties")) {
                if (inputStream != null) {
                    config.load(inputStream);
                }
            }
            
            // Parse command line arguments
            for (int i = 1; i < args.length; i++) {
                switch (args[i]) {
                    case "-config":
                        if (i + 1 < args.length) {
                            try (var fileInput = new FileInputStream(args[++i])) {
                                config.load(fileInput);
                            }
                        }
                        break;
                    case "-url":
                        if (i + 1 < args.length) {
                            config.setProperty("url", args[++i]);
                        }
                        break;
                    case "-username":
                        if (i + 1 < args.length) {
                            config.setProperty("username", args[++i]);
                        }
                        break;
                    case "-password":
                        if (i + 1 < args.length) {
                            config.setProperty("password", args[++i]);
                        }
                        break;
                }
            }
            
            String url = config.getProperty("url");
            String username = config.getProperty("username");
            String password = config.getProperty("password", "");
            
            if (url == null || username == null) {
                System.err.println("Error: Database URL and username must be specified");
                System.exit(1);
            }
            
            switch (action.toLowerCase()) {
                case "update":
                    System.out.println("Applying database migrations...");
                    DatabaseMigrationUtil.updateDatabase(url, username, password, null);
                    System.out.println("Migration completed successfully!");
                    break;
                    
                case "status":
                    System.out.println("Checking migration status...");
                    int unrunCount = DatabaseMigrationUtil.checkMigrationStatus(url, username, password);
                    System.out.println("Unrun changesets: " + unrunCount);
                    break;
                    
                case "rollback":
                    int rollbackCount = 1;
                    if (args.length > 1) {
                        try {
                            rollbackCount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid rollback count: " + args[1]);
                            System.exit(1);
                        }
                    }
                    System.out.println("Rolling back " + rollbackCount + " changeset(s)...");
                    DatabaseMigrationUtil.rollbackDatabase(url, username, password, rollbackCount);
                    System.out.println("Rollback completed successfully!");
                    break;
                    
                case "test-connection":
                    System.out.println("Testing database connection...");
                    boolean connected = DatabaseMigrationUtil.testConnection(url, username, password);
                    if (connected) {
                        System.out.println("Connection successful!");
                    } else {
                        System.out.println("Connection failed!");
                        System.exit(1);
                    }
                    break;
                    
                default:
                    System.err.println("Unknown action: " + action);
                    printUsage();
                    System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void printUsage() {
        System.out.println("JForum Database Migration Tool");
        System.out.println();
        System.out.println("Usage: java -cp jforum.jar net.jforum.util.MigrationRunner [action] [options]");
        System.out.println();
        System.out.println("Actions:");
        System.out.println("  update              Apply all pending migrations");
        System.out.println("  status              Check migration status");
        System.out.println("  rollback [count]    Rollback specified number of changesets (default: 1)");
        System.out.println("  test-connection     Test database connectivity");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -config [file]      Use specified properties file");
        System.out.println("  -url [url]          Database JDBC URL");
        System.out.println("  -username [user]    Database username");
        System.out.println("  -password [pass]    Database password");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java -cp jforum.jar net.jforum.util.MigrationRunner update");
        System.out.println("  java -cp jforum.jar net.jforum.util.MigrationRunner status -url jdbc:mysql://localhost/jforum -username root");
        System.out.println("  java -cp jforum.jar net.jforum.util.MigrationRunner rollback 2");
    }
}