package com.seek.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper for generating export filenames with timestamps.
 */
object ExportFilenames {
    
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
    
    /**
     * Generate timestamped JSON export filename.
     * Example: seek_export_2025-12-25_14-30-45.json
     */
    fun jsonFilename(): String {
        val timestamp = timestampFormat.format(Date())
        return "seek_export_$timestamp.json"
    }
    
    /**
     * Generate timestamped CSV export filename.
     * Example: seek_export_2025-12-25_14-30-45.csv
     */
    fun csvFilename(): String {
        val timestamp = timestampFormat.format(Date())
        return "seek_export_$timestamp.csv"
    }
    
    /**
     * Generate timestamped backup filename for offboarding.
     * Example: seek_backup_2025-12-25_14-30-45.json
     */
    fun backupFilename(): String {
        val timestamp = timestampFormat.format(Date())
        return "seek_backup_$timestamp.json"
    }
}
