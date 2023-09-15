package com.tynkovski.di

import com.mongodb.ConnectionString
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<MongoDatabase> {
        val connectionString = ConnectionString("mongodb://localhost:27017")
        val databaseName = "tynkovski_ktor_sample"

        MongoClient.create(connectionString).getDatabase(databaseName)
    }
}