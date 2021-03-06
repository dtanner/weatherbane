package weathervane

import groovy.json.JsonSlurper
import spock.lang.Specification
import weathervane.collector.WundergroundCollector

class DBTest extends Specification {

    def "store predictions"() {
        given:
        InputStream sampleFileIS = this.class.classLoader.getResourceAsStream('wunderground-3day-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = new WundergroundCollector().parsePredictions(Location.MSP, json)
        predictions*.responseId = UUID.randomUUID()
        predictions*.provider = 'a'
        DB.instance.storePredictions(predictions)

        then:
        predictions.size() == 4
    }

    def "store response"() {
        when:
        UUID id = DB.instance.storeResponse('a', 200, '{}')

        then:
        id
    }
}
