# JForum Database Baseline Schema

This directory contains the Liquibase changelog that creates the initial JForum database schema.

## Purpose

The baseline changelog serves as the "initial state script" that creates the foundation database schema before any version-specific upgrades are applied. This is equivalent to running the original `mysql_db_struct.sql`, `postgresql_db_struct.sql`, etc. files.

## Migration Flow

1. **Baseline** - Creates initial schema (all core tables)
2. **Version 2.1** - Applies 2.1 upgrades on top of baseline
3. **Version 2.1.5** - Applies 2.1.5 upgrades 
4. **Version 2.1.7** - Applies 2.1.7 upgrades
5. *... and so on*

## Current Status

This baseline implementation is partially complete, containing the first few core tables as an example. The complete baseline should include all ~30 tables from the original schema files.

To complete the baseline:
1. Review the original `src/main/config/database/mysql/mysql_db_struct.sql`
2. Convert remaining table definitions to Liquibase changesets
3. Follow the existing pattern for consistent changeset IDs and structure

## Schema Tables Converted

- ✅ jforum_banlist
- ✅ jforum_categories  
- ✅ jforum_config
- ✅ jforum_forums
- ⏳ ~26 additional tables pending conversion

## Original Source Files

The baseline is derived from these original database creation scripts:
- `src/main/config/database/mysql/mysql_db_struct.sql`
- `src/main/config/database/postgresql/postgresql_db_struct.sql`
- `src/main/config/database/oracle/oracle_db_struct.sql`
- `src/main/config/database/hsqldb/hsqldb_db_struct.sql`
- `src/main/config/database/sqlserver/sqlserver_db_struct.sql`