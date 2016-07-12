package weathervane

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import spock.lang.Specification
import weathervane.collector.wunderground.WundergroundCollector
import weathervane.util.CommandUtil

@Slf4j
class DBTest extends Specification {

    def "store predictions"() {
        given:
        String path = this.class.getResource("").path
        log.info "class.getResource path = $path"

        String classloaderPath = this.class.classLoader.getResource("").path
        log.info "classloaderPath.getResource path = $classloaderPath"

        CommandUtil.execute('ls -lR')

        InputStream sampleFileIS = this.class.classLoader.getResourceAsStream('./wunderground-3day-sample.json')
        def json = new JsonSlurper().parse(sampleFileIS)

        when:
        List<Prediction> predictions = WundergroundCollector.parsePredictions('KMSP', json)
        DB.instance.storePredictions(predictions)

        then:
        predictions.size() == 4
    }
}
