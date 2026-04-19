package com.muhammadnoman.photocraft.components.sidebar

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.breakpoint.rememberBreakpoint
import com.muhammadnoman.photocraft.models.ActiveTool
import com.muhammadnoman.photocraft.styles.SidebarItemStyle
import com.muhammadnoman.photocraft.styles.SidebarStyle
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import com.varabyte.kobweb.compose.ui.graphics.Color


data class SidebarItem(
    val tool: ActiveTool,
    val icon: String,
    val label: String
)

val sidebarItems = listOf(
    SidebarItem(ActiveTool.UPLOAD,      "fa-upload",      "Upload"),
    SidebarItem(ActiveTool.CROP,        "fa-crop-simple", "Crop"),
    SidebarItem(ActiveTool.FILTERS,     "fa-palette",     "Filters"),
    SidebarItem(ActiveTool.ADJUSTMENTS, "fa-sliders",     "Adjustments"),
    SidebarItem(ActiveTool.TEXT,        "fa-t",           "Text"),
    SidebarItem(ActiveTool.STICKERS,    "fa-face-smile",  "Stickers"),
    SidebarItem(ActiveTool.SHAPES,      "fa-shapes",      "Shapes"),
    SidebarItem(ActiveTool.LAYERS,      "fa-layer-group", "Layers"),
)


@Composable
fun Sidebar(
    activeTool: ActiveTool,
    onToolSelected: (ActiveTool) -> Unit
) {
    Column(
        modifier = SidebarStyle.toModifier(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sidebarItems.forEach { item ->
            SidebarToolItem(
                item = item,
                isActive = activeTool == item.tool,
                onClick = {
                    if (activeTool == item.tool) onToolSelected(ActiveTool.NONE)
                    else onToolSelected(item.tool)
                }
            )
        }

        // Push remaining space to bottom
        Box(modifier = Modifier.weight(1))
    }
}


@Composable
fun SidebarToolItem(
    item: SidebarItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val breakpoint = rememberBreakpoint()
    val showLabel  = breakpoint >= Breakpoint.LG

    // Active left-border accent + background via inline style
    // (CssStyle hover is defined in AppStyles.kt; active state is dynamic so inline)
    Column(
        modifier = SidebarItemStyle.toModifier()
            .fillMaxWidth()
            .borderLeft(
                width = 2.px,
                style = LineStyle.Solid,
                color = if (isActive) Color.rgb(0xc8923f) else Colors.Transparent
            )
            .backgroundColor(
                if (isActive) Color.rgba(0xc8, 0x92, 0x3f, 38) else Colors.Transparent
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(32.px)
                .display(DisplayStyle.Flex)
                .alignItems(AlignItems.Center)
                .justifyContent(JustifyContent.Center)
                .borderRadius(4.px)
                .cursor(Cursor.Pointer)
                .onClick { onClick() },
            contentAlignment = Alignment.Center
        ) {
            I(attrs = {
                classes("fa-solid", item.icon)
                style {
                    property("font-size", "16px")
                    property(
                        "color",
                        if (isActive) "#c8923f" else "#888888"
                    )
                    property("transition", "color 0.2s ease")
                }
            })
        }

        // Label — hidden on mobile/tablet via Breakpoint
        if (showLabel) {
            SpanText(
                text = item.label,
                modifier = Modifier
                    .fontSize(10.px)
                    .fontWeight(FontWeight.Medium)
                    .color(if (isActive) Color.rgb(0xc8923f) else Color.rgb(0x888888))
                    .textAlign(TextAlign.Center)
                    .lineHeight(1.2)
                    .transition(Transition.of("color", 200.ms))
            )
        }
    }
}
