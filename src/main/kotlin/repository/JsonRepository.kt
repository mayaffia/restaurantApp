package repository

import java.io.File

abstract class JsonRepository<T> {
    abstract fun serialize(data: List<T>): String

    abstract fun deserialize(data: String): List<T>

    fun saveToFile(data: List<T>, path: String) {
        val file = File(path)
        val jsonData = serialize(data)
        file.writeText(jsonData)
    }

    fun loadFromFile(path: String): List<T> {
        val file = File(path)
        val data = file.readText()
        return deserialize(data)
    }
}


