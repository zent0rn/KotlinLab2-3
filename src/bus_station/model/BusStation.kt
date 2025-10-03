package bus_station.model

import bus_station.model.BusStation.BusStationDsl
import bus_station.model.SearchCriteria.SearchBuilder
import java.time.LocalDateTime

class BusStation {
    private val _trips = mutableListOf<BusTrip>()
    private val _tickets = mutableListOf<BusTicket>()

    val trips: List<BusTrip> get() = _trips.toList()
    val tickets: List<BusTicket> get() = _tickets.toList()

    fun addTrip(trip: BusTrip) = _trips.add(trip)

    fun addTicket(ticket: BusTicket): Boolean {
        val availableSeats = ticket.trip.availableSeats - tickets.count { it.trip.id == ticket.trip.id }
        return if (availableSeats > 0) _tickets.add(ticket) else false
    }

    fun getAvailableSeats(tripId: Int): Int {
        val trip = trips.find { it.id == tripId } ?: return 0
        return trip.availableSeats - tickets.count { it.trip.id == tripId }
    }

    class BusStationDsl {
        val station = BusStation()
        private var nextTripId = 1
        private var nextTicketId = 1

        fun trip(block: BusTrip.BusTripBuilder.() -> Unit) {
            station.addTrip(BusTrip.BusTripBuilder(nextTripId++).apply(block).build())
        }

        fun ticket(block: BusTicket.BusTicketBuilder.() -> Unit) {
            val ticket = BusTicket.BusTicketBuilder(nextTicketId++).apply(block).build()
            if (!station.addTicket(ticket)) {
                throw IllegalStateException("Нет свободных мест для поездки ${ticket.trip.id}")
            }
        }

        fun build(): BusStation = station
    }
}

fun busStation(block: BusStationDsl.() -> Unit): BusStation = BusStationDsl().apply(block).build()

// Операторы
operator fun BusStation.unaryPlus(): BusStation = BusStation().apply {
    trips.sortedBy { it.arrivalTime }.forEach { addTrip(it) }
}

operator fun BusStation.unaryMinus(): BusStation = BusStation().apply {
    trips.sortedByDescending { it.price }.forEach { addTrip(it) }
}

operator fun BusStation.not(): Boolean = trips.isEmpty()

operator fun BusStation.plus(other: BusStation): BusStation = BusStation().apply {
    this@plus.trips.forEach { addTrip(it) }
    other.trips.forEach { addTrip(it) }
}

operator fun BusStation.minus(busType: BusType): BusStation = BusStation().apply {
    trips.filter { it.busType != busType }.forEach { addTrip(it) }
}

operator fun BusStation.times(multiplier: Double): BusStation = BusStation().apply {
    trips.map { it.copy(price = it.price * multiplier) }.forEach { addTrip(it) }
}

// DSL для поиска рейсов
infix fun BusStation.from(departure: String): SearchBuilder = SearchBuilder(this).from(departure)
infix fun BusStation.to(destination: String): SearchBuilder = SearchBuilder(this).to(destination)
infix fun BusStation.after(time: LocalDateTime): SearchBuilder = SearchBuilder(this).after(time)
infix fun BusStation.before(time: LocalDateTime): SearchBuilder = SearchBuilder(this).before(time)

// Альтернативный DSL для поиска
fun BusStation.search(block: SearchCriteria.() -> Unit): List<BusTrip> {
    val criteria = SearchCriteria().apply(block)
    return trips.filter { trip ->
        (criteria.departurePoint == null || trip.departurePoint.equals(criteria.departurePoint, ignoreCase = true)) &&
                (criteria.destination == null || trip.destination.equals(criteria.destination, ignoreCase = true)) &&
                (criteria.minDepartureTime == null || trip.departureTime >= criteria.minDepartureTime) &&
                (criteria.maxDepartureTime == null || trip.departureTime <= criteria.maxDepartureTime) &&
                (criteria.maxPrice == null || trip.price <= criteria.maxPrice!!) &&
                (criteria.busTypes.isEmpty() || trip.busType in criteria.busTypes)
    }
}