package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    public static final int FLOAT_BYTE_LENGTH = 4;

    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mColourHandle;
    private Shader mShader;

    private Resources mResources;

    private SceneFactory mSceneFactory;
    private SceneFactory.TYPE mCurrentSceneType;
    private Scene mCurrentScene;

    private boolean mSurfaceCreated;

    public GLTTTSurfaceRenderer( Resources resources, SceneFactory.TYPE initialScene )
    {
    	super();
    	
    	mResources = resources;
    	mCurrentScene = null;
        mCurrentSceneType = initialScene;
        mSceneFactory = new SceneFactory();
        mColourHandle = -1;
        mPositionHandle = -1;
        mMVPMatrixHandle = -1;
        mSurfaceCreated = false;
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        mShader = Shader.create(
                mResources.getString(R.string.vertex_shader),
                mResources.getString(R.string.fragment_shader)
        );

        mPositionHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "a_position");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "u_MVPMatrix");
        mColourHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "a_color");

        mPositionHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "a_position");
        if (mPositionHandle == -1)
        {
        	throw new ShaderException("could not get position handle");
        }
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "u_VPMatrix");
        if (mMVPMatrixHandle == -1)
        {
        	throw new ShaderException("could not get MVP matrix handle");
        }
        mColourHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "a_color");
        if (mColourHandle == -1)
        {
        	throw new ShaderException("could not get color handle");
        }

        checkGlError("glGetUniformLocation");
        
    	GLES20.glUseProgram(mShader.getProgram());
        
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        mSurfaceCreated = true;
        mCurrentScene = mSceneFactory.create(mCurrentSceneType, mPositionHandle, mColourHandle, mMVPMatrixHandle);
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

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("opengl", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public void setCurrentScene( SceneFactory.TYPE type ) {
        mCurrentSceneType = type;
        if (mSurfaceCreated) {
            mCurrentScene = mSceneFactory.create(mCurrentSceneType, mPositionHandle, mColourHandle, mMVPMatrixHandle);
        }
    }
}
