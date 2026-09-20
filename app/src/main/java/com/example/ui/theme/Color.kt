package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand Core: Deep Teal & Protective Navy
val TealPrimary = Color(0xFF0F766E)
val TealPrimaryDark = Color(0xFF14B8A6)
val TealContainer = Color(0xFFCCFBF1)
val OnTealContainer = Color(0xFF115E59)

val NavySecondary = Color(0xFF1E293B)
val NavySecondaryDark = Color(0xFF334155)
val NavyContainer = Color(0xFFF1F5F9)
val OnNavyContainer = Color(0xFF0F172A)

// Emergency & Alert: High contrast crimson
val EmergencyRed = Color(0xFFB91C1C)
val EmergencyRedDark = Color(0xFFDC2626)
val EmergencyContainer = Color(0xFFFEE2E2)
val OnEmergencyContainer = Color(0xFF7F1D1D)

// Status Colors
val FreshGreen = Color(0xFF15803D)
val FreshGreenContainer = Color(0xFFDCFCE7)
val WarningAmber = Color(0xFFB45309)
val WarningAmberContainer = Color(0xFFFEF3C7)
val MutedSlate = Color(0xFF64748B)
val MutedSlateContainer = Color(0xFFF1F5F9)

// Light Theme Palette
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF8FAFC)
val LightOutline = Color(0xFFE2E8F0)
val LightOutlineVariant = Color(0xFFCBD5E1)

// Full App Light Gradient Form Definitions
object AppGradients {
    // Soft, soothing, and protective light background gradient
    val LightBackgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8F5F1), // Gentle soft mint-teal tint at top
            Color(0xFFF4F9F8), // Transition to clean light canvas
            Color(0xFFF8FAFC), // Pure light body
            Color(0xFFEFF6FF)  // Soft sky tint at base
        )
    )

    // Gentle card surface gradient for elevated cards
    val LightCardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFFBFDFD)
        )
    )

    // Hero banner / highlight card gradient
    val HeroGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0F7F4),
            Color(0xFFE6F4EA),
            Color(0xFFF0FDF4)
        )
    )

    // Primary action button gradient (rich teal)
    val PrimaryButtonGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0F766E),
            Color(0xFF0D9488)
        )
    )

    // Soft alert gradient
    val AlertLightGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFF1F2),
            Color(0xFFFEE2E2)
        )
    )
}

// Light-mapped aliases for consistency
val DarkBackground = Color(0xFFF8FAFC)
val DarkSurface = Color(0xFFFFFFFF)
val DarkSurfaceVariant = Color(0xFFF8FAFC)
val DarkOutline = Color(0xFFE2E8F0)

