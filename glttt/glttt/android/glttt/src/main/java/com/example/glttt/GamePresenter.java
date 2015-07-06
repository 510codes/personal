package com.example.glttt;

import com.example.glttt.pulser.PulseManager;
import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;

public class GamePresenter implements IPresenter {

    private static final int PHYSICS_FPS = 60;
    private static final float ORIGINAL_TRI_VERTEX_DIVISOR = 500.0f;  // this is for the new game scene
    private static final float BOARD_VERTEX_DIVISOR = 50.0f;

    private PEG_SELECT_COLOUR mCurrentTurnColour;
    private Scene mCurrentScene;
    private final ShapeFactory mShapeFactory;
    private int mPegSerialCount;
    private IGameStateListener mGameStateListener;
    private String mMoveSphereName;
    private final Hud mHud;

    private TurnManager mTurnManager;
    private GameBoard mGameBoard;
    private int mNextHumanMove;
    private boolean mFirstHumanMove;

    public static enum PEG_SELECT_COLOUR {
        NONE,
        WHITE,
        RED
    }

    public GamePresenter(GestureManager gestureManager, Hud hud, ShapeFactory shapeFactory) {
        mCurrentTurnColour = PEG_SELECT_COLOUR.RED;
        mPegSerialCount = 0;
        mGameStateListener = null;
        mNextHumanMove = -1;
        mShapeFactory = shapeFactory;
        mHud = hud;
        SceneFactory sceneFactory = new SceneFactory(mShapeFactory, new PulseManager(PHYSICS_FPS), gestureManager);
        mCurrentScene = sceneFactory.create(SceneFactory.TYPE.GAME_BOARD_SCENE, this, BOARD_VERTEX_DIVISOR);
        mMoveSphereName = null;
    }

    public void startNewGame( GameBoard gameBoard, IPlayer player1, IPlayer player2, PEG_SELECT_COLOUR player1Colour, long currentTimeInNanos ) {
        mGameBoard = gameBoard;
        mTurnManager = new TurnManager(player1, player2, player1Colour, this, mGameBoard);
        mFirstHumanMove = true;
        mHud.addMessage("The game begins!", currentTimeInNanos);
    }

    @Override
    public void pegSelected(int peg) {
        synchronized (this) {
            mNextHumanMove = peg;
            notify();
        }
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
        mHud.onViewportChanged(viewport);
    }

    @Override
    public void setYRotation(float yr) {
        mCurrentScene.setYRotation(yr);
    }

    @Override
    public void draw(IShader shader, ISpriteShader spriteShader, long currentTimeInNanos) {
        shader.switchTo();
        mCurrentScene.draw(shader);

        spriteShader.switchTo();
        mHud.draw(currentTimeInNanos);
    }

    @Override
    public void setZoomFactor(float zf) {
        mCurrentScene.setZoomFactor(zf);
    }

    public void initiateNextMove( PEG_SELECT_COLOUR colour ) {
        addNewPegForMove(colour);
    }

    public int getNextHumanMove(long currentTimeInNanos) {
        int nextMove = -1;

        if (mFirstHumanMove) {
            mHud.addMessage("Drag the round piece to a peg to move!", currentTimeInNanos);
            mFirstHumanMove = false;
        }

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

        return nextMove;
    }

    public void acceptMove( int peg, int height, int deltaScore, IPlayer player, long currentTimeInNanos ) {
        addSphereToPeg(peg, height);
        int redScore = mGameBoard.getCompleteRows(GamePresenter.PEG_SELECT_COLOUR.RED);
        int whiteScore = mGameBoard.getCompleteRows(GamePresenter.PEG_SELECT_COLOUR.WHITE);
        mHud.updateScore(redScore, whiteScore);
        processScore(deltaScore, player.getName(), currentTimeInNanos);

        if (mGameBoard.isGameDone()) {
            if (redScore > whiteScore) {
                mHud.setMiddleText("Red wins!");
                mHud.addMessage("Game over - red wins " + redScore + " - " + whiteScore, currentTimeInNanos);
            }
            else if (whiteScore > redScore) {
                mHud.setMiddleText("White wins!");
                mHud.addMessage("Game over - white wins " + whiteScore + " - " + redScore, currentTimeInNanos);
            }
            else {
                mHud.setMiddleText("It's a tie!");
                mHud.addMessage("Game over - tie " + redScore + " - " + whiteScore, currentTimeInNanos);
            }
        }
    }

    private void processScore(int deltaScore, String name, long currentTimeInNanos) {
        if (deltaScore > 0) {
            if (deltaScore == 1 ) {
                mHud.addMessage(name + " scores a row!", currentTimeInNanos);
            }
            else {
                mHud.addMessage(name + " scores " + deltaScore + " rows!", currentTimeInNanos);
            }
        }
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
            c = Colours.GAME_PIECE_RED;
        }
        else {
            c = Colours.GAME_PIECE_WHITE;
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
