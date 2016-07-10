package weathervane.collector.wunderground

import groovy.json.JsonSlurper
import spock.lang.Specification
import weathervane.Prediction

import java.time.Instant

class WundergroundCollectorTest extends Specification {

    def "ParsePredictions"() {
        given:
        InputStream sampleFileIS = this.getClass().getResourceAsStream('/wunderground-3day-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = WundergroundCollector.parsePredictions('KMSP', json)

        then:
        predictions.size() == 4
        predictions[0].targetDate == Instant.ofEpochSecond(1468195200)
        predictions[0].high == 85
        predictions[0].low == 74
        predictions[0].pop == 50
    }
}
