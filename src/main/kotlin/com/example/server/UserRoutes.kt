package com.example.server

import com.example.models.User
import com.example.models.UserErrorResponse
import com.example.models.UserRequest
import com.example.models.UserResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


private fun User?.toUserResponse(): UserResponse? =
    this?.let { UserResponse(it.userId!!, it.userName) }

fun Application.configureUserRoutes() {
    routing {
        route("/users") {
            val userService = UserService()
            createUser(userService)
            getAllUsersRoute(userService)
            getUserByIdRoute(userService)
            updateUserByIdRoute(userService)
            deleteUserByIdRoute(userService)
        }
    }
}

fun Route.createUser(userService: UserService) {
    post {

        val request = call.receive<UserRequest>()

        val success = userService.createUser(userRequest = request)

        if (success)
            call.respond(HttpStatusCode.Created)
        else
            call.respond(HttpStatusCode.BadRequest, UserErrorResponse("Cannot create user"))
    }
}

fun Route.getAllUsersRoute(userService: UserService) {
    get {
        val users = userService.findAllUsers()
            .map(User::toUserResponse)

        call.respond(message = users)
    }
}

fun Route.getUserByIdRoute(userService: UserService) {
    get("/{userId}") {
        val userId: Long = call.parameters["userId"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, UserErrorResponse("Invalid id"))

        userService.findUserById(userId)
            ?.let { foundUser -> foundUser.toUserResponse() }
            ?.let { response -> call.respond(response) }
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                UserErrorResponse("com.example.User with id [$userId] not found")
            )
    }
}

fun Route.updateUserByIdRoute(userService: UserService) {
    patch("/{userId}") {
        val userId: Long = call.parameters["userId"]?.toLongOrNull()
            ?: return@patch call.respond(HttpStatusCode.BadRequest, UserErrorResponse("Invalid id"))

        val request = call.receive<UserRequest>()
        val success = userService.updateUserById(userId, request)

        if (success)
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, UserErrorResponse("Cannot update user with id [$userId]"))
    }
}

fun Route.deleteUserByIdRoute(userService: UserService) {
    delete("/{userId}") {
        val userId: Long = call.parameters["userId"]?.toLongOrNull()
            ?: return@delete call.respond(HttpStatusCode.BadRequest, UserErrorResponse("Invalid id"))

        val success = userService.deleteUserById(userId)

        if (success)
            call.respond(HttpStatusCode.NoContent)
        else
            call.respond(HttpStatusCode.BadRequest, UserErrorResponse("Cannot delete user with id [$userId]"))
    }
}