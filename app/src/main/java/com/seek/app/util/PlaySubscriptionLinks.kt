package com.seek.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Helper for Google Play subscription management links.
 * Per Play Billing best practices, apps should provide easy access to subscription management.
 */
object PlaySubscriptionLinks {
    
    // TODO: Replace with actual subscription product ID when Play Billing is integrated
    private const val SUBSCRIPTION_PRODUCT_ID = "seek_subscription_monthly"
    
    /**
     * URI for viewing all subscriptions in Play Store.
     */
    fun allSubscriptions(): Uri {
        return Uri.parse("https://play.google.com/store/account/subscriptions")
    }
    
    /**
     * URI for viewing a specific subscription in Play Store.
     * Recommended by Google for better UX.
     */
    fun specific(productId: String, packageName: String): Uri {
        return Uri.parse(
            "https://play.google.com/store/account/subscriptions" +
                "?sku=$productId&package=$packageName"
        )
    }
    
    /**
     * Get the appropriate subscription management URI.
     * Uses specific subscription link if product ID is known.
     */
    fun getManageSubscriptionUri(packageName: String): Uri {
        return specific(SUBSCRIPTION_PRODUCT_ID, packageName)
    }
    
    /**
     * Safely open subscription management in Play Store.
     * Falls back gracefully if Play Store is unavailable.
     */
    fun openManageSubscription(context: Context) {
        val packageName = context.packageName
        val specificUri = getManageSubscriptionUri(packageName)
        
        try {
            // Try specific subscription link first
            context.startActivity(Intent(Intent.ACTION_VIEW, specificUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: ActivityNotFoundException) {
            // Fall back to all subscriptions
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, allSubscriptions()).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (e2: ActivityNotFoundException) {
                // Show gentle message if Play Store unavailable
                Toast.makeText(
                    context,
                    "Couldn't open Play Store. Please check your subscriptions in Google Play.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
