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
import com.muhammadnoman.photocraft.components.widgets.SectionLabel
import com.muhammadnoman.photocraft.components.widgets.SliderRow
import com.muhammadnoman.photocraft.models.AdjustmentState
import com.muhammadnoman.photocraft.utils.fabricApplyAdjustments
import com.muhammadnoman.photocraft.utils.fabricGetAdjustmentState
import com.muhammadnoman.photocraft.utils.fabricSaveHistorySnapshot
import com.muhammadnoman.photocraft.utils.getActiveImageObject
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import kotlinx.browser.window
import org.jetbrains.compose.web.css.px

@Composable
fun AdjustmentsPanel(canvas: dynamic, historyRef: MutableState<dynamic>, onAdjustmentChanged: () -> Unit) {
    var adj by remember(canvas) {
        val restored = if (canvas != null) fabricGetAdjustmentState(canvas) else null
        mutableStateOf(restored ?: AdjustmentState())
    }

    // Debounce timer ref for slider-drag coalescing
    val debounceTimer = remember { mutableStateOf<Int?>(null) }

    fun applyAdj(new: AdjustmentState) {
        adj = new
        if (canvas != null) {
            val img = getActiveImageObject(canvas)
            if (img != null) {
                fabricApplyAdjustments(img, new)
                // Debounce history save: wait 400ms after last slider move
                debounceTimer.value?.let { window.clearTimeout(it) }
                debounceTimer.value = window.setTimeout({
                    fabricSaveHistorySnapshot(historyRef)
                    onAdjustmentChanged()
                }, 400)
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.px)) {
        PanelTitle("Adjustments")

        SectionLabel("LIGHT")
        SliderRow("Brightness", adj.brightness, -100.0, 100.0) { applyAdj(adj.copy(brightness = it)) }
        SliderRow("Contrast", adj.contrast, -100.0, 100.0) { applyAdj(adj.copy(contrast = it)) }
        SliderRow("Highlights", adj.highlights, -100.0, 100.0) { applyAdj(adj.copy(highlights = it)) }
        SliderRow("Shadows", adj.shadows, -100.0, 100.0) { applyAdj(adj.copy(shadows = it)) }

        PanelDivider()
        SectionLabel("COLOR")
        SliderRow("Saturation", adj.saturation, -100.0, 100.0) { applyAdj(adj.copy(saturation = it)) }
        SliderRow("Hue", adj.hue, -180.0, 180.0) { applyAdj(adj.copy(hue = it)) }
        SliderRow("Temperature", adj.temperature, -100.0, 100.0) { applyAdj(adj.copy(temperature = it)) }
        SliderRow("Tint", adj.tint, -100.0, 100.0) { applyAdj(adj.copy(tint = it)) }

        PanelDivider()
        SectionLabel("DETAIL")
        SliderRow("Blur", adj.blur, 0.0, 100.0) { applyAdj(adj.copy(blur = it)) }
        SliderRow("Noise", adj.noise, 0.0, 100.0) { applyAdj(adj.copy(noise = it)) }
        SliderRow("Vignette", adj.vignette, 0.0, 100.0) { applyAdj(adj.copy(vignette = it)) }

        PanelDivider()
        SectionLabel("OPACITY")
        SliderRow("Opacity", adj.opacity, 0.0, 100.0) { applyAdj(adj.copy(opacity = it)) }

        PanelDivider()
        ActionButton("Reset Adjustments", "fa-rotate-left", onClick = {
            val reset = AdjustmentState()
            adj = reset
            if (canvas != null) {
                val img = getActiveImageObject(canvas)
                if (img != null) fabricApplyAdjustments(img, reset)
            }
            fabricSaveHistorySnapshot(historyRef)
            onAdjustmentChanged()
        })
    }
}
