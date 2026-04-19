package com.muhammadnoman.photocraft.utils

import com.muhammadnoman.photocraft.models.AdjustmentState
import com.muhammadnoman.photocraft.models.FilterPreset
import com.muhammadnoman.photocraft.models.LayerItem
import com.muhammadnoman.photocraft.models.ShapeType
import com.muhammadnoman.photocraft.models.TextProperties

/**
 * Fabric.js interop
 */

fun getFabric(): dynamic = js("typeof fabric !== 'undefined' ? fabric : null")
fun isFabricLoaded(): Boolean = js("typeof fabric !== 'undefined'") as Boolean

// Image Loading

private var imageLoadCallbackCounter = 0

fun fabricLoadImageFromDataUrl(canvas: dynamic, dataUrl: String, onLoad: (dynamic) -> Unit) {
    val fabric = getFabric() ?: run { console.error("Fabric.js not loaded!"); return }
    val callbackId = "imgCb_${imageLoadCallbackCounter++}"
    js("window.__fabricCallbacks = window.__fabricCallbacks || {}")
    js("window.__fabricCallbacks[callbackId] = function(img) { onLoad(img); }")
    js(
        """
        var cb = window.__fabricCallbacks[callbackId];
        fabric.Image.fromURL(dataUrl, function(img) {
            if (!img || !img.width || !img.height) {
                console.error('Image failed to load or has zero dimensions');
                delete window.__fabricCallbacks[callbackId];
                return;
            }

            img.set({
                selectable: false,
                evented: false,
                hasControls: false,
                hasBorders: false,
                lockMovementX: true,
                lockMovementY: true,
                lockScalingX: true,
                lockScalingY: true,
                lockRotation: true,
                hoverCursor: 'default'
            });

            var maxW = canvas.__pcMaxWidth  || window.innerWidth  - 320;
            var maxH = canvas.__pcMaxHeight || window.innerHeight - 100;
            maxW = Math.max(300, maxW);
            maxH = Math.max(250, maxH);

            var iw = img.width, ih = img.height;
            var scale = Math.min(maxW / iw, maxH / ih, 1);
            var newW = Math.round(iw * scale);
            var newH = Math.round(ih * scale);

            canvas.setDimensions({ width: newW, height: newH });

            img.set({
                left: 0,
                top: 0,
                originX: 'left',
                originY: 'top',
                scaleX: scale,
                scaleY: scale
            });

            canvas.__pcBaseImage = img;
            img._pcFilterState = {
                brightness:0, contrast:0, saturation:0, hue:0,
                blur:0, noise:0, opacity:100, grayscale:0, sepia:0,
                presetBrightness:0, presetContrast:0, presetSaturation:0, presetBlur:0
            };

            canvas.clear();
            canvas.add(img);
            canvas.discardActiveObject();
            canvas.requestRenderAll();

            delete window.__fabricCallbacks[callbackId];
            if (cb) cb(img);
        }, { crossOrigin: 'anonymous' });
    """
    )
}

// Filter helper injection

