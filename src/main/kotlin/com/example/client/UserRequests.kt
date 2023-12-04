package com.example.client

import com.example.models.UserRequest
import com.example.models.UserResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object UserRequests {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        defaultRequest {
            url {
                host = "0.0.0.0"
                path("/")
                port = 8080
            }
        }
    }

    suspend fun getAllUsers(): List<UserResponse> {
        return try {
            val response: HttpResponse = client.get("/users")

            if (response.status == HttpStatusCode.OK) {
                response.body<List<UserResponse>>()
            } else {
                println("Failed to retrieve users. Status: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Error retrieving users: ${e.message}")
            emptyList()
        }
    }

    suspend fun createUser(user: UserRequest) {
        try {
            val response: HttpResponse = client.post("/users") {
                contentType(ContentType.Application.Json)
                setBody(user)
            }

            if (response.status == HttpStatusCode.Created) {
                println("User created successfully")
            } else {
                println("Failed to create user. Status: ${response.status}")
            }
        } catch (e: Exception) {
            println("Error creating user: ${e.message}")
        }
    }

    suspend fun getUserById(userId: Long): UserResponse? {
        val response: HttpResponse = client.get("/users/$userId")

        return if (response.status == HttpStatusCode.OK) {
            response.body<UserResponse>()
        } else {
            println("Failed to retrieve user. Status: ${response.status}")
            null
        }
    }

    suspend fun updateUserById(userId: Long, updatedUser: UserRequest) {
        val response: HttpResponse = client.patch("/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(updatedUser)
        }

        if (response.status == HttpStatusCode.NoContent) {
            println("User updated successfully")
        } else {
            println("Failed to update user. Status: ${response.status}")
        }
    }

    suspend fun deleteUserById(userId: Long) {
        val response: HttpResponse = client.delete("/users/$userId")

        if (response.status == HttpStatusCode.NoContent) {
            println("User deleted successfully")
        } else {
            println("Failed to delete user. Status: ${response.status}")
        }
    }
}