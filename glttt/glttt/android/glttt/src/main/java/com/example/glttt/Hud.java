package com.example.glttt;

import android.opengl.Matrix;

import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;
import com.example.glttt.text.GLText;

public class Hud {
    private int mRedScore;
    private int mWhiteScore;
    private final IShader mShader;
    private final GLText mText;
    private final ShapeFactory mShapeFactory;
    private final float[] mProjectionMatrix;
    private final float[] mViewMatrix;
    private final float[] mVPMatrix;
    private int mViewportWidth;
    private int mViewportHeight;

    public Hud( IShader shader, GLText glText, ShapeFactory shapeFactory ) {
        mShader = shader;
        mText = glText;
        mShapeFactory = shapeFactory;
        mProjectionMatrix = new float[16];
        mViewMatrix = new float[16];
        mVPMatrix = new float[16];
        mViewportWidth = 0;
        mViewportHeight = 0;
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mVPMatrix, 0);
    }

    public void updateScore( int redScore, int whiteScore ) {
        mRedScore = redScore;
        mWhiteScore = whiteScore;
    }

    public void draw( ISpriteShader spriteShader ) {
        float scaleX = mText.getScaleX();
        float scaleY = mText.getScaleY();
        mText.setScale(2.0f);

        float y = -mViewportHeight / 2.0f + 50;
        float leftx = -mViewportWidth / 2.0f;
        float rightx = mViewportWidth / 2.0f;
        float height = mText.getHeight();

        float[] vertices = {
                leftx, y, 0,
                rightx, y, 0,
                rightx, y + height, 0,
                leftx, y + height, 0
        };

        Triangle[] tris = mShapeFactory.createRectangle(vertices, Colours.MSG_BOX_COLOUR, 1.0f, "score_msg_box_rect");
        ModelObject scoreMsgBox = new ModelObject("score_msg_box");
        scoreMsgBox.add(tris);

        mShader.switchTo();
        scoreMsgBox.draw(mViewMatrix, mProjectionMatrix, mShader);
        spriteShader.switchTo();
        mText.draw(spriteShader, "Red: " + mRedScore, leftx, y, mVPMatrix, Colours.SCORE_TEXT_RED);
        mText.drawRJ(spriteShader, "White: " + mWhiteScore, rightx, y, mVPMatrix, Colours.SCORE_TEXT_WHITE);
        
        mText.setScale(scaleX, scaleY);
    }

    public void onViewportChanged( int[] viewport ) {
        mViewportWidth = viewport[2];
        mViewportHeight = viewport[3];

        float ratio = (float) mViewportWidth / mViewportHeight;

        // Take into account device orientation
        if (mViewportWidth > mViewportHeight) {
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        }
        else {
            Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
        }

        int useForOrtho = Math.min(mViewportWidth, mViewportHeight);

        //TODO: Is this wrong?
        Matrix.orthoM(mViewMatrix, 0,
                -useForOrtho/2,
                useForOrtho/2,
                -useForOrtho/2,
                useForOrtho/2, 0.1f, 100f);

        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }
}
