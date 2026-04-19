package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muhammadnoman.photocraft.components.widgets.ColorPickerRow
import com.muhammadnoman.photocraft.components.widgets.PanelDivider
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.components.widgets.SectionLabel
import com.muhammadnoman.photocraft.components.widgets.SliderRow
import com.muhammadnoman.photocraft.models.ShapeType
import com.muhammadnoman.photocraft.styles.ShapeBtnStyle
import com.muhammadnoman.photocraft.utils.fabricAddShape
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I


@Composable
fun ShapesPanel(canvas: dynamic, onChanged: () -> Unit,) {
    var shapeColor by remember { mutableStateOf("#c8923f") }
    var strokeColor by remember { mutableStateOf("#ffffff") }
    var strokeWidth by remember { mutableStateOf(0.0) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        PanelTitle("Shapes")

        SectionLabel("BASIC SHAPES")
        Div(attrs = {
            style {
                property("display", "grid")
                property("grid-template-columns", "repeat(4, 1fr)")
                property("gap", "8px")
                property("margin-bottom", "14px")
            }
        }) {
            listOf(
                ShapeType.RECTANGLE to ("fa-square" to "Rect"),
                ShapeType.CIRCLE to ("fa-circle" to "Circle"),
                ShapeType.TRIANGLE to ("fa-play" to "Tri"),
                ShapeType.LINE to ("fa-minus" to "Line"),
                ShapeType.STAR to ("fa-star" to "Star"),
                ShapeType.POLYGON to ("fa-diamond" to "Poly"),
                ShapeType.ARROW to ("fa-arrow-right" to "Arrow")
            ).forEach { (shapeType, pair) ->
                val (icon, label) = pair
                ShapeCardBtn(icon, label, onClick = {
                    if (canvas != null) {
                        fabricAddShape(canvas, shapeType, shapeColor); onChanged()
                    }
                })
            }
        }

        PanelDivider()
        SectionLabel("APPEARANCE")
        ColorPickerRow("Fill Color", shapeColor) { shapeColor = it }
        ColorPickerRow("Stroke Color", strokeColor) { strokeColor = it }
        SliderRow("Stroke Width", strokeWidth, 0.0, 20.0, 1.0) { strokeWidth = it }
    }
}

@Composable
private fun ShapeCardBtn(icon: String, label: String, onClick: () -> Unit) {
    Column(
        modifier = ShapeBtnStyle.toModifier().cursor(Cursor.Pointer).onClick { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        I(attrs = {
            classes("fa-solid", icon)
            style { property("font-size", "16px"); property("color", "#888888") }
        })
        SpanText(label, modifier = Modifier.fontSize(9.px).color(Color.rgb(0x555555)))
    }
}