package weathervane.collector

import groovy.json.JsonSlurper
import weathervane.DB
import weathervane.Location
import weathervane.Prediction

abstract class PredictionCollector {

    List<Prediction> collect(Location location) {
        HttpURLConnection connection = new URL(getPathString(location)).openConnection() as HttpURLConnection
        int responseCode = connection.responseCode
        String responseText = connection.inputStream.text

        UUID responseId = DB.storeResponse(providerName, responseCode, responseText)

        def json = new JsonSlurper().parseText(responseText)
        List<Prediction> predictions = parsePredictions(location, json)
        predictions*.responseId = responseId
        predictions*.provider = providerName

        return predictions
    }

    abstract String getPathString(Location location)

    abstract List<Prediction> parsePredictions(Location location, Object response)

    abstract String getProviderName()
}
