package com.muhammadnoman.photocraft.components.canvas

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.*
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.toModifier
import com.muhammadnoman.photocraft.models.ActiveTool
import com.muhammadnoman.photocraft.styles.CanvasAreaStyle
import com.muhammadnoman.photocraft.utils.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.accept
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.JustifyContent
import com.varabyte.kobweb.compose.ui.graphics.Color


const val FABRIC_CANVAS_ID = "photocraft-canvas"

@Composable
fun CanvasArea(
    activeTool: ActiveTool,
    canvasRef: MutableState<dynamic>,
    historyRef: MutableState<dynamic>,
    cropRectRef: MutableState<dynamic>,
    onImageLoaded: () -> Unit,
    onObjectSelected: () -> Unit,
    onHistoryChanged: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDragOver by remember { mutableStateOf(false) }

    fun loadImageFile(file: File) {
        val reader = FileReader()
        reader.onload = { event ->
            val dataUrl = event.target.asDynamic().result as? String
            fun tryLoad(attempt: Int) {
                val canvas = canvasRef.value
                if (canvas != null) {
                    if (dataUrl != null) {
                        fabricLoadImageFromDataUrl(canvas, dataUrl) {
                            // Save snapshot after image load so undo can return to blank
                            fabricSaveHistorySnapshot(historyRef)
                            onImageLoaded()
                            onHistoryChanged()
                        }
                    }
                } else if (attempt < 30) {
                    window.setTimeout({ tryLoad(attempt + 1) }, 200)
                } else {
                    console.error("Canvas never became available after 30 attempts")
                }
            }
            tryLoad(0)
        }
        reader.readAsDataURL(file)
    }

    Box(
        modifier = CanvasAreaStyle.toModifier().then(modifier).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Div(attrs = {
            id("canvas-wrapper")
            style {
                property("display", "inline-block"); property("position", "relative")
                property("box-shadow", "0 8px 40px rgba(0,0,0,0.7), 0 0 0 1px rgba(255,255,255,0.04)")
                property("border-radius", "2px"); property("line-height", "0")
                property("background", "repeating-conic-gradient(#222 0% 25%, #1a1a1a 0% 50%) 0 0 / 20px 20px")
            }
            onDragOver { e -> e.preventDefault(); isDragOver = true }
            onDragLeave { isDragOver = false }
            onDrop { e ->
                e.preventDefault(); isDragOver = false
                val file = e.dataTransfer?.files?.get(0)
                if (file != null && file.type.startsWith("image/")) loadImageFile(file)
            }
        }) {
            Canvas(attrs = {
                id(FABRIC_CANVAS_ID)
                style { display(DisplayStyle.Block) }
            })
        }

        if (isDragOver) {
            Box(
                modifier = Modifier.position(Position.Fixed).top(0.px).left(0.px).right(0.px).bottom(0.px)
                    .backgroundColor(Color.rgba(0xc8, 0x92, 0x3f, 30))
                    .border(3.px, LineStyle.Dashed, Color.rgb(0xc8923f))
                    .zIndex(300).display(DisplayStyle.Flex).alignItems(AlignItems.Center).justifyContent(JustifyContent.Center)
                    .pointerEvents(PointerEvents.None),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.px)) {
                    I(attrs = { classes("fa-solid", "fa-cloud-arrow-up"); style { property("font-size", "48px"); property("color", "#c8923f") } })
                    SpanText("Drop Image Here", modifier = Modifier.fontSize(18.px).fontWeight(FontWeight.SemiBold).color(Color.rgb(0xc8923f)))
                }
            }
        }

        Input(type = InputType.File, attrs = {
            id("photocraft-file-input")
            accept("image/*")
            style { display(DisplayStyle.None) }
            onChange { e ->
                val target = e.target as? HTMLInputElement
                val file = target?.files?.get(0)
                if (file != null) { loadImageFile(file); target.value = "" }
            }
        })

        LaunchedEffect(Unit) {
            var attempts = 0
            fun tryInit() {
                attempts++
                if (isFabricLoaded()) {
                    injectFilterHelper()
                    window.setTimeout({
                        initFabricCanvas(canvasRef, historyRef, onObjectSelected, onHistoryChanged)
                    }, 50)
                } else if (attempts < 60) {
                    window.setTimeout({ tryInit() }, 200)
                } else {
                    console.error("Fabric.js failed to load after $attempts attempts")
                }
            }
            window.setTimeout({ tryInit() }, 100)
        }
    }
}

