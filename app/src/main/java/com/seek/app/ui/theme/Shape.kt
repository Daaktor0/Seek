package com.seek.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Seek app shapes - rounded and gentle for calm aesthetic.
 */
val SeekShapes = Shapes(
    // Extra small - for chips, badges
    extraSmall = RoundedCornerShape(4.dp),
    
    // Small - for small buttons, text fields
    small = RoundedCornerShape(8.dp),
    
    // Medium - for cards, dialogs
    medium = RoundedCornerShape(16.dp),
    
    // Large - for bottom sheets, large cards
    large = RoundedCornerShape(24.dp),
    
    // Extra large - for full-screen modals
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Semantic shape tokens for consistent usage.
 */
object SeekCorners {
    val card = 16.dp
    val button = 12.dp
    val chip = 8.dp
    val bottomSheet = 24.dp
    val dialog = 20.dp
    val fab = 16.dp
    val textField = 12.dp
}
