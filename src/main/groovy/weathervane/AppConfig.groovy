package weathervane

import weathervane.util.CommandUtil

@Singleton(strict = false)
class AppConfig {

    private static ConfigObject configObject

    private AppConfig() {
        ConfigObject config = new ConfigSlurper().parse(new File("/etc/weathervane-config.groovy").toURI().toURL())

        if (System.getenv()['CONTINUOUS_INTEGRATION']) {
            config.db.url = 'jdbc:postgresql://localhost:5432/weathervane'
            config.db.username = 'postgres'
            config.db.password = ''

            CommandUtil.execute('psql -U postgres -d weathervane -a -f db/create-db.sql')
        }

        assert config.db.url
        assert config.db.username
        assert config.db.password
        // assert config.wunderground.apiKey

        configObject = config
    }

    static ConfigObject getConfig() {
        return configObject
    }
}
