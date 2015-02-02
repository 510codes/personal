package com.example.glttt;

import java.nio.IntBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;
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
	
	public ModelObject getClickedModelObject( int screenX, int screenY )
	{
    	IntBuffer viewport = IntBuffer.allocate(4);
    	GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport);
    	
    	float xpos = screenX;
    	float ypos = viewport.get(3);
    	ypos -= screenY;
    	
    	Log.e("game", "xpos, ypos: ("+xpos+", "+ypos+")");

		for (ModelObject modelObject : modelObjects)
		{
			if (modelObject.clickedOn((int)xpos, (int)ypos, viewMatrix, projectionMatrix, viewport.array()))
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
	
    private void drawModelObject( ModelObject modelObject )
    {
    	float[] mvpMatrix = new float[16];
    		    
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelObject.getModelMatrix(), 0);
	    
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
	    
	    // Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(vpMatrixHandle, 1, false, mvpMatrix, 0);

	    modelObject.draw( positionHandle, colourHandle );

	    //Draw the shape
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }	
}