package com.example.glttt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.example.glttt.shader.IShader;
import com.example.glttt.shapes.Triangle;

public class ModelObject
{
    private final float[] mModelMatrix;
    private ArrayList<Triangle> mTriangles;
    private String mId;
    private float mScaleFactor;
    private float mYRotation;
    private float[] mTranslation;

    private FloatBuffer mVertexBuffer;
    private boolean mVertexBufferDirty;

	public ModelObject( String id )
	{
        this.mId = id;
		this.mModelMatrix = new float[16];
		this.mTriangles = new ArrayList<Triangle>();
    	Matrix.setIdentityM(mModelMatrix, 0);
        mScaleFactor = 1.0f;
        mYRotation = 0.0f;
        mTranslation = new float[4];
        mTranslation[0] = 0.0f;
        mTranslation[0] = 0.0f;
        mTranslation[0] = 0.0f;
        mTranslation[0] = 0.0f;
        mVertexBufferDirty = true;
	}

    public void add( Triangle t ) {
        mTriangles.add(t);
        mVertexBufferDirty = true;
    }

    public void add( Triangle[] tri ) {
        mTriangles.addAll(Arrays.asList(tri));
        mVertexBufferDirty = true;
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

    public void setTranslation( float x, float y, float z ) {
        mTranslation[0] = x;
        mTranslation[1] = y;
        mTranslation[2] = z;
        recalculateModelMatrix();
    }

	private void translate( float x, float y, float z )
	{
        synchronized (mModelMatrix) {
            Matrix.translateM(mModelMatrix, 0, x, y, z);
        }
	}

    private void rotate( float angle, float x, float y, float z ) {
        synchronized(mModelMatrix) {
            Matrix.rotateM(mModelMatrix, 0, angle, x, y, z);
        }
    }

    private void recalculateModelMatrix() {
        synchronized(mModelMatrix) {
            Matrix.setIdentityM(mModelMatrix, 0);
            rotate(mYRotation, 0.0f, 1.0f, 0.0f);
            Matrix.scaleM(mModelMatrix, 0, mScaleFactor, mScaleFactor, mScaleFactor);
            translate(mTranslation[0], mTranslation[1], mTranslation[2]);
        }
    }

    private FloatBuffer getVertexBuffer(int stride) {
        if (mVertexBufferDirty) {
            ByteBuffer vertexBB = ByteBuffer.allocateDirect(stride * 3 * 4 * mTriangles.size());
            vertexBB.order(ByteOrder.nativeOrder());
            mVertexBuffer = vertexBB.asFloatBuffer();
            for (Triangle t : mTriangles)
            {
                mVertexBuffer.put(t.getVertexData());
            }
            mVertexBufferDirty = false;
        }

        return mVertexBuffer;
    }

    public void draw( float[] mvMatrix, float[] mvpMatrix, IShader shader )
	{
        long startTimeNanos = System.nanoTime();
        shader.draw( mvMatrix, mvpMatrix, getVertexBuffer(shader.getStride()), mTriangles.size() );
        long elapsedTimeNanos = System.nanoTime() - startTimeNanos;
        Log.v("chris", "ModelObject.draw(" + mId + "): elapsed time: " + (elapsedTimeNanos / 1000000) + " ms");
	}

	private static float sign( float p1x, float p1y, float p2x, float p2y, float p3x, float p3y )
	{
		float f = (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y); 
		return f;
	}

    private float[] getTransformedPoint( float x, float y, float z ) {
        float[] resultVec = new float[4];
        float[] inputVec = new float[4];
        inputVec[0] = x;
        inputVec[1] = y;
        inputVec[2] = z;
        inputVec[3] = 1;

        synchronized (mModelMatrix) {
            Matrix.multiplyMV(resultVec, 0, mModelMatrix, 0, inputVec, 0);
        }

        return resultVec;
    }

	public boolean clickedOn( int xpos, int ypos, float[] viewMatrix, float[] projectionMatrix, int[] viewport )
	{
		for (Triangle t : mTriangles)
		{
			float[] screen0 = new float[3];
			float[] screen1 = new float[3];
			float[] screen2 = new float[3];

            float[] transformedPoint1 = getTransformedPoint( t.getX(0), t.getY(0), t.getZ(0) );
            float[] transformedPoint2 = getTransformedPoint( t.getX(1), t.getY(1), t.getZ(1) );
            float[] transformedPoint3 = getTransformedPoint( t.getX(2), t.getY(2), t.getZ(2) );

			GLU.gluProject(transformedPoint1[0], transformedPoint1[1], transformedPoint1[2], viewMatrix, 0, projectionMatrix, 0, viewport, 0, screen0, 0);
			GLU.gluProject(transformedPoint2[0], transformedPoint2[1], transformedPoint2[2], viewMatrix, 0, projectionMatrix, 0, viewport, 0, screen1, 0);
			GLU.gluProject(transformedPoint3[0], transformedPoint3[1], transformedPoint3[2], viewMatrix, 0, projectionMatrix, 0, viewport, 0, screen2, 0);
			
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

    public String toString() {
        return mId;
    }
}
