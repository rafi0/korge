// WARNING: File autogenerated DO NOT modify
// https://www.khronos.org/registry/OpenGL/api/GLES2/gl2.h
@file:Suppress("unused", "RedundantUnitReturnType", "PropertyName")

package korlibs.kgl

import korlibs.datastructure.FastStringMap
import korlibs.datastructure.IntMap
import korlibs.datastructure.Pool
import korlibs.datastructure.clear
import korlibs.datastructure.getOrPut
import korlibs.memory.*
import korlibs.image.bitmap.NativeImage

class KmlGlDummy : KmlGlDummyBase()

open class KmlGlDummyBase : KmlGl() {
    class Allocator(val base: Int = 0) {
        var id = base
        var pool = Pool { ++id }
        fun alloc(): Int = pool.alloc()
        fun free(value: Int) {
            pool.free(value)
        }
        fun reset() {
            id = base
            pool = Pool { ++id }
        }
    }
    class ProgramInfo(val id: Int) {
        companion object {
            val BASE_UNIFORM = 7000
            val BASE_ATTRIB = 8000
        }
        var uniformId = BASE_UNIFORM
        val uniforms = FastStringMap<Int>()
        var attribId = BASE_ATTRIB
        val attribs = FastStringMap<Int>()
        fun getUniformLocation(name: String): Int = uniforms.getOrPut(name) { ++uniformId }
        fun getAttribLocation(name: String): Int = attribs.getOrPut(name) { ++attribId }
        fun reset() {
            uniformId = BASE_UNIFORM
            attribId = BASE_ATTRIB
            uniforms.clear()
            attribs.clear()
        }
    }
    val programs = IntMap<ProgramInfo>()
    fun getProgram(id: Int): ProgramInfo = programs.getOrPut(id) { ProgramInfo(id) }
    val programIds = Allocator(1000)
    val shaderIds = Allocator(2000)
    val bufferIds = Allocator(3000)
    val frameBufferIds = Allocator(4000)
    val renderBufferIds = Allocator(5000)
    val textureIds = Allocator(6000)

    override fun handleContextLost() {
        super.handleContextLost()
        programs.clear()
        programIds.reset()
        shaderIds.reset()
        bufferIds.reset()
        frameBufferIds.reset()
        renderBufferIds.reset()
        textureIds.reset()
    }

