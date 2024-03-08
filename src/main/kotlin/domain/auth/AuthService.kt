package domain.auth

import data.UsersDao
import domain.entity.Role
import domain.entity.User
import presentation.model.OutputModel
import java.security.MessageDigest
import kotlin.random.Random


sealed class Result

data object Success : Result()
class Error(val outputModel: OutputModel) : Result()


interface AuthService {
    fun checkLogin(login : String) : Result

    fun registerNewUser(login : String, password: String, role: Role)

    fun generateSalt() : ByteArray

    fun hashPassword(password: String, salt : ByteArray) : String
}

class AuthServiceImpl(private val usersDao: UsersDao) : AuthService{

    override fun checkLogin(login : String) : Result {
        val users = usersDao.getAllUsers()
        val log = users.find { it.login == login }
        return when {
            log == null -> Error(OutputModel("Пользователя с таким логином не существует"))
            else -> Success
        }
    }


    override fun registerNewUser(login : String, password: String, role: Role) {
        val salt = generateSalt()
        val hashedPassword = hashPassword(password, salt)
        val user = User(login, role)
        user.salt = salt
        user.hashedPassword = hashedPassword
        usersDao.addUser(user)

        usersDao.setCurrentUser(login, user.id, user.hashedPassword, user.salt)

    }

    override fun generateSalt() : ByteArray {
        val salt = ByteArray(16)
        Random.nextBytes(salt)
        return salt
    }

    override fun hashPassword(password: String, salt : ByteArray) : String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val hashedBytes = md.digest(password.toByteArray())

        return with(StringBuilder()) {
            hashedBytes.forEach { b -> append(String.format("%02X", b)) }
            toString()
        }
    }

}

