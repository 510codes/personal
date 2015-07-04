package com.example.glttt.shader;

import android.graphics.Bitmap;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface ISpriteShader {
    // must be called on the opengl thread
    public void initialize();
    public void draw(int textureId, IntBuffer vertexFB, ShortBuffer indexFB, float[] uMVPMatrices, int numSprites, float[] colour);
    public int loadTexture(Bitmap bitmap);
    public void switchTo();
}
