package weathervane

import java.time.Instant
import java.time.LocalDate

class Prediction {
    Instant predictedOn
    LocalDate targetDate
    String location
    String provider
    int high
    int low
    BigDecimal pop
}
