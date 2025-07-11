package com.example.voglioisoldi

import android.content.Context
import androidx.room.Room
import com.example.voglioisoldi.data.database.AppDatabase
import com.example.voglioisoldi.data.repositories.AccountRepository
import com.example.voglioisoldi.data.repositories.NotificationRepository
import com.example.voglioisoldi.data.repositories.SettingsRepository
import com.example.voglioisoldi.data.repositories.TransactionRepository
import com.example.voglioisoldi.data.repositories.UserRepository
import com.example.voglioisoldi.data.session.SessionManager
import com.example.voglioisoldi.data.session.dataStore
import com.example.voglioisoldi.ui.viewmodel.AccountManagementViewModel
import com.example.voglioisoldi.ui.viewmodel.AccountViewModel
import com.example.voglioisoldi.ui.viewmodel.AddTransactionViewModel
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import com.example.voglioisoldi.ui.viewmodel.ChangeEmailViewModel
import com.example.voglioisoldi.ui.viewmodel.ChangePasswordViewModel
import com.example.voglioisoldi.ui.viewmodel.DetailsViewModel
import com.example.voglioisoldi.ui.viewmodel.GeneralSettingsViewModel
import com.example.voglioisoldi.ui.viewmodel.GraphsViewModel
import com.example.voglioisoldi.ui.viewmodel.HomeViewModel
import com.example.voglioisoldi.ui.viewmodel.NotificationViewModel
import com.example.voglioisoldi.ui.viewmodel.ProfileViewModel
import com.example.voglioisoldi.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { get<Context>().dataStore }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "voglioisoldi_db"
        ).build()
    }
    single { get<AppDatabase>().userDAO() }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().accountDao() }
    single { get<AppDatabase>().notificationDao() }
    single { UserRepository(get()) }
    single { TransactionRepository(get()) }
    single { AccountRepository(get()) }
    single { SettingsRepository(get()) }
    single { NotificationRepository(get()) }

    single { SessionManager(androidContext()) }

    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { AddTransactionViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { GeneralSettingsViewModel(get(), get()) }
    viewModel { AccountManagementViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
    viewModel { AccountViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { ChangeEmailViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { GraphsViewModel(get(), get(), get(), get()) }
}