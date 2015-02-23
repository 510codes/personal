package com.example.glttt.shader;

import com.example.glttt.shapes.Triangle;

import java.nio.FloatBuffer;
import java.util.List;

public interface IShader {
    public int getProgramHandle();
    public void draw( float[] mvMatrix, float[] mvpMatrix, FloatBuffer vertexFB, int numTris );
    public boolean requiresNormalData();
    public int getStride();

    // must be called on the opengl thread
    public void initialize();
}
