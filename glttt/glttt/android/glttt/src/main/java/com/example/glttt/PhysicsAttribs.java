package com.example.glttt;

import android.util.Log;

public class PhysicsAttribs {
    private float mVelocity;
    private final float mMass;
    private final float mMinVelocity;
    private final float mAcceleration;
    private final float mDampingAcceleration;

    public PhysicsAttribs( float mass, float minVelocity, float acceleration, float dampingAcceleration ) {
        this( 0.0f, mass, minVelocity, acceleration, dampingAcceleration );
    }

    public PhysicsAttribs( float velocity, float mass, float minVelocity, float acceleration, float dampingAcceleration ) {
        mVelocity = velocity;
        mMass = mass;
        mMinVelocity = minVelocity;
        mAcceleration = acceleration;
        mDampingAcceleration = dampingAcceleration;
    }

    public PhysicsAttribs( PhysicsAttribs pa ) {
        this( pa.mVelocity, pa.mMass, pa.mMinVelocity, pa.mAcceleration, pa.mDampingAcceleration );
    }

    public synchronized float onDeltaTime( float dTimeInS ) {
        if (Math.abs(mVelocity) < mMinVelocity && mAcceleration == 0.0f) {
            mVelocity = 0.0f;
        }
        else {
            if (mDampingAcceleration != 0.0f) {
                float dampingForce = mMass * mDampingAcceleration;
                if (mVelocity > 0.0f) {
                    dampingForce *= -1.0f;
                }
                addForce(dTimeInS, dampingForce);
            }

            if (mAcceleration != 0.0f) {
                addForce(dTimeInS, mMass * mAcceleration);
            }
        }

        return mVelocity;
    }

    public synchronized void addForce( float dTimeInS, float dv, float mass ) {
        float force = (dv / dTimeInS) * mass;
        addForce(dTimeInS, force);
    }

    public synchronized void addForce( float dTimeInS, float force ) {
        float acc = force / mMass;
        float dv = acc * dTimeInS;
        mVelocity += dv;
        Log.v("PhysicsAttribs", "new force: dTimeInS: " + dTimeInS + ", force: " + force + ", mVelocity is now: " + mVelocity);
    }

    public void setVelocity( float v ) {
        mVelocity = v;
    }
}
