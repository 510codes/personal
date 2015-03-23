package com.example.glttt;

import android.util.Log;

public class SphereDropPhysicsAction implements IPhysicsAction {

    private final ModelObject mSphere;
    private boolean mStopped;
    private float mMinY;

    public SphereDropPhysicsAction( ModelObject sphere ) {
        mSphere = sphere;
        mStopped = false;
        mMinY = sphere.getExtent();
    }

    @Override
    public boolean onVelocityChange(float dTimeInS, float vel) {
        if (!mStopped) {
            float deltaPos = vel * dTimeInS;
            Transformation transformation = mSphere.getTransformation();
            float yPos = transformation.getTranslationY() - deltaPos;
            if (yPos < mMinY) {
                yPos = mMinY;
                mStopped = true;
            }
            transformation.setTranslationY(yPos);
            mSphere.setTransformation(transformation);
            Log.d("SphereDropPhysicsAction", "onVelocityChange(): dTimeInS: " + dTimeInS + ", deltaPos: " + deltaPos + ", ypos: " + yPos);
        }

        return !mStopped;
    }
}
