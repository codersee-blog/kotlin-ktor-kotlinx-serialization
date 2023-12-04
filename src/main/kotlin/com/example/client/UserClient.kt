package com.example.client

import com.example.models.UserRequest


import kotlinx.coroutines.runBlocking

suspend fun main() {
    runBlocking {
        val newUser = UserRequest(
            userName = "Alice"
        )

        UserRequests.createUser(newUser)

        val allUsers = UserRequests.getAllUsers()
        println("All Users: $allUsers")

        val userIdToRetrieve = 1L
        val retrievedUser = UserRequests.getUserById(userIdToRetrieve)
        println("User with ID $userIdToRetrieve: $retrievedUser")

        val userIdToUpdate = 1L
        val updatedUser = UserRequest(
            userName = "Bob"
        )
        UserRequests.updateUserById(userIdToUpdate, updatedUser)

        val userIdToDelete = 1L
        UserRequests.deleteUserById(userIdToDelete)
    }
}
