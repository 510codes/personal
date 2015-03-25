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
    private Transformation mTransformation;
    private float[] mExtentVertex;
    private float mExtent;

    private FloatBuffer mVertexBuffer;
    private boolean mVertexBufferDirty;
    private PhysicsAttribs mPhysicsAttribs;
    private IPhysicsAction mPhysicsAction;
    private boolean mAtRest;
    private final boolean mUseExtentSphere;

    public ModelObject( String id ) {
        this(id, null, null, false);
    }

    public ModelObject( String id, boolean useExtentSphere ) {
        this(id, null, null, useExtentSphere);
    }

    public ModelObject( String id, PhysicsAttribs attribs, IPhysicsAction action, boolean useExtentSphere )
	{
        this.mId = id;
        this.mModelMatrix = new float[16];
        mTransformation = new Transformation();
		this.mTriangles = new ArrayList<Triangle>();
    	Matrix.setIdentityM(mModelMatrix, 0);
        mVertexBufferDirty = true;
        mExtentVertex = null;
        mExtent = -0.0f;
        mPhysicsAttribs = attribs;
        mPhysicsAction = action;
        mAtRest = true;
        mUseExtentSphere = useExtentSphere;
	}

    public void add( Triangle t ) {
        mTriangles.add(t);
        mVertexBufferDirty = true;
        recalculateExtent();
    }

    public void add( Triangle[] t ) {
        mTriangles.addAll(Arrays.asList(t));
        mVertexBufferDirty = true;
        recalculateExtent();
    }

    private void recalculateExtent() {
        mExtent = -0.0f;
        for (Triangle t : mTriangles) {
            for (int i = 0; i < 3; ++i) {
                float len = Math3d.vectorlength(t.getVertex(i));
                if (len > mExtent) {
                    mExtentVertex = t.getVertex(i);
                    mExtent = len;
                }
            }
        }
    }

    public float[] multiplyMatrixByModelMatrix( float[] matrix, int index ) {
        float[] newMatrix = new float[16];
        synchronized (mModelMatrix) {
            Matrix.multiplyMM(newMatrix, 0, matrix, index, mModelMatrix, 0);
        }
        return newMatrix;
    }

    public float[] getOriginInModelspace() {
        float[] origin = new float[4];
        origin[0] = 0.0f;
        origin[1] = 0.0f;
        origin[2] = 0.0f;
        origin[3] = 1.0f;

        return multiplyVectorByModelMatrix(origin, 0);
    }

    public float[] getExtentVertexInModelspace() {
        float[] extentVertex = new float[4];
        extentVertex[0] = mExtentVertex[0];
        extentVertex[1] = mExtentVertex[1];
        extentVertex[2] = mExtentVertex[2];
        extentVertex[3] = 1.0f;

        return multiplyVectorByModelMatrix(extentVertex, 0);
    }

    public float[] getUpVectorInModelspace() {
        float[] upVector = new float[4];
        upVector[0] = 0.0f;
        upVector[1] = 1.0f;
        upVector[2] = 0.0f;
        upVector[3] = 0.0f;

        float[] upVecInModelSpace = multiplyVectorByModelMatrix(upVector, 0);
        Math3d.normalize(upVecInModelSpace);

        return upVecInModelSpace;
    }

    public float[] multiplyVectorByModelMatrix( float[] vector, int index ) {
        float[] newVector = new float[4];
        synchronized (mModelMatrix) {
            Matrix.multiplyMV(newVector, 0, mModelMatrix, index, vector, 0);
        }
        return newVector;
    }

    public Transformation getTransformation() {
        return new Transformation(mTransformation);
    }

    public void setTransformation( Transformation t ) {
        this.mTransformation = new Transformation(t);
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

        float[] mvMatrix = multiplyMatrixByModelMatrix(viewMatrix, 0);

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

    static boolean glhUnProjectf(float winx, float winy, float winz, float[] modelview, float[] projection, int[] viewport, float[] outPos)
    {
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
            return false;

        out[3]=1.0f/out[3];
        outPos[0]=out[0]*out[3];
        outPos[1]=out[1]*out[3];
        outPos[2]=out[2]*out[3];
        outPos[3] = 1.0f;

        return true;
    }

    private static boolean getUnTransformedPoint( float x, float y, float z, float[] modelViewMatrix, float[] projectionMatrix, int[] viewport, float[] outPos ) {
        return glhUnProjectf(x, y, z, modelViewMatrix, projectionMatrix, viewport, outPos);
    }

    private static String vectorToString( float[] v ) {
        return "[" + v[0] + ", " + v[1] + ", " + v[2] + ", " + v[3] + "]";
    }

    // returns the position, in modelspace, of the click
    public boolean clickedOn( boolean quick, int screenX, int screenY, float[] viewMatrix, float[] projectionMatrix, int[] viewport, float[] outPos, float[] outDir )
	{
        float[] mvMatrix = multiplyMatrixByModelMatrix(viewMatrix, 0);
        if (mUseExtentSphere) {
            float[] rayOrigin = new float[4];
            float[] rayDirection = new float[4];
            getScreenTouchRay(screenX, screenY, mvMatrix, projectionMatrix, viewport, rayOrigin, rayDirection);
            float[] origin = new float[4];
            origin[0] = 0.0f;
            origin[1] = 0.0f;
            origin[2] = 0.0f;
            origin[3] = 1.0f;
            float r = getExtent();
            if (!Math3d.getSphereIntersection(origin, r, rayOrigin, rayDirection)) {
                return false;
            }

            if (quick) {
                outPos[0] = getExtent();
                outPos[1] = getExtent();
                outPos[2] = getExtent();
                outPos[3] = 1.0f;

                outDir[0] = -rayDirection[0];
                outDir[1] = -rayDirection[1];
                outDir[2] = -rayDirection[2];
                outDir[3] = 0.0f;
            }

            return true;
        }

        boolean found = false;

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
                float[] surfaceNormal = new float[4];
                if (Math3d.getSurfaceNormal(surfaceNormal, t.getVertex(0), t.getVertex(1), t.getVertex(2))) {
                    float[] rayOrigin = new float[4];
                    float[] rayDirection = new float[4];
                    getScreenTouchRay(screenX, screenY, mvMatrix, projectionMatrix, viewport, rayOrigin, rayDirection);
                    if (Math3d.getPlaneIntersection(t.getVertex(0), surfaceNormal, rayOrigin, rayDirection, outPos, outDir)) {
                        found = true;
                        break;
                    }
                }
            }
        }

		return found;
	}

    private static void getScreenTouchRay( int screenX, int screenY, float[] mvMatrix, float[] projectionMatrix, int[] viewport, float[] outPos, float[] outDir ) {
        float[] pos1 = new float[4];
        getUnTransformedPoint(screenX, screenY, 0.0f, mvMatrix, projectionMatrix, viewport, outPos);
        getUnTransformedPoint(screenX, screenY, 1.0f, mvMatrix, projectionMatrix, viewport, pos1);
        Math3d.vector(outDir, pos1, outPos);
        Math3d.normalize(outDir);
    }

    public boolean getPlaneIntersection( int screenX, int screenY, float[] surfacePoint, float[] surfaceNormal, float[] viewMatrix, float[] projectionMatrix, int[] viewport, float[] outPos, float[] outDir ) {
        float[] mvMatrix = multiplyMatrixByModelMatrix(viewMatrix, 0);

        float[] p;
        float[] n;
        p = multiplyVectorByModelMatrix(surfacePoint, 0);
        n = multiplyVectorByModelMatrix(surfaceNormal, 0);

        float[] rayOrigin = new float[4];
        float[] rayDirection = new float[4];
        getScreenTouchRay(screenX, screenY, mvMatrix, projectionMatrix, viewport, rayOrigin, rayDirection);

        return Math3d.getPlaneIntersection(p, n, rayOrigin, rayDirection, outPos, outDir);
    }

    private static float getIntersection( Triangle tri, float[] p, float[] d ) {
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
        Math3d.vector(e2,v2,v0);

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

    public void setId( String id ) {
        mId = id;
    }

    public void updatePhysics( float deltaTimeInS ) {
        if (!mAtRest) {
            if (mPhysicsAttribs != null) {
                float vel = mPhysicsAttribs.onDeltaTime(deltaTimeInS);
                if (vel != 0.0f) {
                    //Log.d("ModelObject", mId + ": updatePhysics(): deltaTimeInS: " + deltaTimeInS + ", vel: " + vel);
                    if (!mPhysicsAction.onVelocityChange(deltaTimeInS, vel)) {
                        mAtRest = true;
                    }
                }
                else {
                    mAtRest = true;
                }
            }
        }
    }

    public PhysicsAttribs getPhysicsAttribs() {
        return new PhysicsAttribs(mPhysicsAttribs);
    }

    public void setPhysicsAttribs( PhysicsAttribs attribs ) {
        mPhysicsAttribs = new PhysicsAttribs(attribs);
        mAtRest = false;
    }

    public void setPhysicsAction( IPhysicsAction action ) {
        mPhysicsAction = action;
    }

    public float getExtent() {
        return mExtent;
    }
}
