package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muhammadnoman.photocraft.components.widgets.PanelDivider
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.models.LayerItem
import com.muhammadnoman.photocraft.styles.LayerRowStyle
import com.muhammadnoman.photocraft.utils.fabricBringForward
import com.muhammadnoman.photocraft.utils.fabricDeleteSelected
import com.muhammadnoman.photocraft.utils.fabricDuplicateSelected
import com.muhammadnoman.photocraft.utils.fabricGetLayers
import com.muhammadnoman.photocraft.utils.fabricSendBackward
import com.muhammadnoman.photocraft.utils.fabricSetObjectVisibility
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun LayersPanel(canvas: dynamic, historyRef: MutableState<dynamic>, onChanged: () -> Unit) {
    var layers by remember { mutableStateOf(listOf<LayerItem>()) }

    LaunchedEffect(canvas) {
        if (canvas != null) layers = fabricGetLayers(canvas)
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        Row(
            modifier = Modifier.fillMaxWidth().margin(bottom = 14.px),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PanelTitle("Layers")
            Button(attrs = {
                onClick { if (canvas != null) layers = fabricGetLayers(canvas) }
                title("Refresh")
                style {
                    property("width", "24px"); property("height", "24px")
                    property("display", "flex"); property("align-items", "center"); property(
                    "justify-content",
                    "center"
                )
                    property("border", "none"); property("border-radius", "4px")
                    property("background", "transparent"); property("color", "#555555")
                    property("cursor", "pointer"); property("font-size", "11px"); property("outline", "none")
                }
            }) { I(attrs = { classes("fa-solid", "fa-rotate") }) }
        }

        Row(modifier = Modifier.gap(6.px).margin(bottom = 12.px).flexWrap(FlexWrap.Wrap)) {
            LayerActionBtn("fa-copy", "Duplicate") {
                if (canvas != null) {
                    fabricDuplicateSelected(canvas); layers = fabricGetLayers(canvas); onChanged()
                }
            }
            LayerActionBtn("fa-trash", "Delete") {
                if (canvas != null) {
                    fabricDeleteSelected(canvas); layers = fabricGetLayers(canvas); onChanged()
                }
            }
            LayerActionBtn("fa-arrow-up", "Bring Fwd") {
                if (canvas != null) {
                    fabricBringForward(canvas); layers = fabricGetLayers(canvas); onChanged()
                }
            }
            LayerActionBtn("fa-arrow-down", "Send Back") {
                if (canvas != null) {
                    fabricSendBackward(canvas); layers = fabricGetLayers(canvas); onChanged()
                }
            }
        }

        PanelDivider()

        if (layers.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(topBottom = 24.px),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                I(attrs = {
                    classes("fa-solid", "fa-layer-group"); style {
                    property(
                        "font-size",
                        "24px"
                    ); property("color", "#555555"); property("opacity", "0.4"); property(
                    "display",
                    "block"
                ); property("margin-bottom", "8px")
                }
                })
                SpanText("No layers yet", modifier = Modifier.fontSize(12.px).color(Color.rgb(0x555555)))
            }
        } else {
            Column(modifier = Modifier.gap(4.px)) {
                layers.forEachIndexed { index, layer ->
                    LayerRowItem(layer, index, onVisibilityToggle = { idx, visible ->
                        if (canvas != null) {
                            fabricSetObjectVisibility(canvas, idx, visible)
                            layers = layers.toMutableList().also { it[idx] = it[idx].copy(visible = visible) }
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun LayerRowItem(layer: LayerItem, index: Int, onVisibilityToggle: (Int, Boolean) -> Unit) {
    val typeIcon = when (layer.type) {
        "image" -> "fa-image"; "i-text", "text" -> "fa-t"; "rect" -> "fa-square"
        "circle" -> "fa-circle"; "triangle" -> "fa-play"; "line" -> "fa-minus"
        "polygon" -> "fa-star"; else -> "fa-layer-group"
    }

    Row(modifier = LayerRowStyle.toModifier().fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.cursor(Cursor.Pointer).flexShrink(0)
                .onClick { onVisibilityToggle(index, !layer.visible) }, contentAlignment = Alignment.Center
        ) {
            I(attrs = {
                classes(
                    "fa-solid",
                    if (layer.visible) "fa-eye" else "fa-eye-slash"
                ); style {
                property("font-size", "11px"); property(
                "color",
                if (layer.visible) "#888888" else "#555555"
            )
            }
            })
        }
        I(attrs = {
            classes("fa-solid", typeIcon); style {
            property("font-size", "11px"); property(
            "color",
            "#555555"
        ); property("width", "12px")
        }
        })
        SpanText(
            layer.name,
            modifier = Modifier.fontSize(12.px).color(if (layer.visible) Color.rgb(0xf0f0f0) else Color.rgb(0x555555))
                .weight(1).overflow(Overflow.Hidden).textOverflow(TextOverflow.Ellipsis).whiteSpace(WhiteSpace.NoWrap)
                .textDecorationLine(if (layer.visible) TextDecorationLine.None else TextDecorationLine.LineThrough)
        )
        SpanText(
            index.toString(), modifier = Modifier.fontSize(9.px).fontFamily("'JetBrains Mono'", "monospace")
                .color(Color.rgb(0x555555)).backgroundColor(Color.rgb(0x0f0f0f)).padding(1.px, 4.px).borderRadius(3.px)
                .flexShrink(0)
        )
    }
}

@Composable
private fun LayerActionBtn(icon: String, title: String, onClick: () -> Unit) {
    Button(attrs = {
        onClick { onClick() }
        title(title)
        style {
            property("display", "flex"); property("align-items", "center"); property("gap", "4px")
            property("padding", "5px 8px"); property("border", "1px solid #2a2a2a"); property("border-radius", "4px")
            property("background", "transparent"); property("color", "#888888")
            property("font-family", "'Space Grotesk', sans-serif"); property("font-size", "10px")
            property("cursor", "pointer"); property("transition", "all 0.2s ease"); property("outline", "none")
        }
    }) {
        I(attrs = { classes("fa-solid", icon); style { property("font-size", "9px") } })
        Span { Text(title) }
    }
}