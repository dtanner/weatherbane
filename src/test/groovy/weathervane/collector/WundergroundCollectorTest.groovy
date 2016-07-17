package weathervane.collector

import groovy.json.JsonSlurper
import spock.lang.Specification
import weathervane.Locations
import weathervane.Prediction

import java.time.LocalDate

class WundergroundCollectorTest extends Specification {

    def "ParsePredictions"() {
        given:
        InputStream sampleFileIS = this.getClass().getResourceAsStream('/wunderground-3day-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = WundergroundCollector.parsePredictions(Locations.MSP, json)

        then:
        predictions.size() == 4
        predictions[0].targetDate == LocalDate.of(2016, 07, 10)
        predictions[0].location == 'MSP'
        predictions[0].high == 85
        predictions[0].low == 74
        predictions[0].pop == 0.50
    }
}
