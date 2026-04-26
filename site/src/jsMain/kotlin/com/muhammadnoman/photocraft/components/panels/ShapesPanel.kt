package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import com.muhammadnoman.photocraft.utils.fabricSaveHistorySnapshot
import com.muhammadnoman.photocraft.utils.fabricUpdateSelectedShape
import com.muhammadnoman.photocraft.utils.getActiveObject
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
import kotlinx.browser.window
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I


//@Composable
//fun ShapesPanel(canvas: dynamic, historyRef: MutableState<dynamic>, onChanged: () -> Unit) {
//    var shapeColor by remember { mutableStateOf("#c8923f") }
//    var strokeColor by remember { mutableStateOf("#ffffff") }
//    var strokeWidth by remember { mutableStateOf(0.0) }
//
//    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
//        PanelTitle("Shapes")
//
//        SectionLabel("BASIC SHAPES")
//        Div(attrs = {
//            style {
//                property("display", "grid"); property("grid-template-columns", "repeat(4, 1fr)"); property(
//                "gap",
//                "8px"
//            ); property("margin-bottom", "14px")
//            }
//        }) {
//            listOf(
//                ShapeType.RECTANGLE to ("fa-square" to "Rect"),
//                ShapeType.CIRCLE to ("fa-circle" to "Circle"),
//                ShapeType.TRIANGLE to ("fa-play" to "Tri"),
//                ShapeType.LINE to ("fa-minus" to "Line"),
//                ShapeType.STAR to ("fa-star" to "Star"),
//                ShapeType.POLYGON to ("fa-diamond" to "Poly"),
//                ShapeType.ARROW to ("fa-arrow-right" to "Arrow")
//            ).forEach { (shapeType, pair) ->
//                val (icon, label) = pair
//                ShapeCardBtn(icon, label, onClick = {
//                    if (canvas != null) {
//                        fabricAddShape(canvas, shapeType, shapeColor); onChanged()
//                    }
//                })
//            }
//        }
//
//        PanelDivider()
//        SectionLabel("APPEARANCE")
//        ColorPickerRow("Fill Color", shapeColor) { shapeColor = it }
//        ColorPickerRow("Stroke Color", strokeColor) { strokeColor = it }
//        SliderRow("Stroke Width", strokeWidth, 0.0, 20.0, 1.0) { strokeWidth = it }
//    }
//}

// Global registry for shape panel callbacks
@Suppress("UnsafeCastFromDynamic")
object ShapePanelCallbacks {
    private var updateCallback: (() -> Unit)? = null

    fun register(callback: () -> Unit) {
        updateCallback = callback
        // Also register globally for JS access
        window.asDynamic()._kotlin_shapePanelUpdate = {
            callback()
        }
    }

    fun unregister() {
        updateCallback = null
        window.asDynamic()._kotlin_shapePanelUpdate = undefined
    }

    fun trigger() {
        updateCallback?.invoke()
    }
}

@Composable
fun ShapesPanel(canvas: dynamic, historyRef: MutableState<dynamic>, onChanged: () -> Unit) {
    var shapeColor by remember { mutableStateOf("#c8923f") }
    var strokeColor by remember { mutableStateOf("#ffffff") }
    var strokeWidth by remember { mutableStateOf(0.0) }
    var lastSelectedId by remember { mutableStateOf("") }

    // Function to update UI from selected shape
    fun updateFromSelection() {
        if (canvas == null) return
        val active = getActiveObject(canvas)
        if (active != null) {
            val type = js("active.type") as? String ?: ""
            if (type in listOf("rect", "circle", "triangle", "polygon", "line")) {
                val currentFill = js("active.fill") as? String
                val currentStroke = js("active.stroke") as? String
                val currentStrokeWidth = (js("active.strokeWidth") as? Double) ?: 0.0

                if (currentFill != null && currentFill != shapeColor) {
                    shapeColor = currentFill
                }
                if (currentStroke != null && currentStroke != strokeColor) {
                    strokeColor = currentStroke
                }
                if (currentStrokeWidth != strokeWidth) {
                    strokeWidth = currentStrokeWidth
                }
            }
        }
    }

    // Set up selection listeners when canvas is available
    LaunchedEffect(canvas) {
        if (canvas != null) {
            // Register the Kotlin callback
            ShapePanelCallbacks.register(::updateFromSelection)

            // Set up JS event listeners
            js("""
                if (canvas.__shapePanelUpdateFn === undefined) {
                    canvas.__shapePanelUpdateFn = function() {
                        if (window._kotlin_shapePanelUpdate) {
                            window._kotlin_shapePanelUpdate();
                        }
                    };
                    canvas.on('selection:created', canvas.__shapePanelUpdateFn);
                    canvas.on('selection:updated', canvas.__shapePanelUpdateFn);
                    canvas.on('object:modified', canvas.__shapePanelUpdateFn);
                }
            """)

            // Initial update if something is selected
            updateFromSelection()
        }
    }

    // Cleanup on dispose
    DisposableEffect(canvas) {
        onDispose {
            js("""
                if (canvas && canvas.__shapePanelUpdateFn) {
                    canvas.off('selection:created', canvas.__shapePanelUpdateFn);
                    canvas.off('selection:updated', canvas.__shapePanelUpdateFn);
                    canvas.off('object:modified', canvas.__shapePanelUpdateFn);
                    delete canvas.__shapePanelUpdateFn;
                }
            """)
            ShapePanelCallbacks.unregister()
        }
    }

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
                ShapeType.STAR to ("fa-star" to "Star"),
                ShapeType.POLYGON to ("fa-diamond" to "Poly"),
                ShapeType.ARROW to ("fa-arrow-right" to "Arrow")
            ).forEach { (shapeType, pair) ->
                val (icon, label) = pair
                ShapeCardBtn(icon, label, onClick = {
                    if (canvas != null) {
                        fabricAddShape(canvas, shapeType, shapeColor, strokeColor, strokeWidth)
                        fabricSaveHistorySnapshot(historyRef)
                        onChanged()
                    }
                })
            }
        }

        PanelDivider()
        SectionLabel("APPEARANCE")

        ColorPickerRow("Fill Color", shapeColor) { newColor ->
            shapeColor = newColor
            if (canvas != null) {
                fabricUpdateSelectedShape(canvas, fill = newColor)
                fabricSaveHistorySnapshot(historyRef)
                onChanged()
            }
        }

        ColorPickerRow("Stroke Color", strokeColor) { newColor ->
            strokeColor = newColor
            if (canvas != null) {
                fabricUpdateSelectedShape(canvas, stroke = newColor)
                fabricSaveHistorySnapshot(historyRef)
                onChanged()
            }
        }

        SliderRow("Stroke Width", strokeWidth, 0.0, 20.0, 1.0) { newWidth ->
            strokeWidth = newWidth
            if (canvas != null) {
                fabricUpdateSelectedShape(canvas, strokeWidth = newWidth)
                fabricSaveHistorySnapshot(historyRef)
                onChanged()
            }
        }
    }
}

@Composable
private fun ShapeCardBtn(icon: String, label: String, onClick: () -> Unit) {
    Column(
        modifier = ShapeBtnStyle.toModifier().cursor(Cursor.Pointer).onClick { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        I(attrs = { classes("fa-solid", icon); style { property("font-size", "16px"); property("color", "#888888") } })
        SpanText(label, modifier = Modifier.fontSize(9.px).color(Color.rgb(0x555555)))
    }
}