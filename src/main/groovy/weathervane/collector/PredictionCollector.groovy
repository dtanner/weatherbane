package weathervane.collector

import weathervane.Location
import weathervane.Prediction

import java.time.LocalDate

interface PredictionCollector {

    List<Prediction> collect(LocalDate targetDate, Location location)

}
