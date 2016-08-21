#!/bin/sh

# todo drop existing database if exists

dropdb weathervane
createuser -s weathervane
createuser -s postgres
createdb -T template0 weathervane
psql --set ON_ERROR_STOP=on weathervane < /tmp/weathervane-db-backup.sql