package com.example.glttt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import android.opengl.GLU;
import android.opengl.Matrix;

import com.example.glttt.shader.IShader;
import com.example.glttt.shapes.Triangle;

public class ModelObject
{
    private final float[] mModelMatrix;
    private ArrayList<Triangle> mTriangles;
    private String mId;
    private Transformation mTransformation;

    private FloatBuffer mVertexBuffer;
    private boolean mVertexBufferDirty;

	public ModelObject( String id )
	{
        this.mId = id;
        this.mModelMatrix = new float[16];
        mTransformation = new Transformation();
		this.mTriangles = new ArrayList<Triangle>();
    	Matrix.setIdentityM(mModelMatrix, 0);
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

    private float[] multiplyByModelMatrix( float[] matrix, int index ) {
        float[] newMatrix = new float[16];
        synchronized (mModelMatrix) {
            Matrix.multiplyMM(newMatrix, 0, matrix, index, mModelMatrix, 0);
        }
        return newMatrix;
    }

    public Transformation getTransformation() {
        return new Transformation(mTransformation);
    }

    public void setTransformation( Transformation t ) {
        this.mTransformation = t;
        recalculateModelMatrix();
    }

    public void setScaleFactor( float factor )
	{
        mTransformation.setScaleFactor(factor);
        recalculateModelMatrix();
	}

    public void setYRotation( float degrees ) {
        mTransformation.setYRotation(degrees);
        recalculateModelMatrix();
    }

    public void setTranslation( float x, float y, float z ) {
        mTransformation.setTranslation(x, y, z);
        recalculateModelMatrix();
    }

    private void recalculateModelMatrix() {
        synchronized(mModelMatrix) {
            mTransformation.calculateTransformationMatrix(mModelMatrix);
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

    public void draw( float[] viewMatrix, float[] projectionMatrix, IShader shader )
	{
        long startTimeNanos = System.nanoTime();

        float[] mvMatrix = multiplyByModelMatrix(viewMatrix, 0);

        float[] mvpMatrix = new float[16];
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);

        shader.draw( mvMatrix, mvpMatrix, getVertexBuffer(shader.getStride()), mTriangles.size() );
        long elapsedTimeNanos = System.nanoTime() - startTimeNanos;
        //Log.v("chris", "ModelObject.draw(" + mId + "): elapsed time: " + (elapsedTimeNanos / 1000000) + " ms");
	}

	private static float sign( float p1x, float p1y, float p2x, float p2y, float p3x, float p3y )
	{
		float f = (p1x - p3x) * (p2y - p3y) - (p2x - p3x) * (p1y - p3y); 
		return f;
	}

    private float[] getTransformedPoint( float x, float y, float z, float[] modelViewMatrix, float[] projectionMatrix, int[] viewport ) {
        float[] resultVec = new float[4];

        GLU.gluProject(x, y, z, modelViewMatrix, 0, projectionMatrix, 0, viewport, 0, resultVec, 0);

        return resultVec;
    }

    float[] glhUnProjectf(float winx, float winy, float winz, float[] modelview, float[] projection, int[] viewport)
    {
        float[] objectCoordinate = new float[4];
        float[] m = new float[16], A = new float[16];
        float[] in = new float[4], out = new float[4];

        Matrix.multiplyMM(A, 0, projection, 0, modelview, 0);
        Matrix.invertM(m, 0, A, 0);

        //Transformation of normalized coordinates between -1 and 1
        in[0]=(winx-(float)viewport[0])/(float)viewport[2]*2.0f-1.0f;
        in[1]=(winy-(float)viewport[1])/(float)viewport[3]*2.0f-1.0f;
        in[2]=2.0f*winz-1.0f;
        in[3]=1.0f;

        Matrix.multiplyMV(out, 0, m, 0, in, 0);

        if(out[3]==0.0)
            return null;

        out[3]=1.0f/out[3];
        objectCoordinate[0]=out[0]*out[3];
        objectCoordinate[1]=out[1]*out[3];
        objectCoordinate[2]=out[2]*out[3];

        return objectCoordinate;
    }

    private float[] getUnTransformedPoint( float x, float y, float z, float[] modelViewMatrix, float[] projectionMatrix, int[] viewport ) {
        float[] resultVec;

        //GLU.gluUnProject(x, y, z, modelViewMatrix, 0, projectionMatrix, 0, viewport, 0, resultVec, 0);
        //GLU.gluUnProject(x, y, 1.0f, modelViewMatrix, 0, projectionMatrix, 0, viewport, 0, resultVec, 0);

        //resultVec = glhUnProjectf(x, y, 0.0f, modelViewMatrix, projectionMatrix, viewport);
        //resultVec = glhUnProjectf(x, y, 1.0f, modelViewMatrix, projectionMatrix, viewport);

        resultVec = glhUnProjectf(x, y, z, modelViewMatrix, projectionMatrix, viewport);

        return resultVec;
    }

    // returns the position, in modelspace, of the click
    public float[] clickedOn( int screenX, int screenY, float[] viewMatrix, float[] projectionMatrix, int[] viewport )
	{
        float[] pos = null;
        float[] mvMatrix = multiplyByModelMatrix(viewMatrix, 0);

		for (Triangle t : mTriangles) {
            float[] screen0 = getTransformedPoint(t.getX(0), t.getY(0), t.getZ(0), mvMatrix, projectionMatrix, viewport);
            float[] screen1 = getTransformedPoint(t.getX(1), t.getY(1), t.getZ(1), mvMatrix, projectionMatrix, viewport);
            float[] screen2 = getTransformedPoint(t.getX(2), t.getY(2), t.getZ(2), mvMatrix, projectionMatrix, viewport);

            boolean b1, b2, b3;

            float sign1 = sign(screenX, screenY, screen0[0], screen0[1], screen1[0], screen1[1]);
            b1 = sign1 < 0.0f;
            float sign2 = sign(screenX, screenY, screen1[0], screen1[1], screen2[0], screen2[1]);
            b2 = sign2 < 0.0f;
            float sign3 = sign(screenX, screenY, screen2[0], screen2[1], screen0[0], screen0[1]);
            b3 = sign3 < 0.0f;

            boolean inside = ((b1 == b2) && (b2 == b3));

            if (inside) {
                float[] pos1 = getUnTransformedPoint(screenX, screenY, 0.0f, mvMatrix, projectionMatrix, viewport);
                float[] pos2 = getUnTransformedPoint(screenX, screenY, 1.0f, mvMatrix, projectionMatrix, viewport);
                float[] d = new float[4];
                Math3d.vector(d, pos2, pos1);
                Math3d.normalize(d);
                float[] vec0 = new float[4];
                float[] vec1 = new float[4];
                float[] surfaceNormal = new float[4];
                Math3d.vector(vec0, t.getVertex(1), t.getVertex(0));
                Math3d.vector(vec1, t.getVertex(2), t.getVertex(0));
                Math3d.crossProduct(surfaceNormal, vec0, vec1);
                Math3d.normalize(surfaceNormal);

                if (Math3d.dotProduct(surfaceNormal, d) < 0.0f) {
                    float dist = getIntersection(t, pos1, d);
                    if (!Float.isNaN(dist)) {
                        pos = new float[4];
                        pos[0] = pos1[0] + (d[0] * dist);
                        pos[1] = pos1[1] + (d[1] * dist);
                        pos[2] = pos1[2] + (d[2] * dist);
                        pos[3] = 1.0f;

                        break;
                    }
                }
            }
        }

		return pos;
	}

    private float getIntersection( Triangle tri, float[] p, float[] d ) {
        float e1[] = new float[3];
        float e2[] = new float[3];
        float h[] = new float[3];
        float s[] = new float[3];
        float q[] = new float[3];
        float a,f,u,v;
        float[] v0 = tri.getVertex(0);
        float[] v1 = tri.getVertex(1);
        float[] v2 = tri.getVertex(2);
        Math3d.vector(e1,v1,v0);
        Math3d.vector(e2, v2, v0);

        Math3d.crossProduct(h,d,e2);
        a = Math3d.innerProduct(e1,h);

        if (a > -0.00001 && a < 0.00001)
            return Float.NaN;

        f = 1/a;
        Math3d.vector(s,p,v0);
        u = f * (Math3d.innerProduct(s,h));

        if (u < 0.0 || u > 1.0)
            return Float.NaN;

        Math3d.crossProduct(q,s,e1);
        v = f * Math3d.innerProduct(d,q);

        if (v < 0.0 || u + v > 1.0)
            return Float.NaN;

        // at this stage we can compute t to find out where
        // the intersection point is on the line
        float t = f * Math3d.innerProduct(e2,q);

        if (t > 0.00001) // ray intersection
            return t;

        else // this means that there is a line intersection
            // but not a ray intersection
            return Float.NaN;
    }

    public String toString() {
        return mId;
    }

    public String getId() { return mId; }
}
