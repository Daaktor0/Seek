package com.seek.app.data.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the database encryption passphrase using Android Keystore.
 * 
 * The passphrase is:
 * 1. Generated ONCE as random bytes
 * 2. Encrypted with an AES key stored in Android Keystore
 * 3. Stored encrypted in SharedPreferences
 * 4. Decrypted on each app launch using the Keystore key
 * 
 * This ensures the passphrase is deterministic across launches.
 */
@Singleton
class PassphraseManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val KEYSTORE_ALIAS = "seek_db_key_v2"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val PASSPHRASE_PREFS = "seek_passphrase_prefs"
        private const val ENCRYPTED_PASSPHRASE_KEY = "encrypted_passphrase_v2"
        private const val PASSPHRASE_IV_KEY = "passphrase_iv_v2"
        private const val GCM_TAG_LENGTH = 128
    }
    
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PASSPHRASE_PREFS, Context.MODE_PRIVATE)
    }
    
    /**
     * Get or generate the database passphrase.
     * Uses Android Keystore for encryption and SharedPreferences for storage.
     */
    fun getPassphrase(): ByteArray {
        // Try to retrieve existing passphrase
        val encryptedData = prefs.getString(ENCRYPTED_PASSPHRASE_KEY, null)
        val ivData = prefs.getString(PASSPHRASE_IV_KEY, null)
        
        return if (encryptedData != null && ivData != null) {
            try {
                decryptPassphrase(
                    Base64.decode(encryptedData, Base64.NO_WRAP),
                    Base64.decode(ivData, Base64.NO_WRAP)
                )
            } catch (e: Exception) {
                // Decryption failed - key might have changed, generate new passphrase
                // This will cause DB to be recreated (handled by SeekDatabase recovery)
                generateAndStoreNewPassphrase()
            }
        } else {
            // First launch - generate new passphrase
            generateAndStoreNewPassphrase()
        }
    }
    
    private fun generateAndStoreNewPassphrase(): ByteArray {
        val passphrase = ByteArray(32)
        java.security.SecureRandom().nextBytes(passphrase)
        
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val encryptedData = cipher.doFinal(passphrase)
        val iv = cipher.iv
        
        prefs.edit()
            .putString(ENCRYPTED_PASSPHRASE_KEY, Base64.encodeToString(encryptedData, Base64.NO_WRAP))
            .putString(PASSPHRASE_IV_KEY, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()
        
        return passphrase
    }
    
    private fun decryptPassphrase(encryptedData: ByteArray, iv: ByteArray): ByteArray {
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedData)
    }
    
    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        
        return if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
            keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
        } else {
            generateKey()
        }
    }
    
    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keySpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        
        keyGenerator.init(keySpec)
        return keyGenerator.generateKey()
    }
    
    /**
     * Clear the encryption key and stored passphrase (for complete data wipe).
     */
    fun clearKey() {
        try {
            // Clear stored passphrase
            prefs.edit()
                .remove(ENCRYPTED_PASSPHRASE_KEY)
                .remove(PASSPHRASE_IV_KEY)
                .apply()
            
            // Clear keystore key
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
            keyStore.load(null)
            if (keyStore.containsAlias(KEYSTORE_ALIAS)) {
                keyStore.deleteEntry(KEYSTORE_ALIAS)
            }
        } catch (e: Exception) {
            // Log error but don't crash
        }
    }
}
