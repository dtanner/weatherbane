package weathervane

import weathervane.util.CommandUtil

@Singleton(strict = false)
class AppConfig {

    private static ConfigObject configObject

    private AppConfig() {
        configObject = new ConfigObject()

        def env = System.getenv()
        config.db.url = env.WEATHERVANE_DB_URL
        config.db.username = env.WEATHERVANE_DB_USERNAME
        config.db.password = env.WEATHERVANE_DB_PASSWORD
        config.wunderground.apiKey = env.WEATHERVANE_WUNDERGROUND_API_KEY
        config.forecastio.apiKey = env.WEATHERVANE_FORECASTIO_API_KEY
        config.aeris.apiKey = env.WEATHERVANE_AERIS_API_KEY

        assert config.db.url
        assert config.db.username

        if (System.getenv()['CONTINUOUS_INTEGRATION']) {
            CommandUtil.execute('psql -U postgres -d weathervane -a -f db/create-db.sql')
        } else {
            assert config.db.password
            assert config.wunderground.apiKey
            assert config.forecastio.apiKey
        }

    }

    static ConfigObject getConfig() {
        return configObject
    }
}
