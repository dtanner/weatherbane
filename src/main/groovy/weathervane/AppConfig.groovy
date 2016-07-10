package weathervane

@Singleton(strict = false)
class AppConfig {

    private static ConfigObject configObject

    private AppConfig() {
        ConfigObject config = new ConfigSlurper().parse(new File("/etc/weathervane-config.groovy").toURI().toURL())

        assert config.db.url
        assert config.db.username
        assert config.db.password
        assert config.wunderground.apiKey

        configObject = config
    }

    static ConfigObject getConfig() {
        return configObject
    }
}
