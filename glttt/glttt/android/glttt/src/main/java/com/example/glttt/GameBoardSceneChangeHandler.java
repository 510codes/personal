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
}
