package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muhammadnoman.photocraft.components.widgets.ActionButton
import com.muhammadnoman.photocraft.components.widgets.ColorPickerRow
import com.muhammadnoman.photocraft.components.widgets.PanelDivider
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.components.widgets.SectionLabel
import com.muhammadnoman.photocraft.components.widgets.SliderRow
import com.muhammadnoman.photocraft.components.widgets.ToggleButton
import com.muhammadnoman.photocraft.models.FONT_FAMILIES
import com.muhammadnoman.photocraft.models.TextProperties
import com.muhammadnoman.photocraft.utils.fabricAddText
import com.muhammadnoman.photocraft.utils.fabricUpdateTextObject
import com.muhammadnoman.photocraft.utils.getActiveObject
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextArea


@Composable
fun TextPanel(canvas: dynamic, onChanged: () -> Unit) {
    var props by remember { mutableStateOf(TextProperties()) }

    fun updateActive() {
        if (canvas == null) return
        val active = getActiveObject(canvas)
        val type = active?.type as? String ?: ""
        if (type == "i-text" || type == "text") {
            fabricUpdateTextObject(active, props); onChanged()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        PanelTitle("Text")

        ActionButton("Add Text", "fa-plus", variant = "primary", onClick = {
            if (canvas != null) {
                fabricAddText(canvas, props); onChanged()
            }
        })

        PanelDivider()
        SectionLabel("CONTENT")

        TextArea(attrs = {
            value(props.text)
            onInput { e -> props = props.copy(text = e.value); updateActive() }
            attr("placeholder", "Enter text...")
            style {
                property("width", "100%"); property("background", "#242424")
                property("border", "1px solid #2a2a2a"); property("border-radius", "8px")
                property("color", "#f0f0f0"); property("font-family", "'Space Grotesk', sans-serif")
                property("font-size", "12px"); property("padding", "8px")
                property("resize", "vertical"); property("min-height", "60px")
                property("outline", "none"); property("box-sizing", "border-box")
            }
        })

        PanelDivider()
        SectionLabel("FONT")

        SpanText(
            "Family",
            modifier = Modifier.fontSize(12.px).color(Color.rgb(0x888888)).display(DisplayStyle.Block)
                .margin(bottom = 5.px)
        )
        Select(attrs = {
            onChange { e -> props = props.copy(fontFamily = e.value!!); updateActive() }
            style {
                property("width", "100%"); property("background", "#242424")
                property("border", "1px solid #2a2a2a"); property("border-radius", "8px")
                property("color", "#f0f0f0"); property("font-family", "'Space Grotesk', sans-serif")
                property("font-size", "12px"); property("padding", "6px 8px")
                property("outline", "none"); property("cursor", "pointer")
                property("margin-bottom", "10px")
            }
        }) {
            FONT_FAMILIES.forEach { font ->
                Option(font, attrs = { if (font == props.fontFamily) selected() }) { Text(font) }
            }
        }

        SliderRow("Size", props.fontSize, 8.0, 200.0, 1.0) { props = props.copy(fontSize = it); updateActive() }

        PanelDivider()
        SectionLabel("STYLE")

        Row(modifier = Modifier.gap(6.px).margin(bottom = 10.px)) {
            ToggleButton(
                "B",
                title = "Bold",
                active = props.fontWeight == "bold",
                width = 36,
                onClick = {
                    props =
                        props.copy(fontWeight = if (props.fontWeight == "bold") "normal" else "bold"); updateActive()
                })
            ToggleButton(
                "I",
                title = "Italic",
                active = props.fontStyle == "italic",
                width = 36,
                onClick = {
                    props =
                        props.copy(fontStyle = if (props.fontStyle == "italic") "normal" else "italic"); updateActive()
                })
            ToggleButton(
                "U",
                title = "Underline",
                active = props.underline,
                width = 36,
                onClick = { props = props.copy(underline = !props.underline); updateActive() })
        }

        Row(modifier = Modifier.gap(6.px).margin(bottom = 10.px)) {
            listOf(
                "left" to "fa-align-left",
                "center" to "fa-align-center",
                "right" to "fa-align-right"
            ).forEach { (align, icon) ->
                ToggleButton(
                    icon = icon,
                    title = align,
                    active = props.textAlign == align,
                    onClick = { props = props.copy(textAlign = align); updateActive() })
            }
        }

        PanelDivider()
        SectionLabel("APPEARANCE")

        ColorPickerRow("Fill Color", props.fill) { props = props.copy(fill = it); updateActive() }
        SliderRow("Line Height", props.lineHeight, 0.5, 3.0, 0.1) {
            props = props.copy(lineHeight = it); updateActive()
        }
        SliderRow("Letter Spacing", props.charSpacing, -5.0, 20.0, 0.5) {
            props = props.copy(charSpacing = it); updateActive()
        }
    }
}