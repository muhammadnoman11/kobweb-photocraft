package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.muhammadnoman.photocraft.components.widgets.ActionButton
import com.muhammadnoman.photocraft.components.widgets.PanelDivider
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.components.widgets.SectionLabel
import com.muhammadnoman.photocraft.utils.fabricApplyCrop
import com.muhammadnoman.photocraft.utils.fabricCancelCrop
import com.muhammadnoman.photocraft.utils.fabricSaveHistorySnapshot
import com.muhammadnoman.photocraft.utils.fabricStartCrop
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Text

@Composable
fun CropPanel(
    canvas: dynamic,
    cropRectRef: MutableState<dynamic>,
    historyRef: MutableState<dynamic>,
    onCropApplied: () -> Unit,
    onCropCancelled: () -> Unit,
) {
    var isCropping by remember { mutableStateOf(false) }
    var selectedAspect by remember { mutableStateOf("free") }

    DisposableEffect(Unit) {
        onDispose {
            if (isCropping && canvas != null) {
                fabricCancelCrop(canvas, cropRectRef.value)
                cropRectRef.value = null
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        PanelTitle("Crop & Transform")

        SectionLabel("ASPECT RATIO")
        Div(attrs = {
            style {
                property("display", "grid")
                property("grid-template-columns", "repeat(3, 1fr)")
                property("gap", "6px")
                property("margin-bottom", "14px")
            }
        }) {
            listOf(
                "free" to "Free", "1:1" to "Square", "4:3" to "4:3",
                "16:9" to "16:9", "3:2" to "3:2", "9:16" to "9:16"
            ).forEach { (ratio, label) ->
                AspectBtn(label, selectedAspect == ratio) {
                    selectedAspect = ratio
                    if (isCropping && canvas != null) {
                        fabricCancelCrop(canvas, cropRectRef.value)
                        cropRectRef.value = fabricStartCrop(canvas, ratio)
                    }
                }
            }
        }

        PanelDivider()

        if (!isCropping) {
            ActionButton("Start Crop", "fa-crop-simple", variant = "primary", onClick = {
                if (canvas != null) {
                    cropRectRef.value = fabricStartCrop(canvas, selectedAspect)
                    isCropping = true
                }
            })
        } else {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 38))
                    .border(1.px, LineStyle.Solid, Color.rgba(0xc8, 0x92, 0x3f, 76))
                    .borderRadius(8.px).padding(10.px, 12.px).margin(bottom = 10.px).gap(6.px),
                verticalAlignment = Alignment.Top
            ) {
                I(attrs = {
                    classes("fa-solid", "fa-info-circle"); style {
                    property(
                        "color",
                        "#c8923f"
                    ); property("margin-top", "2px")
                }
                })
                SpanText(
                    "Drag the orange handles to resize the crop area, then click Apply.",
                    modifier = Modifier.fontSize(12.px).color(Color.rgb(0xc8923f)).lineHeight(1.5)
                )
            }

            Row(modifier = Modifier.gap(8.px)) {
                ActionButton("Apply", "fa-check", variant = "primary", fullWidth = false, onClick = {
                    val rect = cropRectRef.value
                    if (canvas != null && rect != null) {
//                        fabricApplyCrop(canvas, rect)
//                        cropRectRef.value = null
//                        isCropping = false
//                        fabricSaveHistorySnapshot(historyRef)
//                        onCropApplied()

                        fabricApplyCrop(canvas, rect, historyRef) {
                            cropRectRef.value = null
                            isCropping = false
                            onCropApplied()
                        }
                    }
                })
                ActionButton("Cancel", "fa-xmark", variant = "danger", fullWidth = false, onClick = {
                    fabricCancelCrop(canvas, cropRectRef.value)
                    cropRectRef.value = null
                    isCropping = false
                    onCropCancelled()
                })
            }
        }

        PanelDivider()
        SectionLabel("QUICK ACTIONS")
        Row(modifier = Modifier.gap(6.px).flexWrap(FlexWrap.Wrap)) {
            QuickActionBtn("Flip H", "fa-left-right") {
                if (canvas != null) {
                    js("var a=canvas.getActiveObject();if(!a)canvas.getObjects().forEach(function(o){if(o.type==='image')a=o;});if(a){a.set('flipX',!a.flipX);canvas.requestRenderAll();}")
                    fabricSaveHistorySnapshot(historyRef)
                }
            }
            QuickActionBtn("Flip V", "fa-up-down") {
                if (canvas != null) {
                    js("var a=canvas.getActiveObject();if(!a)canvas.getObjects().forEach(function(o){if(o.type==='image')a=o;});if(a){a.set('flipY',!a.flipY);canvas.requestRenderAll();}")
                    fabricSaveHistorySnapshot(historyRef)
                }
            }
            QuickActionBtn("Rotate 90°", "fa-rotate-right") {
                if (canvas != null) {
                    js("var a=canvas.getActiveObject();if(!a)canvas.getObjects().forEach(function(o){if(o.type==='image')a=o;});if(a){a.rotate((a.angle+90)%360);canvas.requestRenderAll();}")
                    fabricSaveHistorySnapshot(historyRef)
                }
            }
        }

        PanelDivider()
        SectionLabel("HOW TO CROP")
        Column(modifier = Modifier.gap(4.px), horizontalAlignment = Alignment.Start) {
            listOf(
                "1. Click 'Start Crop'", "2. Drag orange handles to resize",
                "3. Click 'Apply' to confirm", "4. Cancel or switch tools to discard"
            ).forEach { line ->
                SpanText(line, modifier = Modifier.fontSize(11.px).color(Color.rgb(0x555555)).lineHeight(1.8))
            }
        }
    }
}

@Composable
private fun AspectBtn(label: String, active: Boolean, onClick: () -> Unit) {
    org.jetbrains.compose.web.dom.Button(attrs = {
        onClick { onClick() }
        style {
            property("border", if (active) "1px solid #c8923f" else "1px solid #2a2a2a")
            property("border-radius", "4px")
            property("background", if (active) "rgba(200,146,63,0.15)" else "transparent")
            property("color", if (active) "#c8923f" else "#888888")
            property("font-family", "'Space Grotesk', sans-serif"); property("font-size", "11px")
            property("font-weight", if (active) "600" else "400")
            property("padding", "6px"); property("cursor", "pointer")
            property("transition", "all 0.2s ease"); property("outline", "none"); property("text-align", "center")
        }
    }) { Text(label) }
}

@Composable
private fun QuickActionBtn(label: String, icon: String, onClick: () -> Unit) {
    org.jetbrains.compose.web.dom.Button(attrs = {
        onClick { onClick() }
        style {
            property("display", "flex"); property("align-items", "center"); property("gap", "5px")
            property("border", "1px solid #2a2a2a"); property("border-radius", "4px")
            property("background", "transparent"); property("color", "#888888")
            property("font-family", "'Space Grotesk', sans-serif"); property("font-size", "11px")
            property("padding", "6px 10px"); property("cursor", "pointer")
            property("transition", "all 0.2s ease"); property("outline", "none")
        }
    }) {
        I(attrs = { classes("fa-solid", icon); style { property("font-size", "10px") } })
        Text(label)
    }
}