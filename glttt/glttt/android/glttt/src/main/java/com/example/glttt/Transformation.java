package com.example.glttt;

import android.opengl.Matrix;

public class Transformation {
    private float mScaleFactor;
    private float mYRotation;
    private float mTranslationX;
    private float mTranslationY;
    private float mTranslationZ;

    public Transformation() {
        this( 0.0f, 0.0f, 0.0f, 1.0f, 0.0f );
    }

    public Transformation( float tx, float ty, float tz, float scale, float ry ) {
        mTranslationX = tx;
        mTranslationY = ty;
        mTranslationZ = tz;
        mScaleFactor = scale;
        mYRotation = ry;
    }

    public Transformation( Transformation t ) {
        this( t.mTranslationX, t.mTranslationY, t.mTranslationZ, t.mScaleFactor, t.mYRotation );
    }

    public void setScaleFactor( float factor )
    {
        mScaleFactor = factor;
    }

    public void setYRotation( float degrees ) {
        mYRotation = degrees;
    }

    public void setTranslation( float x, float y, float z ) {
        mTranslationX = x;
        mTranslationY = y;
        mTranslationZ = z;
    }

    public void translate( float dx, float dy, float dz ) {
        mTranslationX += dx;
        mTranslationY += dy;
        mTranslationZ += dz;    }

    public void calculateTransformationMatrix(float[] m) {
        Matrix.setIdentityM(m, 0);
        rotate(m, mYRotation, 0.0f, 1.0f, 0.0f);
        Matrix.scaleM(m, 0, mScaleFactor, mScaleFactor, mScaleFactor);
        translate(m, mTranslationX, mTranslationY, mTranslationZ);
    }

    private static void translate( float[] m, float x, float y, float z )
    {
        Matrix.translateM(m, 0, x, y, z);
    }

    private static void rotate( float[] m, float angle, float x, float y, float z ) {
        Matrix.rotateM(m, 0, angle, x, y, z);
    }
}
