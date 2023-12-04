package com.example.models

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    val userId: Long?
    var userName: String
}

object Users : Table<User>("users") {
    val userId = long("userid").primaryKey().bindTo(User::userId)
    val userName = varchar("username").bindTo(User::userName)
}

@Serializable
data class UserResponse(
    val userId: Long,
    val userName: String
)

@Serializable
data class UserRequest(
    val userName: String
)

@Serializable
data class UserErrorResponse(val message: String)