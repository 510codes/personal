package com.example.glttt;

public class GameBoardSceneChangeHandler implements ISceneChangeHandler {
    private Scene mScene;

    @Override
    public void setScene(Scene scene) {
        mScene = scene;
    }

    @Override
    public void onViewportChanged(int width, int height) {
    }

    @Override
    public void preSceneDraw() {
        mScene.setPerspective(35.0f, 1.0f, 10.0f, 1000.0f);
        mScene.setLookAt(100.0f, /*globals.zoom*/250.0f + /*globals.new_zoom*/ 0.0f, 250.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
    }
}
