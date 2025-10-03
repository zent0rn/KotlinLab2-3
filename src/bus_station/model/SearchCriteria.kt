package bus_station.model

import java.time.LocalDateTime

data class SearchCriteria(
    var departurePoint: String? = null,
    var destination: String? = null,
    var minDepartureTime: LocalDateTime? = null,
    var maxDepartureTime: LocalDateTime? = null,
    var maxPrice: Double? = null,
    var busTypes: Set<BusType> = emptySet()
){
    class SearchBuilder(private val station: BusStation) {
        private val criteria = SearchCriteria()

        fun from(departure: String): SearchBuilder = apply { criteria.departurePoint = departure }
        fun to(destination: String): SearchBuilder = apply { criteria.destination = destination }
        fun after(time: LocalDateTime): SearchBuilder = apply { criteria.minDepartureTime = time }
        fun before(time: LocalDateTime): SearchBuilder = apply { criteria.maxDepartureTime = time }
        fun maxPrice(price: Double): SearchBuilder = apply { criteria.maxPrice = price }
        fun busTypes(vararg types: BusType): SearchBuilder = apply { criteria.busTypes = types.toSet() }

        fun execute(): List<BusTrip> = station.trips.filter { trip ->
            (criteria.departurePoint == null || trip.departurePoint.equals(criteria.departurePoint, ignoreCase = true)) &&
                    (criteria.destination == null || trip.destination.equals(criteria.destination, ignoreCase = true)) &&
                    (criteria.minDepartureTime == null || trip.departureTime >= criteria.minDepartureTime) &&
                    (criteria.maxDepartureTime == null || trip.departureTime <= criteria.maxDepartureTime) &&
                    (criteria.maxPrice == null || trip.price <= criteria.maxPrice!!) &&
                    (criteria.busTypes.isEmpty() || trip.busType in criteria.busTypes)
        }
    }
}
