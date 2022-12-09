package com.soywiz.korag

import com.soywiz.kds.*
import com.soywiz.kds.iterators.*
import com.soywiz.klock.*
import com.soywiz.klogger.*
import com.soywiz.kmem.*
import com.soywiz.kmem.unit.*
import com.soywiz.korag.annotation.*
import com.soywiz.korag.gl.*
import com.soywiz.korag.shader.*
import com.soywiz.korag.shader.gl.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*
import kotlin.coroutines.*
import kotlin.jvm.*

inline class AGReadKind(val ordinal: Int) {
    companion object {
        val COLOR = AGReadKind(0)
        val DEPTH = AGReadKind(1)
        val STENCIL = AGReadKind(2)
    }
    val size: Int get() = when (this) {
        COLOR -> 4
        DEPTH -> 4
        STENCIL -> 1
        else -> unreachable
    }

    override fun toString(): String = when (this) {
        COLOR -> "COLOR"
        DEPTH -> "DEPTH"
        STENCIL -> "STENCIL"
        else -> "-"
    }
}

//TODO: there are other possible values
inline class AGTextureTargetKind(val ordinal: Int) {
    val dims: Int get() = when (this) {
        TEXTURE_2D -> 2
        TEXTURE_3D -> 3
        TEXTURE_CUBE_MAP -> 3
        EXTERNAL_TEXTURE -> 3
        else -> 0
    }
    companion object {
        val TEXTURE_2D = AGTextureTargetKind(0)
        val TEXTURE_3D = AGTextureTargetKind(1)
        val TEXTURE_CUBE_MAP = AGTextureTargetKind(2)
        val EXTERNAL_TEXTURE = AGTextureTargetKind(3)
    }
}

inline class AGWrapMode(val ordinal: Int) {
    companion object {
        val CLAMP_TO_EDGE = AGWrapMode(0)
        val REPEAT = AGWrapMode(1)
        val MIRRORED_REPEAT = AGWrapMode(2)
    }
}

/** 2 bits required for encoding */
inline class AGBlendEquation(val ordinal: Int) {
    companion object {
        val ADD = AGBlendEquation(0)
        val SUBTRACT = AGBlendEquation(1)
        val REVERSE_SUBTRACT = AGBlendEquation(2)
    }

    override fun toString(): String = when (this) {
        ADD -> "ADD"
        SUBTRACT -> "SUBTRACT"
        REVERSE_SUBTRACT -> "REVERSE_SUBTRACT"
        else -> "-"
    }

    val op: String get() = when (this) {
        ADD -> "+"
        SUBTRACT -> "-"
        REVERSE_SUBTRACT -> "r-"
        else -> unreachable
    }

    fun apply(l: Double, r: Double): Double = when (this) {
        ADD -> l + r
        SUBTRACT -> l - r
        REVERSE_SUBTRACT -> r - l
        else -> unreachable
    }

    fun apply(l: Float, r: Float): Float = when (this) {
        ADD -> l + r
        SUBTRACT -> l - r
        REVERSE_SUBTRACT -> r - l
        else -> unreachable
    }

    fun apply(l: Int, r: Int): Int = when (this) {
        ADD -> l + r
        SUBTRACT -> l - r
        REVERSE_SUBTRACT -> r - l
        else -> unreachable
    }
}

