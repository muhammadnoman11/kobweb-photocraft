package com.muhammadnoman.photocraft.models

/**
 * Represents the active tool in the sidebar.
 */
enum class ActiveTool {
    NONE,
    UPLOAD,
    CROP,
    FILTERS,
    ADJUSTMENTS,
    TEXT,
    STICKERS,
    SHAPES,
    LAYERS
}

/**
 * Filter preset definition
 */
data class FilterPreset(
    val id: String,
    val name: String,
    val brightness: Double = 0.0,
    val contrast: Double = 0.0,
    val saturation: Double = 0.0,
    val sepia: Double = 0.0,
    val grayscale: Double = 0.0,
    val invert: Double = 0.0,
    val blur: Double = 0.0,
    val hueRotation: Double = 0.0,
    val vibrance: Double = 0.0
)

/**
 * Adjustment sliders state
 */
data class AdjustmentState(
    val brightness: Double = 0.0,
    val contrast: Double = 0.0,
    val saturation: Double = 0.0,
    val hue: Double = 0.0,
    val blur: Double = 0.0,
    val sharpen: Double = 0.0,
    val opacity: Double = 100.0,
    val temperature: Double = 0.0,
    val tint: Double = 0.0,
    val vignette: Double = 0.0,
    val noise: Double = 0.0,
    val highlights: Double = 0.0,
    val shadows: Double = 0.0
)

/**
 * Text object properties
 */
data class TextProperties(
    val text: String = "Your Text Here",
    val fontFamily: String = "Space Grotesk",
    val fontSize: Double = 40.0,
    val fontWeight: String = "normal",
    val fontStyle: String = "normal",
    val underline: Boolean = false,
    val fill: String = "#ffffff",
    val stroke: String = "",
    val strokeWidth: Double = 0.0,
    val textAlign: String = "left",
    val lineHeight: Double = 1.2,
    val charSpacing: Double = 0.0,
    val shadow: Boolean = false
)

/**
 * Shape types available
 */
enum class ShapeType {
    RECTANGLE, CIRCLE, TRIANGLE, STAR, POLYGON, LINE, ARROW
}

/**
 * Background fill type
 */
enum class BackgroundType {
    SOLID_COLOR, GRADIENT, PATTERN, TRANSPARENT
}

/**
 * Layer item representation
 */
data class LayerItem(
    val id: String,
    val name: String,
    val type: String,
    val visible: Boolean = true,
    val locked: Boolean = false
)

/**
 * Canvas state
 */
data class CanvasState(
    val hasImage: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isCropping: Boolean = false,
    val selectedObjectId: String? = null,
    val layers: List<LayerItem> = emptyList(),
    val canvasWidth: Int = 800,
    val canvasHeight: Int = 600
)

/**
 * Sticker category
 */
data class StickerCategory(
    val id: String,
    val name: String,
    val stickers: List<String> // emoji or icon names
)

val FILTER_PRESETS = listOf(
    FilterPreset("none", "Original"),
    FilterPreset("vivid", "Vivid", brightness = 10.0, contrast = 20.0, saturation = 30.0),
    FilterPreset("chrome", "Chrome", brightness = 5.0, contrast = 15.0, saturation = -20.0),
    FilterPreset("fade", "Fade", brightness = 15.0, contrast = -10.0, saturation = -15.0),
    FilterPreset("noir", "Noir", grayscale = 100.0, contrast = 20.0),
    FilterPreset("sepia", "Sepia", sepia = 80.0, brightness = 5.0),
    FilterPreset("dramatic", "Dramatic", contrast = 40.0, brightness = -10.0, saturation = 20.0),
    FilterPreset("warm", "Warm", brightness = 5.0, hueRotation = -15.0, saturation = 20.0),
    FilterPreset("cool", "Cool", hueRotation = 15.0, saturation = 10.0, brightness = -5.0),
    FilterPreset("vintage", "Vintage", sepia = 40.0, contrast = -10.0, brightness = 5.0),
    FilterPreset("matte", "Matte", contrast = -20.0, brightness = 10.0, saturation = -10.0),
    FilterPreset("cinema", "Cinema", contrast = 25.0, saturation = -5.0, brightness = -5.0)
)

val FONT_FAMILIES = listOf(
    "Space Grotesk",
    "JetBrains Mono",
    "Georgia",
    "Times New Roman",
    "Arial",
    "Helvetica",
    "Courier New",
    "Verdana",
    "Impact",
    "Comic Sans MS",
    "Trebuchet MS",
    "Palatino"
)

val STICKER_CATEGORIES = listOf(
    StickerCategory("emoji", "Emoji", listOf("😀","😍","🔥","⭐","❤️","✨","🎉","🌈","🦋","🌸","🎨","🎭")),
    StickerCategory("shapes", "Decorative", listOf("★","♦","♠","♣","♥","✿","❋","✦","✧","◆","▲","●")),
    StickerCategory("arrows", "Arrows", listOf("→","←","↑","↓","↗","↘","↖","↙","⟶","⟵","↔","↕"))
)
