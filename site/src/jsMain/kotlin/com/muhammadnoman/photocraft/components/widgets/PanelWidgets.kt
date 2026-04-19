package com.muhammadnoman.photocraft.components.widgets


import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.muhammadnoman.photocraft.styles.DangerBtnStyle
import com.muhammadnoman.photocraft.styles.PanelDividerStyle
import com.muhammadnoman.photocraft.styles.PanelTitleStyle
import com.muhammadnoman.photocraft.styles.PrimaryBtnStyle
import com.muhammadnoman.photocraft.styles.SecondaryBtnStyle
import com.muhammadnoman.photocraft.styles.SectionLabelStyle
import com.muhammadnoman.photocraft.styles.SliderLabelRowStyle
import com.muhammadnoman.photocraft.styles.SliderValueBadgeStyle
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import com.varabyte.kobweb.compose.ui.graphics.Color


@Composable
fun PanelTitle(text: String) {
    SpanText(
        text = text.uppercase(),
        modifier = PanelTitleStyle.toModifier()
    )
}


@Composable
fun SectionLabel(text: String) {
    SpanText(
        text = text,
        modifier = SectionLabelStyle.toModifier()
    )
}


@Composable
fun PanelDivider() {
    Box(modifier = PanelDividerStyle.toModifier())
}

// Slider Row

@Composable
fun SliderRow(
    label: String,
    value: Double,
    min: Double = -100.0,
    max: Double = 100.0,
    step: Double = 1.0,
    onValueChange: (Double) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().margin(bottom = 14.px)) {
        // Label + value badge
        Row(
            modifier = SliderLabelRowStyle.toModifier().fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpanText(
                text = label,
                modifier = Modifier
                    .fontSize(12.px)
                    .color(Color.rgb(0x888888))
            )
            SpanText(
                text = value.toInt().toString(),
                modifier = SliderValueBadgeStyle.toModifier()
            )
        }

        // Native range input — styled globally via registerStyleBase in AppStyles
        Input(type = InputType.Range, attrs = {
            attr("min", min.toString())
            attr("max", max.toString())
            attr("step", step.toString())
            value(value.toString())
            onInput { e -> onValueChange(e.value!!.toDouble()) }
            style { property("width", "100%") }
        })
    }
}

// Color Picker Row

@Composable
fun ColorPickerRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .justifyContent(JustifyContent.SpaceBetween)
            .alignItems(AlignItems.Center)
            .margin(bottom = 10.px),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpanText(
            text = label,
            modifier = Modifier.fontSize(12.px).color(Color.rgb(0x888888))
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.px)
        ) {
            Input(type = InputType.Color, attrs = {
                value(value)
                onInput { e -> onValueChange(e.value) }
                style {
                    width(28.px); height(28.px)
                    property("border", "1px solid #2a2a2a")
                    property("border-radius", "4px")
                    property("cursor", "pointer")
                    property("background", "none")
                    property("padding", "2px")
                }
            })
            SpanText(
                text = value,
                modifier = Modifier
                    .fontSize(11.px)
                    .fontFamily("'JetBrains Mono'", "monospace")
                    .color(Color.rgb(0x555555))
            )
        }
    }
}

// Action Button (primary / secondary / danger)

@Composable
fun ActionButton(
    label: String,
    icon: String = "",
    onClick: () -> Unit,
    variant: String = "secondary",
    fullWidth: Boolean = true
) {
    val baseModifier = if (fullWidth) Modifier.fillMaxWidth() else Modifier
    val styleModifier = when (variant) {
        "primary" -> PrimaryBtnStyle.toModifier()
        "danger"  -> DangerBtnStyle.toModifier()
        else      -> SecondaryBtnStyle.toModifier()
    }

    Button(attrs = {
        onClick { onClick() }
        style {
            if (fullWidth) property("width", "100%")
        }
    }) {
        // We drive all styling via our CssStyle, so just apply it to the outer button
        // The Button composable from Silk wraps HTML <button>; we prefer direct <button>
        // via the Dom API here for full style control.
    }

    // Direct DOM button (gives us complete Modifier control)
      Button(attrs = {
        onClick { onClick() }
        style {
            if (fullWidth) property("width", "100%")
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "center")
            property("gap", "6px")
            property("border-radius", "8px")
            property("font-family", "'Space Grotesk', sans-serif")
            property("font-size", "12px")
            property("font-weight", "500")
            property("padding", "8px 12px")
            property("cursor", "pointer")
            property("transition", "all 0.2s ease")
            property("outline", "none")
            when (variant) {
                "primary" -> {
                    property("border", "none")
                    property("background", "#c8923f")
                    property("color", "#fff")
                }
                "danger" -> {
                    property("border", "1px solid rgba(220,50,50,0.4)")
                    property("background", "transparent")
                    property("color", "#e05555")
                }
                else -> {
                    property("border", "1px solid #2a2a2a")
                    property("background", "#242424")
                    property("color", "#888888")
                }
            }
        }
        onMouseEnter {
            // hover handled via CSS :hover (or inline style toggling below)
        }
    }) {
        if (icon.isNotEmpty()) {
            I(attrs = {
                classes("fa-solid", icon)
                style { property("font-size", "11px") }
            })
        }
        Text(label)
    }
}


@Composable
fun IconButton(
    icon: String,
    title: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(attrs = {
        onClick { if (enabled) onClick() }
        title(title)
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

// Toggle Button (Bold/Italic/Underline/Alignment)

@Composable
fun ToggleButton(
    label: String? = null,
    icon: String? = null,
    title: String = "",
    active: Boolean,
    width: Int? = null,
    onClick: () -> Unit
) {
    Button(attrs = {
        onClick { onClick() }
        if (title.isNotEmpty()) title(title)
        style {
            if (width != null) property("width", "${width}px")
            property("height", "32px")
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "center")
            property("padding", "6px 8px")
            property("border", if (active) "1px solid #c8923f" else "1px solid #2a2a2a")
            property("border-radius", "4px")
            property("background", if (active) "rgba(200,146,63,0.15)" else "transparent")
            property("color", if (active) "#c8923f" else "#888888")
            property("font-family", if (label != null) "Georgia, serif" else "'Space Grotesk', sans-serif")
            property("font-size", "13px")
            property("font-weight", "bold")
            property("cursor", "pointer")
            property("transition", "all 0.2s ease")
            property("outline", "none")
        }
    }) {
        if (label != null) Text(label)
        if (icon != null) I(attrs = { classes("fa-solid", icon); style { property("font-size", "11px") } })
    }
}

// Accent Dot status indicator in panel header

@Composable
fun AccentDot(active: Boolean) {
    Box(
        modifier = Modifier
            .size(6.px)
            .borderRadius(50.percent)
            .backgroundColor(
                if (active) Color.rgb(0xc8923f) else Color.rgb(0x555555)
            )
    )
}