/** 4 bits required for encoding */
inline class AGBlendFactor(val ordinal: Int) {
    companion object {
        val DESTINATION_ALPHA = AGBlendFactor(0)
        val DESTINATION_COLOR = AGBlendFactor(1)
        val ONE = AGBlendFactor(2)
        val ONE_MINUS_DESTINATION_ALPHA = AGBlendFactor(3)
        val ONE_MINUS_DESTINATION_COLOR = AGBlendFactor(4)
        val ONE_MINUS_SOURCE_ALPHA = AGBlendFactor(5)
        val ONE_MINUS_SOURCE_COLOR = AGBlendFactor(6)
        val SOURCE_ALPHA = AGBlendFactor(7)
        val SOURCE_COLOR = AGBlendFactor(8)
        val ZERO = AGBlendFactor(9)
    }

    override fun toString(): String = when (this) {
        DESTINATION_ALPHA -> "DESTINATION_ALPHA"
        DESTINATION_COLOR -> "DESTINATION_COLOR"
        ONE -> "ONE"
        ONE_MINUS_DESTINATION_ALPHA -> "ONE_MINUS_DESTINATION_ALPHA"
        ONE_MINUS_DESTINATION_COLOR -> "ONE_MINUS_DESTINATION_COLOR"
        ONE_MINUS_SOURCE_ALPHA -> "ONE_MINUS_SOURCE_ALPHA"
        ONE_MINUS_SOURCE_COLOR -> "ONE_MINUS_SOURCE_COLOR"
        SOURCE_ALPHA -> "SOURCE_ALPHA"
        SOURCE_COLOR -> "SOURCE_COLOR"
        ZERO -> "ZERO"
        else -> "-"
    }

    val op: String get() = when (this) {
        DESTINATION_ALPHA -> "dstA"
        DESTINATION_COLOR -> "dstRGB"
        ONE -> "1"
        ONE_MINUS_DESTINATION_ALPHA -> "(1 - dstA)"
        ONE_MINUS_DESTINATION_COLOR -> "(1 - dstRGB)"
        ONE_MINUS_SOURCE_ALPHA -> "(1 - srcA)"
        ONE_MINUS_SOURCE_COLOR -> "(1 - srcRGB)"
        SOURCE_ALPHA -> "srcA"
        SOURCE_COLOR -> "srcRGB"
        ZERO -> "0"
        else -> unreachable
    }

    fun get(srcC: Double, srcA: Double, dstC: Double, dstA: Double): Double = when (this) {
        DESTINATION_ALPHA -> dstA
        DESTINATION_COLOR -> dstC
        ONE -> 1.0
        ONE_MINUS_DESTINATION_ALPHA -> 1.0 - dstA
        ONE_MINUS_DESTINATION_COLOR -> 1.0 - dstC
        ONE_MINUS_SOURCE_ALPHA -> 1.0 - srcA
        ONE_MINUS_SOURCE_COLOR -> 1.0 - srcC
        SOURCE_ALPHA -> srcA
        SOURCE_COLOR -> srcC
        ZERO -> 0.0
        else -> unreachable
    }
}

inline class AGStencilOp(val ordinal: Int) {
    companion object {
        val DECREMENT_SATURATE = AGStencilOp(0)
        val DECREMENT_WRAP = AGStencilOp(1)
        val INCREMENT_SATURATE = AGStencilOp(2)
        val INCREMENT_WRAP = AGStencilOp(3)
        val INVERT = AGStencilOp(4)
        val KEEP = AGStencilOp(5)
        val SET = AGStencilOp(6)
        val ZERO = AGStencilOp(7)
    }

    override fun toString(): String = when (this) {
        DECREMENT_SATURATE -> "DECREMENT_SATURATE"
        DECREMENT_WRAP -> "DECREMENT_WRAP"
        INCREMENT_SATURATE -> "INCREMENT_SATURATE"
        INCREMENT_WRAP -> "INCREMENT_WRAP"
        INVERT -> "INVERT"
        KEEP -> "KEEP"
        SET -> "SET"
        ZERO -> "ZERO"
        else -> "-"
    }
}


/** 2 bits required for encoding */
inline class AGTriangleFace(val ordinal :Int) {
    companion object {
        val FRONT = AGTriangleFace(0)
        val BACK = AGTriangleFace(1)
        val FRONT_AND_BACK = AGTriangleFace(2)
        val NONE = AGTriangleFace(3)
    }

    override fun toString(): String = when (this) {
        FRONT -> "FRONT"
        BACK -> "BACK"
        FRONT_AND_BACK -> "FRONT_AND_BACK"
        NONE -> "NONE"
        else -> "-"
    }
}


/** 3 bits required for encoding */
inline class AGCompareMode(val ordinal: Int) {
    companion object {
        val ALWAYS = AGCompareMode(0)
        val EQUAL = AGCompareMode(1)
        val GREATER = AGCompareMode(2)
        val GREATER_EQUAL = AGCompareMode(3)
        val LESS = AGCompareMode(4)
        val LESS_EQUAL = AGCompareMode(5)
        val NEVER = AGCompareMode(6)
        val NOT_EQUAL = AGCompareMode(7)
    }

    override fun toString(): String = when (this) {
        ALWAYS -> "ALWAYS"
        EQUAL -> "EQUAL"
        GREATER -> "GREATER"
        GREATER_EQUAL -> "GREATER_EQUAL"
        LESS -> "LESS"
        LESS_EQUAL -> "LESS_EQUAL"
        NEVER -> "NEVER"
        NOT_EQUAL -> "NOT_EQUAL"
        else -> "-"
    }

    fun inverted(): AGCompareMode = when (this) {
        ALWAYS -> NEVER
        EQUAL -> NOT_EQUAL
        GREATER -> LESS_EQUAL
        GREATER_EQUAL -> LESS
        LESS -> GREATER_EQUAL
        LESS_EQUAL -> GREATER
        NEVER -> ALWAYS
        NOT_EQUAL -> EQUAL
        else -> NEVER
    }
}

