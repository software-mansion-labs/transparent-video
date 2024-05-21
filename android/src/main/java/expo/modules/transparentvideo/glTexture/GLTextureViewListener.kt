package expo.modules.transparentvideo.glTexture

import android.graphics.SurfaceTexture
import android.view.TextureView
import expo.modules.transparentvideo.glTexture.egl.EGLHandler
import expo.modules.transparentvideo.glTexture.opengl.OpenGLContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class GLTextureViewListener(
    scope: CoroutineScope,
    private val renderer: Renderer,
    requestRender: Flow<Unit>
) : TextureView.SurfaceTextureListener {
  private val openGLContext = OpenGLContext(scope)
  private var currentEGLHandler: EGLHandler? = null

  init {
    requestRender.onEach { drawFrame() }.launchIn(scope)
  }

  private fun getEglHandler(
      surfaceTexture: SurfaceTexture,
      width: Int,
      height: Int
  ): EGLHandler {
    if (currentEGLHandler != null) return currentEGLHandler as EGLHandler
    val eglHandler = EGLHandler(surfaceTexture = surfaceTexture).also { currentEGLHandler = it }
    renderer.onSurfaceCreated(gl = eglHandler.gl, config = eglHandler.config)
    renderer.onSurfaceChanged(gl = eglHandler.gl, width = width, height = height)
    renderer.onDrawFrame(gl = eglHandler.gl)
    return eglHandler
  }

  override fun onSurfaceTextureAvailable(
    surfaceTexture: SurfaceTexture,
    width: Int,
    height: Int
  ) = openGLContext.execute {
    getEglHandler(surfaceTexture, width, height)
  }

  override fun onSurfaceTextureSizeChanged(
    surfaceTexture: SurfaceTexture,
    width: Int,
    height: Int
  ) = openGLContext.execute {
    val eglHandler = getEglHandler(surfaceTexture, width, height)
    renderer.onSurfaceChanged(gl = eglHandler.gl, width = width, height = height)
  }

  override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
    val eglHandler = currentEGLHandler ?: return false
    renderer.onSurfaceDestroyed()
    eglHandler.destroy()
    currentEGLHandler = null
    return true
  }

  override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    // Do nothing
  }

  fun onDetach() = openGLContext.execute {
    val eglHandler = currentEGLHandler ?: return@execute
    renderer.onSurfaceDestroyed()
    eglHandler.destroy()
    currentEGLHandler = null
  }

  private fun drawFrame() = openGLContext.execute {
    val eglHandler = currentEGLHandler ?: return@execute
    renderer.onDrawFrame(gl = eglHandler.gl)
    eglHandler.displaySurface()
  }
}
