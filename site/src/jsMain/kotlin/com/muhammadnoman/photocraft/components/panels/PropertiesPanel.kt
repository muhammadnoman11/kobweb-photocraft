package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.muhammadnoman.photocraft.components.widgets.AccentDot
import com.muhammadnoman.photocraft.models.ActiveTool
import com.muhammadnoman.photocraft.styles.PanelContentStyle
import com.muhammadnoman.photocraft.styles.PanelHeaderStyle
import com.muhammadnoman.photocraft.styles.PropertiesPanelStyle
import com.varabyte.kobweb.compose.ui.graphics.Color
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.I

@Composable
fun PropertiesPanel(
    activeTool: ActiveTool,
    canvas: dynamic,
    hasImage: Boolean,
    cropRectRef: MutableState<dynamic>,
    historyRef: MutableState<dynamic>,
    onChanged: () -> Unit,
    onUploadClick: () -> Unit,
    onCropApplied: () -> Unit,
) {
    val isVisible = activeTool != ActiveTool.NONE
    val requiresImage = activeTool in listOf(ActiveTool.CROP, ActiveTool.FILTERS, ActiveTool.ADJUSTMENTS)
    val isDisabled = requiresImage && !hasImage


    Column(modifier = PropertiesPanelStyle.toModifier()) {

        //  Sticky Panel Header
        if (isVisible) {
            Row(
                modifier = PanelHeaderStyle.toModifier(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpanText(
                    text = activeTool.displayName(),
                    modifier = Modifier.fontSize(13.px).fontWeight(FontWeight.SemiBold).color(Color.rgb(0xf0f0f0))
                )
                AccentDot(active = !isDisabled)
            }

            // "Upload first" notice when panel requires an image
            Box(modifier = PanelContentStyle.toModifier().flexGrow(1).overflow(Overflow.Auto).minHeight(0.px)) {
            if (isDisabled) {
                    Column(
                        modifier = Modifier.position(Position.Sticky).top(0.px).zIndex(20).fillMaxWidth()
                            .backgroundColor(Color.rgba(15, 15, 15, 220)).padding(32.px, 24.px)
                            .textAlign(TextAlign.Center).gap(10.px),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        I(attrs = {
                            classes("fa-solid", "fa-image"); style {
                            property(
                                "font-size",
                                "32px"
                            ); property("color", "#555555"); property("opacity", "0.4")
                        }
                        })
                        SpanText(
                            text = "Upload an image first to use this tool",
                            modifier = Modifier.fontSize(12.px).color(Color.rgb(0x555555)).lineHeight(1.6)
                        )
                    }
                }

                // Panel content (dimmed but still rendered when disabled)
                Box(
                    modifier = Modifier.fillMaxWidth().then(
                        if (isDisabled) Modifier.opacity(0.4f).pointerEvents(PointerEvents.None) else Modifier
                    ).padding(bottom = 24.px)
                ) {
                    when (activeTool) {
                        ActiveTool.UPLOAD -> UploadPanel(onUploadClick = onUploadClick)
                        ActiveTool.CROP -> CropPanel(
                            canvas = canvas,
                            cropRectRef = cropRectRef,
                            historyRef = historyRef,
                            onCropApplied = { onCropApplied(); onChanged() },
                            onCropCancelled = onChanged
                        )

                        ActiveTool.FILTERS -> FiltersPanel(
                            canvas = canvas,
                            hasImage = hasImage,
                            historyRef = historyRef,
                            onFilterApplied = onChanged
                        )

                        ActiveTool.ADJUSTMENTS -> AdjustmentsPanel(
                            canvas = canvas,
                            historyRef = historyRef,
                            onAdjustmentChanged = onChanged
                        )

                        ActiveTool.TEXT -> TextPanel(canvas = canvas, historyRef = historyRef, onChanged = onChanged)
                        ActiveTool.STICKERS -> StickersPanel(
                            canvas = canvas,
                            historyRef = historyRef,
                            onChanged = onChanged
                        )

                        ActiveTool.SHAPES -> ShapesPanel(
                            canvas = canvas,
                            historyRef = historyRef,
                            onChanged = onChanged
                        )

                        ActiveTool.LAYERS -> LayersPanel(
                            canvas = canvas,
                            historyRef = historyRef,
                            onChanged = onChanged
                        )

                        ActiveTool.NONE -> {}
                    }
                }
            }
        } else {
            // Panel is "hidden" — show an empty placeholder so the right edge
            // stays in place.  A thin vertical strip keeps the border visible.
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

private fun ActiveTool.displayName() = when (this) {
    ActiveTool.UPLOAD -> "Upload Image";
    ActiveTool.CROP -> "Crop & Transform"
    ActiveTool.FILTERS -> "Filters";
    ActiveTool.ADJUSTMENTS -> "Adjustments"
    ActiveTool.TEXT -> "Text";
    ActiveTool.STICKERS -> "Stickers"
    ActiveTool.SHAPES -> "Shapes";
    ActiveTool.LAYERS -> "Layers";
    ActiveTool.NONE -> ""
}


