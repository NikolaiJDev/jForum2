#!/bin/bash

# JForum SQL to Liquibase Conversion Helper Script
# This script helps analyze the remaining SQL files for conversion to Liquibase

echo "JForum SQL to Liquibase Migration Analysis"
echo "=========================================="
echo

# Get list of all SQL files
echo "Original SQL files found:"
find upgrade/ -name "*.sql" | head -20
echo "... (total: $(find upgrade/ -name "*.sql" | wc -l) files)"
echo

# Check which versions have been converted
echo "Liquibase conversion status:"
for version_dir in src/main/resources/db/changelog/releases/*/; do
    version=$(basename "$version_dir")
    changelog_file="$version_dir/changelog-$version.xml"
    if [[ -f "$changelog_file" ]]; then
        lines=$(wc -l < "$changelog_file")
        if grep -q "placeholder" "$changelog_file"; then
            echo "  $version: 🚧 PLACEHOLDER ($lines lines)"
        else
            echo "  $version: ✅ CONVERTED ($lines lines)"
        fi
    else
        echo "  $version: ❌ MISSING"
    fi
done
echo

# Analyze remaining SQL files that need conversion
echo "SQL files needing conversion:"
echo

for version in 2.1.5 2.1.7 2.1.8 2.2.0 2.3.5 2.4.1; do
    version_dir="upgrade/$version"
    if [[ -d "$version_dir" ]]; then
        echo "Version $version:"
        for sql_file in "$version_dir"/*.sql; do
            if [[ -f "$sql_file" ]]; then
                filename=$(basename "$sql_file")
                lines=$(wc -l < "$sql_file")
                echo "  📄 $filename ($lines lines)"
                
                # Show first few lines to understand content
                echo "      Sample content:"
                head -5 "$sql_file" | sed 's/^/      /'
                echo
            fi
        done
    fi
done

echo "Next steps:"
echo "1. Convert each SQL file to Liquibase changeset format"
echo "2. Replace placeholder files with actual changesets"
echo "3. Test migrations with sample database"
echo "4. Update documentation"