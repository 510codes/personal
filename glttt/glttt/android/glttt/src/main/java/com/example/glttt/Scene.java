package com.example.glttt;

import java.util.ArrayList;

import android.opengl.Matrix;
import android.util.Log;

public class Scene
{
	private ArrayList<ModelObject> modelObjects;

	private int positionHandle;
	private int colourHandle;
	private int mvpMatrixHandle;

	private float[] viewMatrix;
	private float[] projectionMatrix;

    private int[] mCurrentViewPort;

    private ISceneChangeHandler mSceneChangeHandler;

	private Scene( ISceneChangeHandler sceneChangeHandler )
	{
		this.modelObjects = new ArrayList<ModelObject>();

		this.viewMatrix = new float[16];
		this.projectionMatrix = new float[16];

        this.mSceneChangeHandler = sceneChangeHandler;
        sceneChangeHandler.setScene(this);
	}

	public Scene(int positionHandle, int colourHandle, int mvpMatrixHandle, ISceneChangeHandler viewportChangeHandler)
	{
		this(viewportChangeHandler);
		this.positionHandle = positionHandle;
		this.colourHandle = colourHandle;
		this.mvpMatrixHandle = mvpMatrixHandle;
	}
	
	public void add( ModelObject m )
	{
		this.modelObjects.add(m);
	}
	
	public void draw()
	{
        mSceneChangeHandler.preSceneDraw();

		for (ModelObject modelObject : modelObjects)
		{
			drawModelObject(modelObject);
		}
	}
	
	public ModelObject getClickedModelObject( int screenX, int screenY )
	{
    	float xpos = screenX;
    	float ypos = mCurrentViewPort[3];
    	ypos -= screenY;
    	
		for (ModelObject modelObject : modelObjects)
		{
			if (modelObject.clickedOn((int)xpos, (int)ypos, viewMatrix, projectionMatrix, mCurrentViewPort))
			{
				return modelObject;
			}
		}

		return null;
	}
	
	public void setLookAt( float eyeX, float eyeY, float eyeZ, float lookX, float lookY, float lookZ, float upX, float upY, float upZ )
	{
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);        
	}
	
	public void setFrustum( float left, float right, float bottom, float top, float near, float far )
	{
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
	}

    public void setPerspective( float fovY, float aspect, float zNear, float zFar ) {
        final double pi = 3.1415926535897932384626433832795;
        double fW, fH;
        fH = Math.tan( fovY / 360.0 * pi ) * zNear;
        fW = fH * aspect;
        setFrustum( (float)-fW, (float)fW, (float)-fH, (float)fH, zNear, zFar );
    }
	
    private void drawModelObject( ModelObject modelObject )
    {
    	float[] mvpMatrix = new float[16];
    		    
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelObject.getModelMatrix(), 0);

	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
	    
	    modelObject.draw( mvpMatrix, mvpMatrixHandle, positionHandle, colourHandle );
    }

    public void onViewportChanged( int[] currentViewPort ) {
        mCurrentViewPort = currentViewPort;
        int width = currentViewPort[2];
        int height = currentViewPort[3];

        mSceneChangeHandler.onViewportChanged(width, height);
    }
}