fun injectFilterHelper() {
    js(
        """
        if (!window._pcRebuildFilters) {
            window._pcRebuildFilters = function(img, fabric) {
                var s = img._pcFilterState;
                img.filters = [];
                var totalBrightness = ((s.presetBrightness||0) + (s.brightness||0));
                if (totalBrightness !== 0)
                    img.filters.push(new fabric.Image.filters.Brightness({ brightness: totalBrightness/100 }));
                var totalContrast = ((s.presetContrast||0) + (s.contrast||0));
                if (totalContrast !== 0)
                    img.filters.push(new fabric.Image.filters.Contrast({ contrast: totalContrast/100 }));
                var totalSaturation = ((s.presetSaturation||0) + (s.saturation||0));
                if (totalSaturation !== 0)
                    img.filters.push(new fabric.Image.filters.Saturation({ saturation: totalSaturation/100 }));
                if ((s.hue||0) !== 0)
                    img.filters.push(new fabric.Image.filters.HueRotation({ rotation: s.hue/360 }));
                if ((s.grayscale||0) > 0) img.filters.push(new fabric.Image.filters.Grayscale());
                if ((s.sepia||0) > 0)     img.filters.push(new fabric.Image.filters.Sepia());
                var totalBlur = ((s.presetBlur||0) + (s.blur||0));
                if (totalBlur > 0)
                    img.filters.push(new fabric.Image.filters.Blur({ blur: totalBlur/100 }));
                if ((s.noise||0) > 0)
                    img.filters.push(new fabric.Image.filters.Noise({ noise: s.noise*1.5 }));
                img.set('opacity', (s.opacity !== undefined ? s.opacity : 100) / 100);
                img.applyFilters();
                img.canvas && img.canvas.requestRenderAll();
            };
        }
    """
    )
}

fun fabricApplyPresetFilter(img: dynamic, preset: FilterPreset) {
    val fabric = getFabric() ?: return
    val brightness = preset.brightness
    val contrast = preset.contrast
    val saturation = preset.saturation
    val grayscale = preset.grayscale
    val sepia = preset.sepia
    val blur = preset.blur
    js(
        """
        if (!img._pcFilterState) {
            img._pcFilterState = { brightness:0,contrast:0,saturation:0,hue:0,
                blur:0,noise:0,opacity:100,grayscale:0,sepia:0,
                presetBrightness:0,presetContrast:0,presetSaturation:0,presetBlur:0 };
        }
        img._pcFilterState.presetBrightness = brightness;
        img._pcFilterState.presetContrast   = contrast;
        img._pcFilterState.presetSaturation = saturation;
        img._pcFilterState.grayscale        = grayscale;
        img._pcFilterState.sepia            = sepia;
        img._pcFilterState.presetBlur       = blur;
        _pcRebuildFilters(img, fabric);
    """
    )
}

fun fabricApplyAdjustments(img: dynamic, adj: AdjustmentState) {
    val fabric = getFabric() ?: return
    val brightness = adj.brightness
    val contrast = adj.contrast
    val saturation = adj.saturation
    val hue = adj.hue
    val blur = adj.blur
    val noise = adj.noise
    val opacity = adj.opacity
    js(
        """
        if (!img._pcFilterState) {
            img._pcFilterState = { brightness:0,contrast:0,saturation:0,hue:0,
                blur:0,noise:0,opacity:100,grayscale:0,sepia:0,
                presetBrightness:0,presetContrast:0,presetSaturation:0,presetBlur:0 };
        }
        img._pcFilterState.brightness = brightness;
        img._pcFilterState.contrast   = contrast;
        img._pcFilterState.saturation = saturation;
        img._pcFilterState.hue        = hue;
        img._pcFilterState.blur       = blur;
        img._pcFilterState.noise      = noise;
        img._pcFilterState.opacity    = opacity;
        _pcRebuildFilters(img, fabric);
    """
    )
}

// Read back filter state from image

fun fabricGetAdjustmentState(canvas: dynamic): AdjustmentState? {
    if (canvas == null) return null
    val img = getActiveImageObject(canvas) ?: return null
    val state = js("img._pcFilterState") ?: return null
    return AdjustmentState(
        brightness  = (js("state.brightness")  as? Double) ?: 0.0,
        contrast    = (js("state.contrast")    as? Double) ?: 0.0,
        saturation  = (js("state.saturation")  as? Double) ?: 0.0,
        hue         = (js("state.hue")         as? Double) ?: 0.0,
        blur        = (js("state.blur")        as? Double) ?: 0.0,
        noise       = (js("state.noise")       as? Double) ?: 0.0,
        opacity     = (js("state.opacity")     as? Double) ?: 100.0,
        highlights  = 0.0,
        shadows     = 0.0,
        temperature = 0.0,
        tint        = 0.0,
        vignette    = 0.0
    )
}

