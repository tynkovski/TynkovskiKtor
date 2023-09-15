package com.tynkovski.di

import com.tynkovski.controller.AuthController
import com.tynkovski.controller.UserController
import com.tynkovski.data.datasource.KeyStoreDataSource
import com.tynkovski.data.datasource.UserDataSource
import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.token.TokenService
import org.koin.dsl.module

val controllerModule = module {
    single<UserController> {
        UserController(
            userDataSource = get<UserDataSource>(),
            keyStoreDataSource = get<KeyStoreDataSource>(),
            hashingService = get<HashingService>()
        )
    }

    single<AuthController> {
        AuthController(
            keyStoreDataSource = get<KeyStoreDataSource>(),
            userDataSource = get<UserDataSource>(),
            hashingService = get<HashingService>(),
            tokenService = get<TokenService>()
        )
    }
}