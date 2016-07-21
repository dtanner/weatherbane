package weathervane

import groovy.util.logging.Slf4j
import weathervane.collector.ForecastioCollector
import weathervane.collector.NoaaCollector
import weathervane.collector.PredictionCollector
import weathervane.collector.WundergroundCollector

@Slf4j
class Runner {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))

//        List<PredictionCollector> collectors = [WundergroundCollector] as List<PredictionCollector>
        List<PredictionCollector> collectors = [WundergroundCollector, ForecastioCollector, NoaaCollector] as List<PredictionCollector>

        collectors.each { Class<PredictionCollector> collectorType ->
            PredictionCollector collector = collectorType.newInstance()
            Location.values().each { Location location ->
                List<Prediction> predictions = collector.collect(location)
                log.debug "Storing ${predictions.size()} predictions for ${collector.providerName}"
                DB.instance.storePredictions(predictions)
            }
        }
    }

}
