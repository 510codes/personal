package com.example.glttt;

import android.opengl.Matrix;

import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.text.GLText;

public class Hud {
    private int mPlayer1Score;
    private int mPlayer2Score;
    private final GLText mText;
    private final float[] mVPMatrix;

    public Hud( GLText glText ) {
        mText = glText;
        mVPMatrix = new float[16];
        Matrix.setIdentityM(mVPMatrix, 0);
    }

    public void updateScore( int player1Score, int player2Score ) {
        mPlayer1Score = player1Score;
        mPlayer2Score = player2Score;
    }

    public void draw( ISpriteShader spriteShader ) {
        float[] colour = {0.0f, 0.0f, 1.0f, 1.0f};

        mText.draw(spriteShader, "testing", 100, 100, mVPMatrix, colour);

        /*mText.drawTexture(1200/2, 1774/2, mVPMatrix, spriteShader);            // Draw the Entire Texture

        mText.drawC(spriteShader, "Test String 3D!", 0f, 0f, 0f, 0, -30, 0, mVPMatrix, colour);
        mText.draw( spriteShader, "Diagonal 1", 40, 40, 40, mVPMatrix, colour);
        mText.draw( spriteShader, "Column 1", 100, 100, 90, mVPMatrix, colour);

        mText.draw( spriteShader, "More Lines...", 50, 200, mVPMatrix, colour );
        mText.draw( spriteShader, "The End.", 50, 200 + mText.getCharHeight(), 180, mVPMatrix, colour);*/

    }

    public void onViewportChanged( int[] viewport ) {
        int width = viewport[2];
        int height = viewport[3];

        float ratio = (float) width / height;

        float[] projectionMatrix = new float[16];
        float[] viewMatrix = new float[16];

        // Take into account device orientation
        if (width > height) {
            Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
        }
        else {
            Matrix.frustumM(projectionMatrix, 0, -1, 1, -1/ratio, 1/ratio, 1, 10);
        }

        int useForOrtho = Math.min(width, height);

        //TODO: Is this wrong?
        Matrix.orthoM(viewMatrix, 0,
                -useForOrtho/2,
                useForOrtho/2,
                -useForOrtho/2,
                useForOrtho/2, 0.1f, 100f);

        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }
}
