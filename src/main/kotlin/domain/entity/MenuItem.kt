package domain.entity

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class MenuItem(
    val name : String,
    var price : Int,
    var timeOfCooking : Int,
    var count : Int
    ) {
    @Serializable(with = UUIDSerializer::class)
    val id = UUID.randomUUID()
}