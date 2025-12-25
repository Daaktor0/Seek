package com.seek.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.seek.app.ui.components.SeekApplicationCard
import com.seek.app.ui.components.SeekBackButton
import com.seek.app.ui.components.SeekChip
import com.seek.app.ui.components.SeekPrimaryButton
import com.seek.app.ui.components.SeekSecondaryButton
import com.seek.app.ui.components.SeekSectionHeader
import com.seek.app.ui.components.SeekSettingsCard
import com.seek.app.ui.components.SeekSpacing
import com.seek.app.ui.components.SeekTextButton
import com.seek.app.ui.components.SeekTopAppBar

/**
 * Preview screen showcasing all Seek design tokens.
 */
@Preview(showBackground = true, showSystemUi = true, name = "Seek Theme Preview - Light")
@Composable
fun SeekThemePreviewLight() {
    SeekTheme(darkTheme = false) {
        ThemeShowcase()
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Seek Theme Preview - Dark")
@Composable
fun SeekThemePreviewDark() {
    SeekTheme(darkTheme = true) {
        ThemeShowcase()
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ThemeShowcase() {
    Scaffold(
        topBar = {
            SeekTopAppBar(
                title = "Seek",
                subtitle = "Design Tokens",
                navigationIcon = { SeekBackButton(onClick = {}) },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(SeekSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(SeekSpacing.sectionGap)
        ) {
            // Colors Section
            SeekSectionHeader("Color Palette")
            ColorPalettePreview()
            
            // Typography Section
            SeekSectionHeader("Typography")
            TypographyPreview()
            
            // Buttons Section
            SeekSectionHeader("Buttons")
            ButtonsPreview()
            
            // Cards Section
            SeekSectionHeader("Application Card")
            SeekApplicationCard(
                companyName = "Google",
                roleTitle = "Senior Android Developer",
                status = "Interviewing",
                nextAction = "Prepare for technical interview next Tuesday",
                onClick = {},
                location = "Bangalore, India"
            )
            
            // Chips Section
            SeekSectionHeader("Status Chips")
            ChipsPreview()
            
            // Settings Card Section
            SeekSectionHeader("Settings Card")
            SeekSettingsCard {
                Text("This is a settings card with calm styling")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "No harsh shadows, gentle elevation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ColorPalettePreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorRow("Primary", MaterialTheme.colorScheme.primary)
        ColorRow("Primary Container", MaterialTheme.colorScheme.primaryContainer)
        ColorRow("Secondary", MaterialTheme.colorScheme.secondary)
        ColorRow("Background", MaterialTheme.colorScheme.background)
        ColorRow("Surface", MaterialTheme.colorScheme.surface)
        ColorRow("Surface Variant", MaterialTheme.colorScheme.surfaceVariant)
        ColorRow("Tertiary (Soft Gold)", MaterialTheme.colorScheme.tertiary)
        ColorRow("Outline", MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun ColorRow(name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TypographyPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Headline Large", style = MaterialTheme.typography.headlineLarge)
        Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        Text("Title Medium", style = MaterialTheme.typography.titleMedium)
        Text("Body Large", style = MaterialTheme.typography.bodyLarge)
        Text("Body Medium", style = MaterialTheme.typography.bodyMedium)
        Text("Body Small", style = MaterialTheme.typography.bodySmall)
        Text("Label Large", style = MaterialTheme.typography.labelLarge)
        Text("Label Small", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ButtonsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SeekPrimaryButton(
            text = "Primary Button",
            onClick = {},
            leadingIcon = Icons.Default.Add,
            modifier = Modifier.fillMaxWidth()
        )
        
        SeekSecondaryButton(
            text = "Secondary Button",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SeekPrimaryButton(
                text = "Enabled",
                onClick = {}
            )
            SeekPrimaryButton(
                text = "Disabled",
                onClick = {},
                enabled = false
            )
            SeekTextButton(
                text = "Text Button",
                onClick = {}
            )
        }
    }
}

@Composable
private fun ChipsPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SeekChip(text = "Applied")
        SeekChip(
            text = "Interviewing",
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
        SeekChip(
            text = "Archived",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
