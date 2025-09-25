package bus_station

import java.time.LocalDateTime
import java.time.Duration

enum class BusType { STANDARD, COMFORT, LUXURY, MINIBUS, DOUBLE_DECKER }

data class BusTrip(
    val id: Int,
    val departurePoint: String,
    val destination: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime,
    val busType: BusType,
    val price: Double,
    val availableSeats: Int = 50
) {
    val duration: Duration get() = Duration.between(departureTime, arrivalTime)
    val durationHours: Long get() = duration.toHours()
    fun isUpcoming(relativeTo: LocalDateTime = LocalDateTime.now()) = departureTime > relativeTo
}

data class BusTicket(
    val id: Int,
    val trip: BusTrip,
    val passengerName: String,
    val passengerDocument: String,
    val purchaseDate: LocalDateTime,
    val seatNumber: Int,
    val finalPrice: Double
) {
    val isUpcoming: Boolean get() = trip.isUpcoming()
}

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
}

data class SearchCriteria(
    var departurePoint: String? = null,
    var destination: String? = null,
    var minDepartureTime: LocalDateTime? = null,
    var maxDepartureTime: LocalDateTime? = null,
    var maxPrice: Double? = null,
    var busTypes: Set<BusType> = emptySet()
)