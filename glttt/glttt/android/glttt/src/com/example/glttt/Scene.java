package com.example.glttt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Scene
{
	private ArrayList<Triangle> triangles;
	
	private int positionHandle;
	private int colourHandle;
	private int mvpMatrixHandle;
	
	private float[] mvpMatrix;
	private float[] modelMatrix;
	private float[] viewMatrix;
	private float[] projectionMatrix;

	private Scene()
	{
		this.triangles = new ArrayList<Triangle>();
		
		this.mvpMatrix = new float[16];
		this.modelMatrix = new float[16];
		this.viewMatrix = new float[16];
		this.projectionMatrix = new float[16];
	}

	public Scene(int positionHandle, int colourHandle, int mvpMatrixHandle)
	{
		this();
		this.positionHandle = positionHandle;
		this.colourHandle = colourHandle;
		this.mvpMatrixHandle = mvpMatrixHandle;
	}
	
	public void add( Triangle t )
	{
		this.triangles.add(t);
	}
	
	public void draw()
	{
    	Matrix.setIdentityM(modelMatrix, 0);
		
		for (Triangle t : triangles)
		{
			drawTriangle(t);
		}
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
	
    private void drawTriangle(Triangle tri)
    {
    	float[] vertexData = tri.getVertexData();
		ByteBuffer vertexBB = ByteBuffer.allocateDirect(vertexData.length * 4);
		vertexBB.order(ByteOrder.nativeOrder());
		FloatBuffer vertexFB = vertexBB.asFloatBuffer();
		vertexFB.put(vertexData);

		vertexFB.position(0);
		GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 28, vertexFB);
		GLES20.glEnableVertexAttribArray(positionHandle);
		
		vertexFB.position(3);
		GLES20.glVertexAttribPointer(colourHandle, 4, GLES20.GL_FLOAT, false, 28, vertexFB);
	    GLES20.glEnableVertexAttribArray(colourHandle);
	    
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);
	    
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);
	    
	    // Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

	    //Draw the shape
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }	
}
