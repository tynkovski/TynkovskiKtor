package com.tynkovski.route

import com.tynkovski.controller.AuthController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.authRoute(controller: AuthController) {
    route("/auth") {
        post("/login") { controller.login(call) }
        post("/refreshToken") { controller.refreshToken(call) }

        authenticate {
            post("/logout") { controller.logout(call) }
        }
    }
}