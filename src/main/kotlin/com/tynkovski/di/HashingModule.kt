package com.tynkovski.di

import com.tynkovski.security.hashing.HashingService
import com.tynkovski.security.hashing.SHA256HashingService
import org.koin.dsl.module

val hashingModule = module {
    single<HashingService> { SHA256HashingService() }
}