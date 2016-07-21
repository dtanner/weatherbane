package weathervane.collector

import groovy.json.JsonSlurper
import spock.lang.Specification
import weathervane.Location
import weathervane.Prediction

import java.time.LocalDate

class NoaaCollectorTest extends Specification {

    def "ParsePredictions"() {
        given:
        InputStream sampleFileIS = this.getClass().getResourceAsStream('/noaa-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = new NoaaCollector().parsePredictions(Location.MSP, json)

        then:
        predictions.size() == 6
        predictions[0].targetDate == LocalDate.of(2016, 07, 18)
        predictions[0].location == 'MSP'
        predictions[0].high == 85
        predictions[0].low == 66
        predictions[0].pop == 0.10
    }
}