fun fabricGetActivePresetId(canvas: dynamic): String {
    if (canvas == null) return "none"
    val img = getActiveImageObject(canvas) ?: return "none"
    return (js("img._pcActivePresetId") as? String) ?: "none"
}

fun fabricSetActivePresetId(canvas: dynamic, presetId: String) {
    if (canvas == null) return
    val img = getActiveImageObject(canvas) ?: return
    js("img._pcActivePresetId = presetId")
}

//  Filter thumbnail

fun fabricGetFilterThumbnail(img: dynamic, preset: FilterPreset, size: Int = 60): String {
    val brightness = preset.brightness
    val contrast = preset.contrast
    val saturation = preset.saturation
    val grayscale = preset.grayscale
    val sepia = preset.sepia
    val blur = preset.blur
    return js(
        """
        (function() {
            try {
                var src = img.getElement ? img.getElement() : img._element;
                if (!src) return '';
                var iw = src.naturalWidth || src.width;
                var ih = src.naturalHeight || src.height;
                var offscreen = document.createElement('canvas');
                offscreen.width = size; offscreen.height = size;
                var ctx2 = offscreen.getContext('2d');
                var scale2 = Math.max(size/iw, size/ih);
                var dx=(size-iw*scale2)/2, dy=(size-ih*scale2)/2;
                var filters='';
                if (brightness!==0) filters+='brightness('+(1+brightness/100)+') ';
                if (contrast!==0)   filters+='contrast('+(1+contrast/100)+') ';
                if (saturation!==0) filters+='saturate('+(1+saturation/100)+') ';
                if (grayscale>0)    filters+='grayscale(1) ';
                if (sepia>0)        filters+='sepia(0.8) ';
                if (blur>0)         filters+='blur('+(blur/100*3)+'px) ';
                if (filters.trim()) ctx2.filter = filters.trim();
                ctx2.drawImage(src, dx, dy, iw*scale2, ih*scale2);
                return offscreen.toDataURL('image/jpeg',0.7);
            } catch(e) { return ''; }
        })()
    """
    ) as String
}

//  Text

fun fabricAddText(canvas: dynamic, props: TextProperties) {
    val fabric = getFabric() ?: return
    val text = props.text
    val fontFamily = props.fontFamily
    val fontSize = props.fontSize
    val fill = props.fill
    val fontWeight = props.fontWeight
    val fontStyle = props.fontStyle
    val underline = props.underline
    val textAlign = props.textAlign
    val lineHeight = props.lineHeight
    val charSpacing = props.charSpacing * 10
    val w = canvas.getWidth().unsafeCast<Double>()
    val h = canvas.getHeight().unsafeCast<Double>()
    js(
        """
        var textObj = new fabric.IText(text, {
            left:w/2, top:h/2, originX:'center', originY:'center',
            fontFamily:fontFamily, fontSize:fontSize, fill:fill,
            fontWeight:fontWeight, fontStyle:fontStyle, underline:underline,
            textAlign:textAlign, lineHeight:lineHeight, charSpacing:charSpacing,
            selectable:true, evented:true
        });
        canvas.add(textObj);
        canvas.setActiveObject(textObj);
        canvas.requestRenderAll();
    """
    )
}

fun fabricUpdateTextObject(obj: dynamic, props: TextProperties) {
    val text = props.text
    val fontFamily = props.fontFamily
    val fontSize = props.fontSize
    val fill = props.fill
    val fontWeight = props.fontWeight
    val fontStyle = props.fontStyle
    val underline = props.underline
    val textAlign = props.textAlign
    val lineHeight = props.lineHeight
    val charSpacing = props.charSpacing * 10
    js(
        """
        obj.set({ text:text, fontFamily:fontFamily, fontSize:fontSize, fill:fill,
            fontWeight:fontWeight, fontStyle:fontStyle, underline:underline,
            textAlign:textAlign, lineHeight:lineHeight, charSpacing:charSpacing });
        obj.canvas && obj.canvas.requestRenderAll();
    """
    )
}