// Default: CCW
/** 2 Bits required for encoding */
inline class AGFrontFace(val ordinal: Int) {
    companion object {
        val DEFAULT: AGFrontFace get() = CCW

        // @TODO: This is incorrect
        val BOTH = AGFrontFace(0)
        val CCW = AGFrontFace(1)
        val CW = AGFrontFace(2)
    }

    override fun toString(): String = when (this) {
        BOTH -> "BOTH"
        CCW -> "CCW"
        CW -> "CW"
        else -> "-"
    }
}


/** 2 Bits required for encoding */
inline class AGCullFace(val ordinal: Int) {
    companion object {
        val NONE = AGCullFace(0)
        val BOTH = AGCullFace(1)
        val FRONT = AGCullFace(2)
        val BACK = AGCullFace(3)
    }

    override fun toString(): String = when (this) {
        NONE -> "NONE"
        BOTH -> "BOTH"
        FRONT -> "FRONT"
        BACK -> "BACK"
        else -> "-"
    }
}


/** Encoded in 3 bits */
inline class AGDrawType(val ordinal: Int) {
    companion object {
        val POINTS = AGDrawType(0)
        val LINE_STRIP = AGDrawType(1)
        val LINE_LOOP = AGDrawType(2)
        val LINES = AGDrawType(3)
        val TRIANGLES = AGDrawType(4)
        val TRIANGLE_STRIP = AGDrawType(5)
        val TRIANGLE_FAN = AGDrawType(6)
    }

    override fun toString(): String = when (this) {
        POINTS -> "POINTS"
        LINE_STRIP -> "LINE_STRIP"
        LINE_LOOP -> "LINE_LOOP"
        LINES -> "LINES"
        TRIANGLES -> "TRIANGLES"
        TRIANGLE_STRIP -> "TRIANGLE_STRIP"
        TRIANGLE_FAN -> "TRIANGLE_FAN"
        else -> "-"
    }
}

/** Encoded in 2 bits */
inline class AGIndexType(val ordinal: Int) {
    companion object {
        val NONE = AGIndexType(0)
        val UBYTE = AGIndexType(1)
        val USHORT = AGIndexType(2)
        // https://developer.mozilla.org/en-US/docs/Web/API/WebGLRenderingContext/drawElements
        @Deprecated("UINT is not always supported on webgl")
        val UINT = AGIndexType(3)
    }

    override fun toString(): String = when (this) {
        NONE -> "null"
        UBYTE -> "UBYTE"
        USHORT -> "USHORT"
        UINT -> "UINT"
        else -> "-"
    }
}


/**
 * color(RGB) = (sourceColor * [srcRGB]) + (destinationColor * [dstRGB])
 * color(A) = (sourceAlpha * [srcA]) + (destinationAlpha * [dstA])
 *
 * Instead of + [eqRGB] and [eqA] determines the operation to use (+, - or reversed -)
 */
