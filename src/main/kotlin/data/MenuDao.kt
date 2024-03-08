package data

import domain.entity.MenuItem
import repository.MenuJsonRepository
import java.util.*

sealed class Result

data object Success : Result()
data object Error : Result()

interface MenuDao {
    fun getMenu(): List<MenuItem>

    fun addToMenu(menuItem: MenuItem)

    fun deleteFromMenu(menuItem: MenuItem)

    fun changeCountOfMenuItem(id: UUID, newCount: Int)

    fun changePriceOfMenuItem(id: UUID, newPrice: Int)

    fun changeTimeOfMenuItem(id: UUID, newTime: Int)

    fun decreaseCountOfItems(items: List<MenuItem>): Result

    fun increaseCountOfItems(items: List<MenuItem>)
}

class MenuDaoImpl(private val path: String) : MenuDao {

    private val jsonM = MenuJsonRepository()


    override fun getMenu(): List<MenuItem> {
        return jsonM.loadFromFile(path)
    }

    override fun addToMenu(menuItem: MenuItem) {
        val menu = jsonM.loadFromFile(path)

        val temp = menu.toMutableList()
        temp.add(menuItem)

        jsonM.saveToFile(temp, path)
    }

    override fun deleteFromMenu(menuItem: MenuItem) {
        val menu = jsonM.loadFromFile(path)

        val temp = menu.toMutableList()
        temp.remove(menuItem)

        jsonM.saveToFile(temp, path)
    }

    override fun changeCountOfMenuItem(id: UUID, newCount: Int) {
        val menu = jsonM.loadFromFile(path)

        val item = menu.find { it.id == id } ?: return
        item.count = newCount

        jsonM.saveToFile(menu, path)
    }

    override fun changePriceOfMenuItem(id: UUID, newPrice: Int) {
        val menu = jsonM.loadFromFile(path)

        val item = menu.find { it.id == id } ?: return
        item.price = newPrice

        jsonM.saveToFile(menu, path)
    }

    override fun changeTimeOfMenuItem(id: UUID, newTime: Int) {
        val menu = jsonM.loadFromFile(path)

        val item = menu.find { it.id == id } ?: return
        item.timeOfCooking = newTime

        jsonM.saveToFile(menu, path)
    }

    override fun decreaseCountOfItems(items: List<MenuItem>): Result {
        val menu = jsonM.loadFromFile(path)

        for (el in items) {
            val item = menu.find { it.id == el.id } ?: return Error
            if (item.count - 1 >= 0) {
                item.count--
            } else {
                return Error
            }
        }

        jsonM.saveToFile(menu, path)
        return Success
    }

    override fun increaseCountOfItems(items: List<MenuItem>) {
        val menu = jsonM.loadFromFile(path)

        for (el in items) {
            val item = menu.find { it.id == el.id } ?: return
            item.count++
        }

        jsonM.saveToFile(menu, path)
    }
}