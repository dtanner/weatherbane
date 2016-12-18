# Weathervane
The goal of weathervane is to let us analyze the accuracy of the major weather prediction providers.
Anecdotally, there's a pretty big difference between the various weather apps out there, which implies that some are better than others.
But who keeps track of exactly what they said?  This project's goal is to do that.
Initially, the focus is on collecting the most important data, like highs, lows, and WILL IT RAIN???!!!
Then later it can provide tools to analyze the information.

[![Build Status](https://travis-ci.org/dtanner/weathervane.svg?branch=master)](https://travis-ci.org/dtanner/weathervane)

## Development/Runtime Requirements
It relies on these environment variables for configuration, with sample values:
```
WEATHERVANE_DB_URL                "jdbc:postgresql://localhost:5432/weathervane"
WEATHERVANE_DB_USERNAME           "bob"
WEATHERVANE_DB_PASSWORD           "super secret"
WEATHERVANE_WUNDERGROUND_API_KEY  "abc123"
WEATHERVANE_FORECASTIO_API_KEY    "abc123"
WEATHERVANE_AERIS_API_KEY         "abc123"
```
The AppConfig class asserts for the required config values.

#### Database
postgresql, initialized with `db/create-db.sql`

### To get actual measurements
https://www.ncdc.noaa.gov/cdo-web/search
Get for SAN, DEN, MSP, CMX, ORD, SFO, JFK
