import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

//String encoderPattern = "%d{HH:mm:ss} [%-11thread] %-5level %class{36}.%method - %msg%n"
String encoderPattern = "%d{HH:mm:ss} [%-10thread] %-5level %class{36} - %msg%n"

appender("stdout", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = encoderPattern
    }
}

appender("stderr", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = encoderPattern
    }
    target = 'System.err'
}

String env = System.getProperty('app.env')

if (env == "tournament") {
    root(debug, ['stderr'])
} else {
    root(debug, ['stdout'])
}
