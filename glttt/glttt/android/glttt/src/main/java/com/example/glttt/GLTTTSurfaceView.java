package com.example.glttt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GLTTTSurfaceView extends GLSurfaceView implements IGameView
{
	private GLTTTSurfaceRenderer mSurfaceRenderer;
	private View mContentView;
    private GamePresenter mPresenter;

    public GLTTTSurfaceView(Context context, View contentView)
    {
        super(context);
        
        mContentView = contentView;
        mPresenter = new GamePresenter(this);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        this.mSurfaceRenderer = new GLTTTSurfaceRenderer(getResources(), mPresenter.getCurrentScene());
        setRenderer(mSurfaceRenderer);
    }

    public boolean onTouchEvent(MotionEvent e)
    {
        return mPresenter.onTouchEvent(e);
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
    public ModelObject getClickedModelObject(int x, int y) {
        return mSurfaceRenderer.getClickedModelObject(x, y);
    }

    @Override
    public void setCurrentScene(SceneFactory.TYPE type) {
        mSurfaceRenderer.setCurrentScene(type);
    }
}
