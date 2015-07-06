package com.example.glttt;

import android.opengl.Matrix;

import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;
import com.example.glttt.text.GLText;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Hud {
    private int mRedScore;
    private int mWhiteScore;
    private final IShader mShader;
    private final ISpriteShader mSpriteShader;
    private final GLText mText;
    private final ShapeFactory mShapeFactory;
    private final float[] mProjectionMatrix;
    private final float[] mViewMatrix;
    private final float[] mVPMatrix;
    private int mViewportWidth;
    private int mViewportHeight;
    private HudMessageQueue mMessageQueue;
    private int mMessageBoxWidthInPixels;
    private String mMiddleText;

    private static final long NANOS_PER_MS = 1000000;

    private ArrayList<StringWithTime> mMessagesToProcess;


    public Hud( IShader shader, ISpriteShader spriteShader, GLText glText, ShapeFactory shapeFactory ) {
        mShader = shader;
        mSpriteShader = spriteShader;
        mText = glText;
        mShapeFactory = shapeFactory;
        mProjectionMatrix = new float[16];
        mViewMatrix = new float[16];
        mVPMatrix = new float[16];
        mViewportWidth = 0;
        mViewportHeight = 0;
        mMessageQueue = new HudMessageQueue(15000 * NANOS_PER_MS);
        mMessageBoxWidthInPixels = 0;
        mMessagesToProcess = new ArrayList<StringWithTime>();
        mMiddleText = null;
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mVPMatrix, 0);
    }

    public void updateScore( int redScore, int whiteScore ) {
        mRedScore = redScore;
        mWhiteScore = whiteScore;
    }

    public void draw( long currentTimeInNanos ) {
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

        Triangle[] tris = mShapeFactory.createRectangle(vertices, Colours.SCORE_BOX_COLOUR, 1.0f, "score_msg_box_rect");
        ModelObject scoreMsgBox = new ModelObject("score_msg_box");
        scoreMsgBox.add(tris);

        mShader.switchTo();
        scoreMsgBox.draw(mViewMatrix, mProjectionMatrix, mShader);
        drawMessages(currentTimeInNanos);
        drawMiddleText();
        mText.draw(mSpriteShader, "Red: " + mRedScore, leftx, y, mVPMatrix, Colours.SCORE_TEXT_RED);
        mText.drawRJ(mSpriteShader, "White: " + mWhiteScore, rightx, y, mVPMatrix, Colours.SCORE_TEXT_WHITE);

        mText.setScale(scaleX, scaleY);
    }

    private float getMessageBoxLeft() {
        float width = mViewportWidth / 1.5f;
        return -width / 2;
    }

    private float getMessageBoxRight() {
        float width = mViewportWidth / 1.5f;
        return width / 2;
    }

    private void drawMessages(long currentTimeInNanos) {
        float saveScaleX = mText.getScaleX();
        float saveScaleY = mText.getScaleY();

        mText.setScale(1.2f);

        processStrings();

        String[] msg = mMessageQueue.getMessages(currentTimeInNanos);
        float y = mViewportHeight / 2.0f;
        float height = (msg.length * mText.getHeight()) + (mText.getHeight() / 5.0f);
        float left = getMessageBoxLeft();
        float right = getMessageBoxRight();

        float[] vertices = {
                left, y - height, 0,
                right, y - height, 0,
                right, y, 0,
                left, y, 0
        };

        Triangle[] tris = mShapeFactory.createRectangle(vertices, Colours.MSG_BOX_COLOUR, 1.0f, "msg_box_rect");
        ModelObject msgBox = new ModelObject("msg_box");
        msgBox.add(tris);
        msgBox.draw(mViewMatrix, mProjectionMatrix, mShader);
        mSpriteShader.switchTo();

        float msgY = y;
        for (int i=0; i<msg.length; ++i) {
            msgY -= mText.getHeight();
            mText.draw(mSpriteShader, msg[i], left + 5, msgY, mVPMatrix, Colours.SCORE_TEXT_WHITE);
        }

        mText.setScale(saveScaleX, saveScaleY);
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
            Matrix.frustumM(mProjectionMatrix, 0, -1, 1, -1 / ratio, 1 / ratio, 1, 10);
        }

        int useForOrtho = Math.min(mViewportWidth, mViewportHeight);

        //TODO: Is this wrong?
        Matrix.orthoM(mViewMatrix, 0,
                -useForOrtho / 2,
                useForOrtho / 2,
                -useForOrtho / 2,
                useForOrtho / 2, 0.1f, 100f);

        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        int left = (int)getMessageBoxLeft();
        int right = (int)getMessageBoxRight();

        mMessageBoxWidthInPixels = right - left;
    }

    public synchronized void addMessage(String s, long currentTimeInNanos) {
        mMessagesToProcess.add(new StringWithTime(s, currentTimeInNanos));
    }

    public void setMiddleText( String s ) {
        mMiddleText = s;
    }

    private void drawMiddleText() {
        if (mMiddleText != null) {
            float saveScaleX = mText.getScaleX();
            float saveScaleY = mText.getScaleY();

            float y = -mText.getHeight() / 2.0f;
            float leftx = -mViewportWidth / 2.0f;
            float rightx = mViewportWidth / 2.0f;
            float height = mText.getHeight();

            float[] vertices = {
                    leftx, y, 0,
                    rightx, y, 0,
                    rightx, y + height, 0,
                    leftx, y + height, 0
            };

            Triangle[] tris = mShapeFactory.createRectangle(vertices, Colours.MIDDLE_BOX_COLOUR, 1.0f, "score_msg_box_rect");
            ModelObject scoreMsgBox = new ModelObject("score_msg_box");
            scoreMsgBox.add(tris);

            mShader.switchTo();
            scoreMsgBox.draw(mViewMatrix, mProjectionMatrix, mShader);

            mSpriteShader.switchTo();
            mText.setScale(3.0f);
            mText.drawC(mSpriteShader, mMiddleText, 0, 0, mVPMatrix, Colours.SCORE_TEXT_RED);

            mText.setScale(saveScaleX, saveScaleY);
        }
    }

    private synchronized void processStrings() {
        for (StringWithTime swt : mMessagesToProcess) {
            String s = swt.mString.trim();
            StringTokenizer st = new StringTokenizer(s);
            String curString = "";
            while (st.hasMoreTokens()) {
                String nextString = st.nextToken();
                if (mText.getLength(curString + nextString) >= mMessageBoxWidthInPixels) {
                    if (curString.length() == 0) {
                        // will bleed over the edge....
                        mMessageQueue.add(nextString, swt.mTime);
                    }
                    else {
                        mMessageQueue.add(curString, swt.mTime);
                        curString = nextString;
                    }
                }
                else {
                    curString += " " + nextString;
                }
            }

            if (curString.length() > 0) {
                mMessageQueue.add(curString, swt.mTime);
            }
        }

        mMessagesToProcess.clear();
    }

    private static class StringWithTime {
        private final String mString;
        private final long mTime;

        StringWithTime(String s, long timeInNanos) {
            mString = s;
            mTime = timeInNanos;
        }
    }
}
