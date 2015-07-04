package com.example.glttt.shader;

public class Sprite {
    private final float mX;
    private final float mY;
    private final float mWidth;
    private final float mHeight;
    private final TextureRegion mTextureRegion;
    private final float[] mModelMatrix;

    public Sprite(float x, float y, float width, float height, TextureRegion region, float[] modelMatrix) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
        mTextureRegion = region;
        mModelMatrix = modelMatrix;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public TextureRegion getTextureRegion() {
        return mTextureRegion;
    }

    public float[] getModelMatrix() {
        return mModelMatrix;
    }
}
