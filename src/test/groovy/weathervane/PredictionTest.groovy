package weathervane

import spock.lang.Specification

import java.time.Instant
import java.time.LocalDate

class PredictionTest extends Specification {

    def "days before"() {
        when:
        Prediction prediction = new Prediction(predictedOn: Instant.now(), targetDate: LocalDate.now().plusDays(1))

        then:
        prediction.daysBefore == 1
    }

}
