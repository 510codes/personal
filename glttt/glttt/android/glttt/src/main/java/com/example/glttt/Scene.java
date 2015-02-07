package com.example.glttt;

import java.util.ArrayList;

import android.opengl.Matrix;

public class Scene
{
	private ArrayList<ModelObject> mModelObjects;

	private int mPositionHandle;
	private int mColourHandle;
	private int mMvpMatrixHandle;

	private float[] mViewMatrix;
	private float[] mProjectionMatrix;

    private int[] mCurrentViewPort;

    private ISceneChangeHandler mSceneChangeHandler;

	public Scene(int positionHandle, int colourHandle, int mvpMatrixHandle, ISceneChangeHandler viewportChangeHandler)
	{
		this.mPositionHandle = positionHandle;
		this.mColourHandle = colourHandle;
		this.mMvpMatrixHandle = mvpMatrixHandle;
        mModelObjects = new ArrayList<ModelObject>();

        mViewMatrix = new float[16];
        mProjectionMatrix = new float[16];

        this.mSceneChangeHandler = viewportChangeHandler;
        viewportChangeHandler.setScene(this);
	}
	
	public void add( ModelObject m )
	{
		mModelObjects.add(m);
	}
	
	public void draw()
	{
        mSceneChangeHandler.preSceneDraw();

		for (ModelObject modelObject : mModelObjects)
		{
			drawModelObject(modelObject);
		}
	}
	
	public ModelObject getClickedModelObject( int screenX, int screenY )
	{
    	float xpos = screenX;
    	float ypos = mCurrentViewPort[3];
    	ypos -= screenY;
    	
		for (ModelObject modelObject : mModelObjects)
		{
			if (modelObject.clickedOn((int)xpos, (int)ypos, mViewMatrix, mProjectionMatrix, mCurrentViewPort))
			{
				return modelObject;
			}
		}

		return null;
	}
	
	public void setLookAt( float eyeX, float eyeY, float eyeZ, float lookX, float lookY, float lookZ, float upX, float upY, float upZ )
	{
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
	}
	
	public void setFrustum( float left, float right, float bottom, float top, float near, float far )
	{
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
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
	    Matrix.multiplyMM(mvpMatrix, 0, mViewMatrix, 0, modelObject.getModelMatrix(), 0);

	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvpMatrix, 0);
	    
	    modelObject.draw( mvpMatrix, mMvpMatrixHandle, mPositionHandle, mColourHandle);
    }

    public void onViewportChanged( int[] currentViewPort ) {
        mCurrentViewPort = currentViewPort;
        int width = currentViewPort[2];
        int height = currentViewPort[3];

        mSceneChangeHandler.onViewportChanged(width, height);
    }

    public void setZoomFactor( float zoomFactor ) {
        for (ModelObject modelObject : mModelObjects)
        {
            modelObject.setScaleFactor(zoomFactor);
        }
    }

    public void setYRotation( float degrees ) {
        for (ModelObject modelObject : mModelObjects)
        {
            modelObject.setYRotation(degrees);
        }
    }
}
