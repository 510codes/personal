package com.example.glttt.shader;

import com.example.glttt.shapes.Triangle;

public interface IShader {
    public int getProgramHandle();
    public void draw( float[] mvMatrix, float[] mvpMatrix, Iterable<Triangle> t );
    public boolean requiresNormalData();

    // must be called on the opengl thread
    public void initialize();
}
