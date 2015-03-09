package com.example.glttt;

import java.util.LinkedHashMap;

import android.opengl.Matrix;

import com.example.glttt.pulser.IPulseReceiver;
import com.example.glttt.shader.IShader;

public class Scene
{
	private LinkedHashMap<String, ModelObject> mModelObjects;

    private IShader mShader;
    private float[] mViewMatrix;
	private float[] mProjectionMatrix;
    private float[] mEyePos;
    private float[] mEyeLookAt;
    private float[] mEyeUpVec;

    private int[] mCurrentViewPort;

    private ISceneChangeHandler mSceneChangeHandler;

    public Scene(IShader shader, ISceneChangeHandler viewportChangeHandler) {
        this( shader, viewportChangeHandler, null);
    }

    public Scene(IShader shader, ISceneChangeHandler viewportChangeHandler, IPulseReceiver pulseReceiver)
	{
        mShader = shader;
        mModelObjects = new LinkedHashMap<String, ModelObject>();

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
		mModelObjects.put(m.getId(), m);
	}
	
	public void draw()
	{
        for (LinkedHashMap.Entry<String, ModelObject> entry : mModelObjects.entrySet()) {
            entry.getValue().draw(mViewMatrix, mProjectionMatrix, mShader);
        }
	}
	
	public ModelObject getClickedModelObject( int screenX, int screenY )
	{
    	float xpos = screenX;
    	float ypos = mCurrentViewPort[3];
    	ypos -= screenY;

        for (LinkedHashMap.Entry<String, ModelObject> entry : mModelObjects.entrySet()) {
            ModelObject modelObject = entry.getValue();
			if (modelObject.clickedOn((int)xpos, (int)ypos, mViewMatrix, mProjectionMatrix, mCurrentViewPort) != null)
			{
				return modelObject;
			}
		}

		return null;
	}

    public float[] getClickPosition( String modelObject, int screenX, int screenY ) {
        ModelObject obj = mModelObjects.get(modelObject);
        if (obj != null) {
            return getClickPosition(obj, screenX, screenY);
        }

        return null;
    }

        // You would project a coordinate C onto the screen using the formula:
    // C' = P * V * M * C
    public float[] getClickPosition( ModelObject obj, int screenX, int screenY ) {
        float xpos = screenX;
        float ypos = mCurrentViewPort[3];
        ypos -= screenY;

        return obj.clickedOn((int)xpos, (int)ypos, mViewMatrix, mProjectionMatrix, mCurrentViewPort);
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

    public void onViewportChanged( int[] currentViewPort ) {
        mCurrentViewPort = currentViewPort;
        int width = currentViewPort[2];
        int height = currentViewPort[3];

        mSceneChangeHandler.onViewportChanged(width, height);
    }

    public void setZoomFactor( float zoomFactor ) {
        for (LinkedHashMap.Entry<String, ModelObject> entry : mModelObjects.entrySet()) {
            entry.getValue().setScaleFactor(zoomFactor);
        }
    }

    public void setYRotation( float degrees ) {
        for (LinkedHashMap.Entry<String, ModelObject> entry : mModelObjects.entrySet()) {
            entry.getValue().setYRotation(degrees);
        }
    }

    public ModelObject getObjectByName( String name ) {
        return mModelObjects.get(name);
    }

    public float[] getEyePos() {
        return mEyePos;
    }

    public float[] getEyeLookAt() {
        return mEyeLookAt;
    }
}
