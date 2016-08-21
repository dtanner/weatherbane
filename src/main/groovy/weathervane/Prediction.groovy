package weathervane

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class Prediction {
    Instant predictedOn
    LocalDate targetDate
    String location
    String provider
    int high
    int low
    BigDecimal pop
    UUID responseId

    long getDaysBefore() {
        // hardcoding Chicago TZ...should suffice.  If not we can specify the Location's timezone.
        ChronoUnit.DAYS.between(predictedOn.atZone(ZoneId.of('America/Chicago')).toLocalDate(), targetDate)
    }
}
