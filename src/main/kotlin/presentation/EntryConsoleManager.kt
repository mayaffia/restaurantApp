package presentation

import data.UsersDao
import domain.auth.AuthService
import domain.auth.Error
import domain.auth.Success
import domain.entity.Role

class EntryConsoleManager(
    private val authService: AuthService,
    private val consoleManagerForAdmin: ConsoleManager,
    private val consoleManagerForVisitor: ConsoleManager,
    private val usersDao: UsersDao
) {

    suspend fun start() {
        println("Выберите операцию. Нажмите:")
        println("1 - войти")
        println("2 - зарегистрироваться")

        var oper = "3"
        while (oper != "1" && oper != "2") {
            oper = readln()
            when (oper) {
                "1" -> logIn()
                "2" -> register()
                else -> println("Нет такой операции. Повторите ввод")
            }
        }

    }

    private suspend fun register() {
        println("Если вы посетитель нажмите 0, если администартор нажмите 1")
        val r = readln().toInt()
        println("Введите логин")
        val login = readln()

        when (authService.checkLogin(login)) {
            is Error -> {}
            Success -> {
                println("Пользователь уже зарегистрирован")
                start()
                return
            }
        }

        println("Введите пароль")
        val password = readln()

        if (r == 0) {
            authService.registerNewUser(login, password, Role.VISITOR)  //role!!
        } else {
            authService.registerNewUser(login, password, Role.ADMIN)  //role!!!
        }

        println("Вы успешно зарегистрировались")

        if (r == 0) {
            consoleManagerForVisitor.operationsHandler()
        } else {
            consoleManagerForAdmin.operationsHandler()
        }
    }

    private suspend fun logIn() {
        println("Введите логин")
        val login = readln()
        when (val checkResult = authService.checkLogin(login)) {
            is Error -> {
                println(checkResult.outputModel.message)
                start()
                return
            }
            Success -> {}
        }

        val user = usersDao.getAllUsers().find { it.login == login } ?: return

        println("Введите пароль")
        val password = readln()

        val hashedPass = authService.hashPassword(password, user.salt)

        if (hashedPass != user.hashedPassword) {
            println("Неверный пароль")
            start()
            return
        }
        user.loggedIn = true
        println("Вход успешно выполнен")

        usersDao.setCurrentUser(login, user.id, user.hashedPassword, user.salt)

        if (user.role == Role.VISITOR) {
            consoleManagerForVisitor.operationsHandler()
        } else {
            consoleManagerForAdmin.operationsHandler()
        }
    }
}