// Shapes

fun fabricAddShape(canvas: dynamic, shapeType: ShapeType, color: String = "#c8923f") {
    val fabric = getFabric() ?: return
    val w = canvas.getWidth().unsafeCast<Double>()
    val h = canvas.getHeight().unsafeCast<Double>()
    val cx = w / 2
    val cy = h / 2
    when (shapeType) {
        ShapeType.RECTANGLE -> js(
            """
            var r=new fabric.Rect({left:cx-75,top:cy-50,width:150,height:100,fill:color,rx:6,ry:6});
            canvas.add(r);canvas.setActiveObject(r);canvas.requestRenderAll();"""
        )
        ShapeType.CIRCLE -> js(
            """
            var c=new fabric.Circle({left:cx-60,top:cy-60,radius:60,fill:color});
            canvas.add(c);canvas.setActiveObject(c);canvas.requestRenderAll();"""
        )
        ShapeType.TRIANGLE -> js(
            """
            var t=new fabric.Triangle({left:cx-60,top:cy-60,width:120,height:100,fill:color});
            canvas.add(t);canvas.setActiveObject(t);canvas.requestRenderAll();"""
        )
        ShapeType.LINE -> js(
            """
            var l=new fabric.Line([cx-80,cy,cx+80,cy],{stroke:color,strokeWidth:4});
            canvas.add(l);canvas.setActiveObject(l);canvas.requestRenderAll();"""
        )
        else -> js(
            """
            var s=new fabric.Polygon([
                {x:0,y:-60},{x:14,y:-20},{x:57,y:-20},{x:23,y:8},
                {x:35,y:52},{x:0,y:28},{x:-35,y:52},{x:-23,y:8},{x:-57,y:-20},{x:-14,y:-20}
            ],{left:cx-60,top:cy-60,fill:color});
            canvas.add(s);canvas.setActiveObject(s);canvas.requestRenderAll();"""
        )
    }
}

// Stickers

fun fabricAddSticker(canvas: dynamic, emoji: String) {
    val fabric = getFabric() ?: return
    val w = canvas.getWidth().unsafeCast<Double>()
    val h = canvas.getHeight().unsafeCast<Double>()
    js(
        """
        var s=new fabric.Text(emoji,{
            left:w/2,top:h/2,originX:'center',originY:'center',
            fontSize:60,selectable:true,evented:true
        });
        canvas.add(s);canvas.setActiveObject(s);canvas.requestRenderAll();
    """
    )
}

// Crop

fun fabricStartCrop(canvas: dynamic, aspectRatio: String = "free"): dynamic {
    val w = canvas.getWidth().unsafeCast<Double>()
    val h = canvas.getHeight().unsafeCast<Double>()
    return js(
        """
        (function() {
            // Calculate initial crop rect dimensions respecting aspect ratio
            var cropW = w * 0.8;
            var cropH = h * 0.8;
            var cropX = w * 0.1;
            var cropY = h * 0.1;

            if (aspectRatio !== 'free') {
                var parts = aspectRatio.split(':');
                if (parts.length === 2) {
                    var ar = parseFloat(parts[0]) / parseFloat(parts[1]);
                    // Fit within the 80% area maintaining aspect ratio
                    var maxW = w * 0.8;
                    var maxH = h * 0.8;
                    if (maxW / ar <= maxH) {
                        cropW = maxW;
                        cropH = maxW / ar;
                    } else {
                        cropH = maxH;
                        cropW = maxH * ar;
                    }
                    cropX = (w - cropW) / 2;
                    cropY = (h - cropH) / 2;
                }
            }

            var cropRect = new fabric.Rect({
                left: cropX,
                top: cropY,
                width: cropW,
                height: cropH,
                fill: 'rgba(200,146,63,0.1)',
                stroke: '#c8923f',
                strokeWidth: 2,
                strokeDashArray: [8, 4],
                id: '_pcCropRect',
                cornerColor: '#c8923f',
                cornerSize: 12,
                transparentCorners: false,
                selectable: true,
                evented: true,
                lockRotation: true,
                hasRotatingPoint: false,
                _pcAspectRatio: aspectRatio
            });

            // Lock aspect ratio if not free
            if (aspectRatio !== 'free') {
                var parts2 = aspectRatio.split(':');
                if (parts2.length === 2) {
                    var targetAR = parseFloat(parts2[0]) / parseFloat(parts2[1]);
                    cropRect.on('scaling', function() {
                        var newW = cropRect.width * cropRect.scaleX;
                        var newH = newW / targetAR;
                        cropRect.set({
                            scaleY: newH / cropRect.height
                        });
                    });
                }
            }

            canvas.add(cropRect);
            canvas.setActiveObject(cropRect);
            canvas.requestRenderAll();
            return cropRect;
        })()
    """
    )
}

