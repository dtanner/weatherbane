package weathervane

import groovy.transform.ToString

@ToString
enum Location {

    MSP(44.88536835, -93.23130798, "KMSP"),
    SAN(32.733556, -117.189667, "KSAN"),
    CMX(47.1684, -88.4891, "KCMX"),
    DEN(39.861656, -104.673178, "KDEN"),
    JFK(40.639751, -73.778925, "KJFK"),
    ORD(41.978603, -87.904842, "KORD"),
    SFO(37.618972, -122.374889, "KSFO")

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
