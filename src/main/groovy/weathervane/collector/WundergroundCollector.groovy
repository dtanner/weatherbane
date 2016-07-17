package weathervane.collector

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import weathervane.AppConfig
import weathervane.DB
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

        String pathString = "http://api.wunderground.com/api/${apiKey}/forecast/q/${location.airportCode}.json"

        HttpURLConnection connection = new URL(pathString).openConnection() as HttpURLConnection
        int responseCode = connection.responseCode
        String responseText = connection.inputStream.text

        UUID responseId = DB.storeResponse(providerName, responseCode, responseText)

        def json = new JsonSlurper().parseText(responseText)
        List<Prediction> predictions = parsePredictions(location, json)
        predictions*.responseId = responseId
        predictions*.provider = providerName

        return predictions
    }

    @Override
    String getProviderName() {
        'wunderground'
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
                    high: forecast.high.fahrenheit.toInteger(),
                    low: forecast.low.fahrenheit.toInteger(),
                    pop: forecast.pop.toInteger() / 100)
            return prediction
        }

        return predictions
    }

}