fun fabricApplyCrop(canvas: dynamic, cropRect: dynamic) {
    js(
        """
        (function() {
            var activeImg = null;
            canvas.getObjects().forEach(function(obj) {
                if (obj.type === 'image') activeImg = obj;
            });
            if (!activeImg) {
                canvas.remove(cropRect);
                canvas.requestRenderAll();
                return;
            }

            // Get the crop rect's actual screen coordinates
            var cropLeft   = cropRect.left;
            var cropTop    = cropRect.top;
            var cropWidth  = cropRect.getScaledWidth();
            var cropHeight = cropRect.getScaledHeight();

            // Get image's actual screen coordinates
            var imgLeft = activeImg.left;
            var imgTop  = activeImg.top;
            var imgScaleX = activeImg.scaleX || 1;
            var imgScaleY = activeImg.scaleY || 1;

            // Convert screen crop coordinates to image-local pixel coordinates
            var localX = (cropLeft - imgLeft) / imgScaleX;
            var localY = (cropTop  - imgTop)  / imgScaleY;
            var localW = cropWidth  / imgScaleX;
            var localH = cropHeight / imgScaleY;

            // Clamp to image bounds
            var imgNatW = activeImg.width;
            var imgNatH = activeImg.height;
            localX = Math.max(0, Math.min(localX, imgNatW));
            localY = Math.max(0, Math.min(localY, imgNatH));
            localW = Math.max(1, Math.min(localW, imgNatW - localX));
            localH = Math.max(1, Math.min(localH, imgNatH - localY));

            // Apply crop to the image object
            activeImg.set({
                cropX: localX,
                cropY: localY,
                width: localW,
                height: localH,
                left: cropLeft,
                top: cropTop,
                scaleX: 1,
                scaleY: 1,
                originX: 'left',
                originY: 'top'
            });

            activeImg.setCoords();
            canvas.remove(cropRect);

            // Resize canvas to fit the cropped image
            canvas.setDimensions({ width: Math.round(cropWidth), height: Math.round(cropHeight) });
            activeImg.set({ left: 0, top: 0 });
            activeImg.setCoords();

            canvas.requestRenderAll();
        })()
    """
    )
}

fun fabricCancelCrop(canvas: dynamic, cropRect: dynamic?) {
    js(
        """
        if (cropRect) { try { canvas.remove(cropRect); } catch(e){} }
        canvas.getObjects().forEach(function(obj){
            if (obj.id === '_pcCropRect') { canvas.remove(obj); }
        });
        canvas.discardActiveObject();
        canvas.requestRenderAll();
    """
    )
}

// Export

fun fabricExport(canvas: dynamic, fileName: String = "photocraft-export.png", quality: Double = 1.0) {
    js(
        """
        var dataUrl = canvas.toDataURL({ format:'png', quality:quality, multiplier:1 });
        var link = document.createElement('a');
        link.download = fileName;
        link.href = dataUrl;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    """
    )
}

