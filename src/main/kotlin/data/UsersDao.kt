package data

import domain.entity.CurrentUser
import domain.entity.Role
import domain.entity.User
import repository.UsersJsonRepository
import java.util.*

interface UsersDao {
    fun getAllUsers() : List<User>

    fun addUser(user: User)

    fun getAdmins() : List<User>

    fun getVisitors() : List<User>

    fun setCurrentUser(login : String, id : UUID, hashPass : String, salt : ByteArray)

    fun getCurrentUser() : CurrentUser
}

class UsersDaoImpl(private val path : String) : UsersDao {

    private val jsonU = UsersJsonRepository()

    override fun getAllUsers() : List<User> {
        return jsonU.loadFromFile(path)
    }

    override fun addUser(user : User) {
        val users = jsonU.loadFromFile(path)

        val temp = users.toMutableList()
        temp.add(user)

        jsonU.saveToFile(temp, path)

    }

    override fun getAdmins(): List<User> {
        val users = jsonU.loadFromFile(path)
        return users.filter{it.role == Role.ADMIN}
    }

    override fun getVisitors(): List<User> {
        val users = jsonU.loadFromFile(path)
        return users.filter{it.role == Role.VISITOR}
    }

    override fun setCurrentUser(login : String, id : UUID, hashPass : String, salt : ByteArray) {
        CurrentUser.id = id
        CurrentUser.login = login
        CurrentUser.hashedPassword = hashPass
        CurrentUser.salt = salt
    }

    override fun getCurrentUser(): CurrentUser {
        return CurrentUser
    }

}