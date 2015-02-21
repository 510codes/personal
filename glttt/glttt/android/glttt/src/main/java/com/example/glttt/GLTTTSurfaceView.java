package com.example.glttt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.glttt.pulser.PulseManager;

public class GLTTTSurfaceView extends GLSurfaceView implements IGameView
{
    private static final int PHYSICS_FPS = 60;
	private final GLTTTSurfaceRenderer mSurfaceRenderer;
	private View mContentView;
    private GestureManager mGestureManager;
    private GamePresenter mPresenter;

    public GLTTTSurfaceView(Context context, View contentView)
    {
        super(context);
        
        mContentView = contentView;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        mGestureManager = new GestureManager(context);
        SceneFactory sceneFactory = new SceneFactory(new PulseManager(PHYSICS_FPS), mGestureManager);
        mSurfaceRenderer = new GLTTTSurfaceRenderer(getResources(), sceneFactory);
        setRenderer(mSurfaceRenderer);

        mPresenter = new GamePresenter(this);
        mSurfaceRenderer.setCurrentScene(mPresenter.getCurrentSceneType());
    }

    @Override
    public float getContentViewLeft() {
        return mContentView.getLeft();
    }

    @Override
    public float getContentViewTop() {
        return mContentView.getTop();
    }

    @Override
    public void setCurrentScene(SceneFactory.TYPE type) {
        mSurfaceRenderer.setCurrentScene(type);
    }

    public void setScaleFactor( float scaleFactor ) {
        mSurfaceRenderer.setScaleFactor(scaleFactor);
    }

    @Override
    public void setRotation( float degrees ) {
        mSurfaceRenderer.setRotation(degrees);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        return mGestureManager.onTouchEvent(e);
    }
}
