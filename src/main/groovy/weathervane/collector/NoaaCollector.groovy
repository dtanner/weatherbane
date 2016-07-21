package weathervane.collector

import weathervane.Location
import weathervane.Prediction

import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NoaaCollector extends PredictionCollector {

    @Override
    String getPathString(Location location) {
        return "http://forecast.weather.gov/MapClick.php?lat=${location.latitude}&lon=${location.longitude}&FcstType=json"
    }

    @Override
    String getProviderName() {
        'noaa'
    }

    @Override
    List<Prediction> parsePredictions(Location location, Object response) {

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
                    location: location.name(),
                    high: high,
                    low: low,
                    pop: highestPop)
            return prediction
        }

        return predictions
    }

}
