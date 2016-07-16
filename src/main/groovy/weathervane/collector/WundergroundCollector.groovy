package weathervane.collector

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import weathervane.AppConfig
import weathervane.Location
import weathervane.Prediction

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Slf4j
class WundergroundCollector implements PredictionCollector {

    @Override
    List<Prediction> collect(LocalDate targetDate, Location location) {
        String apiKey = AppConfig.config.wunderground.apiKey

        List<Prediction> predictions = null

        HTTPBuilder http = new HTTPBuilder('http://api.wunderground.com')
        String pathString = "/api/${apiKey}/forecast/q/${location.airportCode}.json"
        http.get(path: pathString) { response, json ->
            log.debug(json.toString())
            predictions = parsePredictions(location, json)
        }
        return predictions
    }

    static List<Prediction> parsePredictions(Location location, response) {
        List forecasts = response.forecast.simpleforecast.forecastday
        List<Prediction> predictions = forecasts.collect { forecast ->

            ZoneId zoneId = ZoneId.of(forecast.date.tz_long)
            Instant targetEpoch = Instant.ofEpochSecond(forecast.date.epoch.toLong())
            LocalDate targetDate = targetEpoch.atZone(zoneId).toLocalDate()

            Prediction prediction = new Prediction(predictedOn: Instant.now(),
                    targetDate: targetDate,
                    location: location.name,
                    provider: 'wunderground',
                    high: forecast.high.fahrenheit.toInteger(),
                    low: forecast.low.fahrenheit.toInteger(),
                    pop: forecast.pop.toInteger() / 100)
            return prediction
        }

        return predictions
    }

}
