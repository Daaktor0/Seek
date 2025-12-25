package com.seek.app.data.database

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.seek.app.data.model.ApplicationEntity
import com.seek.app.data.model.MilestoneEntity
import com.seek.app.data.model.ReminderEntity
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.io.File

/**
 * Room database with SQLCipher encryption.
 * All user data is encrypted at rest.
 * 
 * Uses sqlcipher-android (>= 4.6.1) for 16KB page size compatibility.
 * 
 * Includes crash recovery: if the database file is corrupted or encrypted
 * with a different key, it will be renamed to .bad and recreated.
 */
@Database(
    entities = [
        ApplicationEntity::class,
        MilestoneEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SeekDatabase : RoomDatabase() {
    
    abstract fun applicationDao(): ApplicationDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun reminderDao(): ReminderDao
    
    companion object {
        private const val TAG = "SeekDatabase"
        private const val DATABASE_NAME = "seek_database.db"
        
        @Volatile
        private var INSTANCE: SeekDatabase? = null
        
        /**
         * Get or create the encrypted database instance.
         * 
         * Includes crash recovery: if the database cannot be opened (wrong key,
         * corrupted, plaintext), it will be backed up and recreated.
         * 
         * @param context Application context
         * @param passphrase The encryption passphrase from PassphraseManager.
         */
        fun getInstance(context: Context, passphrase: ByteArray): SeekDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabaseSafely(context, passphrase).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabaseSafely(context: Context, passphrase: ByteArray): SeekDatabase {
            val db = buildDatabase(context, passphrase)
            
            // Verify the database can be opened
            return try {
                // Force open to trigger decryption
                db.openHelper.writableDatabase
                Log.d(TAG, "Database opened successfully")
                db
            } catch (e: Exception) {
                Log.e(TAG, "Database open failed: ${e.message}", e)
                
                // Check if it's a "file is not a database" error
                if (isNotADatabaseError(e)) {
                    Log.w(TAG, "Attempting database recovery...")
                    
                    // Close the failed instance
                    try {
                        db.close()
                    } catch (closeEx: Exception) {
                        Log.w(TAG, "Error closing failed database", closeEx)
                    }
                    
                    // Backup the corrupted database file
                    backupCorruptedDatabase(context)
                    
                    // Delete the corrupted database
                    context.deleteDatabase(DATABASE_NAME)
                    
                    // Rebuild fresh database
                    val freshDb = buildDatabase(context, passphrase)
                    
                    // Verify the fresh database opens
                    try {
                        freshDb.openHelper.writableDatabase
                        Log.d(TAG, "Fresh database created successfully after recovery")
                        freshDb
                    } catch (retryEx: Exception) {
                        Log.e(TAG, "Fresh database also failed to open", retryEx)
                        throw retryEx
                    }
                } else {
                    throw e
                }
            }
        }
        
        private fun buildDatabase(context: Context, passphrase: ByteArray): SeekDatabase {
            val factory = SupportOpenHelperFactory(passphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                SeekDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }
        
        private fun isNotADatabaseError(e: Exception): Boolean {
            val message = e.message?.lowercase() ?: ""
            return e is SQLiteException && 
                   (message.contains("file is not a database") ||
                    message.contains("file is encrypted") ||
                    message.contains("code 26"))
        }
        
        private fun backupCorruptedDatabase(context: Context) {
            try {
                val dbFile = context.getDatabasePath(DATABASE_NAME)
                if (dbFile.exists()) {
                    val backupFile = File(dbFile.parentFile, "$DATABASE_NAME.bad")
                    // Delete old backup if exists
                    if (backupFile.exists()) {
                        backupFile.delete()
                    }
                    // Rename corrupted file
                    val renamed = dbFile.renameTo(backupFile)
                    Log.d(TAG, "Corrupted database backed up: $renamed to ${backupFile.absolutePath}")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to backup corrupted database", e)
            }
        }
        
        /**
         * Clear the database instance (for testing or data wipe).
         */
        fun clearInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}
