package weathervane.collector

import weathervane.AppConfig
import weathervane.Location
import weathervane.Prediction

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class WundergroundCollector extends PredictionCollector {

    @Override
    String getPathString(Location location) {
        String apiKey = AppConfig.config.wunderground.apiKey
        return "http://api.wunderground.com/api/${apiKey}/forecast/q/${location.airportCode}.json"
    }

    @Override
    String getProviderName() {
        'wunderground'
    }

    @Override
    List<Prediction> parsePredictions(Location location, response) {
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
