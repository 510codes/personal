package com.example.glttt;

import com.example.glttt.pulser.PulseManager;
import com.example.glttt.shader.IShader;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;

public class GamePresenter implements IPresenter {

    private static final int PHYSICS_FPS = 60;
    private static final float ORIGINAL_TRI_VERTEX_DIVISOR = 500.0f;  // this is for the new game scene
    private static final float BOARD_VERTEX_DIVISOR = 50.0f;

    private final PEG_SELECT_COLOUR[][] mPegState;
    private PEG_SELECT_COLOUR mCurrentTurnColour;
    private Scene mCurrentScene;
    private final ShapeFactory mShapeFactory;
    private int mPegSerialCount;
    private IGameStateListener mGameStateListener;
    private String mMoveSphereName;

    public static enum PEG_SELECT_COLOUR {
        NONE,
        WHITE,
        RED
    }

    public GamePresenter(GestureManager gestureManager, IShader shader) {
        mPegState = new PEG_SELECT_COLOUR[8][3];
        mCurrentTurnColour = PEG_SELECT_COLOUR.RED;
        mPegSerialCount = 0;
        mGameStateListener = null;
        mShapeFactory = new ShapeFactory(shader.requiresNormalData());
        SceneFactory sceneFactory = new SceneFactory(mShapeFactory, new PulseManager(PHYSICS_FPS), gestureManager);
        mCurrentScene = sceneFactory.create(SceneFactory.TYPE.GAME_BOARD_SCENE, this, BOARD_VERTEX_DIVISOR);
        mMoveSphereName = null;

        for (int i=0; i<8; ++i) {
            for (int j=0; j<3; ++j) {
                mPegState[i][j] = PEG_SELECT_COLOUR.NONE;
            }
        }

        addNewPegForMove();
    }

    @Override
    public int pegSelected(int peg) {
        for (int i=0; i<3; ++i) {
            if (mPegState[peg][i] == PEG_SELECT_COLOUR.NONE) {
                mPegState[peg][i] = mCurrentTurnColour;
                mCurrentTurnColour = (mCurrentTurnColour == PEG_SELECT_COLOUR.RED ?
                        PEG_SELECT_COLOUR.WHITE : PEG_SELECT_COLOUR.RED);

                mGameStateListener.setDropSphereName(mMoveSphereName);
                addNewPegForMove();

                return i;
            }
        }

        return -1;
    }

    private void addNewPegForMove() {
        mPegSerialCount++;
        ModelObject sphere = createSphere(mCurrentTurnColour, mPegSerialCount / 2);
        mCurrentScene.add(sphere);
    }

    @Override
    public Scene getCurrentScene() {
        return mCurrentScene;
    }

    @Override
    public PEG_SELECT_COLOUR getCurrentTurnColour() {
        return mCurrentTurnColour;
    }

    @Override
    public void onViewportChanged(int[] viewport) {
        mCurrentScene.onViewportChanged(viewport);
    }

    @Override
    public void setYRotation(float yr) {
        mCurrentScene.setYRotation(yr);
    }

    @Override
    public void drawScene(IShader shader) {
        mCurrentScene.draw(shader);
    }

    @Override
    public void setZoomFactor(float zf) {
        mCurrentScene.setZoomFactor(zf);
    }

    private ModelObject createSphere( PEG_SELECT_COLOUR colour, int serialNum ) {
        float[] c;
        if (colour == PEG_SELECT_COLOUR.RED) {
            c = new float[]{0.4f, 0.0f, 0.0f, 1.0f};
        }
        else {
            c = new float[]{0.9f, 0.9f, 0.9f, 1.0f};
        }

        String strColour = (colour == PEG_SELECT_COLOUR.RED ? "red" : "white");
        mMoveSphereName = "sphere_" + strColour + "_" + serialNum;

        Triangle[] sphereTris = mShapeFactory.createSphere(mMoveSphereName, 11.5f, 20, 20, c, BOARD_VERTEX_DIVISOR);
        ModelObject obj = new ModelObject(mMoveSphereName, true);
        Transformation t = mCurrentScene.getTransformation();
        t.setTranslation(0.0f, 1.0f, 0.0f);
        obj.setTransformation(t);
        obj.add(sphereTris);

        mGameStateListener.setMoveSphereName(mMoveSphereName);

        return obj;
    }

    public void setGameStateListener( IGameStateListener listener ) {
        mGameStateListener = listener;
    }
}
