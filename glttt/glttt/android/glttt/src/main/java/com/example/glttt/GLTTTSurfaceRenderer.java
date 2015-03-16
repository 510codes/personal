package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.glttt.shader.IShader;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    private SceneFactory mSceneFactory;
    private IShader mShader;
    private SceneFactory.TYPE mCurrentSceneType;
    private Scene mCurrentScene;
    private int mFrameCount;
    private long mIntervalStartTime;

    public GLTTTSurfaceRenderer( SceneFactory sceneFactory, IShader shader )
    {
    	super();
    	
    	mCurrentScene = null;
        mCurrentSceneType = SceneFactory.TYPE.NO_SCENE;
        mSceneFactory = sceneFactory;
        mShader = shader;
        mFrameCount = 0;
        mIntervalStartTime = 0;
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

        setCurrentScene(mCurrentSceneType);
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

        mCurrentScene.onViewportChanged(currentViewPort);
        mIntervalStartTime = System.nanoTime();
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        mCurrentScene.draw();

        mFrameCount++;
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - mIntervalStartTime;
        if (elapsedTime > 1000000000) {
            float fps = mFrameCount / ((float)elapsedTime / 1000000000.0f);
            //Log.d("GLTTTSurfaceRenderer", "onDrawFrame(): frames: " + mFrameCount + ", fps: " + fps);
            mFrameCount = 0;
            mIntervalStartTime = currentTime;
        }
    }
    
    public void setCurrentScene( SceneFactory.TYPE type ) {
        mCurrentSceneType = type;
        mCurrentScene = mSceneFactory.create(mCurrentSceneType, mShader);
    }

    public void setScaleFactor( float scaleFactor ) {
        mCurrentScene.setZoomFactor(scaleFactor);
    }

    public void setRotation( float degrees ) {
        mCurrentScene.setYRotation(degrees);
    }
}
