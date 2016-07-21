package weathervane.collector

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import weathervane.AppConfig
import weathervane.DB
import weathervane.Location
import weathervane.Prediction

import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Slf4j
class NoaaCollector implements PredictionCollector {

    @Override
    List<Prediction> collect(LocalDate targetDate, Location location) {
        String apiKey = AppConfig.config.noaa.apiKey

        String pathString = "http://forecast.weather.gov/MapClick.php?lat=${location.latitude}&lon=${location.longitude}&FcstType=json"

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
        'noaa'
    }

    static List<Prediction> parsePredictions(Location location, response) {

        List<Map> rows = []
        14.times {
            ZonedDateTime targetDate = ZonedDateTime.parse(response.time.startValidTime[it], DateTimeFormatter.ISO_DATE_TIME)
            Map row = [targetDate: targetDate.toLocalDate(),
                       tempLabel : response.time.tempLabel[it],
                       temp      : response.data.temperature[it].toInteger(),
                       pop       : (response.data.pop[it] ?: "0").toInteger() / 100
            ]
            rows << row
        }

        Map<LocalDate, List> rowsByDate = rows.groupBy { it.targetDate }

        List<Prediction> predictions = rowsByDate.findResults { LocalDate targetDate, List data ->

            Integer high = data.find { it.tempLabel == "High" }?.temp
            Integer low = data.find { it.tempLabel == "Low" }?.temp
            // in the evening the first bit of data is just tonight's low.  not enough to make a day's prediction
            if (high == null || low == null) {
                return null
            }

            BigDecimal highestPop = data.max { it.pop }.pop

            Prediction prediction = new Prediction(predictedOn: Instant.now(),
                    targetDate: targetDate,
                    location: location.name,
                    high: high,
                    low: low,
                    pop: highestPop)
            return prediction
        }

        return predictions
    }

}
