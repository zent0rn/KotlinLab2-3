package bus_station

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
        departurePoint = "Москва"
        destination = "Санкт-Петербург"
        minDepartureTime = todayAt(9, 0)
    }

    searchResults.forEach { trip ->
        println("${trip.departureTime.formatted} -> ${trip.arrivalTime.formatted}: ${trip.price} руб.")
    }

    // Использование операторов
    println("\n------------- Сортировка -------------")
    val cheapFirst = +station
    val expensiveFirst = -station

    println("Самый дешевый: ${station.trips.cheapest()?.price} руб.")
    println("Самый быстрый: ${station.trips.fastest()?.durationHours} ч.")

    // Красивый способ покупки билетов
    val ticket = station.trips.first() bookFor "Петр Сидоров" withDocument "9876 543210"
    println("\nКуплен билет для: ${ticket.passengerName}")

    // Статистика
    println("\n------------- Статистика -------------")
    println("Всего рейсов: ${station.trips.size}")
    println("Предстоящие: ${station.trips.upcoming().size}")
    println("Доход с рейсов: ${station.trips.totalRevenue()} руб.")
}

