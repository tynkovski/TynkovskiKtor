package com.tynkovski.plugin

import com.tynkovski.controller.AuthController
import com.tynkovski.controller.UserController
import com.tynkovski.route.authRoute
import com.tynkovski.route.userRoute
import io.ktor.server.application.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authController by inject<AuthController>()
    val userController by inject<UserController>()

    routing {
        // region Swagger
        swaggerUI(path = "swagger", swaggerFile = "swagger.yaml") // https://editor.swagger.io/
        // endregion

        authRoute(authController)
        userRoute(userController)
    }
}
