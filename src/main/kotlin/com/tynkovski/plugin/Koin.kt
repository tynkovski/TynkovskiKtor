package com.tynkovski.plugin

import com.tynkovski.di.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(
            tokenModule,
            databaseModule,
            dataSourceModule,
            hashingModule,
            serializationModule,
            controllerModule
        )
    }
}