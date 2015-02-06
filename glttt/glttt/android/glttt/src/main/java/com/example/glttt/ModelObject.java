package com.example.glttt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;

import com.example.glttt.shapes.Triangle;

public class ModelObject
{
	public ModelObject( String id )
	{
        this.id = id;
		this.modelMatrix = new float[16];
		this.triangles = new ArrayList<Triangle>();
    	Matrix.setIdentityM(modelMatrix, 0);
        mScaleFactor = 1.0f;
	}
	
	private float[] modelMatrix;
	private ArrayList<Triangle> triangles;
    private String id;
    private float mScaleFactor;

	public float[] getModelMatrix()
	{
		return modelMatrix;
	}

    public void add( Triangle t ) {
        triangles.add(t);
    }

    public void add( Triangle[] tri ) {
        triangles.addAll(Arrays.asList(tri));
    }

    public void setScaleFactor( float factor )
	{
        mScaleFactor = factor;
        recalculateModelMatrix();
	}

	void translate( float x, float y, float z )
	{
		Matrix.translateM(modelMatrix, 0, x, y, z);
	}

    void rotate( float angle, float x, float y, float z ) {
        Matrix.rotateM(modelMatrix, 0, angle, x, y, z);
    }

    private void recalculateModelMatrix() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.scaleM(modelMatrix, 0, mScaleFactor, mScaleFactor, mScaleFactor);
    }

	public void draw( float[] mvpMatrix, int mvpMatrixHandle, int positionHandle, int colourHandle )
	{
        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        for (Triangle t : triangles)
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
		for (Triangle t : triangles)
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
        return id;
    }
}