inline class AGBlending(val data: Int) {
    val srcRGB: AGBlendFactor get() = AGBlendFactor(data.extract4(0))
    val srcA: AGBlendFactor get() = AGBlendFactor(data.extract4(4))
    val dstRGB: AGBlendFactor get() = AGBlendFactor(data.extract4(8))
    val dstA: AGBlendFactor get() = AGBlendFactor(data.extract4(12))
    val eqRGB: AGBlendEquation get() = AGBlendEquation(data.extract2(16))
    val eqA: AGBlendEquation get() = AGBlendEquation(data.extract2(18))

    fun withSRC(rgb: AGBlendFactor, a: AGBlendFactor = rgb): AGBlending = AGBlending(data.insert4(rgb.ordinal, 0).insert4(a.ordinal, 4))
    fun withDST(rgb: AGBlendFactor, a: AGBlendFactor = rgb): AGBlending = AGBlending(data.insert4(rgb.ordinal, 8).insert4(a.ordinal, 12))
    fun withEQ(rgb: AGBlendEquation, a: AGBlendEquation = rgb): AGBlending = AGBlending(data.insert2(rgb.ordinal, 16).insert2(a.ordinal, 18))

    private fun applyColorComponent(srcC: Double, dstC: Double, srcA: Double, dstA: Double): Double {
        return this.eqRGB.apply(srcC * this.srcRGB.get(srcC, srcA, dstC, dstA), dstC * this.dstRGB.get(srcC, srcA, dstC, dstA))
    }

    private fun applyAlphaComponent(srcA: Double, dstA: Double): Double {
        return eqRGB.apply(srcA * this.srcA.get(0.0, srcA, 0.0, dstA), dstA * this.dstA.get(0.0, srcA, 0.0, dstA))
    }

    fun apply(src: RGBAf, dst: RGBAf, out: RGBAf = RGBAf()): RGBAf {
        out.rd = applyColorComponent(src.rd, dst.rd, src.ad, dst.ad)
        out.gd = applyColorComponent(src.gd, dst.gd, src.ad, dst.ad)
        out.bd = applyColorComponent(src.bd, dst.bd, src.ad, dst.ad)
        out.ad = applyAlphaComponent(src.ad, dst.ad)
        return out
    }

    fun apply(src: RGBA, dst: RGBA): RGBA {
        val srcA = src.ad
        val dstA = dst.ad
        val r = applyColorComponent(src.rd, dst.rd, srcA, dstA)
        val g = applyColorComponent(src.gd, dst.gd, srcA, dstA)
        val b = applyColorComponent(src.bd, dst.bd, srcA, dstA)
        val a = applyAlphaComponent(srcA, dstA)
        return RGBA.float(r, g, b, a)
    }

    val disabled: Boolean get() = this == NONE
    val enabled: Boolean get() = this != NONE

    override fun toString(): String = "Blending(outRGB = (srcRGB * ${srcRGB.op}) ${eqRGB.op} (dstRGB * ${dstRGB.op}), outA = (srcA * ${srcA.op}) ${eqA.op} (dstA * ${dstA.op}))"

    companion object {
        operator fun invoke(
            srcRGB: AGBlendFactor,
            dstRGB: AGBlendFactor,
            srcA: AGBlendFactor = srcRGB,
            dstA: AGBlendFactor = dstRGB,
            eqRGB: AGBlendEquation = AGBlendEquation.ADD,
            eqA: AGBlendEquation = eqRGB
        ): AGBlending = AGBlending(0).withSRC(srcRGB, srcA).withDST(dstRGB, dstA).withEQ(eqRGB, eqA)

        operator fun invoke(
            src: AGBlendFactor,
            dst: AGBlendFactor,
            eq: AGBlendEquation = AGBlendEquation.ADD,
        ): AGBlending = AGBlending(0).withSRC(src).withDST(dst).withEQ(eq)

        val INVALID = AGBlending(-1)
        val NONE = AGBlending(AGBlendFactor.ONE, AGBlendFactor.ZERO, AGBlendFactor.ONE, AGBlendFactor.ZERO)
        val NORMAL = AGBlending(
            //GL_ONE, GL_ONE_MINUS_SRC_ALPHA <-- premultiplied
            AGBlendFactor.SOURCE_ALPHA, AGBlendFactor.ONE_MINUS_SOURCE_ALPHA,
            AGBlendFactor.ONE, AGBlendFactor.ONE_MINUS_SOURCE_ALPHA
        )
        val NORMAL_PRE = AGBlending(
            AGBlendFactor.ONE, AGBlendFactor.ONE_MINUS_SOURCE_ALPHA,
        )
        val ADD = AGBlending(
            AGBlendFactor.SOURCE_ALPHA, AGBlendFactor.DESTINATION_ALPHA,
            AGBlendFactor.ONE, AGBlendFactor.ONE
        )
        val ADD_PRE = AGBlending(
            AGBlendFactor.ONE, AGBlendFactor.ONE,
            AGBlendFactor.ONE, AGBlendFactor.ONE
        )
    }
}

