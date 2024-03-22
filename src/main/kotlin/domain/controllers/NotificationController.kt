package domain.controllers

import domain.entity.Order
import domain.entity.OrderStatus

interface NotificationController {

    fun addNotification(order: Order)

    fun printNotification()
}


class NotificationControllerImpl() : NotificationController {
    private var listOfFinishedNumbersOrders = listOf<Int>()

    override fun addNotification(order: Order) {
        if (order.status != OrderStatus.CANCELLED) {
            val temp = listOfFinishedNumbersOrders.toMutableList()
            temp.add(order.num)
            listOfFinishedNumbersOrders = temp
        }
    }

    override fun printNotification() {
        if (listOfFinishedNumbersOrders.isNotEmpty()) {
            for (num in listOfFinishedNumbersOrders) {
                println("\u001B[34mЗаказ ${num} готов\u001B[0m")
                val temp = listOfFinishedNumbersOrders.toMutableList()
                temp.remove(num)
                listOfFinishedNumbersOrders = temp
            }
        }
    }

}