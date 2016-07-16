package weathervane.collector

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import weathervane.AppConfig
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

        List<Prediction> predictions = null

        HTTPBuilder http = new HTTPBuilder('https://api.forecast.io')
        String pathString = "/forecast/${apiKey}/${location.latCommaLong()}"
        http.get(path: pathString) { response, json ->
//            log.debug response.entity.content.text
            log.debug(json.toString())
            predictions = parsePredictions(location, json)
        }
        return predictions
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
                    provider: 'forecast.io',
                    high: forecast.temperatureMax.setScale(0, HALF_UP).toInteger(),
                    low: forecast.temperatureMin.setScale(0, HALF_UP).toInteger(),
                    pop: forecast.precipProbability)
            return prediction
        }

        return predictions
    }

}
