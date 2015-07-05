package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.text.GLText;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    private final IShader mShader;
    private final ISpriteShader mSpriteShader;
    private int mFrameCount;
    private long mIntervalStartTime;
    private final IPresenter mPresenter;
    private final GLText mGLText;

    public GLTTTSurfaceRenderer( IPresenter presenter, IShader shader, ISpriteShader spriteShader, GLText glText )
    {
    	super();
    	
        mShader = shader;
        mGLText = glText;
        mSpriteShader = spriteShader;
        mFrameCount = 0;
        mIntervalStartTime = 0;
        mPresenter = presenter;
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        mShader.initialize();
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mSpriteShader.initialize();
        if (!mGLText.load( mSpriteShader, "Roboto-Regular.ttf", 40, 2, 2 )) {
            throw new RuntimeException("could not load the font");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d("game", "onSurfaceChanged(): width: " + width + ", height: " + height);
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        int[] currentViewPort = new int[4];
        currentViewPort[0] = 0;
        currentViewPort[1] = 0;
        currentViewPort[2] = width;
        currentViewPort[3] = height;

        mPresenter.onViewportChanged(currentViewPort);
        mIntervalStartTime = System.nanoTime();
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        mPresenter.draw(mShader, mSpriteShader, System.nanoTime());

        mFrameCount++;
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - mIntervalStartTime;
        if (elapsedTime > 1000000000) {
            float fps = mFrameCount / ((float) elapsedTime / 1000000000.0f);
            //Log.d("GLTTTSurfaceRenderer", "onDrawFrame(): frames: " + mFrameCount + ", fps: " + fps);
            mFrameCount = 0;
            mIntervalStartTime = currentTime;
        }
    }
}
