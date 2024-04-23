package expo.modules.transparentvideo.renderer

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import expo.modules.transparentvideo.glTexture.Renderer
import expo.modules.transparentvideo.renderer.opengl.OpenGLHelper
import expo.modules.transparentvideo.renderer.opengl.OpenGLHelper.createVerticesBuffer
import expo.modules.transparentvideo.renderer.opengl.OpenGLProgramFactory
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class TransparentVideoRenderer(
  private val onSurfaceTextureCreated: (SurfaceTexture) -> Unit
) : Renderer {
  private val verticesBuffer: FloatBuffer = createVerticesBuffer()
  private var openGlProgram: OpenGLProgram? = null

  override fun onDrawFrame(gl: GL10) {
    val program = openGlProgram ?: return
    OpenGLHelper.updateTexture(program = program)
    OpenGLHelper.updateTransformMatrix(program = program)
    OpenGLHelper.setVertexPositionsInSurface(
      parameterHandle = program.positionInSurfaceHandle,
      verticesBuffer = verticesBuffer
    )
    OpenGLHelper.setVertexPositionsInTexture(
      parameterHandle = program.positionInTextureHandle,
      verticesBuffer = verticesBuffer
    )
    OpenGLHelper.draw()
  }

  override fun onSurfaceDestroyed() {
    verticesBuffer.clear()
    val program = openGlProgram ?: return
    GLES20.glDeleteProgram(program.nativeProgram)
    openGlProgram = null
  }

  override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
    GLES20.glViewport(0, 0, width, height)
  }

  override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
    val program = OpenGLProgramFactory.create() ?: return
    openGlProgram = program
    onSurfaceTextureCreated(program.surfaceTexture)
    GLES20.glUseProgram(program.nativeProgram)
  }
}
