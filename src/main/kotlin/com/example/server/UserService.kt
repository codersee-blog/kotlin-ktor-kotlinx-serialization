package com.example.server

import com.example.models.User
import com.example.models.UserRequest
import com.example.models.Users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toSet

class UserService {

    private val database = Database.connect(
        url = "jdbc:postgresql://localhost:5438/postgres",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )

    fun createUser(userRequest: UserRequest): Boolean {
        val newUser = User { userName = userRequest.userName }
        val affectedRecordsNumber =
            database.sequenceOf(Users)
                .add(newUser)
        return affectedRecordsNumber == 1
    }

    fun findAllUsers(): Set<User> = database.sequenceOf(Users).toSet()

    fun findUserById(userId: Long): User? =
        database.sequenceOf(Users)
            .find { user -> Users.userId eq userId }

    fun updateUserById(userId: Long, userRequest: UserRequest): Boolean {
        val foundUser = findUserById(userId)
        foundUser?.userName = userRequest.userName

        val affectedRecordsNumber = foundUser?.flushChanges()

        return affectedRecordsNumber == 1
    }

    fun deleteUserById(userId: Long): Boolean {
        val foundUser = findUserById(userId)

        val affectedRecordsNumber = foundUser?.delete()

        return affectedRecordsNumber == 1
    }
}
