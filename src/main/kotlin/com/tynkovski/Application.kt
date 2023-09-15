package com.tynkovski

import com.tynkovski.plugin.configureKoin
import com.tynkovski.plugin.configureMonitoring
import com.tynkovski.plugin.configureSerialization
import com.tynkovski.plugin.configureSecurity
import com.tynkovski.plugin.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    configureKoin()
    configureMonitoring()
    configureSerialization()
    configureSecurity()
    configureRouting()
}
