package com.muhammadnoman.photocraft.utils

import androidx.compose.runtime.MutableState
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

// Image Loading — single image at a time, no counter leaking

fun fabricLoadImageFromDataUrl(canvas: dynamic, dataUrl: String, onLoad: (dynamic) -> Unit) {
    val fabric = getFabric() ?: run { console.error("Fabric.js not loaded!"); return }
    js(
        """
        fabric.Image.fromURL(dataUrl, function(img) {
            if (!img || !img.width || !img.height) {
                console.error('Image failed to load or has zero dimensions');
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
                highlights:0, shadows:0, temperature:0, tint:0, vignette:0,
                presetBrightness:0, presetContrast:0, presetSaturation:0, presetBlur:0
            };
 
            // Clear everything before adding new image
            canvas.clear();
            canvas.add(img);
            canvas.discardActiveObject();
            canvas.requestRenderAll();
 
            if (onLoad) onLoad(img);
        }, { crossOrigin: 'anonymous' });
    """
    )
}

// Filter helper injection — now includes highlights, shadows, temperature, tint, vignette

fun injectFilterHelper() {
    js(
        """
        if (!window._pcRebuildFilters) {
            window._pcRebuildFilters = function(img, fabric) {
                var s = img._pcFilterState;
                img.filters = [];
 
                // --- Brightness (preset + manual) ---
                var totalBrightness = ((s.presetBrightness||0) + (s.brightness||0));
                if (totalBrightness !== 0)
                    img.filters.push(new fabric.Image.filters.Brightness({ brightness: totalBrightness/100 }));
 
                // --- Contrast (preset + manual) ---
                var totalContrast = ((s.presetContrast||0) + (s.contrast||0));
                if (totalContrast !== 0)
                    img.filters.push(new fabric.Image.filters.Contrast({ contrast: totalContrast/100 }));
 
                // --- Saturation (preset + manual) ---
                var totalSaturation = ((s.presetSaturation||0) + (s.saturation||0));
                if (totalSaturation !== 0)
                    img.filters.push(new fabric.Image.filters.Saturation({ saturation: totalSaturation/100 }));
 
                // --- Hue ---
                if ((s.hue||0) !== 0)
                    img.filters.push(new fabric.Image.filters.HueRotation({ rotation: (s.hue||0)/360 }));
 
                // --- Grayscale ---
                if ((s.grayscale||0) > 0) img.filters.push(new fabric.Image.filters.Grayscale());
 
                // --- Sepia ---
                if ((s.sepia||0) > 0) img.filters.push(new fabric.Image.filters.Sepia());
 
                // --- Blur (preset + manual) ---
                var totalBlur = ((s.presetBlur||0) + (s.blur||0));
                if (totalBlur > 0)
                    img.filters.push(new fabric.Image.filters.Blur({ blur: totalBlur/100 }));
 
                // --- Noise ---
                if ((s.noise||0) > 0)
                    img.filters.push(new fabric.Image.filters.Noise({ noise: (s.noise||0)*1.5 }));

                 // --- Highlights
                 var hl = (s.highlights||0);
                 if (hl !== 0) {
                     var hlFactor = 1 + (hl / 150);
                     var hlG = Math.max(0.1, hlFactor > 1 ? 1 + (hlFactor - 1) * 0.5 : hlFactor);
                     img.filters.push(new fabric.Image.filters.Gamma({ gamma: [hlG, hlG, hlG] }));
                 }
                 
                 // --- Shadows
                 var sh = (s.shadows||0);
                 if (sh !== 0) {
                     var shGamma = sh > 0 ? Math.max(0.3, 1 - sh/200) : Math.min(3.0, 1 - sh/200);
                     var shBrightness = sh / 400;
                     img.filters.push(new fabric.Image.filters.Gamma({ gamma: [shGamma, shGamma, shGamma] }));
                     if (Math.abs(shBrightness) > 0.01)
                         img.filters.push(new fabric.Image.filters.Brightness({ brightness: shBrightness }));
                 }
 
                // --- Temperature: warm (positive) = more red/yellow, cool (negative) = more blue ---
                var temp = (s.temperature||0);
                if (temp !== 0) {
                    var t = temp / 100;
                    // ColorMatrix: shift R and B channels in opposite directions
                    img.filters.push(new fabric.Image.filters.ColorMatrix({
                        matrix: [
                            1 + t*0.3, 0,         0,         0, 0,
                            0,         1 + t*0.05, 0,         0, 0,
                            0,         0,         1 - t*0.3, 0, 0,
                            0,         0,         0,         1, 0
                        ]
                    }));
                }
 
                // --- Tint: positive = green tint, negative = magenta tint ---
                var tint = (s.tint||0);
                if (tint !== 0) {
                    var ti = tint / 100;
                    img.filters.push(new fabric.Image.filters.ColorMatrix({
                        matrix: [
                            1 - ti*0.1, 0,          0,          0, 0,
                            0,          1 + ti*0.2, 0,          0, 0,
                            0,          0,          1 - ti*0.1, 0, 0,
                            0,          0,          0,          1, 0
                        ]
                    }));
                }
 
                // --- Opacity ---
                img.set('opacity', ((s.opacity !== undefined ? s.opacity : 100)) / 100);
 
                img.applyFilters();
 
                // --- Vignette: drawn as overlay on canvas after render ---
                // We store it and re-draw after applyFilters triggers render
                if (img.canvas) {
                    img.canvas._pcVignetteStrength = (s.vignette||0) / 100;
                    img.canvas.requestRenderAll();
                }
            };
        }
 
        // Vignette overlay — drawn via canvas 'after:render' event
        if (!window._pcSetupVignette) {
            window._pcSetupVignette = function(fabricCanvas) {
                fabricCanvas.on('after:render', function() {
                    var strength = fabricCanvas._pcVignetteStrength || 0;
                    if (strength <= 0) return;
                    var ctx = fabricCanvas.getContext();
                    var w = fabricCanvas.getWidth();
                    var h = fabricCanvas.getHeight();
                    var gradient = ctx.createRadialGradient(
                        w/2, h/2, Math.min(w,h) * 0.3,
                        w/2, h/2, Math.max(w,h) * 0.75
                    );
                    gradient.addColorStop(0, 'rgba(0,0,0,0)');
                    gradient.addColorStop(1, 'rgba(0,0,0,' + Math.min(0.85, strength) + ')');
                    ctx.save();
                    ctx.fillStyle = gradient;
                    ctx.fillRect(0, 0, w, h);
                    ctx.restore();
                });
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
                highlights:0,shadows:0,temperature:0,tint:0,vignette:0,
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
    val highlights = adj.highlights
    val shadows = adj.shadows
    val temperature = adj.temperature
    val tint = adj.tint
    val vignette = adj.vignette
    js(
        """
        if (!img._pcFilterState) {
            img._pcFilterState = { brightness:0,contrast:0,saturation:0,hue:0,
                blur:0,noise:0,opacity:100,grayscale:0,sepia:0,
                highlights:0,shadows:0,temperature:0,tint:0,vignette:0,
                presetBrightness:0,presetContrast:0,presetSaturation:0,presetBlur:0 };
        }
        img._pcFilterState.brightness  = brightness;
        img._pcFilterState.contrast    = contrast;
        img._pcFilterState.saturation  = saturation;
        img._pcFilterState.hue         = hue;
        img._pcFilterState.blur        = blur;
        img._pcFilterState.noise       = noise;
        img._pcFilterState.opacity     = opacity;
        img._pcFilterState.highlights  = highlights;
        img._pcFilterState.shadows     = shadows;
        img._pcFilterState.temperature = temperature;
        img._pcFilterState.tint        = tint;
        img._pcFilterState.vignette    = vignette;
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
        brightness = (js("state.brightness") as? Double) ?: 0.0,
        contrast = (js("state.contrast") as? Double) ?: 0.0,
        saturation = (js("state.saturation") as? Double) ?: 0.0,
        hue = (js("state.hue") as? Double) ?: 0.0,
        blur = (js("state.blur") as? Double) ?: 0.0,
        noise = (js("state.noise") as? Double) ?: 0.0,
        opacity = (js("state.opacity") as? Double) ?: 100.0,
        highlights = (js("state.highlights") as? Double) ?: 0.0,
        shadows = (js("state.shadows") as? Double) ?: 0.0,
        temperature = (js("state.temperature") as? Double) ?: 0.0,
        tint = (js("state.tint") as? Double) ?: 0.0,
        vignette = (js("state.vignette") as? Double) ?: 0.0
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
            var cropW = w * 0.8;
            var cropH = h * 0.8;
            var cropX = w * 0.1;
            var cropY = h * 0.1;
 
            if (aspectRatio !== 'free') {
                var parts = aspectRatio.split(':');
                if (parts.length === 2) {
                    var ar = parseFloat(parts[0]) / parseFloat(parts[1]);
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
 
            if (aspectRatio !== 'free') {
                var parts2 = aspectRatio.split(':');
                if (parts2.length === 2) {
                    var targetAR = parseFloat(parts2[0]) / parseFloat(parts2[1]);
                    cropRect.on('scaling', function() {
                        var newW = cropRect.width * cropRect.scaleX;
                        var newH = newW / targetAR;
                        cropRect.set({ scaleY: newH / cropRect.height });
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
 
            var cropLeft   = cropRect.left;
            var cropTop    = cropRect.top;
            var cropWidth  = cropRect.getScaledWidth();
            var cropHeight = cropRect.getScaledHeight();
 
            var imgLeft = activeImg.left;
            var imgTop  = activeImg.top;
            var imgScaleX = activeImg.scaleX || 1;
            var imgScaleY = activeImg.scaleY || 1;
 
            var localX = (cropLeft - imgLeft) / imgScaleX;
            var localY = (cropTop  - imgTop)  / imgScaleY;
            var localW = cropWidth  / imgScaleX;
            var localH = cropHeight / imgScaleY;
 
            var imgNatW = activeImg.width;
            var imgNatH = activeImg.height;
            localX = Math.max(0, Math.min(localX, imgNatW));
            localY = Math.max(0, Math.min(localY, imgNatH));
            localW = Math.max(1, Math.min(localW, imgNatW - localX));
            localH = Math.max(1, Math.min(localH, imgNatH - localY));
 
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

// ============================================================
// HISTORY — Proper granular undo/redo
// Key design:
//  1. history.saveSnapshot() is called EXPLICITLY after every
//     discrete user action (filter apply, adjustment change, add
//     shape, add text, crop, etc.) — NOT on every canvas event.
//  2. Canvas object:added/modified/removed still save, but we
//     debounce them so rapid slider drags coalesce into ONE entry.
//  3. Filter/adjustment changes call saveSnapshot() manually via
//     Kotlin after the JS filter rebuild completes.
// ============================================================

fun setupFabricHistory(canvas: dynamic): dynamic {
    return js(
        """
        var history = { states:[], current:-1, maxStates:100 };
        history._saving = false;
        history._debounceTimer = null;
 
        // Save a snapshot immediately (used for explicit actions)
        history.saveSnapshot = function() {
            if (history._saving) return;
            history._saving = true;
            // Trim redo branch
            if (history.current < history.states.length - 1)
                history.states.splice(history.current + 1);
            var json = canvas.toJSON([
                '_pcFilterState','_pcActivePresetId','selectable','evented',
                'hasControls','hasBorders','lockMovementX','lockMovementY',
                'lockScalingX','lockScalingY','lockRotation','hoverCursor',
                'flipX','flipY','angle'
            ]);
            // Deep-clone so mutations to live objects don't corrupt history
            history.states.push(JSON.parse(JSON.stringify(json)));
            if (history.states.length > history.maxStates) history.states.shift();
            else history.current++;
            history._saving = false;
        };
 
        // Debounced save — coalesces rapid events (e.g. object:modified during drag)
        history._debouncedSave = function() {
            clearTimeout(history._debounceTimer);
            history._debounceTimer = setTimeout(function() {
                history.saveSnapshot();
            }, 300);
        };
 
        history.undo = function() {
            if (history.current <= 0) return false;
            history.current--;
            history._saving = true;
            canvas.loadFromJSON(history.states[history.current], function() {
                canvas.requestRenderAll();
                history._saving = false;
            });
            return true;
        };
 
        history.redo = function() {
            if (history.current >= history.states.length - 1) return false;
            history.current++;
            history._saving = true;
            canvas.loadFromJSON(history.states[history.current], function() {
                canvas.requestRenderAll();
                history._saving = false;
            });
            return true;
        };
 
        history.canUndo = function() { return history.current > 0; };
        history.canRedo = function() { return history.current < history.states.length - 1; };
 
        // Only structural changes (add/remove/move/resize/rotate) auto-save via debounce.
        // Filter and adjustment changes are saved explicitly by Kotlin callers.
        canvas.on('object:added',    function(e) {
            // Don't save when the crop rect is added
            if (e.target && e.target.id === '_pcCropRect') return;
            if (!history._saving) history._debouncedSave();
        });
        canvas.on('object:modified', function(e) {
            if (e.target && e.target.id === '_pcCropRect') return;
            if (!history._saving) history._debouncedSave();
        });
        canvas.on('object:removed',  function(e) {
            if (e.target && e.target.id === '_pcCropRect') return;
            if (!history._saving) history._debouncedSave();
        });
 
        // Save initial blank state
        history.saveSnapshot();
        history;
    """
    )
}

// Expose a Kotlin-callable helper to explicitly save a history snapshot
// (called after every filter/adjustment/preset change)
fun fabricSaveHistorySnapshot(historyRef: MutableState<dynamic>) {
    val hist = historyRef.value ?: return
    js("hist.saveSnapshot()")
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
