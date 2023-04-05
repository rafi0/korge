package korlibs.korge.view

import korlibs.datastructure.*
import korlibs.io.lang.*
import korlibs.korge.internal.*
import korlibs.korge.render.*
import korlibs.korge.view.property.*
import korlibs.math.geom.*
import korlibs.render.*

inline fun Container.fixedSizeCachedContainer(width: Float, height: Float, cache: Boolean = true, clip: Boolean = true, callback: @ViewDslMarker CachedContainer.() -> Unit = {}) =
    FixedSizeCachedContainer(width, height, cache, clip).addTo(this, callback)

inline fun Container.cachedContainer(cache: Boolean = true, callback: @ViewDslMarker CachedContainer.() -> Unit = {}) =
    CachedContainer(cache).addTo(this, callback)

open class FixedSizeCachedContainer(
    override var width: Float = 100f,
    override var height: Float = 100f,
    cache: Boolean = true,
    var clip: Boolean = true,
) : CachedContainer(cache), View.Reference {
    override fun getLocalBoundsInternal(): Rectangle = Rectangle(0f, 0f, width, height)

    private var renderingInternalRef = Ref(false)

    private val tempRect = MRectangle()

    @OptIn(KorgeInternal::class)
    override fun renderInternal(ctx: RenderContext) {
        FixedSizeContainer.renderClipped(this, ctx, clip, renderingInternalRef) { super.renderInternal(ctx) }
    }
}

open class CachedContainer(
    @property:ViewProperty
    var cache: Boolean = true
) : Container(), InvalidateNotifier {
    //@ViewProperty
    //var cache: Boolean = cache

    inner class CacheTexture(val ctx: RenderContext) : Closeable {
        val rb = ctx.unsafeAllocateFrameBuffer(16, 16, onlyThisFrame = false)
        val texBase = TextureBase(rb.tex, 16, 16)
        var tex = Texture(texBase)

        fun resize(width: Int, height: Int) {
            rb.setSize(0, 0, width, height)
            texBase.width = width
            texBase.height = height
            tex = Texture(texBase)
        }
        override fun close() {
            _cacheTex = null
            ctx.unsafeFreeFrameBuffer(rb)
        }
    }

    private var _cacheTex: CacheTexture? = null
    private var dirty = true
    private var scaledCache = -1.0
    private var lbounds = MRectangle()

    override fun invalidateRender() {
        super.invalidateRender()
        dirty = true
    }

    override fun renderInternal(ctx: RenderContext) {
        if (!visible) return
        if (!cache) {
            return super.renderInternal(ctx)
        }

        if (_cacheTex == null) {
            _cacheTex = CacheTexture(ctx)
            dirty = true
        }
        val cache = _cacheTex!!
        ctx.refGcCloseable(cache)

        val renderScale = when (ctx.views?.gameWindow?.quality) {
            GameWindow.Quality.PERFORMANCE -> 1.0
            else -> ctx.devicePixelRatio
        }
        //val renderScale = 1.0

        if (dirty || scaledCache != renderScale) {
            scaledCache = renderScale
            lbounds.copyFrom(getLocalBoundsOptimizedAnchored(includeFilters = false))
            dirty = false
            val texWidth = (lbounds.width * renderScale).toInt().coerceAtLeast(1)
            val texHeight = (lbounds.height * renderScale).toInt().coerceAtLeast(1)
            cache.resize(texWidth, texHeight)
            ctx.flush()
            ctx.renderToFrameBuffer(cache.rb) {
                //ctx.ag.clear(Colors.TRANSPARENT, clearColor = true)
                ctx.setViewMatrixTemp(globalMatrixInv
                    .translated(-lbounds.x, -lbounds.y)
                    .scaled(renderScale)
                ) {
                    super.renderInternal(ctx)
                }
            }
        }

        ctx.useBatcher { batch ->
            batch.drawQuad(
                cache.tex,
                m = globalMatrix
                    .pretranslated(lbounds.x, lbounds.y)
                    .prescaled(1.0 / renderScale)
                ,
                colorMul = renderColorMul,
                blendMode = blendMode,
            )
        }
    }

    override val _invalidateNotifierForChildren: InvalidateNotifier get() = this

    override fun invalidatedView(view: BaseView?) {
        dirty = true
        parent?._invalidateNotifier?.invalidatedView(view)
    }
}
