package com.example.glttt;

import com.example.glttt.pulser.PulseManager;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;

public class SceneFactory {

    private static final float ORIGINAL_TRI_VERTEX_DIVISOR = 500.0f;
    private static final float BOARD_VERTEX_DIVISOR = 50.0f;

    private static final float[] PEG_COLOUR_NORMAL = {0.55f, 0.55f, 0.48f, 0.3f};

    private enum PegLabel {
        PEG_A,
        PEG_B,
        PEG_C,
        PEG_D,
        PEG_E,
        PEG_F,
        PEG_G,
        PEG_H,
        PEG_NONE
    }

    private static final int[][] PEG_POS = {
            {-50,50},{0,50},{50,50},
            {-25,0},{25,0},
            {-50,-50},{0,-50},{50,-50}
    };

    private static final int PEG_THICK = 1;

    private ShapeFactory mShapeFactory;
    private PulseManager mPulseManager;
    private GestureManager mGestureManager;

    public SceneFactory( PulseManager pulseManager, GestureManager gestureManager ) {
        mShapeFactory = new ShapeFactory();
        mPulseManager = pulseManager;
        mGestureManager = gestureManager;
    }

    public static enum TYPE {
        NO_SCENE,
        NEW_GAME_SCENE,
        GAME_BOARD_SCENE
    }

    public Scene create( TYPE type, int positionHandle, int colourHandle, int mvpMatrixHandle ) {
        Scene scene;

        switch (type) {
            case NEW_GAME_SCENE:
                scene = createNewGameScene(positionHandle, colourHandle, mvpMatrixHandle);
                break;

            case GAME_BOARD_SCENE:
                scene = createGameBoardScene( positionHandle, colourHandle, mvpMatrixHandle );
                break;

            case NO_SCENE:
                scene = createEmptyScene( positionHandle, colourHandle, mvpMatrixHandle );
                break;

            default:
                throw new RuntimeException("unknown scene type: " + type);
        }

        return scene;
    }

    private Scene createGameBoardScene( int positionHandle, int colourHandle, int mvpMatrixHandle ) {
        GameBoardInputReceiver inputReceiver = new GameBoardInputReceiver();
        Scene scene = new Scene(positionHandle, colourHandle, mvpMatrixHandle,
                new GameBoardSceneChangeHandler(),
                inputReceiver);
        mPulseManager.setPulseReceiver(inputReceiver);
        mGestureManager.setGestureListener(inputReceiver);

        float boardVertices[] = {
                -100.0f, 0.0f, -100.0f,
                -100.0f, 0.0f, 100.0f,
                100.0f, 0.0f, 100.0f,
                100.0f, 0.0f, -100.0f
        };

        for (int x=0; x<8; ++x) {
            float[] pegVertices1 = {
                    PEG_POS[x][0] - PEG_THICK, 0.0f, PEG_POS[x][1] - PEG_THICK,
                    PEG_POS[x][0] - PEG_THICK, 75.0f, PEG_POS[x][1] - PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 75.0f, PEG_POS[x][1] - PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 0.0f, PEG_POS[x][1] - PEG_THICK
            };

            float[] pegVertices2 = {
                    PEG_POS[x][0] - PEG_THICK, 0.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] - PEG_THICK, 75.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 75.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 0.0f, PEG_POS[x][1] + PEG_THICK
            };

            float[] pegVertices3 = {
                    PEG_POS[x][0] - PEG_THICK, 0.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] - PEG_THICK, 75.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] - PEG_THICK, 75.0f, PEG_POS[x][1] - PEG_THICK,
                    PEG_POS[x][0] - PEG_THICK, 0.0f, PEG_POS[x][1] - PEG_THICK
            };

            float[] pegVertices4 = {
                    PEG_POS[x][0] + PEG_THICK, 0.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 75.0f, PEG_POS[x][1] + PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 75.0f, PEG_POS[x][1] - PEG_THICK,
                    PEG_POS[x][0] + PEG_THICK, 0.0f, PEG_POS[x][1] - PEG_THICK
            };

            Triangle[] pegTris;
            ModelObject peg;

            pegTris = mShapeFactory.createRectangle(pegVertices1, PEG_COLOUR_NORMAL, BOARD_VERTEX_DIVISOR, "peg"+x);
            peg = new ModelObject("peg"+x+"_1");
            peg.add(pegTris);
            scene.add(peg);

            pegTris = mShapeFactory.createRectangle(pegVertices2, PEG_COLOUR_NORMAL, BOARD_VERTEX_DIVISOR, "peg"+x);
            peg = new ModelObject("peg"+x+"_2");
            peg.add(pegTris);
            scene.add(peg);

            pegTris = mShapeFactory.createRectangle(pegVertices3, PEG_COLOUR_NORMAL, BOARD_VERTEX_DIVISOR, "peg"+x);
            peg = new ModelObject("peg"+x+"_3");
            peg.add(pegTris);
            scene.add(peg);

            pegTris = mShapeFactory.createRectangle(pegVertices4, PEG_COLOUR_NORMAL, BOARD_VERTEX_DIVISOR, "peg"+x);
            peg = new ModelObject("peg"+x+"_4");
            peg.add(pegTris);
            scene.add(peg);
        }

        Triangle[] boardTris = mShapeFactory.createRectangle(boardVertices, new float[]{0.1f, 0.2f, 0.5f, 1.0f}, BOARD_VERTEX_DIVISOR, "board");
        ModelObject obj = new ModelObject("board");
        obj.add(boardTris);

        scene.add(obj);

        return scene;
    }

    private Scene createNewGameScene( int colourHandle, int positionHandle, int mvpMatrixHandle )
    {
        Scene scene = new Scene(colourHandle, positionHandle, mvpMatrixHandle, new NewGameSceneChangeHandler());

        float whiteVertices[] = {
                250f, 300f, 0f,
                250f, 500f, 0f,
                450f, 500f, 0f,
                450f, 300f, 0f
        };

        float redVertices[] = {
                500f, 300f, 0f,
                500f, 500f, 0f,
                700f, 500f, 0f,
                700f, 300f, 0f
        };

        //gl.glColor4f( 1.0f, 0.0f, 0.0f, 1.0f );
        //draw_message( 0.5, 200, 200, "Select your colour:" );

        Triangle[] whiteSquareTris = mShapeFactory.createRectangle(whiteVertices, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, ORIGINAL_TRI_VERTEX_DIVISOR, "white");
        Triangle[] redSquareTris = mShapeFactory.createRectangle(redVertices, new float[]{1.0f, 0.0f, 0.0f, 1.0f}, ORIGINAL_TRI_VERTEX_DIVISOR, "red");

        ModelObject redSquare = new ModelObject("redSquare");
        redSquare.add( redSquareTris );

        ModelObject whiteSquare = new ModelObject("whiteSquare");
        whiteSquare.add( whiteSquareTris );

        scene.add(redSquare);
        scene.add(whiteSquare);

        return scene;
    }

    private Scene createEmptyScene( int colourHandle, int positionHandle, int mvpMatrixHandle ) {
        Scene scene = new Scene(colourHandle, positionHandle, mvpMatrixHandle, new NewGameSceneChangeHandler());

        return scene;
    }
}
