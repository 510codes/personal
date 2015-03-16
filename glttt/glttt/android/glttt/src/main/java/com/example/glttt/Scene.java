package com.example.glttt;

import java.util.LinkedHashMap;

import android.opengl.Matrix;

import com.example.glttt.pulser.IPulseReceiver;
import com.example.glttt.shader.IShader;

public class Scene
{
	private LinkedHashMap<String, ModelObject> mModelObjects;

    private float[] mViewMatrix;
	private float[] mProjectionMatrix;
    private float[] mEyePos;
    private float[] mEyeLookAt;
    private float[] mEyeUpVec;

    private int[] mCurrentViewPort;

    private ISceneChangeHandler mSceneChangeHandler;

    public Scene(ISceneChangeHandler viewportChangeHandler) {
        this( viewportChangeHandler, null);
    }

    public Scene(ISceneChangeHandler viewportChangeHandler, IPulseReceiver pulseReceiver)
	{
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
	
	public void draw(IShader shader)
	{
        for (LinkedHashMap.Entry<String, ModelObject> entry : mModelObjects.entrySet()) {
            entry.getValue().draw(mViewMatrix, mProjectionMatrix, shader);
        }
	}
	
	public ModelObject getClickedModelObject( int screenX, int screenY )
	{
    	float xpos = screenX;
    	float ypos = mCurrentViewPort[3];
    	ypos -= screenY;
        ModelObject closestObject = null;
        float closestDist = Float.NaN;

        for (LinkedHashMap.Entry<String, ModelObject> entry : mModelObjects.entrySet()) {
            ModelObject modelObject = entry.getValue();
            float[] pos = new float[4];
            float[] dir = new float[4];
			boolean found = modelObject.clickedOn((int)xpos, (int)ypos, mViewMatrix, mProjectionMatrix, mCurrentViewPort, pos, dir);
            if (found) {
                float[] transformedPos;
                transformedPos = modelObject.multiplyVectorByModelMatrix(pos, 0);
                float[] vec = new float[4];
                Math3d.vector(vec, transformedPos, mEyePos);
                float dist = Math3d.vectorlength(vec);

                if (closestObject != null) {
                    if (Float.isNaN(closestDist) || dist < closestDist) {
                        closestDist = dist;
                        closestObject = modelObject;
                    }
                }
                else {
                    closestObject = modelObject;
                    closestDist = dist;
                }
			}
		}

		return closestObject;
	}

    public boolean getClickPosition( String modelObject, int screenX, int screenY, float[] outPos, float[] outDir ) {
        ModelObject obj = mModelObjects.get(modelObject);
        if (obj != null) {
            return getClickPosition(obj, screenX, screenY, outPos, outDir);
        }

        return false;
    }

    public boolean getClickPosition( ModelObject obj, int screenX, int screenY, float[] outPos, float[] outDir ) {
        float xpos = screenX;
        float ypos = mCurrentViewPort[3];
        ypos -= screenY;

        return obj.clickedOn((int)xpos, (int)ypos, mViewMatrix, mProjectionMatrix, mCurrentViewPort, outPos, outDir);
    }

    public boolean getPlaneIntersection( ModelObject obj, int screenX, int screenY, float[] planePoint, float[] planeNormal, float[] outPos, float[] outDir ) {
        float xpos = screenX;
        float ypos = mCurrentViewPort[3];
        ypos -= screenY;

        return obj.getPlaneIntersection((int)xpos, (int)ypos, planePoint, planeNormal, mViewMatrix, mProjectionMatrix, mCurrentViewPort, outPos, outDir);
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

    public void setEyePos( float x, float y, float z ) {
        mEyePos[0] = x;
        mEyePos[1] = y;
        mEyePos[2] = z;

        updateLookAt();
    }

    public void setEyeLookAt( float x, float y, float z ) {
        mEyeLookAt[0] = x;
        mEyeLookAt[1] = y;
        mEyeLookAt[2] = z;

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
}
