import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("stdout", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss} [%-12thread] %-5level %class{36}.%method - %msg%n"
    }
}

appender("stderr", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss} [%-12thread] %-5level %class{36}.%method - %msg%n"
    }
    target = 'System.err'
    withJansi = true
}

String env = System.properties['app.env']

root(DEBUG, (env == "tournament") ? ["stderr"] : ["stdout"])
