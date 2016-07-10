package weathervane

import java.time.Instant

class Prediction {
    Instant predictedOn
    Instant targetDate
    String location
    String provider
    int high
    int low
    int pop
}
