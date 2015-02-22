package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ShaderFactory;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    public static final int FLOAT_BYTE_LENGTH = 4;

    private Resources mResources;

    private SceneFactory mSceneFactory;
    private IShader mShader;
    private SceneFactory.TYPE mCurrentSceneType;
    private Scene mCurrentScene;

    public GLTTTSurfaceRenderer( Resources resources, SceneFactory sceneFactory, IShader shader )
    {
    	super();
    	
    	mResources = resources;
    	mCurrentScene = null;
        mCurrentSceneType = SceneFactory.TYPE.NO_SCENE;
        mSceneFactory = sceneFactory;
        mShader = shader;
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        mShader.initialize();
        GLES20.glUseProgram(mShader.getProgramHandle());

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
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
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        mCurrentScene.draw();
    }
    
    public ModelObject getClickedModelObject( int screenX, int screenY )
    {
    	return mCurrentScene.getClickedModelObject(screenX, screenY);
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
