package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.glttt.shader.IShader;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    private IShader mShader;
    private int mFrameCount;
    private long mIntervalStartTime;
    private final IPresenter mPresenter;

    public GLTTTSurfaceRenderer( IPresenter presenter, IShader shader )
    {
    	super();
    	
        mShader = shader;
        mFrameCount = 0;
        mIntervalStartTime = 0;
        mPresenter = presenter;
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        mShader.initialize();
        GLES20.glUseProgram(mShader.getProgramHandle());

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
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

        mPresenter.drawScene(mShader);

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
