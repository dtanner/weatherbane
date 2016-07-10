package weathervane.collector

import weathervane.Prediction

import java.time.LocalDate

interface PredictionCollector {

    List<Prediction> collect(LocalDate targetDate, String location)

}