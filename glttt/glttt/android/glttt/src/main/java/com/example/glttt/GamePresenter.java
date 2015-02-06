package com.example.glttt;

import android.util.Log;
import android.view.MotionEvent;

public class GamePresenter {

    private IGameView mGameView;

    public GamePresenter( IGameView gameView ) {
        mGameView = gameView;
    }

    public boolean onTouchEvent( MotionEvent e ) {
        Log.e("game", "onTouchEvent(): pointer count: " + e.getPointerCount());
        float x = e.getRawX() - mGameView.getContentViewLeft();
        float y = e.getRawY() - mGameView.getContentViewLeft();

        ModelObject mo = mGameView.getClickedModelObject((int)x, (int)y);
        Log.e("game", "clicked object: " + mo);

        return true;
    }

    public SceneFactory.TYPE getCurrentScene() {
        return SceneFactory.TYPE.GAME_BOARD_SCENE;
    }
}