inline class AGColorMask(
    val data: Int
) {
    val red: Boolean get() = data.extractBool(0)
    val green: Boolean get() = data.extractBool(1)
    val blue: Boolean get() = data.extractBool(2)
    val alpha: Boolean get() = data.extractBool(3)

    constructor(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean) : this(0.insert(red, 0).insert(green, 1).insert(blue, 2).insert(alpha, 3))
    constructor(value: Boolean = true) : this(value, value, value, value)

    fun copy(
        red: Boolean = this.red,
        green: Boolean = this.green,
        blue: Boolean = this.blue,
        alpha: Boolean = this.alpha
    ): AGColorMask = AGColorMask(red, green, blue, alpha)

    companion object {
        val INVALID = AGColorMask(-1)
        internal val DUMMY = AGColorMask()
        val DEFAULT = AGColorMask(true)
        val ALL_ENABLED = AGColorMask(true)
        val ALL_BUT_ALPHA_ENABLED = AGColorMask(true, true, true, false)
        val ALL_DISABLED = AGColorMask(false)
    }
}

inline class AGDepthAndFrontFace(val data: Int) {
    companion object {
        operator fun invoke(): AGDepthAndFrontFace = DEFAULT
        val DEFAULT = AGDepthAndFrontFace(0).withDepth(0f, 1f).withDepthMask(true).withDepthFunc(AGCompareMode.ALWAYS).withFrontFace(AGFrontFace.BOTH)
    }

    val depthNear: Float get() = data.extractScaledf01(0, 12)
    val depthFar: Float get() = data.extractScaledf01(12, 12)
    val depthMask: Boolean get() = data.extractBool(24)
    val depthFunc: AGCompareMode get() = AGCompareMode(data.extract3(26))
    val frontFace: AGFrontFace get() = AGFrontFace(data.extract2(30))

    fun withDepth(near: Float, far: Float): AGDepthAndFrontFace = AGDepthAndFrontFace(data.insertScaledf01(near, 0, 12).insertScaledf01(far, 12, 12))
    fun withDepthMask(depthMask: Boolean): AGDepthAndFrontFace = AGDepthAndFrontFace(data.insert(depthMask, 24))
    fun withDepthFunc(depthFunc: AGCompareMode): AGDepthAndFrontFace = AGDepthAndFrontFace(data.insert3(depthFunc.ordinal, 26))
    fun withFrontFace(frontFace: AGFrontFace): AGDepthAndFrontFace = AGDepthAndFrontFace(data.insert2(frontFace.ordinal, 30))
}

inline class AGStencilFullState private constructor(private val data: Long) {
    constructor(opFunc: AGStencilOpFunc = AGStencilOpFunc.DEFAULT, ref: AGStencilReference = AGStencilReference.DEFAULT) : this(Long.fromLowHigh(opFunc.data, ref.data))
    val opFunc: AGStencilOpFunc get() = AGStencilOpFunc(data.low)
    val ref: AGStencilReference get() = AGStencilReference(data.high)

    fun withOpFunc(opFunc: AGStencilOpFunc): AGStencilFullState = AGStencilFullState(opFunc, ref)
    fun withRef(ref: AGStencilReference): AGStencilFullState = AGStencilFullState(opFunc, ref)
    fun withReferenceValue(referenceValue: Int): AGStencilFullState = withRef(ref.withReferenceValue(referenceValue))
}

inline class AGStencilReference(val data: Int) {
    companion object {
        val INVALID = AGStencilReference(-1)
        val DEFAULT = AGStencilReference(0).withReferenceValue(0).withReadMask(0xFF).withWriteMask(0xFF)
    }

    val referenceValue: Int get() = data.extract8(0)
    val readMask: Int get() = data.extract8(8)
    val writeMask: Int get() = data.extract8(16)

    fun withReferenceValue(referenceValue: Int): AGStencilReference = AGStencilReference(data.insert8(referenceValue, 0))
    fun withReadMask(readMask: Int): AGStencilReference = AGStencilReference(data.insert8(readMask, 8))
    fun withWriteMask(writeMask: Int): AGStencilReference = AGStencilReference(data.insert8(writeMask, 16))
}

