package di

import data.*
import domain.auth.AuthService
import domain.auth.AuthServiceImpl
import domain.controllers.*
import presentation.ConsoleManager
import presentation.ConsoleManagerForAdmin
import presentation.ConsoleManagerForVisitor
import presentation.EntryConsoleManager


object DI {
    private const val PATH_FOR_MENU = "menu.json"
    private const val PATH_FOR_ORDERS = "orders.json"
    private const val PATH_FOR_USERS = "users.json"

    private val menuDao: MenuDao = MenuDaoImpl(PATH_FOR_MENU)

    private val ordersDao: OrdersDao = OrdersDaoImpl(PATH_FOR_ORDERS)

    private val usersDao: UsersDao = UsersDaoImpl(PATH_FOR_USERS)

    val notificationController: NotificationController = NotificationControllerImpl()

    private val orderController: OrderController =
        OrderControllerImpl(usersDao, ordersDao, notificationController, menuDao)


    private val authService: AuthService = AuthServiceImpl(usersDao)

    private val consoleManagerForVisitor: ConsoleManager =
        ConsoleManagerForVisitor(menuDao, ordersDao, usersDao, orderController)

    private val consoleManagerForAdmin: ConsoleManager =
        ConsoleManagerForAdmin(menuDao, ordersDao, usersDao, orderController)

    val entryConsoleManager: EntryConsoleManager = EntryConsoleManager(
        authService, consoleManagerForAdmin, consoleManagerForVisitor,
        usersDao
    )
}



