package com.seek.app.di

import android.content.Context
import com.seek.app.data.database.ApplicationDao
import com.seek.app.data.database.MilestoneDao
import com.seek.app.data.database.ReminderDao
import com.seek.app.data.database.SeekDatabase
import com.seek.app.data.security.PassphraseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun providePassphraseManager(
        @ApplicationContext context: Context
    ): PassphraseManager {
        return PassphraseManager(context)
    }
    
    @Provides
    @Singleton
    fun provideSeekDatabase(
        @ApplicationContext context: Context,
        passphraseManager: PassphraseManager
    ): SeekDatabase {
        return SeekDatabase.getInstance(
            context = context,
            passphrase = passphraseManager.getPassphrase()
        )
    }
    
    @Provides
    @Singleton
    fun provideApplicationDao(database: SeekDatabase): ApplicationDao {
        return database.applicationDao()
    }
    
    @Provides
    @Singleton
    fun provideMilestoneDao(database: SeekDatabase): MilestoneDao {
        return database.milestoneDao()
    }
    
    @Provides
    @Singleton
    fun provideReminderDao(database: SeekDatabase): ReminderDao {
        return database.reminderDao()
    }
}