    override fun activeTexture(texture: Int): Unit = Unit
    override fun attachShader(program: Int, shader: Int): Unit = Unit
    override fun bindAttribLocation(program: Int, index: Int, name: String): Unit = Unit
    override fun bindBuffer(target: Int, buffer: Int): Unit = Unit
    override fun bindFramebuffer(target: Int, framebuffer: Int): Unit = Unit
    override fun bindRenderbuffer(target: Int, renderbuffer: Int): Unit = Unit
    override fun bindTexture(target: Int, texture: Int): Unit = Unit
    override fun blendColor(red: Float, green: Float, blue: Float, alpha: Float): Unit = Unit
    override fun blendEquation(mode: Int): Unit = Unit
    override fun blendEquationSeparate(modeRGB: Int, modeAlpha: Int): Unit = Unit
    override fun blendFunc(sfactor: Int, dfactor: Int): Unit = Unit
    override fun blendFuncSeparate(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int): Unit = Unit
    override fun bufferData(target: Int, size: Int, data: Buffer, usage: Int): Unit = Unit
    override fun bufferSubData(target: Int, offset: Int, size: Int, data: Buffer): Unit = Unit
    override fun checkFramebufferStatus(target: Int): Int = 0
    override fun clear(mask: Int): Unit = Unit
    override fun clearColor(red: Float, green: Float, blue: Float, alpha: Float): Unit = Unit
    override fun clearDepthf(d: Float): Unit = Unit
    override fun clearStencil(s: Int): Unit = Unit
    override fun colorMask(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean): Unit = Unit
    override fun compileShader(shader: Int): Unit = Unit
    override fun compressedTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, imageSize: Int, data: Buffer): Unit = Unit
    override fun compressedTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, imageSize: Int, data: Buffer): Unit = Unit
    override fun copyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int): Unit = Unit
    override fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int): Unit = Unit
    override fun createProgram(): Int = programIds.alloc()
    override fun createShader(type: Int): Int = shaderIds.alloc()
    override fun cullFace(mode: Int): Unit = Unit

    private fun delete(n: Int, buffer: Buffer, allocator: Allocator) {
        for (i in 0 until n) allocator.free(buffer.i32[i])
    }
    override fun deleteBuffers(n: Int, items: Buffer): Unit = delete(n, items, bufferIds)
    override fun deleteFramebuffers(n: Int, items: Buffer): Unit = delete(n, items, frameBufferIds)
    override fun deleteProgram(program: Int): Unit = programIds.free(program)
    override fun deleteRenderbuffers(n: Int, items: Buffer): Unit = delete(n, items, renderBufferIds)
    override fun deleteShader(shader: Int): Unit = shaderIds.free(shader)
    override fun deleteTextures(n: Int, items: Buffer): Unit = delete(n, items, textureIds)

    override fun depthFunc(func: Int): Unit = Unit
    override fun depthMask(flag: Boolean): Unit = Unit
    override fun depthRangef(n: Float, f: Float): Unit = Unit
    override fun detachShader(program: Int, shader: Int): Unit = Unit
    override fun disable(cap: Int): Unit = Unit
    override fun disableVertexAttribArray(index: Int): Unit = Unit
    override fun drawArrays(mode: Int, first: Int, count: Int): Unit = Unit
    override fun drawElements(mode: Int, count: Int, type: Int, indices: Int): Unit = Unit
    override fun enable(cap: Int): Unit = Unit
    override fun enableVertexAttribArray(index: Int): Unit = Unit
    override fun finish(): Unit = Unit
    override fun flush(): Unit = Unit
    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int): Unit = Unit
    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int): Unit = Unit
    override fun frontFace(mode: Int): Unit = Unit

    private fun gen(n: Int, buffer: Buffer, allocator: Allocator) {
        for (i in 0 until n) buffer.i32[i] = allocator.alloc()
    }
    override fun genBuffers(n: Int, buffers: Buffer): Unit = gen(n, buffers, bufferIds)
    override fun generateMipmap(target: Int): Unit = Unit
    override fun genFramebuffers(n: Int, framebuffers: Buffer): Unit = gen(n, framebuffers, frameBufferIds)
    override fun genRenderbuffers(n: Int, renderbuffers: Buffer): Unit = gen(n, renderbuffers, renderBufferIds)
    override fun genTextures(n: Int, textures: Buffer): Unit = gen(n, textures, textureIds)

    override fun getActiveAttrib(program: Int, index: Int, bufSize: Int, length: Buffer, size: Buffer, type: Buffer, name: Buffer): Unit = Unit
    override fun getActiveUniform(program: Int, index: Int, bufSize: Int, length: Buffer, size: Buffer, type: Buffer, name: Buffer): Unit = Unit
    override fun getAttachedShaders(program: Int, maxCount: Int, count: Buffer, shaders: Buffer): Unit = Unit
    override fun getAttribLocation(program: Int, name: String): Int = getProgram(program).getAttribLocation(name)
    override fun getUniformLocation(program: Int, name: String): Int = getProgram(program).getUniformLocation(name)
    override fun getBooleanv(pname: Int, data: Buffer): Unit = Unit
    override fun getBufferParameteriv(target: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getError(): Int = 0
    override fun getFloatv(pname: Int, data: Buffer): Unit = Unit
    override fun getFramebufferAttachmentParameteriv(target: Int, attachment: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getIntegerv(pname: Int, data: Buffer): Unit = Unit
    override fun getProgramInfoLog(program: Int, bufSize: Int, length: Buffer, infoLog: Buffer): Unit = Unit
    override fun getRenderbufferParameteriv(target: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getProgramiv(program: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getShaderiv(shader: Int, pname: Int, params: Buffer) {
        when (pname) {
            COMPILE_STATUS -> params.i32[0] = GTRUE
        }
    }
    override fun getShaderInfoLog(shader: Int, bufSize: Int, length: Buffer, infoLog: Buffer): Unit = Unit
    override fun getShaderPrecisionFormat(shadertype: Int, precisiontype: Int, range: Buffer, precision: Buffer): Unit = Unit
    override fun getShaderSource(shader: Int, bufSize: Int, length: Buffer, source: Buffer): Unit = Unit
    override fun getString(name: Int): String = ""
    override fun getTexParameterfv(target: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getTexParameteriv(target: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getUniformfv(program: Int, location: Int, params: Buffer): Unit = Unit
    override fun getUniformiv(program: Int, location: Int, params: Buffer): Unit = Unit
    override fun getVertexAttribfv(index: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getVertexAttribiv(index: Int, pname: Int, params: Buffer): Unit = Unit
    override fun getVertexAttribPointerv(index: Int, pname: Int, pointer: Buffer): Unit = Unit
    override fun hint(target: Int, mode: Int): Unit = Unit
    override fun isBuffer(buffer: Int): Boolean = false
    override fun isEnabled(cap: Int): Boolean = false
    override fun isFramebuffer(framebuffer: Int): Boolean = false
    override fun isProgram(program: Int): Boolean = false
    override fun isRenderbuffer(renderbuffer: Int): Boolean = false
    override fun isShader(shader: Int): Boolean = false
    override fun isTexture(texture: Int): Boolean = false
    override fun lineWidth(width: Float): Unit = Unit
    override fun linkProgram(program: Int): Unit = Unit
    override fun pixelStorei(pname: Int, param: Int): Unit = Unit
    override fun polygonOffset(factor: Float, units: Float): Unit = Unit
    override fun readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: Buffer): Unit = Unit
    override fun releaseShaderCompiler(): Unit = Unit
    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int): Unit = Unit
    override fun sampleCoverage(value: Float, invert: Boolean): Unit = Unit
    override fun scissor(x: Int, y: Int, width: Int, height: Int): Unit = Unit
    override fun shaderBinary(count: Int, shaders: Buffer, binaryformat: Int, binary: Buffer, length: Int): Unit = Unit
    override fun shaderSource(shader: Int, string: String): Unit = Unit
    override fun stencilFunc(func: Int, ref: Int, mask: Int): Unit = Unit
    override fun stencilFuncSeparate(face: Int, func: Int, ref: Int, mask: Int): Unit = Unit
    override fun stencilMask(mask: Int): Unit = Unit
    override fun stencilMaskSeparate(face: Int, mask: Int): Unit = Unit
    override fun stencilOp(fail: Int, zfail: Int, zpass: Int): Unit = Unit
    override fun stencilOpSeparate(face: Int, sfail: Int, dpfail: Int, dppass: Int): Unit = Unit
    override fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: Buffer?): Unit = Unit
    override fun texImage2D(target: Int, level: Int, internalformat: Int, format: Int, type: Int, data: NativeImage): Unit = Unit
    override fun texParameterf(target: Int, pname: Int, param: Float): Unit = Unit
    override fun texParameterfv(target: Int, pname: Int, params: Buffer): Unit = Unit
    override fun texParameteri(target: Int, pname: Int, param: Int): Unit = Unit
    override fun texParameteriv(target: Int, pname: Int, params: Buffer): Unit = Unit
    override fun texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, type: Int, pixels: Buffer): Unit = Unit
    override fun uniform1f(location: Int, v0: Float): Unit = Unit
    override fun uniform1fv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform1i(location: Int, v0: Int): Unit = Unit
    override fun uniform1iv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform2f(location: Int, v0: Float, v1: Float): Unit = Unit
    override fun uniform2fv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform2i(location: Int, v0: Int, v1: Int): Unit = Unit
    override fun uniform2iv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform3f(location: Int, v0: Float, v1: Float, v2: Float): Unit = Unit
    override fun uniform3fv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform3i(location: Int, v0: Int, v1: Int, v2: Int): Unit = Unit
    override fun uniform3iv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform4f(location: Int, v0: Float, v1: Float, v2: Float, v3: Float): Unit = Unit
    override fun uniform4fv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniform4i(location: Int, v0: Int, v1: Int, v2: Int, v3: Int): Unit = Unit
    override fun uniform4iv(location: Int, count: Int, value: Buffer): Unit = Unit
    override fun uniformMatrix2fv(location: Int, count: Int, transpose: Boolean, value: Buffer): Unit = Unit
    override fun uniformMatrix3fv(location: Int, count: Int, transpose: Boolean, value: Buffer): Unit = Unit
    override fun uniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: Buffer): Unit = Unit
    override fun useProgram(program: Int): Unit = Unit
    override fun validateProgram(program: Int): Unit = Unit
    override fun vertexAttrib1f(index: Int, x: Float): Unit = Unit
    override fun vertexAttrib1fv(index: Int, v: Buffer): Unit = Unit
    override fun vertexAttrib2f(index: Int, x: Float, y: Float): Unit = Unit
    override fun vertexAttrib2fv(index: Int, v: Buffer): Unit = Unit
    override fun vertexAttrib3f(index: Int, x: Float, y: Float, z: Float): Unit = Unit
    override fun vertexAttrib3fv(index: Int, v: Buffer): Unit = Unit
    override fun vertexAttrib4f(index: Int, x: Float, y: Float, z: Float, w: Float): Unit = Unit
    override fun vertexAttrib4fv(index: Int, v: Buffer): Unit = Unit
    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, pointer: Long): Unit = Unit
    override fun viewport(x: Int, y: Int, width: Int, height: Int): Unit = Unit

    override fun drawArraysInstanced(mode: Int, first: Int, count: Int, instancecount: Int): Unit = Unit
    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, indices: Int, instancecount: Int): Unit = Unit
    override fun vertexAttribDivisor(index: Int, divisor: Int): Unit = Unit

    override fun genVertexArrays(n: Int, arrays: Buffer) = Unit
    override fun deleteVertexArrays(n: Int, arrays: Buffer) = Unit
    override fun bindVertexArray(array: Int) = Unit
}
