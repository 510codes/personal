package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.glttt.shapes.ShapeFactory;
import com.example.glttt.shapes.Triangle;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    public static final int FLOAT_BYTE_LENGTH = 4;

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

    private final float ORIGINAL_TRI_VERTEX_DIVISOR = 500.0f;
    private final float NEW_TRI_VERTEX_DIVISOR = 1.0f;
    private final float BOARD_VERTEX_DIVISOR = 50.0f;

    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private Shader mShader;

    private Resources resources;

    private ShapeFactory shapeFactory;

    private Scene currentScene;

    public GLTTTSurfaceRenderer( Resources resources )
    {
    	super();
    	
    	this.resources = resources;
    	this.currentScene = null;
        this.shapeFactory = new ShapeFactory();
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        mShader = Shader.create(
        		resources.getString(R.string.vertex_shader),
        		resources.getString(R.string.fragment_shader)
        		);
        mPositionHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "a_position");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "u_MVPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "a_color");

        mPositionHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "a_position");
        if (mPositionHandle == -1)
        {
        	throw new ShaderException("could not get position handle");
        }
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mShader.getProgram(), "u_VPMatrix");
        if (mMVPMatrixHandle == -1)
        {
        	throw new ShaderException("could not get MVP matrix handle");
        }
        mColorHandle = GLES20.glGetAttribLocation(mShader.getProgram(), "a_color");
        if (mColorHandle == -1)
        {
        	throw new ShaderException("could not get color handle");
        }

        checkGlError("glGetUniformLocation");
        
    	GLES20.glUseProgram(mShader.getProgram());
        
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d("game", "onSurfaceChanged(): width: " + width + ", height: " + height);
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        int[] currentViewPort = new int[4];
        currentViewPort[0] = 0;
        currentViewPort[1] = 0;
        currentViewPort[2] = width;
        currentViewPort[3] = height;

        getCurrentScene().onViewportChanged(currentViewPort);
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        getCurrentScene().draw();
    }
    
    public ModelObject getClickedModelObject( int screenX, int screenY )
    {
    	return getCurrentScene().getClickedModelObject(screenX, screenY);
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("opengl", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
    
    private Scene getCurrentScene()
    {
    	if (currentScene == null)
    	{
            //currentScene = createNewGameScene();
            currentScene = createGameBoardScene();
        }
    	
    	return currentScene;
    }

    private Scene createGameBoardScene() {
        Scene scene = new Scene(mPositionHandle, mColorHandle, mMVPMatrixHandle, new GameBoardSceneChangeHandler());

        float boardVertices[] = {
                -100.0f, 0.0f, -100.0f,
                -100.0f, 0.0f, 100.0f,
                100.0f, 0.0f, 100.0f,
                100.0f, 0.0f, -100.0f
        };

        Triangle[] boardTris = shapeFactory.createRectangle(boardVertices, new float[]{0.1f, 0.2f, 0.5f, 1.0f}, BOARD_VERTEX_DIVISOR, "board");

        ModelObject obj = new ModelObject("board");
        obj.add(boardTris);
        //obj.rotate(45.0f, 0.0f, 1.0f, 0.0f);

        scene.add(obj);

        return scene;
    }
    
    private Scene createNewGameScene()
    {
        Scene scene = new Scene(mPositionHandle, mColorHandle, mMVPMatrixHandle, new NewGameSceneChangeHandler());
    	
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
		
		float newVertices0[] = {
				0.25f, 0.3f, 0f,
				0.25f, 0.5f, 0f,
				0.45f, 0.5f, 0f
		};
		
		float newVertices1[] = {
				0.25f, 0.3f, 0f,
				0.45f, 0.5f, 0f,
				0.45f, 0.3f, 0f
		};
		
		//gl.glColor4f( 1.0f, 0.0f, 0.0f, 1.0f );
		//draw_message( 0.5, 200, 200, "Select your colour:" );

        Triangle[] whiteSquareTris = shapeFactory.createRectangle(whiteVertices, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, ORIGINAL_TRI_VERTEX_DIVISOR, "white");
        Triangle[] redSquareTris = shapeFactory.createRectangle(redVertices, new float[]{1.0f, 0.0f, 0.0f, 1.0f}, ORIGINAL_TRI_VERTEX_DIVISOR, "red");

		Triangle newWhiteTri0 = shapeFactory.createTriangle(newVertices0, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, NEW_TRI_VERTEX_DIVISOR, "white0");
		Triangle newWhiteTri1 = shapeFactory.createTriangle(newVertices1, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, NEW_TRI_VERTEX_DIVISOR, "white1");

		Triangle newRedTri0 = shapeFactory.createTriangle(newVertices0, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, NEW_TRI_VERTEX_DIVISOR, "red0");
		Triangle newRedTri1 = shapeFactory.createTriangle(newVertices1, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, NEW_TRI_VERTEX_DIVISOR, "red1");

		ModelObject redSquare = new ModelObject("redSquare");
        //redSquare.add( newRedTri0 );
        //redSquare.add( newRedTri1 );
        redSquare.add( redSquareTris );

		ModelObject whiteSquare = new ModelObject("whiteSquare");
        //whiteSquare.add( newWhiteTri0 );
        //whiteSquare.add( newWhiteTri1 );
        whiteSquare.add( whiteSquareTris );

        //redSquare.scale(1000.0f);
        //redSquare.translate(500.0f, 0.0f, 0.0f);
        //whiteSquare.scale(1000.0f);

        scene.add(redSquare);
		scene.add(whiteSquare);

		//scene.add( whiteTri0 );
		//scene.add( whiteTri1 );
		//scene.add( redTri0 );
		//scene.add( redTri1 );
		
    	return scene;
    }
}
