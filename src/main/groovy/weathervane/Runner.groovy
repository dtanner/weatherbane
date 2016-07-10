package weathervane

import groovy.util.logging.Slf4j
import weathervane.collector.PredictionCollector
import weathervane.collector.wunderground.WundergroundCollector

import java.time.LocalDate

@Slf4j
class Runner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))

        PredictionCollector collector = new WundergroundCollector()
        List<Prediction> predictions = collector.collect(LocalDate.now().plusDays(1), Locations.MSP.airportCode)

        DB.instance.storePredictions(predictions)
    }

}
