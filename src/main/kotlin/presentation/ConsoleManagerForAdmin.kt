package presentation

import data.MenuDao
import data.OrdersDao
import data.UsersDao
import domain.controllers.OrderController
import domain.entity.MenuItem
import domain.entity.OrderStatus
import kotlin.system.exitProcess

class ConsoleManagerForAdmin(
    menuDao: MenuDao,
    ordersDao: OrdersDao,
    usersDao: UsersDao,
    orderController: OrderController
) : ConsoleManager(menuDao, ordersDao, usersDao, orderController) {
    override fun showOperations() {
        println(".....................................")
        println("Выберите операцию. Нажмите:")
        println("1 - посмотреть меню")
        println("2 - редактировать меню")
        println("3 - посмотреть статистику")
        println("4 - посмотреть выручку")
        println("5 - выход")
    }

    override suspend fun operationsHandler() {
        var oper: String
        while (true) {
            showOperations()
            oper = readln()
            if (oper == "5") {
                exitProcess(0)
            }
            when (oper) {
                "1" -> {
                    showMenu()
                }

                "2" -> {
                    changeMenu()
                }

                "3" -> {
                    showStatistics()
                }
                "4" -> {
                    showRevenue()
                }

            }
        }
    }

    private fun readMenuItem(): MenuItem? {
        val menu = menuDao.getMenu()

        println("Введите название блюда")
        val name = readln()
        val item = menu.find { it.name == name }

        if (item == null) {
            println("В меню нет такого блюда")
            return null
        }
        return item
    }

    private fun deleteItemFromMenu() {
        val item = readMenuItem() ?: return
        menuDao.deleteFromMenu(item)
    }

    private fun addItemToMenu() {
        println("Введите название блюда:")
        val name = readln()
        println("Введите цену:")
        val price = readln().toIntOrNull()
        if (price == null) {
            println("Некорректная стоимость")
            return
        }
        println("Введите количество:")
        val count = readln().toIntOrNull()
        if (count == null) {
            println("Некорректное количество")
            return
        }
        println("Введите время выполения блюда:")
        val time = readln().toIntOrNull()
        if (time == null) {
            println("Некорректное время")
            return
        }

        menuDao.addToMenu(MenuItem(name, price, time, count))
    }

    private fun changeCountOfItem() {
        val item = readMenuItem() ?: return
        println("Введите новое количество:")
        val count = readln().toIntOrNull()
        if (count == null) {
            println("Некорректное количество")
            return
        }

        menuDao.changeCountOfMenuItem(item.id, count)
    }

    private fun changePriceOfItem() {
        val item = readMenuItem() ?: return
        println("Введите новую цену:")
        val price = readln().toIntOrNull()
        if (price == null) {
            println("Некорректная цена")
            return
        }

        menuDao.changePriceOfMenuItem(item.id, price)
    }

    private fun changeTimeOfItem() {
        val item = readMenuItem() ?: return
        println("Введите новое время выполнения:")
        val time = readln().toIntOrNull()
        if (time == null) {
            println("Некорректная время выполнения")
            return
        }

        menuDao.changeTimeOfMenuItem(item.id, time)
    }

    private fun changeMenu() {
        println("Нажмите:")
        println("1 - удалить позицию из меню")
        println("2 - добавить позицию в меню")
        println("3 - изменить количество блюда")
        println("4 - изменить цену")
        println("5 - изменить время выполнения блюда")
        println("6 - назад")


        val oper = readln()

        when (oper) {
            "1" -> {
                deleteItemFromMenu()
            }

            "2" -> {
                addItemToMenu()
            }

            "3" -> {
                changeCountOfItem()
            }

            "4" -> {
                changePriceOfItem()
            }

            "5" -> {
                changeTimeOfItem()
            }

            "6" -> {
                return
            }
        }
    }

    private fun showStatistics() {
        println("Нажмите:")
        println("1 - посмотреть самые популярные блюда")
        println("2 - средняя оценка блюд")
        println("3 - количество заказов")
        println("4 - назад")


        val oper = readln()

        when (oper) {
            "1" -> {
                mostPopularItem()
            }

            "2" -> {
                averageMark()
            }

            "3" -> {
                countOrders()
            }

            "4" -> {
                return
            }
        }
    }

    private fun averageMark() {
        val orders = ordersDao.getAllOrders()

        var sum = 0
        var count = 0
        for (order in orders) {
            if (order.reviewMark > 0) {
                sum += order.reviewMark
                count++
            }
        }

        val avMark = sum.toDouble() / count

        println("Средняя оценка заказов - $avMark")
    }

    private fun mostPopularItem() {
        val orders = ordersDao.getAllOrders()
        val dict = mutableMapOf<MenuItem, Int>()

        for (order in orders) {
            for (item in order.allDishes) {
                dict[item] = dict.getOrDefault(item, 0) + 1
            }
        }

        val maxCount = dict.values.maxOfOrNull { it }
        val popularItems = dict.filterKeys { dict[it] == maxCount }.keys.map { el -> el.name }

        if (popularItems.size == 1) {
            val item = popularItems.joinToString()
            println("Самое популярное блюдо - $item. Его заказали $maxCount раз")
        } else {
            val items = popularItems.joinToString(", ")
            println("Самые популярные блюда: $items. Их заказали $maxCount раз")
        }

    }


    private fun countOrders() {
        val orders = ordersDao.getAllOrders()
        val ordersCount = orders.size

        println("Всего заказов - $ordersCount")
        val ordersInProccessCount = orders.filter { it.status == OrderStatus.IN_PROCESS }.size
        println("Заказов в обработке - $ordersInProccessCount")

        val cancelledOrdersCount = orders.filter { it.status == OrderStatus.CANCELLED }.size
        println("Отмененных заказов - $cancelledOrdersCount")

        val finishedOrderCount = orders.filter { it.status == OrderStatus.FINISHED }.size
        println("Завершенных заказов - $finishedOrderCount")
    }

    private fun showRevenue() {
        val orders = ordersDao.getAllOrders()
        val revenue = orders.filter { it.paid }.sumOf { it.allDishes.sumOf { it.price } }
        println("Выручка - $revenue")
    }

}