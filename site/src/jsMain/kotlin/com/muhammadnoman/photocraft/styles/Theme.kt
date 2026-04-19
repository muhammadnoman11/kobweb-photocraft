package com.muhammadnoman.photocraft.styles

import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color

/**
 * Only dark mode is used (PhotoCraft is always dark), but the structure
 * keeps it open for a future light theme.
 */
class PhotoCraftPalette(
    val bgPrimary: Color,
    val bgSecondary: Color,
    val bgTertiary: Color,
    val bgPanel: Color,
    val border: Color,
    val borderHover: Color,
    val accent: Color,
    val accentHover: Color,
    val accentMuted: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
)

object PhotoCraftPalettes {
    val dark = PhotoCraftPalette(
        bgPrimary = Color.rgb(0x0f0f0f),
        bgSecondary = Color.rgb(0x1a1a1a),
        bgTertiary = Color.rgb(0x242424),
        bgPanel = Color.rgb(0x1e1e1e),
        border = Color.rgb(0x2a2a2a),
        borderHover = Color.rgb(0x3a3a3a),
        accent = Color.rgb(0xc8923f),
        accentHover = Color.rgb(0xd9a455),
        accentMuted = Color.rgba(0xc8, 0x92, 0x3f, 38),   // ~15% alpha
        textPrimary = Color.rgb(0xf0f0f0),
        textSecondary = Color.rgb(0x888888),
        textMuted = Color.rgb(0x555555),
    )

    // Light palette mirrors dark for now; extend later if needed
    val light = dark
}

fun ColorMode.toPhotoCraftPalette(): PhotoCraftPalette = when (this) {
    ColorMode.DARK -> PhotoCraftPalettes.dark
    ColorMode.LIGHT -> PhotoCraftPalettes.light
}

@InitSilk
fun initPhotoCraftTheme(ctx: InitSilkContext) {
    // Force dark backgrounds on the Silk palette so Surface picks them up
    ctx.theme.palettes.dark.background = Color.rgb(0x0f0f0f)
    ctx.theme.palettes.dark.color = Color.rgb(0xf0f0f0)
    ctx.theme.palettes.light.background = Color.rgb(0x0f0f0f)
    ctx.theme.palettes.light.color = Color.rgb(0xf0f0f0)
}
