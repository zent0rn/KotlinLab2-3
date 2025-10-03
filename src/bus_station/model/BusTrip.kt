package bus_station.model

import bus_station.model.BusTrip.BusTripBuilder
import java.time.Duration
import java.time.LocalDateTime

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

    class BusTripBuilder(private val id: Int) {
        var departurePoint: String = ""
        var destination: String = ""
        var departureTime: LocalDateTime = LocalDateTime.now()
        var arrivalTime: LocalDateTime = LocalDateTime.now().plusHours(1)
        var busType: BusType = BusType.STANDARD
        var price: Double = 0.0
        var availableSeats: Int = 50

        fun build(): BusTrip {
            require(departurePoint.isNotBlank()) { "Пункт отправления должен быть указан!" }
            require(destination.isNotBlank()) { "Пункт назначения должен быть указан!" }
            require(arrivalTime > departureTime) { "Время прибытия должно быть после времени отправления!" }
            return BusTrip(id, departurePoint, destination, departureTime, arrivalTime, busType, price, availableSeats)
        }
    }
}

fun trip(id: Int, block: BusTripBuilder.() -> Unit): BusTrip = BusTripBuilder(id).apply(block).build()

infix fun BusTrip.bookFor(passenger: String): BusTicket = BusTicket(
    id = 0,
    trip = this,
    passengerName = passenger,
    passengerDocument = "unknown",
    purchaseDate = LocalDateTime.now(),
    seatNumber = (1..this.availableSeats).random(),
    finalPrice = this.price
)