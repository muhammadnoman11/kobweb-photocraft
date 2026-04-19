package com.muhammadnoman.photocraft.components.common

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.muhammadnoman.photocraft.styles.HeaderStyle
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import com.varabyte.kobweb.compose.ui.graphics.Color
import org.jetbrains.compose.web.css.DisplayStyle

@Composable
fun TopHeader(
    title: String,
    canUndo: Boolean,
    canRedo: Boolean,
    hasImage: Boolean,
    onTitleChange: (String) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onSave: () -> Unit,
    onExport: () -> Unit
) {
    Row(
        modifier = HeaderStyle.toModifier(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Left: Logo
        Row(
            modifier = Modifier.gap(8.px).flexShrink(0),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo mark
            Box(
                modifier = Modifier
                    .size(28.px)
                    .borderRadius(7.px)
                    .background(
                        Color.rgb(0xe8a844)
                    )
                    .display(DisplayStyle.Flex)
                    .alignItems(AlignItems.Center)
                    .justifyContent(JustifyContent.Center),
                contentAlignment = Alignment.Center
            ) {
                I(attrs = {
                    classes("fa-solid", "fa-star")
                    style { property("color", "#fff"); property("font-size", "13px") }
                })
            }

            SpanText(
                text = "PhotoCraft",
                modifier = Modifier
                    .fontFamily("'Space Grotesk'", "sans-serif")
                    .fontWeight(FontWeight.Bold)
                    .fontSize(16.px)
                    .color(Color.rgb(0xf0f0f0))
                    .letterSpacing((-0.3).px)
                    .whiteSpace(WhiteSpace.NoWrap)
            )
        }

        // Center: Editable Title
        Input(type = InputType.Text, attrs = {
            value(title)
            onInput { e -> onTitleChange(e.value) }
            attr("placeholder", "Untitled Design")
            style {
                // Inherit all from HeaderTitleInputStyle; applied inline for input
                property("background", "#242424")
                property("border", "1px solid #2a2a2a")
                property("border-radius", "8px")
                property("color", "#f0f0f0")
                property("font-family", "'Space Grotesk', sans-serif")
                property("font-size", "13px")
                property("font-weight", "500")
                property("padding", "6px 14px")
                property("text-align", "center")
                property("outline", "none")
                property("transition", "all 0.2s ease")
                property("max-width", "300px")
                property("width", "100%")
            }
        })

        // Right: Action Buttons
        Row(
            modifier = Modifier.gap(8.px).flexShrink(0),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo / Redo icon buttons
            HeaderIconButton(
                icon = "fa-rotate-left",
                tooltip = "Undo (Ctrl+Z)",
                enabled = canUndo,
                onClick = onUndo
            )
            HeaderIconButton(
                icon = "fa-rotate-right",
                tooltip = "Redo (Ctrl+Y)",
                enabled = canRedo,
                onClick = onRedo
            )

            // Vertical divider
            Box(
                modifier = Modifier
                    .width(1.px)
                    .height(20.px)
                    .backgroundColor(Color.rgb(0x2a2a2a))
                    .margin(leftRight = 4.px)
            )

            // Save button (secondary style)
            HeaderTextButton(
                label = "Save",
                icon = "fa-floppy-disk",
                isPrimary = false,
                onClick = onSave
            )

            // Export button (primary/accent style)
            HeaderTextButton(
                label = "Export",
                icon = "fa-download",
                isPrimary = true,
                onClick = onExport
            )
        }
    }
}

// Private header sub-composables

@Composable
private fun HeaderIconButton(
    icon: String,
    tooltip: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(attrs = {
        onClick { if (enabled) onClick() }
        title(tooltip)
        style {
            property("width", "32px"); property("height", "32px")
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "center")
            property("border", "none")
            property("border-radius", "4px")
            property("background", "transparent")
            property("color", if (enabled) "#f0f0f0" else "#555555")
            property("cursor", if (enabled) "pointer" else "not-allowed")
            property("transition", "all 0.2s ease")
            property("outline", "none")
            property("font-size", "13px")
        }
    }) {
        I(attrs = { classes("fa-solid", icon) })
    }
}

@Composable
private fun HeaderTextButton(
    label: String,
    icon: String,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    Button(attrs = {
        onClick { onClick() }
        style {
            property("display", "flex")
            property("align-items", "center")
            property("gap", "6px")
            property("border", if (isPrimary) "none" else "1px solid #2a2a2a")
            property("border-radius", "8px")
            property("background", if (isPrimary) "#c8923f" else "transparent")
            property("color", if (isPrimary) "#fff" else "#f0f0f0")
            property("font-family", "'Space Grotesk', sans-serif")
            property("font-size", "13px")
            property("font-weight", "500")
            property("padding", "6px 12px")
            property("cursor", "pointer")
            property("transition", "all 0.2s ease")
            property("white-space", "nowrap")
            property("outline", "none")
        }
    }) {
        I(attrs = { classes("fa-solid", icon) })
        Span { Text(label) }
    }
}
