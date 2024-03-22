package domain.entity

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Order(
    var allDishes: List<MenuItem>,
    @Serializable(with = UUIDSerializer::class)
    val userId : UUID,
    ) {


    @Serializable(with = UUIDSerializer::class)
    val id = UUID.randomUUID()
    var timeOfCooking : Int = 0
    var status = OrderStatus.NEW
    var paid = false
    var reviewMark : Int = 0
    var review : String = ""
    var num : Int = 0
}
