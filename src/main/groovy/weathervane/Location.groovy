package weathervane

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = ['latitude', 'longitude'])
@ToString
class Location {
    String name
    double latitude
    double longitude
    String airportCode

    String latCommaLong() {
        "${latitude},${longitude}"
    }
}
