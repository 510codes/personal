package com.example.glttt;

public class GameBoardSceneChangeHandler implements ISceneChangeHandler {
    private Scene mScene;

    @Override
    public void setScene(Scene scene) {
        mScene = scene;
    }

    @Override
    public void onViewportChanged(int width, int height) {
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 3.0f;
        final float far = 30.0f;

        mScene.setFrustum(left, right, bottom, top, near, far);
    }

    @Override
    public void preSceneDraw() {
        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        mScene.setLookAt(eyeX, eyeY + 5.0f, eyeZ + 15.0f, lookX, lookY, lookZ - 10.0f, upX, upY, upZ);
    }
}
