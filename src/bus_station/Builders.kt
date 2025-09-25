package bus_station

import java.time.LocalDateTime

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

class BusTicketBuilder(private val id: Int) {
    lateinit var trip: BusTrip
    lateinit var passengerName: String
    lateinit var passengerDocument: String
    var purchaseDate: LocalDateTime = LocalDateTime.now()
    var seatNumber: Int = 0
    var finalPrice: Double = 0.0

    fun build(): BusTicket {
        require(::trip.isInitialized) { "Рейс должен быть указан!" }
        require(seatNumber in 1..trip.availableSeats) { "Некорректное число мест!" }
        return BusTicket(id, trip, passengerName, passengerDocument, purchaseDate, seatNumber, finalPrice)
    }
}

fun busStation(block: BusStationDsl.() -> Unit): BusStation = BusStationDsl().apply(block).build()

class BusStationDsl {
    val station = BusStation()
    private var nextTripId = 1
    private var nextTicketId = 1

    fun trip(block: BusTripBuilder.() -> Unit) {
        station.addTrip(BusTripBuilder(nextTripId++).apply(block).build())
    }

    fun ticket(block: BusTicketBuilder.() -> Unit) {
        val ticket = BusTicketBuilder(nextTicketId++).apply(block).build()
        if (!station.addTicket(ticket)) {
            throw IllegalStateException("Нет свободных мест для поездки ${ticket.trip.id}")
        }
    }

    fun build(): BusStation = station
}

fun trip(id: Int, block: BusTripBuilder.() -> Unit): BusTrip = BusTripBuilder(id).apply(block).build()
fun ticket(id: Int, block: BusTicketBuilder.() -> Unit): BusTicket = BusTicketBuilder(id).apply(block).build()