package com.seek.app.domain.usecase

import android.content.Context
import android.net.Uri
import com.seek.app.data.repository.ApplicationRepository
import javax.inject.Inject

/**
 * Use case for exporting application data.
 * Supports JSON and CSV formats.
 */
class ExportDataUseCase @Inject constructor(
    private val repository: ApplicationRepository
) {
    /**
     * Export all applications to JSON format.
     */
    suspend fun exportToJson(): String {
        return repository.exportToJson()
    }
    
    /**
     * Export all applications to CSV format.
     */
    suspend fun exportToCsv(): String {
        return repository.exportToCsv()
    }
    
    /**
     * Export to JSON and write to a URI.
     */
    suspend fun exportToJsonUri(context: Context, uri: Uri): Result<Unit> {
        return try {
            val json = repository.exportToJson()
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray(Charsets.UTF_8))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Export to CSV and write to a URI.
     */
    suspend fun exportToCsvUri(context: Context, uri: Uri): Result<Unit> {
        return try {
            val csv = repository.exportToCsv()
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csv.toByteArray(Charsets.UTF_8))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
