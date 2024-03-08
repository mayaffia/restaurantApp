package domain.controllers

import data.MenuDao
import data.OrdersDao
import data.UsersDao
import domain.entity.MenuItem
import domain.entity.Order
import domain.entity.OrderStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*


interface OrderController {

    fun createNewOrder(userId: UUID, allDishes: List<MenuItem>): Int

    fun getStatus(order: Order): OrderStatus

    fun payForOrder(order: Order)

    fun addItemsToOrder(orderId: UUID, items: List<MenuItem>)

    fun cancelOrder(order: Order)

    suspend fun processOrder(order: Order)

    fun makeReview(order: Order, mark: Int, review: String)
}

class OrderControllerImpl(
    private val usersDao: UsersDao,
    private val ordersDao: OrdersDao,
    private val notificationController: NotificationController, private val menuDao: MenuDao
) : OrderController {

    override fun createNewOrder(userId: UUID, allDishes: List<MenuItem>): Int {
        val users = usersDao.getAllUsers()
        users.find { it.id == userId } ?: return 0

        val order = Order(allDishes, userId)
        ordersDao.addNewOrder(order)

        return order.num
    }

    override fun getStatus(order: Order): OrderStatus {
        return order.status
    }

    override fun payForOrder(order: Order) {
        ordersDao.payForOrder(order)
    }

    override fun addItemsToOrder(orderId: UUID, items: List<MenuItem>) {
        val orders = ordersDao.getAllOrders()

        val order = orders.find { it.id == orderId } ?: return
        ordersDao.addItemsToOrder(order, items)
    }

    override fun cancelOrder(order: Order) {
        if (order.status != OrderStatus.FINISHED) {
            ordersDao.changeStatus(order, OrderStatus.CANCELLED)
            menuDao.increaseCountOfItems(order.allDishes)
        }
    }

    private val mutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    override suspend fun processOrder(order: Order) {
        coroutineScope.launch {
            mutex.withLock {
                ordersDao.changeStatus(order, OrderStatus.IN_PROCESS)
                delay(order.timeOfCooking * 1000L)

                val orderUpdated = ordersDao.getOrder(order.id) ?: return@launch

                var timeDelay = 0L
                if (orderUpdated.allDishes.size != order.allDishes.size) {
                    for (i in order.allDishes.size..<orderUpdated.allDishes.size) {
                        timeDelay += orderUpdated.allDishes[i].timeOfCooking * 1000L
                    }
                }

                delay(timeDelay)

                if (orderUpdated.status != OrderStatus.CANCELLED) {
                    ordersDao.changeStatus(orderUpdated, OrderStatus.FINISHED)
                    notificationController.addNotification(order)
                }
            }


        }
    }

    override fun makeReview(order: Order, mark: Int, review: String) {
        ordersDao.makeReview(order, mark, review)
    }

}