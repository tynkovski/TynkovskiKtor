package com.tynkovski.route

import com.tynkovski.controller.UserController
import com.tynkovski.data.datasource.UserDataSource
import com.tynkovski.data.mapper.userMapper
import com.tynkovski.model.request.EditUserRequest
import com.tynkovski.model.request.ChangePasswordRequest
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.hashing.SaltedHash
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(controller: UserController) {
    route("/user") {
        post("/register") { controller.register(call) }
        authenticate {
            get("/get") { controller.getUser(call) }
            delete("/delete") { controller.deleteUser(call) }
            post("/edit") { controller.editUser(call) }
            post("/changePassword") { controller.changePassword(call) }
        }
    }
}