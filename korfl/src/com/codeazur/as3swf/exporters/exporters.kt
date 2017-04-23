package com.codeazur.as3swf.exporters

import com.codeazur.as3swf.data.GradientType
import com.codeazur.as3swf.data.consts.GradientInterpolationMode
import com.codeazur.as3swf.data.consts.GradientSpreadMode
import com.codeazur.as3swf.data.consts.LineCapsStyle
import com.codeazur.as3swf.utils.NumberUtils
import com.soywiz.korma.Matrix2d

open class ShapeExporter {
	open fun beginShape() = Unit
	open fun endShape() = Unit

	open fun beginFills() = Unit
	open fun beginFill(color: Int, alpha: Double = 1.0) = Unit
	open fun beginGradientFill(type: GradientType, colors: List<Int>, alphas: List<Double>, ratios: List<Int>, matrix: Matrix2d = Matrix2d(), spreadMethod: GradientSpreadMode = GradientSpreadMode.PAD, interpolationMethod: GradientInterpolationMode = GradientInterpolationMode.NORMAL, focalPointRatio: Double = 0.0) = Unit
	open fun beginBitmapFill(bitmapId: Int, matrix: Matrix2d = Matrix2d(), repeat: Boolean = true, smooth: Boolean = false) = Unit
	open fun endFill() = Unit
	open fun endFills() = Unit

	open fun beginLines() = Unit
	open fun lineStyle(thickness: Double = Double.NaN, color: Int = 0, alpha: Double = 1.0, pixelHinting: Boolean = false, scaleMode: String = "normal", startCaps: LineCapsStyle = LineCapsStyle.ROUND, endCaps: LineCapsStyle = LineCapsStyle.ROUND, joints: String? = null, miterLimit: Double = 3.0) = Unit
	open fun lineGradientStyle(type: GradientType, colors: List<Int>, alphas: List<Double>, ratios: List<Int>, matrix: Matrix2d = Matrix2d(), spreadMethod: GradientSpreadMode = GradientSpreadMode.PAD, interpolationMethod: GradientInterpolationMode = GradientInterpolationMode.NORMAL, focalPointRatio: Double = 0.0) = Unit
	open fun endLines() = Unit

	open fun moveTo(x: Double, y: Double) = Unit
	open fun lineTo(x: Double, y: Double) = Unit
	open fun curveTo(controlX: Double, controlY: Double, anchorX: Double, anchorY: Double) = Unit
	open fun closePath() = Unit
}

open class LoggerShapeExporter(val parent: ShapeExporter, val logger: (String) -> Unit = ::println) : ShapeExporter() {
	fun log(msg: String): LoggerShapeExporter = this.apply { logger(msg) }

	override fun beginShape() = log("beginShape()").parent.beginShape()
	override fun endShape() = log("endShape()").parent.endShape()
	override fun beginFills() = log("beginFills()").parent.beginFills()
	override fun endFills() = log("endFills()").parent.endFills()
	override fun beginLines() = log("beginLines()").parent.beginLines()
	override fun endLines() = log("endLines()").parent.endLines()
	override fun closePath() = log("closePath()").parent.closePath()

	override fun beginFill(color: Int, alpha: Double) = log("beginFill(${"%06X".format(color)}, $alpha)").parent.beginFill(color, alpha)
	override fun beginGradientFill(type: GradientType, colors: List<Int>, alphas: List<Double>, ratios: List<Int>, matrix: Matrix2d, spreadMethod: GradientSpreadMode, interpolationMethod: GradientInterpolationMode, focalPointRatio: Double) {
		log("beginGradientFill($type, $colors, $alphas, $ratios, $matrix, $spreadMethod, $interpolationMethod, $focalPointRatio)").parent.beginGradientFill(type, colors, alphas, ratios, matrix, spreadMethod, interpolationMethod, focalPointRatio)
	}

	override fun beginBitmapFill(bitmapId: Int, matrix: Matrix2d, repeat: Boolean, smooth: Boolean) {
		log("beginBitmapFill($bitmapId, $matrix, $repeat, $smooth)").parent.beginBitmapFill(bitmapId, matrix, repeat, smooth)
	}

	override fun endFill() = log("endFill()").parent.endFill()
	override fun lineStyle(thickness: Double, color: Int, alpha: Double, pixelHinting: Boolean, scaleMode: String, startCaps: LineCapsStyle, endCaps: LineCapsStyle, joints: String?, miterLimit: Double) {
		log("lineStyle($thickness, $color, $alpha, $pixelHinting, $scaleMode, $startCaps, $endCaps, $joints, $miterLimit)").parent.lineStyle(thickness, color, alpha, pixelHinting, scaleMode, startCaps, endCaps, joints, miterLimit)
	}

	override fun lineGradientStyle(type: GradientType, colors: List<Int>, alphas: List<Double>, ratios: List<Int>, matrix: Matrix2d, spreadMethod: GradientSpreadMode, interpolationMethod: GradientInterpolationMode, focalPointRatio: Double) {
		log("lineGradientStyle($type, $colors, $alphas, $ratios, $matrix, $spreadMethod, $interpolationMethod, $focalPointRatio)").parent.lineGradientStyle(type, colors, alphas, ratios, matrix, spreadMethod, interpolationMethod, focalPointRatio)
	}

	override fun moveTo(x: Double, y: Double) = log("moveTo($x, $y)").parent.moveTo(x, y)
	override fun lineTo(x: Double, y: Double) = log("lineTo($x, $y)").parent.lineTo(x, y)
	override fun curveTo(controlX: Double, controlY: Double, anchorX: Double, anchorY: Double) = log("curveTo($controlX, $controlY, $anchorX, $anchorY)").parent.curveTo(controlX, controlY, anchorX, anchorY)
}
