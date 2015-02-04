package com.example.glttt;

import java.util.ArrayList;

import android.opengl.Matrix;
import android.util.Log;

public class Scene
{
	private ArrayList<ModelObject> modelObjects;

	private int positionHandle;
	private int colourHandle;
	private int vpMatrixHandle;

	private float[] viewMatrix;
	private float[] projectionMatrix;

	private Scene()
	{
		this.modelObjects = new ArrayList<ModelObject>();

		this.viewMatrix = new float[16];
		this.projectionMatrix = new float[16];
	}

	public Scene(int positionHandle, int colourHandle, int vpMatrixHandle)
	{
		this();
		this.positionHandle = positionHandle;
		this.colourHandle = colourHandle;
		this.vpMatrixHandle = vpMatrixHandle;
	}
	
	public void add( ModelObject m )
	{
		this.modelObjects.add(m);
	}
	
	public void draw()
	{
		for (ModelObject modelObject : modelObjects)
		{
			drawModelObject(modelObject);
		}
	}
	
	public ModelObject getClickedModelObject( int screenX, int screenY, int[] viewport )
	{
    	float xpos = screenX;
    	float ypos = viewport[3];
    	ypos -= screenY;
    	
		for (ModelObject modelObject : modelObjects)
		{
			if (modelObject.clickedOn((int)xpos, (int)ypos, viewMatrix, projectionMatrix, viewport))
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
        final float pi = 3.1415926535897932384626433832795f;
        float fW, fH;
        fH = (float)(Math.tan( fovY / 360.0f * pi ) * zNear);
        fW = fH * aspect;
        setFrustum( -fW, fW, -fH, fH, zNear, zFar );
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
	    
	    modelObject.draw( mvpMatrix, vpMatrixHandle, positionHandle, colourHandle );
    }
}
