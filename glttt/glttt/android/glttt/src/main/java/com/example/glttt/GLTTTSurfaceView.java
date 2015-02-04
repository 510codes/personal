package com.example.glttt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GLTTTSurfaceView extends GLSurfaceView
{
	private GameController controller;
	private GLTTTSurfaceRenderer surfaceRenderer;
	private View contentView;

    public GLTTTSurfaceView(Context context, GameController controller, View contentView)
    {
        super(context);
        
        this.contentView = contentView;
        this.controller = controller;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        this.surfaceRenderer = new GLTTTSurfaceRenderer(getResources()); 
        setRenderer(surfaceRenderer);
    }

    public boolean onTouchEvent(MotionEvent e)
    {
    	float x = e.getRawX() - contentView.getLeft();
    	float y = e.getRawY() - contentView.getTop();
    	
    	ModelObject mo = surfaceRenderer.getClickedModelObject((int)x, (int)y);
    	Log.e("game", "clicked object: " + mo);
    	
    	switch (e.getAction())
    	{
    		case MotionEvent.ACTION_DOWN:
    			controller.actionStart((int)x, (int)y);
    	}
    	
    	return true;
    }
}
