package com.example.glttt;

public interface IPhysicsAction {
    // return 'false' if the object has come to rest
    public boolean onVelocityChange( float dTimeInS, float vel );
}
