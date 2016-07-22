package weathervane.collector

import weathervane.AppConfig
import weathervane.Location
import weathervane.Prediction

import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime

class AerisCollector extends PredictionCollector {

    @Override
    String getPathString(Location location) {
        String apiKey = AppConfig.config.aeris.apiKey
        return "http://api.aerisapi.com/forecasts/closest?client_id=O8bSR6C9dVkOj3EMA34la&client_secret=${apiKey}&p=${location.latCommaLong()}"
    }

    @Override
    String getProviderName() {
        'aeris'
    }

    @Override
    List<Prediction> parsePredictions(Location location, response) {

        List dailyForecasts = response.response[0].periods
        List<Prediction> predictions = dailyForecasts.collect { forecast ->

            LocalDate targetDate = ZonedDateTime.parse(forecast.dateTimeISO).toLocalDate()

            Prediction prediction = new Prediction(predictedOn: Instant.now(),
                    targetDate: targetDate,
                    location: location.name(),
                    high: forecast.maxTempF.toInteger(),
                    low: forecast.minTempF.toInteger(),
                    pop: forecast.pop.toInteger() / 100)
            return prediction
        }

        return predictions
    }

}
