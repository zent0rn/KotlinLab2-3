package bus_station

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Расширения для дат
fun String.toLocalTime(): LocalTime = LocalTime.parse(this)
fun LocalDateTime.plusHours(hours: Long): LocalDateTime = this.plusHours(hours)
fun LocalDateTime.atTime(hour: Int, minute: Int = 0): LocalDateTime =
    this.toLocalDate().atTime(hour, minute)

val LocalDateTime.formatted: String
    get() = format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

fun tomorrow(): LocalDateTime = LocalDateTime.now().plusDays(1)
fun todayAt(hour: Int, minute: Int = 0): LocalDateTime =
    LocalDateTime.now().toLocalDate().atTime(hour, minute)

// Расширения для списков рейсов
fun List<BusTrip>.cheapest(): BusTrip? = minByOrNull { it.price }
fun List<BusTrip>.fastest(): BusTrip? = minByOrNull { it.duration }
fun List<BusTrip>.mostExpensive(): BusTrip? = maxByOrNull { it.price }
fun List<BusTrip>.groupByBusType(): Map<BusType, List<BusTrip>> = groupBy { it.busType }
fun List<BusTrip>.upcoming(): List<BusTrip> = filter { it.isUpcoming() }
fun List<BusTrip>.totalRevenue(): Double = sumOf { it.price }

// Расширения для списков билетов
fun List<BusTicket>.totalSales(): Double = sumOf { it.finalPrice }
fun List<BusTicket>.upcomingTickets(): List<BusTicket> = filter { it.isUpcoming }

// DSL для поиска рейсов
infix fun BusStation.from(departure: String): SearchBuilder = SearchBuilder(this).from(departure)
infix fun BusStation.to(destination: String): SearchBuilder = SearchBuilder(this).to(destination)
infix fun BusStation.after(time: LocalDateTime): SearchBuilder = SearchBuilder(this).after(time)
infix fun BusStation.before(time: LocalDateTime): SearchBuilder = SearchBuilder(this).before(time)

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

// Инфиксные методы для покупки билетов
infix fun BusTrip.bookFor(passenger: String): BusTicket = BusTicket(
    id = 0,
    trip = this,
    passengerName = passenger,
    passengerDocument = "unknown",
    purchaseDate = LocalDateTime.now(),
    seatNumber = (1..this.availableSeats).random(),
    finalPrice = this.price
)

infix fun BusTicket.withDocument(document: String): BusTicket =
    copy(passengerDocument = document)