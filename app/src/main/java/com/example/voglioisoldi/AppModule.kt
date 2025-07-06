package com.example.voglioisoldi

import androidx.room.Room
import com.example.voglioisoldi.data.database.AppDatabase
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.session.SessionManager
import com.example.voglioisoldi.ui.viewmodel.AddTransactionViewModel
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "voglioisoldi_db"
        ).build()
    }
    single { get<AppDatabase>().userDAO() }
    single { UserRepository(get()) }
    single { SessionManager(androidContext()) }

    viewModel { AuthViewModel(get(), get()) }
    viewModel { AddTransactionViewModel(get()) }
}