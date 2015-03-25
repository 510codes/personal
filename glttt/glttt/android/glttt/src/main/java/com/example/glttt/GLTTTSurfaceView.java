package com.example.glttt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ShaderFactory;

public class GLTTTSurfaceView extends GLSurfaceView
{
    private final GestureManager mGestureManager;
    private GamePresenter mPresenter;

    public GLTTTSurfaceView(Context context)
    {
        super(context);

        ShaderFactory shaderFactory = new ShaderFactory(getResources());
        IShader shader = shaderFactory.createPerFragShader();

        mGestureManager = new GestureManager(context);
        mPresenter = new GamePresenter(mGestureManager, shader);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new GLTTTSurfaceRenderer(mPresenter, shader));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        return mGestureManager.onTouchEvent(e);
    }
}
