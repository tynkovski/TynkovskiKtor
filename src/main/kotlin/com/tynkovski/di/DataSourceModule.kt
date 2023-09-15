package com.tynkovski.di

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.tynkovski.data.datasource.*
import com.tynkovski.security.token.TokenService
import org.koin.dsl.module

val dataSourceModule = module {
    single<UserDataSource> { UserDataSourceImpl(get<MongoDatabase>()) }

    single<NoteDataSource> { NoteDataSourceImpl(get<MongoDatabase>()) }

    single<KeyStoreDataSource> { KeyStoreDataSourceImpl(get<MongoDatabase>(), get<TokenService>()) }
}