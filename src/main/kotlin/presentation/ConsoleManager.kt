package presentation

import data.MenuDao
import data.OrdersDao
import data.UsersDao
import domain.controllers.OrderController

abstract class ConsoleManager(val menuDao: MenuDao,
                              val ordersDao: OrdersDao,
                              val usersDao: UsersDao, val orderController: OrderController
) {
    fun showMenu() {
        val menu = menuDao.getMenu()
        for (item in menu) {
            val name = item.name
            val price = item.price
            val time = item.timeOfCooking
            val count = item.count
            println("Название: $name , цена: $price, время приготовления: $time, количество: $count")
        }
    }

    abstract fun showOperations()

    abstract suspend fun operationsHandler()

}




