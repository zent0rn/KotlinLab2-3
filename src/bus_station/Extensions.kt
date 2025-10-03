package bus_station

import bus_station.model.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import bus_station.model.SearchCriteria.SearchBuilder

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
fun List<BusTrip>.slowest(): BusTrip? = maxByOrNull { it.duration }
fun List<BusTrip>.mostExpensive(): BusTrip? = maxByOrNull { it.price }
fun List<BusTrip>.groupByBusType(): Map<BusType, List<BusTrip>> = groupBy { it.busType }
fun List<BusTrip>.upcoming(): List<BusTrip> = filter { it.isUpcoming() }
fun List<BusTrip>.totalRevenue(): Double = sumOf { it.price }

// Расширения для списков билетов
fun List<BusTicket>.totalSales(): Double = sumOf { it.finalPrice }
fun List<BusTicket>.upcomingTickets(): List<BusTicket> = filter { it.isUpcoming }

