package weathervane

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification
import weathervane.collector.wunderground.WundergroundCollector

@Slf4j
class DBTest extends Specification {

    def "store predictions"() {
        given:
        InputStream sampleFileIS = this.class.classLoader.getResourceAsStream('wunderground-3day-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = WundergroundCollector.parsePredictions('KMSP', json)
        DB.instance.storePredictions(predictions)

        then:
        predictions.size() == 4
    }
}
