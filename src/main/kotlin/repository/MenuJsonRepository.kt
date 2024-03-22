package repository

import domain.entity.MenuItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MenuJsonRepository : JsonRepository<MenuItem>() {
    private val json = Json { prettyPrint = true }

    override fun serialize(data: List<MenuItem>): String {
        return json.encodeToString(data)
    }

    override fun deserialize(data: String): List<MenuItem> {
        return json.decodeFromString(data)
    }

}

