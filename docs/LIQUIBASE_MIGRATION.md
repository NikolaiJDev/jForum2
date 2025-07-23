# JForum Liquibase Migration

This document describes the migration from SQL-based database upgrades to Liquibase for JForum.

## Overview

JForum has been migrated from using manual SQL upgrade scripts to Liquibase for database schema management. This provides several benefits:

- **Database Independence**: Liquibase changesets work across MySQL, PostgreSQL, Oracle, HSQLDB, and SQL Server
- **Rollback Support**: All changes can be rolled back if needed
- **Change Tracking**: Liquibase tracks which changes have been applied
- **Safer Deployments**: Changes are applied in a controlled, atomic manner

## Structure

### Original SQL Files
The original SQL upgrade files are located in the `upgrade/` directory:
- Each version has its own subdirectory (e.g., `upgrade/2.3.2/`)
- Each subdirectory contains SQL files for different databases (e.g., `mysql_2.3.1_to_2.3.2.sql`)

### New Liquibase Structure
```
src/main/resources/
├── liquibase.properties                           # Configuration file
└── db/
    └── changelog/
        ├── db.changelog-master.xml                 # Master changelog file
        └── releases/                               # Version-specific changelogs
            ├── 2.1/
            │   └── changelog-2.1.xml
            ├── 2.3.2/
            │   └── changelog-2.3.2.xml
            ├── 2.4.0/
            │   └── changelog-2.4.0.xml
            ├── 2.5.0/
            │   └── changelog-2.5.0.xml
            ├── 2.7.0/
            │   └── changelog-2.7.0.xml
            ├── 2.8.0/
            │   └── changelog-2.8.0.xml
            └── ...
```

## Configuration

### Database Configuration
Edit `src/main/resources/liquibase.properties` to configure your database connection:

For MySQL:
```properties
driver=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/jforum?useSSL=false&allowPublicKeyRetrieval=true
username=jforum
password=jforum
```

For PostgreSQL:
```properties
driver=org.postgresql.Driver
url=jdbc:postgresql://localhost:5432/jforum
username=jforum
password=jforum
```

### Maven Plugin
The Liquibase Maven plugin is configured in `pom.xml` with support for all database drivers.

## Running Migrations

### Using Maven
```bash
# Update database to latest version
mvn liquibase:update

# Check status
mvn liquibase:status

# Generate SQL for review (without applying)
mvn liquibase:updateSQL

# Rollback last changeset
mvn liquibase:rollbackCount -Dliquibase.rollbackCount=1

# Rollback to specific tag
mvn liquibase:rollback -Dliquibase.rollbackTag=2.7.0
```

### Using Command Line
```bash
# Update database
liquibase --changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml \
          --url=jdbc:mysql://localhost:3306/jforum \
          --username=jforum \
          --password=jforum \
          update

# Check status
liquibase --changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml \
          --url=jdbc:mysql://localhost:3306/jforum \
          --username=jforum \
          --password=jforum \
          status
```

## Migration Status

### Completed Conversions
The following versions have been fully converted to Liquibase:

- **2.1 (from 2.0.2)**: ✅ Complete
  - Table creation (karma, bookmarks, quota_limit, extensions, etc.)
  - Column additions and modifications
  - Index creation
  - Data updates

- **2.3.2 (from 2.3.1)**: ✅ Complete
  - Column modifications (mimetype field enlargement)

- **2.4.0 (from 2.3.5)**: ✅ Complete
  - Spam table creation
  - Column size increases for security and usability

- **2.5.0 (from 2.4.1)**: ✅ Complete
  - IPv6 support (enlarged IP address fields)

- **2.7.0 (from 2.6.2)**: ✅ Complete
  - User profile enhancements (Skype field)
  - Removal of obsolete instant messaging fields
  - Configuration enhancements
  - New smilie additions

- **2.8.0 (from 2.7.0)**: ✅ Complete
  - Registration management table
  - Foreign key constraints
  - Banner name field enlargement

### Pending Conversions
The following versions have placeholder files and need to be converted:

