package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.models.STICKER_CATEGORIES
import com.muhammadnoman.photocraft.styles.StickerCardStyle
import com.muhammadnoman.photocraft.utils.fabricAddSticker
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


@Composable
fun StickersPanel(canvas: dynamic, historyRef: MutableState<dynamic>, onChanged: () -> Unit) {
    var activeCategory by remember { mutableStateOf("emoji") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        PanelTitle("Stickers")

        Row(modifier = Modifier.gap(6.px).margin(bottom = 14.px).flexWrap(FlexWrap.Wrap)) {
            STICKER_CATEGORIES.forEach { cat ->
                val isActive = activeCategory == cat.id
                org.jetbrains.compose.web.dom.Button(attrs = {
                    onClick { activeCategory = cat.id }
                    style {
                        property("border", if (isActive) "1px solid #c8923f" else "1px solid #2a2a2a")
                        property("border-radius", "4px")
                        property("background", if (isActive) "rgba(200,146,63,0.15)" else "transparent")
                        property("color", if (isActive) "#c8923f" else "#888888")
                        property("font-family", "'Space Grotesk', sans-serif"); property("font-size", "11px")
                        property("font-weight", if (isActive) "600" else "400")
                        property("padding", "5px 10px"); property("cursor", "pointer")
                        property("transition", "all 0.2s ease"); property("outline", "none")
                    }
                }) { Text(cat.name) }
            }
        }

        val currentCat = STICKER_CATEGORIES.find { it.id == activeCategory }
        currentCat?.let { cat ->
            Div(attrs = {
                style {
                    property("display", "grid"); property(
                    "grid-template-columns",
                    "repeat(5, 1fr)"
                ); property("gap", "8px")
                }
            }) {
                cat.stickers.forEach { sticker ->
                    Box(
                        modifier = StickerCardStyle.toModifier().cursor(Cursor.Pointer).onClick {
                            if (canvas != null) {
                                fabricAddSticker(canvas, sticker); onChanged()
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) { Text(sticker) }
                }
            }
        }
    }
}
