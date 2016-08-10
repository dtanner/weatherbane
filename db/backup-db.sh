#!/bin/sh
ssh weathervane "pg_dump -U weathervane weathervane | gzip -c" > /Users/dan/Dropbox/backups/weathervane/weathervane-db-backup.sql.gz