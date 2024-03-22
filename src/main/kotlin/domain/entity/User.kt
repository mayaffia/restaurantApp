package domain.entity

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    val login : String,
    val role : Role
    ) {

    @Serializable(with = UUIDSerializer::class)
    val id = UUID.randomUUID()
    var salt = ByteArray(16)
    var hashedPassword = String()
    var loggedIn = false //maybe delete
}

object CurrentUser {
    lateinit var id : UUID
    lateinit var login : String
    lateinit var hashedPassword: String
    lateinit var salt : ByteArray
}