package weathervane.collector

import groovy.json.JsonSlurper
import spock.lang.Specification
import weathervane.Locations
import weathervane.Prediction

import java.time.LocalDate

class ForecastioCollectorTest extends Specification {

    def "ParsePredictions"() {
        given:
        InputStream sampleFileIS = this.getClass().getResourceAsStream('/forecastio-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = ForecastioCollector.parsePredictions(Locations.MSP, json)

        then:
        predictions.size() == 8
        predictions[0].targetDate == LocalDate.of(2016, 07, 12)
        predictions[0].location == 'MSP'
        predictions[0].provider == 'forecast.io'
        predictions[0].high == 85
        predictions[0].low == 71
        predictions[0].pop == 0.54
    }
}