inline class AGStencilOpFunc(val data: Int) {
    companion object {
        val INVALID = AGStencilOpFunc(-1)
        val DEFAULT = AGStencilOpFunc(0)
            .withEnabled(false)
            .withTriangleFace(AGTriangleFace.FRONT_AND_BACK)
            .withCompareMode(AGCompareMode.ALWAYS)
            .withAction(AGStencilOp.KEEP, AGStencilOp.KEEP, AGStencilOp.KEEP)
    }

    val enabled: Boolean get() = data.extractBool(0)
    val triangleFace: AGTriangleFace get() = AGTriangleFace(data.extract2(4))
    val compareMode: AGCompareMode get() = AGCompareMode(data.extract3(8))
    val actionOnBothPass: AGStencilOp get() = AGStencilOp(data.extract3(12))
    val actionOnDepthFail: AGStencilOp get() = AGStencilOp(data.extract3(16))
    val actionOnDepthPassStencilFail: AGStencilOp get() = AGStencilOp(data.extract3(20))

    fun withEnabled(enabled: Boolean): AGStencilOpFunc = AGStencilOpFunc(data.insert(enabled, 0))
    fun withTriangleFace(triangleFace: AGTriangleFace): AGStencilOpFunc = AGStencilOpFunc(data.insert2(triangleFace.ordinal, 4))
    fun withCompareMode(compareMode: AGCompareMode): AGStencilOpFunc = AGStencilOpFunc(data.insert3(compareMode.ordinal, 8))
    fun withActionOnBothPass(actionOnBothPass: AGStencilOp): AGStencilOpFunc = AGStencilOpFunc(data.insert3(actionOnBothPass.ordinal, 12))
    fun withActionOnDepthFail(actionOnDepthFail: AGStencilOp): AGStencilOpFunc = AGStencilOpFunc(data.insert3(actionOnDepthFail.ordinal, 16))
    fun withActionOnDepthPassStencilFail(actionOnDepthPassStencilFail: AGStencilOp): AGStencilOpFunc = AGStencilOpFunc(data.insert3(actionOnDepthPassStencilFail.ordinal, 20))

    // Shortcut
    fun withAction(actionOnBothPass: AGStencilOp, actionOnDepthFail: AGStencilOp = actionOnBothPass, actionOnDepthPassStencilFail: AGStencilOp = actionOnDepthFail): AGStencilOpFunc = withActionOnBothPass(actionOnBothPass).withActionOnDepthFail(actionOnDepthFail).withActionOnDepthPassStencilFail(actionOnDepthPassStencilFail)
}

//open val supportInstancedDrawing: Boolean get() = false

inline class AGFullState(val data: Int32Buffer = Int32Buffer(6)) {
    var blending: AGBlending ; get() = AGBlending(data[0]) ; set(value) { data[0] = value.data }
    var stencilOpFunc: AGStencilOpFunc ; get() = AGStencilOpFunc(data[1]) ; set(value) { data[1] = value.data }
    var stencilRef: AGStencilReference ; get() = AGStencilReference(data[2]) ; set(value) { data[2] = value.data }
    var colorMask: AGColorMask ; get() = AGColorMask(data[3]) ; set(value) { data[3] = value.data }
    var scissor: AGScissor ; get() = AGScissor(data[4], data[5]) ; set(value) { data[4] = value.xy; data[5] = value.wh }
}