fun fabricSaveJson(canvas: dynamic, fileName: String = "photocraft-design.json") {
    js(
        """
        var jsonStr = JSON.stringify(canvas.toJSON(['_pcFilterState','_pcActivePresetId',
            'selectable','evented','hasControls','hasBorders',
            'lockMovementX','lockMovementY','lockScalingX','lockScalingY','lockRotation','hoverCursor']));
        var blob = new Blob([jsonStr], { type:'application/json' });
        var url  = URL.createObjectURL(blob);
        var link = document.createElement('a');
        link.download = fileName;
        link.href = url;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    """
    )
}

// Layer Management

fun fabricGetLayers(canvas: dynamic): List<LayerItem> {
    val objects = js("canvas.getObjects()") as Array<dynamic>
    return objects.mapIndexed { i, obj ->
        val type = (obj.type as? String) ?: "object"
        val name = when (type) {
            "image" -> "Image"
            "i-text", "text" -> "Text: ${(obj.text as? String)?.take(14) ?: ""}"
            "rect" -> "Rectangle"
            "circle" -> "Circle"
            "triangle" -> "Triangle"
            "line" -> "Line"
            "polygon" -> "Star"
            else -> "Object $i"
        }
        LayerItem(
            id = "layer_$i", name = name, type = type,
            visible = (obj.visible as? Boolean) ?: true
        )
    }.reversed()
}

fun fabricSetObjectVisibility(canvas: dynamic, index: Int, visible: Boolean) {
    js(
        """
        var objects = canvas.getObjects();
        var realIndex = objects.length - 1 - index;
        if (objects[realIndex]) { objects[realIndex].visible = visible; canvas.requestRenderAll(); }
    """
    )
}

fun fabricDeleteSelected(canvas: dynamic) {
    js(
        """
        var active = canvas.getActiveObject();
        if (active && active.selectable !== false) { canvas.remove(active); canvas.requestRenderAll(); }
    """
    )
}

fun fabricDuplicateSelected(canvas: dynamic) {
    js(
        """
        var active = canvas.getActiveObject();
        if (active && active.selectable !== false) {
            active.clone(function(cloned) {
                cloned.set({ left: active.left + 20, top: active.top + 20 });
                canvas.add(cloned); canvas.setActiveObject(cloned); canvas.requestRenderAll();
            });
        }
    """
    )
}

fun fabricBringForward(canvas: dynamic) {
    js(
        """
        var active = canvas.getActiveObject();
        if (active && active.selectable !== false) { canvas.bringForward(active); canvas.requestRenderAll(); }
    """
    )
}

fun fabricSendBackward(canvas: dynamic) {
    js(
        """
        var active = canvas.getActiveObject();
        if (active && active.selectable !== false) { canvas.sendBackwards(active); canvas.requestRenderAll(); }
    """
    )
}

// Background

fun fabricSetSolidBackground(canvas: dynamic, color: String) {
    js("canvas.setBackgroundColor(color, canvas.requestRenderAll.bind(canvas))")
}

fun fabricSetGradientBackground(canvas: dynamic, color1: String, color2: String, angle: Int = 0) {
    val fabric = getFabric() ?: return
    val w = canvas.getWidth().unsafeCast<Double>()
    val h = canvas.getHeight().unsafeCast<Double>()
    js(
        """
        var rad = angle * Math.PI / 180;
        var gradient = new fabric.Gradient({
            type: 'linear', gradientUnits: 'pixels',
            coords: {
                x1: w/2 - Math.cos(rad)*w/2, y1: h/2 - Math.sin(rad)*h/2,
                x2: w/2 + Math.cos(rad)*w/2, y2: h/2 + Math.sin(rad)*h/2
            },
            colorStops: [{ offset:0, color:color1 }, { offset:1, color:color2 }]
        });
        canvas.setBackgroundColor(gradient, canvas.requestRenderAll.bind(canvas));
    """
    )
}

