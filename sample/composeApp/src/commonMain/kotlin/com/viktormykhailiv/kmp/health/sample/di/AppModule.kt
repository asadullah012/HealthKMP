package com.viktormykhailiv.kmp.health.sample.di

import com.viktormykhailiv.kmp.health.HealthManager
import com.viktormykhailiv.kmp.health.HealthManagerFactory
import com.viktormykhailiv.kmp.health.sample.data.HealthRepositoryImpl
import com.viktormykhailiv.kmp.health.sample.domain.HealthRepository
import com.viktormykhailiv.kmp.health.sample.presentation.HomeScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    single { HealthManagerFactory().createManager() }

    single<HealthRepository> { (healthManager: HealthManager) ->
        HealthRepositoryImpl(healthManager)
    }

    viewModel { (healthManager: HealthManager) ->
        HomeScreenViewModel(get<HealthRepository> { parametersOf(healthManager) })
    }
}