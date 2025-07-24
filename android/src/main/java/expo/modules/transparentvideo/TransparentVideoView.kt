package expo.modules.transparentvideo

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Gravity
import android.view.Surface
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoView
import expo.modules.transparentvideo.glTexture.GLTextureViewListener
import expo.modules.transparentvideo.renderer.TransparentVideoRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

@OptIn(UnstableApi::class)
class TransparentVideoView(context: Context, appContext: AppContext) : ExpoView(context, appContext) {
  val id: String = UUID.randomUUID().toString()
  private val onEnd by EventDispatcher()
  private val onError by EventDispatcher()
  private val onProgress by EventDispatcher()


  var videoPlayer: VideoPlayer? = null
    set(videoPlayer) {
      field?.let {
        TransparentVideoManager.onVideoPlayerDetachedFromView(it, this)
      }
      field = videoPlayer
      videoPlayer?.let {
        TransparentVideoManager.onVideoPlayerAttachedToView(it, this)
      }
    }

  var videoAspectRatio: Float? = null

  private lateinit var mediaPlayerSurface: Surface
  private var coroutineScope: CoroutineScope? = null
  private val onFrameAvailable =
    MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  private val renderer = TransparentVideoRenderer(onSurfaceTextureCreated = { surface -> onSurfaceTextureCreated(surface) })
  private val textureView = object : TextureView(context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      if (videoAspectRatio == null) {
        // Aspect ratio not set.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        return
      }

      videoAspectRatio?.let { ratio ->
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val viewAspectRatio = width.toFloat() / height.toFloat()
        val aspectDeformation = (ratio / viewAspectRatio) - 1f
        if (abs(aspectDeformation) <= 0.01f) {
          // We're within the allowed tolerance.
          super.onMeasure(widthMeasureSpec, heightMeasureSpec)
          return
        }
        if (aspectDeformation > 0) {
          width = (height * ratio).toInt()
        } else {
          height = (width / ratio).toInt()
        }

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY))
      }
    }
  }.apply { isOpaque = false }

  init {
    clipToOutline = true
    gravity = Gravity.CENTER
    TransparentVideoManager.registerVideoView(this)
    addView(textureView)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
    coroutineScope = scope
    with(textureView) {
      surfaceTextureListener = GLTextureViewListener(scope, renderer, onFrameAvailable)
      if (isAvailable) {
        val texture = surfaceTexture ?: return
        surfaceTextureListener?.onSurfaceTextureAvailable(texture, width, height)
      }
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    (textureView.surfaceTextureListener as GLTextureViewListener).onDetach()
    textureView.surfaceTextureListener = null
    coroutineScope?.cancel()
    coroutineScope = null
  }

  private fun onSurfaceTextureCreated(surfaceTexture: SurfaceTexture) {
    surfaceTexture.setOnFrameAvailableListener { onFrameAvailable.tryEmit(Unit) }
    val surface = Surface(surfaceTexture).also { mediaPlayerSurface = it }
    coroutineScope?.launch {
      videoPlayer?.player?.setVideoSurface(surface)
      videoPlayer?.onEndCallback = onEnd
      videoPlayer?.onErrorCallback = onError
      videoPlayer?.onProgress = onProgress
    }
  }

  companion object {
    fun isPictureInPictureSupported(): Boolean {
      return false
    }
  }
}
