package com.example.glttt.shader;

import com.example.glttt.shapes.Triangle;

public interface IShader {
    public int getProgram();
    public void draw( float[] mvpMatrix, Iterable<Triangle> t );
}
