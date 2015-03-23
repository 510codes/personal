package com.example.glttt;

import android.util.Log;

public class PhysicsAttribs {
    private float mVelocity;
    private final float mMass;
    private final float mMinVelocity;
    private final float mDampingAcceleration;

    public PhysicsAttribs( float mass, float minVelocity, float dampingAcceleration ) {
        mMass = mass;
        mMinVelocity = minVelocity;
        mDampingAcceleration = dampingAcceleration;
    }

    public synchronized float onDeltaTime( float dTimeInS ) {
        if (Math.abs(mVelocity) < mMinVelocity) {
            mVelocity = 0.0f;
        }
        else {
            float dampingForce = mMass * mDampingAcceleration;
            if (mVelocity > 0.0f) {
                dampingForce *= -1.0f;
            }
            addForce(dTimeInS, dampingForce);
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
