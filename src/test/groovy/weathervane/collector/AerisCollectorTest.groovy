package weathervane.collector

import groovy.json.JsonSlurper
import spock.lang.Specification
import weathervane.Location
import weathervane.Prediction

import java.time.LocalDate

class AerisCollectorTest extends Specification {

    def "ParsePredictions"() {
        given:
        InputStream sampleFileIS = this.getClass().getResourceAsStream('/aeris-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = new AerisCollector().parsePredictions(Location.MSP, json)

        then:
        predictions.size() == 7
        predictions[0].targetDate == LocalDate.of(2016, 07, 21)
        predictions[0].location == 'MSP'
        predictions[0].high == 97
        predictions[0].low == 73
        predictions[0].pop == 0.54
    }
}
