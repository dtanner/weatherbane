import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.net.SyslogAppender
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.DEBUG

if (System.getProperty("os.name").startsWith('Mac')) {
    appender('console', ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = '%date{ISO8601} %-5level %logger{36} - %msg%n'
        }
    }
    root(DEBUG, ['console'])
} else {
    appender("syslog", SyslogAppender) {
        syslogHost = "localhost"
        facility = "LOCAL6"
        suffixPattern = "%logger %msg"
    }
    root(DEBUG, ["syslog"])
}