fun fabricSetTransparentBackground(canvas: dynamic) {
    js("canvas.setBackgroundColor('', canvas.requestRenderAll.bind(canvas))")
    js("canvas.setBackgroundImage(null, canvas.requestRenderAll.bind(canvas))")
}

// History

fun setupFabricHistory(canvas: dynamic): dynamic {
    return js(
        """
        var history = { states:[], current:-1, maxStates:50 };
        var saving = false;
        function saveState() {
            if (saving) return;
            saving = true;
            if (history.current < history.states.length - 1)
                history.states.splice(history.current + 1);
            history.states.push(canvas.toJSON([
                '_pcFilterState','_pcActivePresetId','selectable','evented',
                'hasControls','hasBorders','lockMovementX','lockMovementY',
                'lockScalingX','lockScalingY','lockRotation','hoverCursor'
            ]));
            if (history.states.length > history.maxStates) history.states.shift();
            else history.current++;
            saving = false;
        }
        history.undo = function() {
            if (history.current > 0) {
                history.current--;
                saving = true;
                canvas.loadFromJSON(history.states[history.current], function() {
                    canvas.requestRenderAll();
                    saving = false;
                });
                return true;
            }
            return false;
        };
        history.redo = function() {
            if (history.current < history.states.length - 1) {
                history.current++;
                saving = true;
                canvas.loadFromJSON(history.states[history.current], function() {
                    canvas.requestRenderAll();
                    saving = false;
                });
                return true;
            }
            return false;
        };
        history.canUndo = function() { return history.current > 0; };
        history.canRedo = function() { return history.current < history.states.length - 1; };
        canvas.on('object:added',    saveState);
        canvas.on('object:modified', saveState);
        canvas.on('object:removed',  saveState);
        saveState();
        history;
    """
    )
}

// Touch support

fun setupTouchSupport(canvas: dynamic) {
    js("canvas.allowTouchScrolling = false; canvas.selection = true;")
}

// Boundary clamping

fun setupBoundaryClamp(canvas: dynamic) {
    js(
        """
        canvas.on('object:moving', function(e) {
            var obj = e.target;
            if (obj.selectable === false) return;
            var cw = canvas.getWidth(), ch = canvas.getHeight();
            var bound = obj.getBoundingRect(true);
            if (bound.left < 0) obj.left -= bound.left;
            if (bound.top  < 0) obj.top  -= bound.top;
            if (bound.left + bound.width  > cw) obj.left -= (bound.left + bound.width  - cw);
            if (bound.top  + bound.height > ch) obj.top  -= (bound.top  + bound.height - ch);
            obj.setCoords();
        });
    """
    )
}

//  Utility

fun getActiveObject(canvas: dynamic): dynamic = js("canvas.getActiveObject()")

fun getActiveImageObject(canvas: dynamic): dynamic? = js(
    """
    (function() {
        var obj = null;
        canvas.getObjects().forEach(function(o) { if (o.type === 'image') obj = o; });
        return obj;
    })()
"""
)

fun fabricResizeCanvasToImage(canvas: dynamic, maxW: Double, maxH: Double) {
    js(
        """
        var img = canvas.__pcBaseImage;
        if (!img) {
            canvas.setDimensions({ width: maxW, height: maxH });
            canvas.requestRenderAll();
            return;
        }
        var iw = img.width, ih = img.height;
        var scale = Math.min(maxW / iw, maxH / ih, 1);
        var newW = Math.round(iw * scale);
        var newH = Math.round(ih * scale);
        canvas.setDimensions({ width: newW, height: newH });
        img.set({ left:0, top:0, originX:'left', originY:'top', scaleX:scale, scaleY:scale });
        img.setCoords();
        canvas.requestRenderAll();
    """
    )
}
