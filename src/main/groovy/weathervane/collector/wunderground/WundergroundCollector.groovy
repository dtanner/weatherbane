package weathervane.collector.wunderground

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import weathervane.AppConfig
import weathervane.Prediction
import weathervane.collector.PredictionCollector

import java.time.Instant
import java.time.LocalDate

@Slf4j
class WundergroundCollector implements PredictionCollector {

    @Override
    List<Prediction> collect(LocalDate targetDate, String location) {
        String apiKey = AppConfig.config.wunderground.apiKey

        List<Prediction> predictions = null

        String pathString = "/api/${apiKey}/conditions/q/${location}.json"
        HTTPBuilder http = new HTTPBuilder('http://api.wunderground.com')
        http.get(path: pathString) { response, json ->
            log.debug(json.toString())
            predictions = parsePredictions(location, json)
        }
        return predictions
    }

    static List<Prediction> parsePredictions(String location, response) {
        List forecasts = response.forecast.simpleforecast.forecastday
        List<Prediction> predictions = forecasts.collect { forecast ->
            Prediction prediction = new Prediction(predictedOn: Instant.now(),
                    targetDate: Instant.ofEpochSecond(forecast.date.epoch.toLong()),
                    location: location,
                    provider: 'wunderground',
                    high: forecast.high.fahrenheit.toInteger(),
                    low: forecast.low.fahrenheit.toInteger(),
                    pop: forecast.pop.toInteger())
            return prediction
        }

        return predictions
    }

}