fun initFabricCanvas(
    canvasRef: MutableState<dynamic>,
    historyRef: MutableState<dynamic>,
    onObjectSelected: () -> Unit,
    onHistoryChanged: () -> Unit
) {
    val fabric = getFabric()
    if (fabric == null) { console.error("Fabric not available during init"); return }

    val canvasEl = document.getElementById(FABRIC_CANVAS_ID) as? HTMLCanvasElement
    if (canvasEl == null) { console.error("Canvas element not found"); return }

    val vw = window.innerWidth; val vh = window.innerHeight
    val sidebarW = if (vw < 640) 56 else 260
    val propsW = if (vw < 640) 0 else 280
    val headerH = 52; val padding = 48
    val canvasW = (vw - sidebarW - propsW - padding * 2).coerceIn(280, 1400)
    val canvasH = (vh - headerH - padding * 2).coerceIn(250, 1000)

    if (canvasRef.value != null) {
        try { js("canvasRef.value.dispose()") } catch (e: Exception) { }
    }

    val fabricCanvas = js("""
        (function() {
            return new fabric.Canvas(canvasEl, {
                width: canvasW, height: canvasH,
                backgroundColor: '#1e1e1e',
                preserveObjectStacking: true,
                enableRetinaScaling: false,
                allowTouchScrolling: false,
                isDrawingMode: false,
                selection: true,
                renderOnAddRemove: true,
                stopContextMenu: true,
                fireRightClick: false
            });
        })()
    """)

    if (fabricCanvas == null) { console.error("fabric.Canvas() returned null"); return }

    js("fabricCanvas.__pcMaxWidth = canvasW; fabricCanvas.__pcMaxHeight = canvasH;")
    canvasRef.value = fabricCanvas

    // Setup history BEFORE vignette (history object needed first)
    historyRef.value = setupFabricHistory(fabricCanvas)

    // Setup vignette overlay renderer
    js("_pcSetupVignette(fabricCanvas)")

    setupTouchSupport(fabricCanvas)
    setupBoundaryClamp(fabricCanvas)

    val onSelCb = { onObjectSelected() }
    val onHistCb = { onHistoryChanged() }
    js("""
        fabricCanvas.on('selection:created', function(){ onSelCb(); });
        fabricCanvas.on('selection:updated', function(){ onSelCb(); });
        fabricCanvas.on('selection:cleared', function(){ onSelCb(); });
        fabricCanvas.on('object:added',      function(){ onHistCb(); });
        fabricCanvas.on('object:modified',   function(){ onHistCb(); });
        fabricCanvas.on('object:removed',    function(){ onHistCb(); });
    """)

    val histRef = historyRef
    js("""
        if (window.__photoCraftKeyHandler)
            document.removeEventListener('keydown', window.__photoCraftKeyHandler);
        window.__photoCraftKeyHandler = function(e) {
            var tag = document.activeElement && document.activeElement.tagName;
            if (tag==='INPUT' || tag==='TEXTAREA') return;
            if ((e.ctrlKey||e.metaKey) && e.key==='z' && !e.shiftKey) {
                e.preventDefault();
                var h=histRef.value; if(h&&h.canUndo()){h.undo();onHistCb();}
            }
            if ((e.ctrlKey||e.metaKey) && (e.key==='y'||(e.key==='z'&&e.shiftKey))) {
                e.preventDefault();
                var h=histRef.value; if(h&&h.canRedo()){h.redo();onHistCb();}
            }
            if (e.key==='Delete'||e.key==='Backspace') {
                var active=fabricCanvas.getActiveObject();
                if(active&&active.selectable!==false&&active.type!=='i-text'&&!active.isEditing){
                    fabricCanvas.remove(active); fabricCanvas.requestRenderAll(); onHistCb();
                }
            }
        };
        document.addEventListener('keydown', window.__photoCraftKeyHandler);
    """)

    js("""
        var _canvas=fabricCanvas;
        if(window.__photoCraftResizeHandler)
            window.removeEventListener('resize',window.__photoCraftResizeHandler);
        var resizeTimer;
        window.__photoCraftResizeHandler=function(){
            clearTimeout(resizeTimer);
            resizeTimer=setTimeout(function(){
                var vw2=window.innerWidth,vh2=window.innerHeight;
                var sw=vw2<640?56:260,pw=vw2<640?0:280,hh=52,pad=48;
                var maxW=Math.max(280,vw2-sw-pw-pad*2);
                var maxH=Math.max(250,vh2-hh-pad*2);
                _canvas.__pcMaxWidth=maxW; _canvas.__pcMaxHeight=maxH;
                var img=_canvas.__pcBaseImage;
                if(img){
                    var iw=img.width,ih=img.height;
                    var scale=Math.min(maxW/iw,maxH/ih,1);
                    _canvas.setDimensions({width:Math.round(iw*scale),height:Math.round(ih*scale)});
                    img.set({left:0,top:0,originX:'left',originY:'top',scaleX:scale,scaleY:scale});
                    img.setCoords();
                } else {
                    _canvas.setDimensions({width:maxW,height:maxH});
                }
                _canvas.requestRenderAll();
            },250);
        };
        window.addEventListener('resize',window.__photoCraftResizeHandler);
    """)

    fabricCanvas.requestRenderAll()
    console.log("Fabric canvas initialized: ${canvasW}x${canvasH}")
}