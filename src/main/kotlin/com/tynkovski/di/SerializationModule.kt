package com.tynkovski.di

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
val serializationModule = module{
    single<Json> {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = false
            prettyPrint = true
            coerceInputValues = true
            encodeDefaults = true
            allowStructuredMapKeys = true
            explicitNulls = true
        }
    }
}