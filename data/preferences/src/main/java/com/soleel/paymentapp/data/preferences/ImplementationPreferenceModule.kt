package com.soleel.paymentapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.soleel.paymentapp.data.preferences.developer.DeveloperPreferences
import com.soleel.paymentapp.data.preferences.developer.IDeveloperPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


//const val DATASTORE_APP_PREFERENCES = "app_preferences"
const val DATASTORE_DEVELOPER_PREFERENCES = "developer_preferences"

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

//    @Singleton
//    @Provides
//    @Named("AppPreferences")
//    fun provideAppPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
//        return PreferenceDataStoreFactory.create(
//            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
//            produceFile = { context.preferencesDataStoreFile(DATASTORE_APP_PREFERENCES) }
//        )
//    }
//
//    @Singleton
//    @Provides
//    fun provideAppPreferences(
//        @Named("AppPreferences") dataStore: DataStore<Preferences>
//    ): IAppPreferences {
//        return ImplementationAppPreferences(dataStore)
//    }

    @Singleton
    @Provides
    @Named("DeveloperPreferences")
    fun provideDeveloperPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { context.preferencesDataStoreFile(DATASTORE_DEVELOPER_PREFERENCES) }
        )
    }

    @Singleton
    @Provides
    fun provideDeveloperPreferences(
        @Named("DeveloperPreferences") dataStore: DataStore<Preferences>
    ): IDeveloperPreferences {
        return DeveloperPreferences(dataStore)
    }
}