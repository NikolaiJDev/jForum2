# JForum SQL to Liquibase Migration - Quick Start Guide

This guide provides quick instructions for using the new Liquibase migration system in JForum.

## Summary of Changes

JForum has been migrated from manual SQL upgrade scripts to Liquibase for better database management across multiple database platforms.

### What's Included

1. **Liquibase Dependencies**: Added to `pom.xml` with Maven plugin support
2. **Configuration**: `src/main/resources/liquibase.properties` 
3. **Master Changelog**: `src/main/resources/db/changelog/db.changelog-master.xml`
4. **Version Changelogs**: Individual XML files for each JForum version
5. **Java Utilities**: Programmatic migration control classes
6. **Documentation**: Comprehensive migration guide

### Conversion Status

**✅ Fully Converted (8 versions):**
- 2.1 (from 2.0.2) - 178 lines, complex table creation
- 2.1.8 (from 2.1.7) - 155 lines, indexes and moderation log  
- 2.3.2 (from 2.3.1) - 18 lines, simple column modification
- 2.4.0 (from 2.3.5) - 55 lines, spam table and enlargements
- 2.4.1 (from 2.4.0) - 28 lines, cleanup of obsolete features
- 2.5.0 (from 2.4.1) - 28 lines, IPv6 support
- 2.7.0 (from 2.6.2) - 99 lines, user enhancements
- 2.8.0 (from 2.7.0) - 48 lines, registration system

**🚧 Placeholder (4 versions):**
- 2.1.5, 2.1.7, 2.2.0, 2.3.5 - Basic structure created, needs SQL conversion

## Quick Usage

### Configure Database
Edit `src/main/resources/liquibase.properties`:
```properties
# For MySQL
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/jforum
username=jforum
password=jforum
```

### Run Migrations
```bash
# Apply all pending migrations
mvn liquibase:update

# Check status
mvn liquibase:status

# Generate SQL preview
mvn liquibase:updateSQL

# Using command line tool
java -cp target/jforum.jar net.jforum.util.MigrationRunner update
```

### Programmatic Usage
```java
// Simple migration
DatabaseMigrationUtil.updateDatabase(
    "jdbc:mysql://localhost:3306/jforum", 
    "jforum", 
    "password", 
    null
);

// Check status
int unrunCount = DatabaseMigrationUtil.checkMigrationStatus(url, user, pass);
```

## Benefits

1. **Cross-Database**: Single changeset works across MySQL, PostgreSQL, Oracle, HSQLDB, SQL Server
2. **Rollback Support**: All changes can be safely rolled back
3. **Change Tracking**: Liquibase tracks what's been applied
4. **Atomic**: Changes applied in transactions
5. **Documentation**: Self-documenting change history

## Next Steps

1. **Convert Remaining Versions**: Use patterns from completed examples
2. **Test Migrations**: Validate on copy of production data  
3. **Integrate CI/CD**: Add migration testing to build pipeline
4. **Production Deployment**: Plan migration strategy for live systems

## Migration Patterns

### Common Conversions
- `ALTER TABLE` → `<modifyDataType>`, `<addColumn>`, `<dropColumn>`
- `CREATE TABLE` → `<createTable>`
- `CREATE INDEX` → `<createIndex>`
- `INSERT` → `<insert>`
- `UPDATE` → `<update>`
- `DROP TABLE` → `<dropTable>`

### Database-Specific Handling
- Auto-increment → `autoIncrement="true"`
- Data types → Liquibase handles mapping
- Sequences → Handled automatically per database

For complete documentation, see `docs/LIQUIBASE_MIGRATION.md`.