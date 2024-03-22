package presentation

import data.MenuDao
import data.OrdersDao
import data.Success
import data.UsersDao
import di.DI.notificationController
import domain.controllers.OrderController
import domain.entity.MenuItem
import domain.entity.Order
import domain.entity.OrderStatus
import kotlin.system.exitProcess

class ConsoleManagerForVisitor(
    menuDao: MenuDao,
    ordersDao: OrdersDao,
    usersDao: UsersDao,
    orderController: OrderController
) : ConsoleManager(menuDao, ordersDao, usersDao, orderController) {
    override fun showOperations() {
        notificationController.printNotification()
        println(".....................................")
        println("Выберите операцию. Нажмите:")
        println("1 - посмотреть меню")
        println("2 - сделать заказ")
        println("3 - добавить блюдо в существующий заказ")
        println("4 - отменить заказ")
        println("5 - посмотреть статус моего заказа")
        println("6 - оплатить заказ")
        println("7 - оставить отзыв")
        println("8 - выход")
    }

    override suspend fun operationsHandler() {
        notificationController.printNotification()
        var oper: String
        while (true) {
            showOperations()
            oper = readln()
            if (oper == "8") {
                exitProcess(0)
            }
            when (oper) {
                "1" -> {
                    showMenu()
                }

                "2" -> {
                    makeOrder()
                }

                "3" -> {
                    addItemToOrder()
                }

                "4" -> {
                    cancelOrder()
                }

                "5" -> {
                    showStatus()
                }

                "6" -> {
                    payForOrder()
                }

                "7" -> {
                    makeReview()
                }

                else -> {
                    println("Неверная операция")
                }
            }
        }
    }


    private fun readOrder(): Order? {
        println("Введите номер вашего заказа")
        val num = readln().toIntOrNull()

        val orders = ordersDao.getAllOrders()
        val order = orders.find { it.num == num }
        if (order == null) {
            println("Заказа с таким номером нет")
            return null
        }
        return order
    }

    private fun readItemsList(): List<MenuItem> {
        println("Введите через запятую названия блюд, которые хотите заказать")
        val items = readln()
        val list = items.split(", ").map { it.trim() }
        val allDishes = mutableListOf<MenuItem>()
        val menu = menuDao.getMenu()
        for (el in list) {
            val item = menu.find { it.name == el }
            if (item == null) {
                println("$el нет в меню, поэтому оно не будет добавлено в заказ")
            } else {
                allDishes.add(item)
            }
        }
        return allDishes
    }

    private suspend fun makeOrder() {
        val allDishes = readItemsList()

        if (allDishes.isNotEmpty()) {
            val user = usersDao.getCurrentUser()

            if (menuDao.decreaseCountOfItems(allDishes) != Success) {
                println("К сожалению мы не можем оформить заказ, т.к. некоторых блюд недостаточное количество")
                return
            }

            val num = orderController.createNewOrder(user.id, allDishes)

            println("Заказ оформлен")
            println("Номер вашего заказа-$num")
        } else {
            println("Заказ не оформлен, т.к. вы не выбрали блюда из меню")
        }

        orderController.processOrder(ordersDao.getAllOrders().last())
        notificationController.printNotification()
    }

    private fun addItemToOrder() {
        val order = readOrder() ?: return

        val allDishes = readItemsList()

        if (allDishes.isNotEmpty()) {
            if (menuDao.decreaseCountOfItems(allDishes) != Success) {
                println("К сожалению мы не можем добавить эти блюда в заказ из-за недостаточного количества")
                return
            }
            orderController.addItemsToOrder(order.id, allDishes)
            println("Блюда добавлены в заказ")
        } else {
            println("Блюда не добавлены в заказ")
        }
    }


    private fun cancelOrder() {
        val order = readOrder() ?: return

        if (order.status == OrderStatus.FINISHED) {
            println("Нельзя отменить завершенный заказ")
            return
        }
        orderController.cancelOrder(order)

        println("Заказ отменен")
    }

    private fun showStatus() {
        val order = readOrder() ?: return

        when (order.status) {
            OrderStatus.IN_PROCESS -> println("Заказ готовится")
            OrderStatus.NEW -> println("Заказ принят")
            OrderStatus.CANCELLED -> println("Заказ отменен")
            OrderStatus.FINISHED -> println("Заказ завершен")
        }
    }

    private fun readMark(): Int? {
        println("Введите оценку от 1 до 5")
        val mark = readln().toIntOrNull()
        if (mark !in 1..5 || mark == null) {
            println("Неверная оценка")
            return null
        }
        return mark
    }

    private fun makeReview() {
        val order = readOrder() ?: return

        if (order.status != OrderStatus.FINISHED) {
            println("Нельзя оставить отзыв о незавершенном заказе")
            return
        }
        if (!order.paid) {
            println("Нельзя оставить отзыв о неоплаченном заказе")
            return
        }

        val mark = readMark() ?: return

        println("Введите отзыв")
        val review = readln()

        orderController.makeReview(order, mark, review)
        println("Спасибо за отзыв")
    }


    private fun makeReviewAfterPayment(order: Order) {

        println("Хотите оставить отзыв о заказе? (да/нет)")
        val answer = readln()
        if (answer == "да") {
            val mark = readMark() ?: return

            println("Введите отзыв")
            val review = readln()

            orderController.makeReview(order, mark, review)
            println("Спасибо за отзыв")
        }

    }

    private fun payForOrder() {
        val order = readOrder() ?: return

        orderController.payForOrder(order)
        println("Заказ оплачен")

        makeReviewAfterPayment(order)
    }

}