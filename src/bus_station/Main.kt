package bus_station

import bus_station.model.*
import bus_station.model.BusStation.*;

fun main() {
    val station = busStation {
        trip {
            departurePoint = "Москва"
            destination = "Санкт-Петербург"
            departureTime = todayAt(10, 0)
            arrivalTime = todayAt(18, 0)
            busType = BusType.COMFORT
            price = 1500.0
            availableSeats = 40
        }

        trip {
            departurePoint = "Москва"
            destination = "Пенза"
            departureTime = todayAt(12, 0)
            arrivalTime = todayAt(22, 30)
            busType = BusType.LUXURY
            price = 3500.0
            availableSeats = 30
        }

        trip {
            departurePoint = "Москва"
            destination = "Санкт-Петербург"
            departureTime = todayAt(8, 0)
            arrivalTime = todayAt(16, 0)
            busType = BusType.LUXURY
            price = 2500.0
        }

        trip {
            departurePoint = "Москва"
            destination = "Казань"
            departureTime = tomorrow().atTime(12, 0)
            arrivalTime = tomorrow().atTime(22, 0)
            busType = BusType.STANDARD
            price = 1200.0
        }

        // Покупка билета
        ticket {
            trip = station.trips.first()
            passengerName = "Иван Иванов"
            passengerDocument = "1234 567890"
            seatNumber = 15
            finalPrice = 1500.0
        }
    }

    // Использование DSL для поиска
    println("------------- Поиск рейсов -------------")
    val searchResults = station.search {
        departurePoint  = "Москва"
        minDepartureTime = todayAt(9, 0)
        maxDepartureTime = todayAt(12, 0)
    }

    searchResults.forEach { trip : BusTrip ->
        println(
            "${trip.departureTime.formatted}(${trip.departurePoint}) -> " +
                    "${trip.arrivalTime.formatted}(${trip.destination}) : ${trip.price} руб."
        )
    }

    // Использование операторов
    println("\n------------- Сортировка -------------")
    println("Самый дешевый: ${station.trips.cheapest()?.price} руб.")
    println("Самый дорогой: ${station.trips.mostExpensive()?.price} руб.")
    println(
        "Самый быстрый: ${station.trips.fastest()?.duration.let {"${it?.toHours()} ч. ${it?.toMinutesPart()} мин." }}")
    println(
        "Самый долгий: ${station.trips.slowest()?.duration.let {"${it?.toHours()} ч. ${it?.toMinutesPart()} мин." }}")

    // Красивый способ покупки билетов
    val ticket = station.trips.first() bookFor "Петр Сидоров" withDocument "9876 543210"
    station.addTicket(ticket)
    println("\nКуплен билет для: ${ticket.passengerName}")

    // Статистика
    println("\n------------- Статистика -------------")
    println("Всего рейсов: ${station.trips.size}")
    println("Предстоящие: ${station.trips.upcoming().size}")
    println("Доход с рейсов: ${station.trips.totalRevenue()} руб.")
    println("Текущая сумма купленных билетов: ${station.tickets.totalSales()} руб.")
}

