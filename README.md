# Weathervane
The goal of weathervane is to let us analyze the accuracy of the major weather prediction providers.
Anecdotally, there's a pretty big difference between the various weather apps out there, which implies that some are better than others.
But who keeps track of exactly what they said?  This project's goal is to do that.
Initially, the focus is on collecting the most important data, like highs, lows, and WILL IT RAIN???!!!
Then later it can provide tools to analyze the information.


## Development/Runtime Requirements
`/etc/weathervane-config.groovy` for configuration.
The AppConfig class asserts required config values. e.g.:
```
db {
    url = 'jdbc:postgresql://localhost:5432/weathervane'
    username = 'weathervane_app'
    password = ''
}
wunderground.apiKey = ''
```
postgresql, currently initialized with create-db.sql
