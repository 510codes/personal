package com.example.glttt.shader;

import java.nio.FloatBuffer;

public interface IShader {
    public void drawTriangles( float[] mvMatrix, float[] mvpMatrix, FloatBuffer vertexFB, int numTris );
    public boolean requiresNormalData();
    public int getStride();
    public void switchTo();

    // must be called on the opengl thread
    public void initialize();
}
