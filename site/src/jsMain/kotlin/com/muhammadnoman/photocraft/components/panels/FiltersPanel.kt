package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muhammadnoman.photocraft.components.widgets.ActionButton
import com.muhammadnoman.photocraft.components.widgets.PanelDivider
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.models.FILTER_PRESETS
import com.muhammadnoman.photocraft.models.FilterPreset
import com.muhammadnoman.photocraft.styles.FilterCardStyle
import com.muhammadnoman.photocraft.utils.fabricApplyPresetFilter
import com.muhammadnoman.photocraft.utils.fabricGetActivePresetId
import com.muhammadnoman.photocraft.utils.fabricGetFilterThumbnail
import com.muhammadnoman.photocraft.utils.fabricSaveHistorySnapshot
import com.muhammadnoman.photocraft.utils.fabricSetActivePresetId
import com.muhammadnoman.photocraft.utils.getActiveImageObject
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.right
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Img

@Composable
fun FiltersPanel(canvas: dynamic, hasImage: Boolean, historyRef: MutableState<dynamic>, onFilterApplied: () -> Unit) {
    var activePresetId by remember(canvas, hasImage) {
        mutableStateOf(if (hasImage && canvas != null) fabricGetActivePresetId(canvas) else "none")
    }

    val thumbnails = remember(hasImage, canvas) {
        if (!hasImage || canvas == null) return@remember mapOf<String, String>()
        val img = getActiveImageObject(canvas) ?: return@remember mapOf<String, String>()
        FILTER_PRESETS.associate { it.id to fabricGetFilterThumbnail(img, it, 60) }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        PanelTitle("Filters")

        Div(attrs = {
            style {
                property("display", "grid"); property("grid-template-columns", "repeat(3, 1fr)"); property("gap", "8px")
            }
        }) {
            FILTER_PRESETS.forEach { preset ->
                FilterCard(
                    preset = preset,
                    isActive = activePresetId == preset.id,
                    thumbnail = thumbnails[preset.id] ?: "",
                    onClick = {
                        activePresetId = preset.id
                        applyPreset(canvas, preset)
                        fabricSetActivePresetId(canvas, preset.id)
                        // Save snapshot explicitly after filter change
                        fabricSaveHistorySnapshot(historyRef)
                        onFilterApplied()
                    }
                )
            }
        }

        PanelDivider()
        ActionButton("Reset Filters", "fa-rotate-left", onClick = {
            activePresetId = "none"
            val nonePreset = FILTER_PRESETS.first { it.id == "none" }
            applyPreset(canvas, nonePreset)
            fabricSetActivePresetId(canvas, "none")
            fabricSaveHistorySnapshot(historyRef)
            onFilterApplied()
        })
    }
}

@Composable
private fun FilterCard(preset: FilterPreset, isActive: Boolean, thumbnail: String, onClick: () -> Unit) {
    Column(
        modifier = FilterCardStyle.toModifier()
            .then(
                if (isActive) Modifier.border(2.px, LineStyle.Solid, Color.rgb(0xc8923f))
                    .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 38)) else Modifier
            )
            .cursor(Cursor.Pointer).onClick { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(60.px).overflow(Overflow.Hidden).position(Position.Relative),
            contentAlignment = Alignment.Center
        ) {
            if (thumbnail.isNotEmpty()) {
                Img(src = thumbnail, attrs = {
                    style {
                        property("width", "100%"); property("height", "60px")
                        property("object-fit", "cover"); property("display", "block")
                        property("filter", getCssFilterStr(preset))
                    }
                    attr("alt", preset.name)
                })
            } else {
                I(attrs = {
                    classes("fa-solid", "fa-image"); style {
                    property(
                        "color",
                        if (isActive) "#c8923f" else "#555555"
                    ); property("font-size", "20px")
                }
                })
            }

            if (isActive) {
                Box(
                    modifier = Modifier.position(Position.Absolute).top(4.px).right(4.px)
                        .size(16.px).borderRadius(50.percent).backgroundColor(Color.rgb(0xc8923f))
                        .display(DisplayStyle.Flex).alignItems(AlignItems.Center).justifyContent(JustifyContent.Center),
                    contentAlignment = Alignment.Center
                ) {
                    I(attrs = {
                        classes("fa-solid", "fa-check"); style {
                        property("font-size", "8px"); property(
                        "color",
                        "#fff"
                    )
                    }
                    })
                }
            }
        }

        SpanText(
            preset.name,
            modifier = Modifier.fontSize(10.px).padding(4.px).fillMaxWidth().textAlign(TextAlign.Center)
                .color(if (isActive) Color.rgb(0xc8923f) else Color.rgb(0x888888))
                .fontWeight(if (isActive) FontWeight.SemiBold else FontWeight.Normal)
                .overflow(Overflow.Hidden).textOverflow(TextOverflow.Ellipsis).whiteSpace(WhiteSpace.NoWrap)
        )
    }
}

private fun applyPreset(canvas: dynamic, preset: FilterPreset) {
    if (canvas == null) return
    var img = js("canvas.getActiveObject()")
    if (img == null || (img.type as? String) != "image") img = getActiveImageObject(canvas)
    if (img != null) fabricApplyPresetFilter(img, preset)
}

private fun getCssFilterStr(preset: FilterPreset): String {
    val parts = mutableListOf<String>()
    if (preset.brightness != 0.0) parts += "brightness(${1.0 + preset.brightness / 100.0})"
    if (preset.contrast != 0.0) parts += "contrast(${1.0 + preset.contrast / 100.0})"
    if (preset.saturation != 0.0) parts += "saturate(${1.0 + preset.saturation / 100.0})"
    if (preset.grayscale > 0.0) parts += "grayscale(1)"
    if (preset.sepia > 0.0) parts += "sepia(0.8)"
    if (preset.blur > 0.0) parts += "blur(${preset.blur / 100.0 * 2}px)"
    return parts.joinToString(" ").ifEmpty { "none" }
}