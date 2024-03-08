package repository

import domain.entity.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UsersJsonRepository : JsonRepository<User>() {
    private val json = Json { prettyPrint = true }

    override fun serialize(data: List<User>): String {
        return json.encodeToString(data)
    }

    override fun deserialize(data: String): List<User> {
        return json.decodeFromString(data)
    }
}