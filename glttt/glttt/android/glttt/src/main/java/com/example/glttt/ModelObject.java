package com.example.glttt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;

import com.example.glttt.shapes.Triangle;

public class ModelObject
{
    private final float[] mModelMatrix;
    private ArrayList<Triangle> mTriangles;
    private String mId;
    private float mScaleFactor;
    private float mYRotation;

	public ModelObject( String id )
	{
        this.mId = id;
		this.mModelMatrix = new float[16];
		this.mTriangles = new ArrayList<Triangle>();
    	Matrix.setIdentityM(mModelMatrix, 0);
        mScaleFactor = 1.0f;
        mYRotation = 0.0f;
	}

    public void add( Triangle t ) {
        mTriangles.add(t);
    }

    public void add( Triangle[] tri ) {
        mTriangles.addAll(Arrays.asList(tri));
    }

    public float[] multiplyByModelMatrix( float[] matrix, int index ) {
        float[] newMatrix = new float[16];
        synchronized (mModelMatrix) {
            Matrix.multiplyMM(newMatrix, 0, matrix, index, mModelMatrix, 0);
        }
        return newMatrix;
    }

    public void setScaleFactor( float factor )
	{
        mScaleFactor = factor;
        recalculateModelMatrix();
	}

    public void setYRotation( float degrees ) {
        mYRotation = degrees;
        recalculateModelMatrix();
    }

	void translate( float x, float y, float z )
	{
        synchronized (mModelMatrix) {
            Matrix.translateM(mModelMatrix, 0, x, y, z);
        }
	}

    void rotate( float angle, float x, float y, float z ) {
        synchronized(mModelMatrix) {
            Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);
        }
    }

    private void recalculateModelMatrix() {
        synchronized(mModelMatrix) {
            Matrix.setIdentityM(mModelMatrix, 0);
            rotate(mYRotation, 0.0f, 1.0f, 0.0f);
            Matrix.scaleM(mModelMatrix, 0, mScaleFactor, mScaleFactor, mScaleFactor);
        }
    }

	public void draw( float[] mvpMatrix, int mvpMatrixHandle, int positionHandle, int colourHandle )
	{
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        for (Triangle t : mTriangles)
		{
			drawTriangle( t, positionHandle, colourHandle );
		}
	}

	private static float sign( float p1x, float p1y, float p2x, float p2y, float p3x, float p3y )
	{
		float f = (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y); 
		return f;
	}

	public boolean clickedOn( int xpos, int ypos, float[] viewMatrix, float[] projectionMatrix, int[] viewport )
	{
		for (Triangle t : mTriangles)
		{
			float[] screen0 = new float[3];
			float[] screen1 = new float[3];
			float[] screen2 = new float[3];
			GLU.gluProject(t.getX(0), t.getY(0), t.getZ(0), viewMatrix, 0, projectionMatrix, 0, viewport, 0, screen0, 0);
			GLU.gluProject(t.getX(1), t.getY(1), t.getZ(1), viewMatrix, 0, projectionMatrix, 0, viewport, 0, screen1, 0);
			GLU.gluProject(t.getX(2), t.getY(2), t.getZ(2), viewMatrix, 0, projectionMatrix, 0, viewport, 0, screen2, 0);
			
			boolean b1, b2, b3;
			
			b1 = sign(xpos, ypos, screen0[0], screen0[1], screen1[0], screen1[1]) < 0.0f;
			b2 = sign(xpos, ypos, screen1[0], screen1[1], screen2[0], screen2[1]) < 0.0f;
			b3 = sign(xpos, ypos, screen2[0], screen2[1], screen0[0], screen0[1]) < 0.0f;
	
			boolean inside = ((b1 == b2) && (b2 == b3));
			
			if (inside)
			{
				return true;
			}			
		}
		
		return false;
	}

    private void drawTriangle(Triangle tri, int positionHandle, int colourHandle) {
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

        //Draw the shape
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

    public String toString() {
        return mId;
    }
}
