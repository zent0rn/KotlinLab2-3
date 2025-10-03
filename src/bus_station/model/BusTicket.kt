package bus_station.model

import java.time.LocalDateTime

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

    class BusTicketBuilder(private val id: Int) {
        lateinit var trip: BusTrip
        lateinit var passengerName: String
        lateinit var passengerDocument: String
        var purchaseDate: LocalDateTime = LocalDateTime.now()
        var seatNumber: Int = 0
        var finalPrice: Double = 0.0

        fun build(): BusTicket {
            require(::trip.isInitialized) { "Рейс должен быть указан!" }
            require(::passengerName.isInitialized) { "Имя пассажира должно быть указано!" }
            require(::passengerDocument.isInitialized) { "Документ пассажира должен быть указан!" }
            require(seatNumber in 1..trip.availableSeats) {
                "Некорректный номер места! Должен быть от 1 до ${trip.availableSeats}"
            }
            require(finalPrice >= 0) { "Цена не может быть отрицательной!" }

            return BusTicket(id, trip, passengerName, passengerDocument, purchaseDate, seatNumber, finalPrice)
        }
    }
}

infix fun BusTicket.withDocument(document: String): BusTicket =
    copy(passengerDocument = document)