package com.yodgorbek.securetunnel.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yodgorbek.securetunnel.core.model.AppThemeMode

// Premium Cyber Privacy Palette
val PrimaryEmerald = Color(0xFF00E599)
val PrimaryEmeraldVariant = Color(0xFF00B377)
val SecondaryIndigo = Color(0xFF6366F1)
val AccentCyan = Color(0xFF06B6D4)
val AlertRuby = Color(0xFFEF4444)
val WarningAmber = Color(0xFFF59E0B)

// Dark Theme Surfaces (Deep Obsidian & Slate Glass)
val DarkBackground = Color(0xFF0B0F19)
val DarkSurface = Color(0xFF111827)
val DarkSurfaceVariant = Color(0xFF1F2937)
val DarkSurfaceHigh = Color(0xFF374151)
val DarkTextPrimary = Color(0xFFF9FAFB)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkBorder = Color(0xFF1F293D)

// Light Theme Surfaces
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)
val LightBorder = Color(0xFFE2E8F0)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmerald,
    onPrimary = Color.Black,
    secondary = SecondaryIndigo,
    onSecondary = Color.White,
    tertiary = AccentCyan,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = AlertRuby
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryEmeraldVariant,
    onPrimary = Color.White,
    secondary = SecondaryIndigo,
    onSecondary = Color.White,
    tertiary = AccentCyan,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = AlertRuby
)

@Composable
fun SecureTunnelTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
