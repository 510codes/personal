package com.example.glttt;

import java.util.ArrayList;

import android.opengl.Matrix;

import com.example.glttt.pulser.IPulseReceiver;

public class Scene
{
	private ArrayList<ModelObject> mModelObjects;

	private int mPositionHandle;
	private int mColourHandle;
	private int mMvpMatrixHandle;

	private float[] mViewMatrix;
	private float[] mProjectionMatrix;
    private float[] mEyePos;
    private float[] mEyeLookAt;
    private float[] mEyeUpVec;

    private int[] mCurrentViewPort;

    private ISceneChangeHandler mSceneChangeHandler;

    public Scene(int positionHandle, int colourHandle, int mvpMatrixHandle, ISceneChangeHandler viewportChangeHandler) {
        this( positionHandle, colourHandle, mvpMatrixHandle, viewportChangeHandler, null);
    }

    public Scene(int positionHandle, int colourHandle, int mvpMatrixHandle, ISceneChangeHandler viewportChangeHandler, IPulseReceiver pulseReceiver)
	{
		mPositionHandle = positionHandle;
		mColourHandle = colourHandle;
		mMvpMatrixHandle = mvpMatrixHandle;
        mModelObjects = new ArrayList<ModelObject>();

        mViewMatrix = new float[16];
        mProjectionMatrix = new float[16];
        mEyePos = new float[4];
        mEyeLookAt = new float[4];
        mEyeUpVec = new float[4];

        mEyePos[0] = 0.0f;
        mEyePos[1] = 0.0f;
        mEyePos[2] = 0.0f;
        mEyePos[3] = 1.0f;      // 4th coord of point == 1, vector == 0

        mEyeLookAt[0] = 0.0f;
        mEyeLookAt[1] = 0.0f;
        mEyeLookAt[2] = 0.0f;
        mEyeLookAt[3] = 1.0f;      // 4th coord of point == 1, vector == 0

        mEyeUpVec[0] = 0.0f;
        mEyeUpVec[1] = 1.0f;
        mEyeUpVec[2] = 0.0f;
        mEyeUpVec[3] = 0.0f;      // 4th coord of point == 1, vector == 0

        this.mSceneChangeHandler = viewportChangeHandler;
        viewportChangeHandler.setScene(this);
        if (pulseReceiver != null) {
            pulseReceiver.setScene(this);
        }
	}
	
	public void add( ModelObject m )
	{
		mModelObjects.add(m);
	}
	
	public void draw()
	{
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
        mEyePos[0] = eyeX;
        mEyePos[1] = eyeY;
        mEyePos[2] = eyeZ;

        mEyeLookAt[0] = lookX;
        mEyeLookAt[1] = lookY;
        mEyeLookAt[2] = lookZ;

        mEyeUpVec[0] = upX;
        mEyeUpVec[1] = upY;
        mEyeUpVec[2] = upZ;

        updateLookAt();
    }

    public void setEyePos( float[] pos ) {
        mEyePos = pos;

        updateLookAt();
    }

    public void setEyeLookAt( float[] pos ) {
        mEyeLookAt = pos;

        updateLookAt();
    }

    private void updateLookAt() {
        Matrix.setLookAtM(mViewMatrix, 0, mEyePos[0], mEyePos[1], mEyePos[2], mEyeLookAt[0], mEyeLookAt[1], mEyeLookAt[2], mEyeUpVec[0], mEyeUpVec[1], mEyeUpVec[2]);
    }
	
	public void setFrustum( float left, float right, float bottom, float top, float near, float far )
	{
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

    private void drawModelObject( ModelObject modelObject )
    {
    	float[] mvpMatrix = new float[16];
    		    
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
        mvpMatrix = modelObject.multiplyByModelMatrix(mViewMatrix, 0);

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

    public float[] getEyePos() {
        return mEyePos;
    }

    public float[] getEyeLookAt() {
        return mEyeLookAt;
    }
}
