package repository

import domain.entity.Order
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OrdersJsonRepository : JsonRepository<Order>() {
    private val json = Json { prettyPrint = true }

    override fun serialize(data: List<Order>): String {
        return json.encodeToString(data)
    }

    override fun deserialize(data: String): List<Order> {
        return json.decodeFromString(data)
    }
}