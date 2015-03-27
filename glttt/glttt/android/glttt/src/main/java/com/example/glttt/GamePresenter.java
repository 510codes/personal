package com.example.glttt;

import android.util.Log;

import com.example.glttt.pulser.PulseManager;
import com.example.glttt.shader.IShader;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;

public class GamePresenter implements IPresenter {

    private static final int PHYSICS_FPS = 60;
    private static final float ORIGINAL_TRI_VERTEX_DIVISOR = 500.0f;  // this is for the new game scene
    private static final float BOARD_VERTEX_DIVISOR = 50.0f;

    private final PEG_SELECT_COLOUR[][] mBoardState;
    private PEG_SELECT_COLOUR mCurrentTurnColour;
    private Scene mCurrentScene;
    private final ShapeFactory mShapeFactory;
    private int mPegSerialCount;
    private IGameStateListener mGameStateListener;
    private String mMoveSphereName;

    private IPlayer mPlayer1;
    private IPlayer mPlayer2;
    private TurnManager mTurnManager;
    private int mNextHumanMove;

    public static enum PEG_SELECT_COLOUR {
        NONE,
        WHITE,
        RED
    }

    public GamePresenter(GestureManager gestureManager, IShader shader) {
        mBoardState = new PEG_SELECT_COLOUR[8][3];
        mCurrentTurnColour = PEG_SELECT_COLOUR.RED;
        mPegSerialCount = 0;
        mGameStateListener = null;
        mNextHumanMove = -1;
        mShapeFactory = new ShapeFactory(shader.requiresNormalData());
        SceneFactory sceneFactory = new SceneFactory(mShapeFactory, new PulseManager(PHYSICS_FPS), gestureManager);
        mCurrentScene = sceneFactory.create(SceneFactory.TYPE.GAME_BOARD_SCENE, this, BOARD_VERTEX_DIVISOR);
        mMoveSphereName = null;

        for (int i=0; i<8; ++i) {
            for (int j=0; j<3; ++j) {
                mBoardState[i][j] = PEG_SELECT_COLOUR.NONE;
            }
        }

        mPlayer1 = new HumanPlayer(this);
        mPlayer2 = new ComputerPlayer(this);

        mTurnManager = new TurnManager(mPlayer1, mPlayer2, PEG_SELECT_COLOUR.RED, mBoardState);
    }

    @Override
    public int pegSelected(int peg) {
        for (int i=0; i<3; ++i) {
            if (mBoardState[peg][i] == PEG_SELECT_COLOUR.NONE) {
                synchronized (this) {
                    mNextHumanMove = peg;
                    notify();
                }

                return i;
            }
        }

        return -1;
    }

    private void addNewPegForMove(PEG_SELECT_COLOUR colour) {
        mPegSerialCount++;
        ModelObject sphere = createSphere(colour, mPegSerialCount / 2);
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

    public int getNextHumanMove(PEG_SELECT_COLOUR colour) {
        addNewPegForMove(colour);
        int nextMove = -1;

        try {
            while (nextMove == -1) {
                synchronized (this) {
                    wait();
                    nextMove = mNextHumanMove;
                    mNextHumanMove = -1;
                }
            }
        }
        catch (InterruptedException e) {}

        if (nextMove != -1) {
            for (int i=0; i<3; ++i) {
                if (mBoardState[nextMove][i] == PEG_SELECT_COLOUR.NONE) {
                    addSphereToPeg(nextMove, i);
                    break;
                }
            }
        }

        return nextMove;
    }

    public int getNextComputerMove( PEG_SELECT_COLOUR colour ) {
        for (int i=0; i<8; ++i) {
            for (int j=0; j<3; ++j) {
                if (mBoardState[i][j] == GamePresenter.PEG_SELECT_COLOUR.NONE) {
                    Log.d("ComputerPlayer", "getMove(): chose peg: " + i);
                    addNewPegForMove(colour);
                    addSphereToPeg(i, j);

                    return i;
                }
            }
        }

        return -1;
    }

    private void addSphereToPeg( int peg, int posOnPeg ) {
        ModelObject sphere = mCurrentScene.getObjectByName(mMoveSphereName);
        mGameStateListener.setDropSphereName(mMoveSphereName);
        ModelObject pegObj = mCurrentScene.getObjectByName("peg" + peg);
        Transformation pegTransformation = pegObj.getTransformation();
        sphere.setTranslation(pegTransformation.getTranslationX(), 1.25f, pegTransformation.getTranslationZ());
        PhysicsAttribs spherePhysicsAttribs = new PhysicsAttribs(2.0f, 0.0f, 9.8f, 0.0f);
        sphere.setPhysicsAttribs(spherePhysicsAttribs);
        sphere.setPhysicsAction(new SphereDropPhysicsAction(sphere, posOnPeg));
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
