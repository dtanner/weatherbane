package weathervane.collector

import weathervane.AppConfig
import weathervane.Location
import weathervane.Prediction

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

import static java.math.RoundingMode.HALF_UP

class ForecastioCollector extends PredictionCollector {

    @Override
    String getPathString(Location location) {
        String apiKey = AppConfig.config.forecastio.apiKey
        return "https://api.forecast.io/forecast/${apiKey}/${location.latCommaLong()}"
    }

    @Override
    String getProviderName() {
        'forecast.io'
    }

    @Override
    List<Prediction> parsePredictions(Location location, response) {
        ZoneId zoneId = ZoneId.of(response.timezone)

        List dailyForecasts = response.daily.data
        List<Prediction> predictions = dailyForecasts.collect { forecast ->

            Instant targetEpoch = Instant.ofEpochSecond(forecast.time)
            LocalDate targetDate = targetEpoch.atZone(zoneId).toLocalDate()

            Prediction prediction = new Prediction(predictedOn: Instant.now(),
                    targetDate: targetDate,
                    location: location.name,
                    high: forecast.temperatureMax.setScale(0, HALF_UP).toInteger(),
                    low: forecast.temperatureMin.setScale(0, HALF_UP).toInteger(),
                    pop: forecast.precipProbability)
            return prediction
        }

        return predictions
    }

}