fun Rectangle?.toAGScissor(): AGScissor {
    if (this == null) return AGScissor.NIL
    return AGScissor(x.toInt(), y.toInt(), width.toInt(), height.toInt())
}
fun BoundsBuilder.add(scissor: AGScissor) {
    add(scissor.x, scissor.y)
}
inline class AGScissor(val data: Long) {
    constructor(xy: Int, wh: Int) : this(Long.fromLowHigh(xy, wh))
    constructor(x: Int, y: Int, width: Int, height: Int) : this(0.insert16(x, 0).insert16(y, 16), 0.insert16(width, 0).insert16(height, 16))
    constructor(x: Double, y: Double, width: Double, height: Double) : this(x.toIntRound(), y.toIntRound(), width.toIntRound(), height.toIntRound())
    //constructor(x: Double, y: Double, width: Double, height: Double) : this(x.toInt(), y.toInt(), width.toInt(), height.toInt())

    val xy: Int get() = data.low
    val wh: Int get() = data.high

    val x: Int get() = xy.extract16Signed(0)
    val y: Int get() = xy.extract16Signed(16)
    val width: Int get() = wh.extract16Signed(0)
    val height: Int get() = wh.extract16Signed(16)

    val top get() = y
    val left get() = x
    val right get() = x + width
    val bottom get() = y + height

    fun withXY(x: Int, y: Int): AGScissor = AGScissor(0.insert16(x, 0).insert16(y, 16), wh)
    fun withWH(width: Int, height: Int): AGScissor = AGScissor(xy, 0.insert16(width, 0).insert16(height, 16))
    fun copy(x: Int = this.x, y: Int = this.y, width: Int = this.width, height: Int = this.height): AGScissor = AGScissor(x, y, width, height)
    override fun toString(): String {
        if (this == NIL) return "null"
        return "Scissor(x=${x}, y=${y}, width=${width}, height=${height})"
    }

    fun toRect(out: Rectangle = Rectangle()): Rectangle = out.setTo(x, y, width, height)
    fun toRectOrNull(out: Rectangle = Rectangle()): Rectangle? {
        if (this == NIL) return null
        return out.setTo(x, y, width, height)
    }

    companion object {
        val EMPTY = AGScissor(0, 0)
        val FULL = AGScissor(0, 0x7FFF7FFF)
        val NIL = AGScissor(-1, 0x7FFF7FFF)
        fun fromBounds(left: Int, top: Int, right: Int, bottom: Int): AGScissor = AGScissor(left, top, right - left, bottom - top)
        fun fromBounds(left: Double, top: Double, right: Double, bottom: Double): AGScissor = AGScissor(left, top, right - left, bottom - top)

        operator fun invoke(rect: IRectangle?): AGScissor {
            if (rect == null) return NIL
            return AGScissor(rect.x, rect.y, rect.width, rect.height)
        }

        // null is equivalent to Scissor(-Inf, -Inf, +Inf, +Inf)
        fun combine(prev: AGScissor, next: AGScissor): AGScissor {
            if (prev == NIL) return next
            if (next == NIL) return prev

            val intersectsX = prev.left <= next.right && prev.right >= next.left
            val intersectsY = prev.top <= next.bottom && prev.bottom >= next.top
            if (!intersectsX || !intersectsY) return EMPTY

            val left = kotlin.math.max(prev.left, next.left)
            val top = kotlin.math.max(prev.top, next.top)
            val right = kotlin.math.min(prev.right, next.right)
            val bottom = kotlin.math.min(prev.bottom, next.bottom)

            return fromBounds(left, top, right, bottom)
        }
    }
}

enum class AGBufferKind { INDEX, VERTEX }

interface AGFactory {
    val supportsNativeFrame: Boolean
    fun create(nativeControl: Any?, config: AGConfig): AG
    fun createFastWindow(title: String, width: Int, height: Int): AGWindow
    //fun createFastWindow(title: String, width: Int, height: Int, config: AGConfig): AGWindow
}

open class AGFrameBuffer(val ag: AG, val isMain: Boolean) : AGObject() {
    var nsamples: Int = 1; protected set
    val hasStencilAndDepth: Boolean get() = hasDepth && hasStencil
    var hasStencil: Boolean = true; protected set
    var hasDepth: Boolean = true; protected set

    val tex: AGTexture = ag.createTexture(premultiplied = true).also { it.isFbo = true }

    init {
        //ag.frameRenderBuffers += this
    }

    //fun init() = Unit
    //fun set() = Unit
    //fun unset() = Unit

    var x = 0
    var y = 0
    var width = AG.RenderBufferConsts.DEFAULT_INITIAL_WIDTH
    var height = AG.RenderBufferConsts.DEFAULT_INITIAL_HEIGHT
    var fullWidth = AG.RenderBufferConsts.DEFAULT_INITIAL_WIDTH
    var fullHeight = AG.RenderBufferConsts.DEFAULT_INITIAL_HEIGHT
    private val _scissor = RectangleInt()
    var scissor: RectangleInt? = null

    var estimatedMemoryUsage: ByteUnits = ByteUnits.fromBytes(0)

    open fun setSize(x: Int, y: Int, width: Int, height: Int, fullWidth: Int = width, fullHeight: Int = height) {
        if (this.x == x && this.y == y && this.width == width && this.height == height && this.fullWidth == fullWidth && this.fullHeight == fullHeight) return
        tex.upload(NullBitmap(width, height))

        estimatedMemoryUsage = ByteUnits.fromBytes(fullWidth * fullHeight * (4 + 4))

        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.fullWidth = fullWidth
        this.fullHeight = fullHeight
        markAsDirty()
    }

    fun scissor(scissor: RectangleInt?) {
        this.scissor = scissor?.let { _scissor.setTo(it) }
    }

    init {
        ag.allRenderBuffers += this
    }

    override fun close() {
        ag.allRenderBuffers -= this
        tex.close()
        //ag.frameRenderBuffers -= this
    }


