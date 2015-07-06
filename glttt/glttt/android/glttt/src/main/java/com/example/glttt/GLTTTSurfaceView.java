package com.example.glttt;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.glttt.shader.IShader;
import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.shader.ShaderFactory;
import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.text.GLText;

public class GLTTTSurfaceView extends GLSurfaceView
{
    private final GestureManager mGestureManager;
    private GamePresenter mPresenter;

    public GLTTTSurfaceView(Context context)
    {
        super(context);

        ShaderFactory shaderFactory = new ShaderFactory(getResources());
        IShader shader = shaderFactory.createPerFragShader();
        ISpriteShader spriteShader = shaderFactory.createBatchTextShader();

        mGestureManager = new GestureManager(context);
        GLText glText = new GLText(context.getAssets());
        ShapeFactory shapeFactory = new ShapeFactory(shader.requiresNormalData());
        Hud hud = new Hud(shader, spriteShader, glText, shapeFactory);

        mPresenter = new GamePresenter(mGestureManager, hud, shapeFactory);

        GameBoard gameBoard = new GameBoard();
        IPlayer redPlayer = new HumanPlayer(mPresenter, "Red");
        IPlayer whitePlayer = new ComputerPlayer(gameBoard, "White");

        mPresenter.startNewGame(gameBoard, redPlayer, whitePlayer, GamePresenter.PEG_SELECT_COLOUR.RED, System.nanoTime());

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new GLTTTSurfaceRenderer(mPresenter, shader, spriteShader, glText));
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        return mGestureManager.onTouchEvent(e);
    }
}
