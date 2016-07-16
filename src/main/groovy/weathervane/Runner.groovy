package weathervane

import groovy.util.logging.Slf4j
import weathervane.collector.ForecastioCollector
import weathervane.collector.PredictionCollector
import weathervane.collector.WundergroundCollector

import java.time.LocalDate

@Slf4j
class Runner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))

        List locations = [Locations.MSP]
        List<PredictionCollector> collectors = [WundergroundCollector, ForecastioCollector] as List<PredictionCollector>

        collectors.each { Class<PredictionCollector> collectorType ->
            PredictionCollector collector = collectorType.newInstance()
            locations.each { Location location ->
                List<Prediction> predictions = collector.collect(LocalDate.now(), location)
                DB.instance.storePredictions(predictions)
            }
        }
    }

}
