package com.muhammadnoman.photocraft.components.panels

import androidx.compose.runtime.Composable
import com.muhammadnoman.photocraft.components.widgets.PanelDivider
import com.muhammadnoman.photocraft.components.widgets.PanelTitle
import com.muhammadnoman.photocraft.components.widgets.SectionLabel
import com.muhammadnoman.photocraft.styles.DropZoneStyle
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.I


@Composable
fun UploadPanel(onUploadClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.px), horizontalAlignment = Alignment.Start) {
        PanelTitle("Upload Image")

        // Drop zone
        Box(
            modifier = DropZoneStyle.toModifier().fillMaxWidth().onClick { onUploadClick() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.px)
            ) {
                I(attrs = {
                    classes("fa-solid", "fa-cloud-arrow-up"); style {
                    property(
                        "font-size",
                        "28px"
                    ); property("color", "#c8923f")
                }
                })
                SpanText(
                    "Click to Browse",
                    modifier = Modifier.fontSize(13.px).fontWeight(FontWeight.Medium).color(Color.rgb(0xf0f0f0))
                )
                SpanText("PNG, JPG, GIF, WebP, SVG", modifier = Modifier.fontSize(11.px).color(Color.rgb(0x555555)))
            }
        }

        SpanText(
            "or drag & drop an image on the canvas",
            modifier = Modifier.fontSize(11.px).color(Color.rgb(0x555555)).fillMaxWidth().textAlign(TextAlign.Center)
                .margin(bottom = 16.px)
        )

        PanelDivider()
        SectionLabel("SUPPORTED FORMATS")
        listOf(
            "fa-image" to "PNG / JPG / WebP",
            "fa-file-image" to "GIF / SVG / BMP",
            "fa-maximize" to "Up to 4K resolution"
        ).forEach { (icon, label) ->
            Row(
                modifier = Modifier.gap(8.px).padding(topBottom = 5.px),
                verticalAlignment = Alignment.CenterVertically
            ) {
                I(attrs = {
                    classes("fa-solid", icon); style {
                    property("color", "#c8923f"); property(
                    "font-size",
                    "11px"
                ); property("width", "14px")
                }
                })
                SpanText(label, modifier = Modifier.fontSize(12.px).color(Color.rgb(0x888888)))
            }
        }
    }
}