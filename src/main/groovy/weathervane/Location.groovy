package weathervane

import groovy.transform.ToString

@ToString
enum Location {

    MSP(44.88536835, -93.23130798, "KMSP")

    final double latitude
    final double longitude
    final String airportCode

    private Location(double latitude, double longitude, String airportCode) {
        this.latitude = latitude
        this.longitude = longitude
        this.airportCode = airportCode
    }

    String latCommaLong() {
        "${latitude},${longitude}"
    }
}
