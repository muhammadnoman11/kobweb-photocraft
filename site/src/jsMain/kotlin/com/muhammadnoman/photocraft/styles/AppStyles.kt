package com.muhammadnoman.photocraft.styles

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.selectors.hover
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import com.varabyte.kobweb.compose.ui.graphics.Color


// Global Silk Init

@InitSilk
fun initPhotoCraftStyles(ctx: InitSilkContext) {
    ctx.stylesheet.registerStyleBase("*") {
        Modifier.boxSizing(BoxSizing.BorderBox)
    }
    ctx.stylesheet.registerStyleBase("html, body") {
        Modifier
            .margin(0.px).padding(0.px)
            .width(100.percent).height(100.percent)
            .overflow(Overflow.Hidden)
            .fontFamily("'Space Grotesk'", "sans-serif")
            .color(Colors.White)
    }
    ctx.stylesheet.registerStyleBase("#root") {
        Modifier
            .width(100.percent).height(100.percent)
            .overflow(Overflow.Hidden)
    }
    ctx.stylesheet.registerStyleBase("*::-webkit-scrollbar") {
        Modifier.width(4.px)
    }
    ctx.stylesheet.registerStyleBase("*::-webkit-scrollbar-track") {
        Modifier.backgroundColor(Color.rgb(0x1a1a1a))
    }
    ctx.stylesheet.registerStyleBase("*::-webkit-scrollbar-thumb") {
        Modifier
            .backgroundColor(Color.rgb(0x3a3a3a))
            .borderRadius(2.px)
    }
    ctx.stylesheet.registerStyleBase("input[type='range']") {
        Modifier
            .width(100.percent)
            .height(4.px)
            .borderRadius(2.px)
            .outline(0.px, LineStyle.None, Colors.Transparent)
            .cursor(Cursor.Pointer)
    }
}

// App Shell Styles

val AppShellStyle = CssStyle.base {


    Modifier
        .display(DisplayStyle.Flex)
        .flexDirection(FlexDirection.Column)
        .height(100.vh)
        .width(100.percent)
        .overflow(Overflow.Hidden)
        .backgroundColor(Color.rgb(0x0f0f0f))
        .fontFamily("'Space Grotesk'", "sans-serif")
        .color(Color.rgb(0xf0f0f0))
}

// Body row: sidebar + canvas + properties.
val AppBodyStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.Flex)
        .flexDirection(FlexDirection.Row)
        .flexGrow(1)
        .minHeight(0.px)
        .width(100.percent)
        .overflow(Overflow.Hidden)
}

// Header Styles

val HeaderStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.Flex)
        .alignItems(AlignItems.Center)
        .justifyContent(JustifyContent.SpaceBetween)
        .fillMaxWidth()
        .height(52.px)
        .minHeight(52.px)
        .flexShrink(0)
        .backgroundColor(Color.rgb(0x1a1a1a))
        .borderBottom(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
        .padding(leftRight = 16.px)
        .zIndex(100)
        .gap(12.px)
}

val HeaderTitleInputStyle = CssStyle.base {
    Modifier
        .backgroundColor(Color.rgb(0x242424))
        .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
        .borderRadius(8.px)
        .color(Color.rgb(0xf0f0f0))
        .fontFamily("'Space Grotesk'", "sans-serif")
        .fontSize(13.px)
        .fontWeight(FontWeight.Medium)
        .padding(6.px, 14.px)
        .textAlign(TextAlign.Center)
        .outline(0.px, LineStyle.None, Colors.Transparent)
        .maxWidth(300.px)
        .fillMaxWidth()
}

val HeaderIconBtnStyle = CssStyle {
    base {
        Modifier
            .width(32.px).height(32.px)
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .borderRadius(4.px)
            .backgroundColor(Colors.Transparent)
            .color(Color.rgb(0xf0f0f0))
            .cursor(Cursor.Pointer)
            .fontSize(13.px)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}

//  Sidebar Tools Styles
val SidebarStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .flexDirection(FlexDirection.Column)
            .width(110.px)
            .minWidth(110.px)
            .flexShrink(0)
            .flexGrow(0)
            .height(100.percent)
            .backgroundColor(Color.rgb(0x1a1a1a))
            .borderRight(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .overflow(Overflow.Hidden)
    }

    Breakpoint.MD {
        Modifier
            .width(80.px)
            .minWidth(80.px)
    }

    Breakpoint.ZERO {
        Modifier
            .width(70.px)
            .minWidth(70.px)
    }
}

val SidebarItemStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .flexDirection(FlexDirection.Column)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .gap(5.px)
            .padding(12.px, 8.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .userSelect(UserSelect.None)
    }
    hover {
        Modifier.backgroundColor(Color.rgba(255, 255, 255, 8))
    }
}

// Tools Properties Panel Styles

val PropertiesPanelStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .flexDirection(FlexDirection.Column)
            .width(264.px)
            .minWidth(264.px)
            .maxWidth(264.px)
            .flexShrink(0)
            .flexGrow(0)
            .height(100.percent)
            .backgroundColor(Color.rgb(0x1a1a1a))
            .borderLeft(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            // Single overflow() call — avoids the overflowY/zIndex resolution errors
            .overflow(Overflow.Hidden)
    }
    Breakpoint.MD {
        Modifier
            .width(240.px)
            .minWidth(240.px)
            .maxWidth(240.px)
    }
}

// Center  Canvas Area

val CanvasAreaStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.Flex)
        .flexDirection(FlexDirection.Column)
        .alignItems(AlignItems.Center)
        .justifyContent(JustifyContent.Center)
        .minWidth(0.px)
        .minHeight(0.px)
        .backgroundColor(Color.rgb(0x0f0f0f))
        .position(Position.Relative)
        .overflow(Overflow.Auto)
        .padding(24.px)

}

// Panel Inner Styles

val PanelHeaderStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.Flex)
        .alignItems(AlignItems.Center)
        .justifyContent(JustifyContent.SpaceBetween)
        .fillMaxWidth()
        .padding(14.px, 16.px, 12.px, 16.px)
        .borderBottom(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
        .position(Position.Sticky)
        .top(0.px)
        .backgroundColor(Color.rgb(0x1a1a1a))
        .zIndex(10)
        .flexShrink(0)
}


val PanelContentStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .minHeight(0.px)
        .position(Position.Relative)
        .overflow(Overflow.Auto)
}

val PanelTitleStyle = CssStyle.base {
    Modifier
        .fontSize(FontSize.Small)
        .fontWeight(FontWeight.SemiBold)
        .color(Color.rgb(0x888888))
        .letterSpacing(0.8.px)
        .margin(bottom = 14.px)
}

val SectionLabelStyle = CssStyle.base {
    Modifier
        .fontSize(10.px)
        .fontWeight(FontWeight.SemiBold)
        .color(Color.rgb(0x555555))
        .letterSpacing(0.8.px)
        .margin(top = 4.px, bottom = 8.px)
}

val PanelDividerStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .height(1.px)
        .backgroundColor(Color.rgb(0x2a2a2a))
        .margin(topBottom = 12.px)
}


val PrimaryBtnStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .gap(6.px)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .borderRadius(8.px)
            .backgroundColor(Color.rgb(0xc8923f))
            .color(Colors.White)
            .fontFamily("'Space Grotesk'", "sans-serif")
            .fontSize(12.px)
            .fontWeight(FontWeight.Medium)
            .padding(8.px, 12.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0xd9a455))
    }
}

val SecondaryBtnStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .gap(6.px)
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(8.px)
            .backgroundColor(Color.rgb(0x242424))
            .color(Color.rgb(0x888888))
            .fontFamily("'Space Grotesk'", "sans-serif")
            .fontSize(12.px)
            .fontWeight(FontWeight.Medium)
            .padding(8.px, 12.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x2a2a2a))
    }
}

val DangerBtnStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .gap(6.px)
            .border(1.px, LineStyle.Solid, Color.rgba(220, 50, 50, 102))
            .borderRadius(8.px)
            .backgroundColor(Colors.Transparent)
            .color(Color.rgb(0xe05555))
            .fontFamily("'Space Grotesk'", "sans-serif")
            .fontSize(12.px)
            .fontWeight(FontWeight.Medium)
            .padding(8.px, 12.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
    }
    hover {
        Modifier.backgroundColor(Color.rgba(220, 50, 50, 38))
    }
}

val IconBtnStyle = CssStyle {
    base {
        Modifier
            .width(32.px).height(32.px)
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(4.px)
            .backgroundColor(Colors.Transparent)
            .color(Color.rgb(0x888888))
            .cursor(Cursor.Pointer)
            .fontSize(11.px)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}

val ToggleBtnStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(4.px)
            .backgroundColor(Colors.Transparent)
            .color(Color.rgb(0x888888))
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
            .padding(6.px)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}


val SliderLabelRowStyle = CssStyle.base {
    Modifier
        .display(DisplayStyle.Flex)
        .justifyContent(JustifyContent.SpaceBetween)
        .alignItems(AlignItems.Center)
        .margin(bottom = 6.px)
}

val SliderValueBadgeStyle = CssStyle.base {
    Modifier
        .fontSize(11.px)
        .fontFamily("'JetBrains Mono'", "monospace")
        .color(Color.rgb(0xc8923f))
        .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 38))
        .padding(1.px, 6.px)
        .borderRadius(3.px)
        .minWidth(36.px)
        .textAlign(TextAlign.Center)
}

// Filter Card

val FilterCardStyle = CssStyle {
    base {
        Modifier
            .border(2.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(8.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .overflow(Overflow.Hidden)
            .backgroundColor(Color.rgb(0x1e1e1e))
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}

// Layer Row

val LayerRowStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .gap(8.px)
            .padding(6.px, 8.px)
            .borderRadius(4.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("background", 200.ms))
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}


val ShapeBtnStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .flexDirection(FlexDirection.Column)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .gap(4.px)
            .padding(10.px, 4.px)
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(8.px)
            .backgroundColor(Color.rgb(0x242424))
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
    }
    hover {
        Modifier
            .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 38))
            .border(1.px, LineStyle.Solid, Color.rgb(0xc8923f))
    }
}

// Sticker Card
val StickerCardStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .height(44.px)
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(8.px)
            .backgroundColor(Color.rgb(0x242424))
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .fontSize(22.px)
            .userSelect(UserSelect.None)
    }
    hover {
        Modifier
            .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 38))
            .border(1.px, LineStyle.Solid, Color.rgb(0xc8923f))
    }
}

// Upload Drop Zone

val DropZoneStyle = CssStyle {
    base {
        Modifier
            .border(2.px, LineStyle.Dashed, Color.rgb(0x3a3a3a))
            .borderRadius(8.px)
            .padding(24.px, 16.px)
            .textAlign(TextAlign.Center)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .backgroundColor(Color.rgb(0x242424))
            .margin(bottom = 16.px)
    }
    hover {
        Modifier
            .border(2.px, LineStyle.Dashed, Color.rgb(0xc8923f))
            .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 38))
    }
}

// Aspect Ratio Button

val AspectBtnStyle = CssStyle {
    base {
        Modifier
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(4.px)
            .backgroundColor(Colors.Transparent)
            .color(Color.rgb(0x888888))
            .fontFamily("'Space Grotesk'", "sans-serif")
            .fontSize(11.px)
            .padding(6.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
            .textAlign(TextAlign.Center)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}

// Tab Button

val TabBtnStyle = CssStyle {
    base {
        Modifier
            .border(1.px, LineStyle.Solid, Color.rgb(0x2a2a2a))
            .borderRadius(4.px)
            .backgroundColor(Colors.Transparent)
            .color(Color.rgb(0x888888))
            .fontFamily("'Space Grotesk'", "sans-serif")
            .fontSize(11.px)
            .padding(5.px, 10.px)
            .cursor(Cursor.Pointer)
            .transition(Transition.of("all", 200.ms))
            .outline(0.px, LineStyle.None, Colors.Transparent)
    }
    hover {
        Modifier.backgroundColor(Color.rgb(0x242424))
    }
}
