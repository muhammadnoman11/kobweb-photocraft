package com.muhammadnoman.photocraft.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.silk.style.toModifier
import com.muhammadnoman.photocraft.components.canvas.CanvasArea
import com.muhammadnoman.photocraft.components.common.TopHeader
import com.muhammadnoman.photocraft.components.panels.PropertiesPanel
import com.muhammadnoman.photocraft.components.sidebar.Sidebar
import com.muhammadnoman.photocraft.models.ActiveTool
import com.muhammadnoman.photocraft.styles.AppBodyStyle
import com.muhammadnoman.photocraft.styles.AppShellStyle
import com.muhammadnoman.photocraft.utils.*
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement


@Page
@Composable
fun IndexPage() {
    var activeTool by remember { mutableStateOf(ActiveTool.UPLOAD) }
    var projectTitle by remember { mutableStateOf("Untitled Design") }
    var hasImage by remember { mutableStateOf(false) }

    val canvasRef = remember { mutableStateOf<dynamic>(null) }
    val historyRef = remember { mutableStateOf<dynamic>(null) }
    val cropRectRef = remember { mutableStateOf<dynamic>(null) }

    var canUndo by remember { mutableStateOf(false) }
    var canRedo by remember { mutableStateOf(false) }

    // History state refresh
    fun refreshHistoryState() {
        val hist = historyRef.value ?: return
        canUndo = (js("hist.canUndo()") as? Boolean) ?: false
        canRedo = (js("hist.canRedo()") as? Boolean) ?: false
    }

    // File dialog
    fun openFileDialog() {
        (document.getElementById("photocraft-file-input") as? HTMLInputElement)?.click()
    }

    fun selectTool(tool: ActiveTool) {
        if (activeTool == ActiveTool.CROP && tool != ActiveTool.CROP) {
            val cropRect = cropRectRef.value
            if (cropRect != null) {
                fabricCancelCrop(canvasRef.value, cropRect)
                cropRectRef.value = null
            }
        }
        activeTool = tool
        if (tool == ActiveTool.UPLOAD) openFileDialog()
    }

//    val onUndo: () -> Unit = {
//        val hist = historyRef.value
//        val canvas = canvasRef.value
//        if (hist != null && canvas != null) {
//            val didUndo = js("hist.undo()") as? Boolean ?: false
//            if (didUndo) refreshHistoryState()
//        }
//    }
//
//    val onRedo: () -> Unit = {
//        val hist = historyRef.value
//        val canvas = canvasRef.value
//        if (hist != null && canvas != null) {
//            val didRedo = js("hist.redo()") as? Boolean ?: false
//            if (didRedo) refreshHistoryState()
//        }
//    }

    val onUndo: () -> Unit = {
        val hist = historyRef.value
        if (hist != null) {
            val didUndo = js("hist.undo(function(){ })") as? Boolean ?: false
            if (didUndo) refreshHistoryState()
        }
    }

    val onRedo: () -> Unit = {
        val hist = historyRef.value
        if (hist != null) {
            val didRedo = js("hist.redo(function(){ })") as? Boolean ?: false
            if (didRedo) refreshHistoryState()
        }
    }

    val onSave: () -> Unit = {
        val canvas = canvasRef.value
        if (canvas != null) fabricSaveJson(canvas, "$projectTitle.json")
    }

    val onExport: () -> Unit = {
        val canvas = canvasRef.value
        if (canvas != null) fabricExport(canvas, "$projectTitle.png")
    }

    // App Shell (Column: header + body)
    Column(
        modifier = AppShellStyle.toModifier(),
        horizontalAlignment = Alignment.Start
    ) {
        TopHeader(
            title = projectTitle,
            canUndo = canUndo,
            canRedo = canRedo,
            hasImage = hasImage,
            onTitleChange = { projectTitle = it },
            onUndo = onUndo,
            onRedo = onRedo,
            onSave = onSave,
            onExport = onExport
        )

        Row(
            modifier = AppBodyStyle.toModifier(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Left Sidebar
            Sidebar(
                activeTool = activeTool,
                onToolSelected = { tool ->
                    if (activeTool == tool) selectTool(ActiveTool.NONE)
                    else selectTool(tool)
                }
            )

            // Canvas Center
            CanvasArea(
                activeTool = activeTool,
                canvasRef = canvasRef,
                historyRef = historyRef,
                cropRectRef = cropRectRef,
                modifier = Modifier.weight(1),
                onImageLoaded = {
                    hasImage = true
                    selectTool(ActiveTool.ADJUSTMENTS)
                    refreshHistoryState()
                },
                onObjectSelected = { refreshHistoryState() },
                onHistoryChanged = { refreshHistoryState() }
            )

            // Right Properties Panel
            PropertiesPanel(
                activeTool = activeTool,
                canvas = canvasRef.value,
                hasImage = hasImage,
                cropRectRef = cropRectRef,
                historyRef = historyRef,
                onChanged = { refreshHistoryState() },
                onUploadClick = { openFileDialog() },
                onCropApplied = { refreshHistoryState() },
            )
        }
    }
}