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

import static java.math.RoundingMode.HALF_UP

@Slf4j
class ForecastioCollector implements PredictionCollector {

    @Override
    List<Prediction> collect(LocalDate targetDate, Location location) {
        String apiKey = AppConfig.config.forecastio.apiKey

        String pathString = "https://api.forecast.io/forecast/${apiKey}/${location.latCommaLong()}"

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
        'forecast.io'
    }

    static List<Prediction> parsePredictions(Location location, response) {
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