- **2.1.5 (from 2.1.4)**: 🚧 Placeholder
- **2.1.7 (from 2.1.5)**: 🚧 Placeholder  
- **2.1.8 (from 2.1.7)**: 🚧 Placeholder
- **2.2.0 (from 2.1.8)**: 🚧 Placeholder
- **2.3.5 (from 2.3.2)**: 🚧 Placeholder
- **2.4.1 (from 2.4.0)**: 🚧 Placeholder

## Converting Remaining SQL Files

To convert the remaining SQL files, follow these patterns:

### 1. Table Creation
```xml
<changeSet id="unique-id" author="jforum">
    <createTable tableName="table_name">
        <column name="id" type="INT" autoIncrement="true">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="name" type="VARCHAR(100)">
            <constraints nullable="false"/>
        </column>
    </createTable>
</changeSet>
```

### 2. Column Modifications
```xml
<changeSet id="unique-id" author="jforum">
    <modifyDataType tableName="table_name" columnName="column_name" newDataType="VARCHAR(255)"/>
</changeSet>
```

### 3. Adding Columns
```xml
<changeSet id="unique-id" author="jforum">
    <addColumn tableName="table_name">
        <column name="new_column" type="INT" defaultValueNumeric="0">
            <constraints nullable="true"/>
        </column>
    </addColumn>
</changeSet>
```

### 4. Index Creation
```xml
<changeSet id="unique-id" author="jforum">
    <createIndex tableName="table_name" indexName="idx_name">
        <column name="column_name"/>
    </createIndex>
</changeSet>
```

### 5. Data Updates
```xml
<changeSet id="unique-id" author="jforum">
    <update tableName="table_name">
        <column name="column_name" value="new_value"/>
        <where>condition</where>
    </update>
</changeSet>
```

## Database-Specific Considerations

### Auto-Increment Differences
- MySQL: `AUTO_INCREMENT`
- PostgreSQL: `SERIAL` or `BIGSERIAL`
- Oracle: Sequences
- Liquibase handles these differences automatically when using `autoIncrement="true"`

### Data Types
Liquibase provides database-independent data types:
- `INT` → Maps to appropriate integer type per database
- `VARCHAR(n)` → Maps to appropriate string type
- `DATETIME` → Maps to appropriate timestamp type
- `TINYINT(1)` → Maps to boolean or small integer as appropriate

### Engine/Storage Differences
- MySQL: `ENGINE=InnoDB` → Handled by database defaults
- PostgreSQL: No equivalent needed
- Oracle: Tablespace specifications can be added if needed

## Testing

### Validation
Before applying migrations to production:

1. Test on a copy of production data
2. Use `liquibase:updateSQL` to review generated SQL
3. Backup database before applying changes
4. Test rollback procedures

### Example Test Workflow
```bash
# 1. Generate SQL for review
mvn liquibase:updateSQL > migration.sql

# 2. Review the generated SQL
cat migration.sql

# 3. Apply to test database
mvn liquibase:update

# 4. Test application functionality

# 5. Test rollback (if needed)
mvn liquibase:rollbackCount -Dliquibase.rollbackCount=1
```

## Benefits of Migration

1. **Cross-Database Compatibility**: Single changesets work across all supported databases
2. **Rollback Capability**: All changes can be undone safely
3. **Change Tracking**: Liquibase tracks applied changes in `DATABASECHANGELOG` table
4. **Atomic Operations**: Changes are applied in transactions
5. **Conditional Logic**: Support for database-specific logic when needed
6. **Integration**: Easy integration with CI/CD pipelines
7. **Documentation**: Self-documenting change history

## Troubleshooting

### Common Issues

1. **Database Lock**: Liquibase creates a lock during execution
   - Solution: `mvn liquibase:releaseLocks`

2. **Checksum Mismatch**: Changeset has been modified after execution
   - Solution: `mvn liquibase:clearCheckSums`

3. **Connection Issues**: Database connectivity problems
   - Check `liquibase.properties` configuration
   - Verify database is running and accessible

### Recovery

If a migration fails:
```bash
# Check current status
mvn liquibase:status

# Release any locks
mvn liquibase:releaseLocks

# Rollback if needed
mvn liquibase:rollbackCount -Dliquibase.rollbackCount=1
```

## Next Steps

1. Convert remaining placeholder changelogs to proper Liquibase format
2. Set up continuous integration with automatic migration testing
3. Create database initialization scripts for new installations
4. Establish migration procedures for production deployments