    fun setSamples(samples: Int) {
        if (this.nsamples == samples) return
        nsamples = samples
        markAsDirty()
    }

    fun setExtra(hasDepth: Boolean = true, hasStencil: Boolean = true) {
        if (this.hasDepth == hasDepth && this.hasStencil == hasStencil) return
        this.hasDepth = hasDepth
        this.hasStencil = hasStencil
        markAsDirty()
    }

    fun readBitmap(bmp: Bitmap32) = ag.readColor(bmp)
    fun readDepth(width: Int, height: Int, out: FloatArray): Unit = ag.readDepth(width, height, out)

    override fun toString(): String = "GlRenderBuffer($width, $height)"
}

data class AGConfig(val antialiasHint: Boolean = true)

interface AGContainer {
    val ag: AG
    //data class Resized(var width: Int, var height: Int) {
    //	fun setSize(width: Int, height: Int): Resized = this.apply {
    //		this.width = width
    //		this.height = height
    //	}
    //}

    fun repaint(): Unit
}

@KoragExperimental
enum class AGTarget {
    DISPLAY,
    OFFSCREEN
}

/** List<VertexData> -> VAO */
@JvmInline
value class AGVertexArrayObject(
    val list: FastArrayList<AGVertexData>
) {
    constructor(vararg datas: AGVertexData) : this(fastArrayListOf(*datas))
}

data class AGVertexData constructor(
    var _buffer: AGBuffer?,
    var layout: VertexLayout = VertexLayout()
) {
    val buffer: AGBuffer get() = _buffer!!
}

interface AGCommandExecutor {
    fun execute(command: AGCommand)
}
fun AGCommandExecutor.draw(batch: AGBatch): Unit = execute(batch)

sealed interface AGCommand

/**
 *
 */
data class AGClear(
    // Frame Buffer
    var frameBuffer: AGFrameBuffer? = null,
    // Reference balues
    var color: RGBA,
    var depth: Float,
    var stencil: Int,
    // Mask
    var clearColor: Boolean,
    var clearDepth: Boolean,
    var clearStencil: Boolean,
) : AGCommand

/**
 * Can be emulated by rendering a quad using the texture of the framebuffer and disabling blending modes
 *
 * @see <https://registry.khronos.org/OpenGL-Refpages/gl4/html/glBlitFramebuffer.xhtml>
 */
data class AGBlitPixels(
    var dstFrameBuffer: AGFrameBuffer,
    var dst: AGScissor,
    var srcFrameBuffer: AGFrameBuffer,
    var src: AGScissor,
) : AGCommand

/**
 * Reads pixels from a [region] inside a [frameBuffer] into a [texture].
 */
data class AGReadPixelsToTexture(
    var frameBuffer: AGFrameBuffer,
    var region: AGScissor,
    var texture: AGTexture,
) : AGCommand

/**
 * Incrementally sets a new state diffing with the previous state, and renders primitives.
 */
data class AGBatch(
    // Frame Buffer
    var frameBuffer: AGFrameBuffer? = null,
    // Vertex & Index data
    var vertexData: AGVertexArrayObject = AGVertexArrayObject(AGVertexData(null)),
    var indices: AGBuffer? = null,
    var indexType: AGIndexType = AGIndexType.USHORT,
    // Program & Uniforms
    var program: Program = DefaultShaders.PROGRAM_DEBUG,
    var uniforms: AGUniformValues = AGUniformValues.EMPTY,
    // State
    var blending: AGBlending = AGBlending.NORMAL,
    var stencilOpFunc: AGStencilOpFunc = AGStencilOpFunc.DEFAULT,
    var stencilRef: AGStencilReference = AGStencilReference.DEFAULT,
    var colorMask: AGColorMask = AGColorMask.DEFAULT,
    var depthAndFrontFace: AGDepthAndFrontFace = AGDepthAndFrontFace.DEFAULT,
    var scissor: AGScissor = AGScissor.NIL,
    var cullFace: AGCullFace = AGCullFace.NONE,
    // Draw
    var drawType: AGDrawType = AGDrawType.TRIANGLES,
    var drawOffset: Int = 0,
    var vertexCount: Int = 0,
    var instances: Int = 1
) : AGCommand {
    var stencilFull: AGStencilFullState
        get() = AGStencilFullState(stencilOpFunc, stencilRef)
        set(value) {
            stencilOpFunc = value.opFunc
            stencilRef = value.ref
        }
}
