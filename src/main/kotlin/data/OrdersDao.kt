package data

import domain.entity.MenuItem
import domain.entity.Order
import domain.entity.OrderStatus
import repository.OrdersJsonRepository
import java.util.*

interface OrdersDao {
    fun getAllOrders(): List<Order>

    fun addNewOrder(order: Order)

    fun deleteOrder(order: Order)

    fun getOrder(id: UUID): Order?

    fun addItemsToOrder(order: Order, items: List<MenuItem>)

    fun getOrderWithStatus(status: OrderStatus): List<Order>

    fun changeStatus(order: Order, status: OrderStatus)

    fun makeReview(order: Order, mark: Int, review: String)

    fun payForOrder(order: Order)
}


class OrdersDaoImpl(private val path: String) : OrdersDao {

    private val jsonO = OrdersJsonRepository()

    private val orders = jsonO.loadFromFile(path)
    private var counter = if (orders.isEmpty()) {
        1
    } else {
        orders.last().num + 1
    }

    override fun getAllOrders(): List<Order> {
        return jsonO.loadFromFile(path)
    }

    override fun addNewOrder(order: Order) {
        val orders = jsonO.loadFromFile(path)

        if (order.status == OrderStatus.NEW) {
            order.num = counter++
        }

        order.timeOfCooking = order.allDishes.sumOf { it.timeOfCooking }

        val temp = orders.toMutableList()
        temp.add(order)

        jsonO.saveToFile(temp, path)
    }

    override fun deleteOrder(order: Order) {
        val orders = jsonO.loadFromFile(path)

        val temp = orders.toMutableList()
        temp.remove(order)

        jsonO.saveToFile(temp, path)
    }

    override fun getOrder(id: UUID): Order? {
        val orders = jsonO.loadFromFile(path)
        return orders.find { it.id == id }
    }

    override fun addItemsToOrder(order: Order, items: List<MenuItem>) {
        val orders = jsonO.loadFromFile(path)
        val or = orders.find { it.id == order.id } ?: return

        if (or.status != OrderStatus.IN_PROCESS) {
            println("нельзя добавить блюда в заказ, т.к. он не находится в обработке")
            return
        }

        val temp = or.allDishes.toMutableList()
        for (el in items) {
            temp.add(el)
        }

        or.allDishes = temp

        jsonO.saveToFile(orders, path)
    }

    override fun getOrderWithStatus(status: OrderStatus): List<Order> {
        val orders = jsonO.loadFromFile(path)
        return orders.filter { it.status == status }
    }

    override fun changeStatus(order: Order, status: OrderStatus) {
        val orders = jsonO.loadFromFile(path)

        val or = orders.find { it.id == order.id } ?: return
        or.status = status

        jsonO.saveToFile(orders, path)
    }

    override fun makeReview(order: Order, mark: Int, review: String) {
        val orders = jsonO.loadFromFile(path)
        val or = orders.find { it.id == order.id } ?: return

        or.reviewMark = mark
        or.review = review

        jsonO.saveToFile(orders, path)
    }

    override fun payForOrder(order: Order) {
        val orders = jsonO.loadFromFile(path)
        val or = orders.find { it.id == order.id } ?: return

        or.paid = true

        jsonO.saveToFile(orders, path)
    }
}