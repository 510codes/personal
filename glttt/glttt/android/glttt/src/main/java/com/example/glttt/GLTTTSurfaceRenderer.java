package com.example.glttt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    public static final int FLOAT_BYTE_LENGTH = 4;

    private int mPositionHandle;
    private int mVPMatrixHandle;
    private int mColorHandle;
    private int[] currentViewPort;
    private Shader shader;

    private Resources resources;
    
    private Scene currentScene;

    public GLTTTSurfaceRenderer( Resources resources )
    {
    	super();
    	
    	this.resources = resources;
    	this.currentScene = null;
        this.currentViewPort = new int[4];
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        shader = Shader.create(
        		resources.getString(R.string.vertex_shader),
        		resources.getString(R.string.fragment_shader)
        		);
        mPositionHandle = GLES20.glGetAttribLocation(shader.getProgram(), "a_position");
        mVPMatrixHandle = GLES20.glGetUniformLocation(shader.getProgram(), "u_VPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(shader.getProgram(), "a_color");

        mPositionHandle = GLES20.glGetAttribLocation(shader.getProgram(), "a_position");
        if (mPositionHandle == -1)
        {
        	throw new ShaderException("could not get position handle");
        }
        mVPMatrixHandle = GLES20.glGetUniformLocation(shader.getProgram(), "u_VPMatrix");
        if (mVPMatrixHandle == -1)
        {
        	throw new ShaderException("could not get MVP matrix handle");
        }
        mColorHandle = GLES20.glGetAttribLocation(shader.getProgram(), "a_color");
        if (mColorHandle == -1)
        {
        	throw new ShaderException("could not get color handle");
        }

        checkGlError("glGetUniformLocation");
        
    	GLES20.glUseProgram(shader.getProgram());
        
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);

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
        
        getCurrentScene().setLookAt(eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);        
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d("game", "onSurfaceChanged(): width: " + width + ", height: " + height);
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
        currentViewPort[0] = 0;
        currentViewPort[1] = 0;
        currentViewPort[2] = width;
        currentViewPort[3] = height;
     
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        getCurrentScene().setFrustum( left, right, bottom, top, near, far );
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

    	getCurrentScene().draw();
    }
    
    public ModelObject getClickedModelObject( int screenX, int screenY )
    {
    	return getCurrentScene().getClickedModelObject(screenX, screenY, currentViewPort);
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
    		currentScene = createNewGameScene();
    	}
    	
    	return currentScene;
    }
    
    private Scene createNewGameScene()
    {
    	Scene scene = new Scene(mPositionHandle, mColorHandle, mVPMatrixHandle);
    	
		float whiteVertices0[] = {
				250f, 300f, 0f,
				250f, 500f, 0f,
				450f, 500f, 0f
		};
		
		float whiteVertices1[] = {
				250f, 300f, 0f,
				450f, 500f, 0f,
				450f, 300f, 0f				
		};
		
		float redVertices0[] = {
				500f, 300f, 0f,
				500f, 500f, 0f,
				700f, 500f, 0f
		};
		
		float redVertices1[] = {
				500f, 300f, 0f,
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

        final float originalTriVertexDivisor = 500.0f;
        final float newTriVertexDivisor = 1.0f;

		Triangle whiteTri0 = Triangle.create(whiteVertices0, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, originalTriVertexDivisor, "white0");
		Triangle whiteTri1 = Triangle.create(whiteVertices1, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, originalTriVertexDivisor, "white1");
		
		Triangle redTri0 = Triangle.create(redVertices0, new float[]{1.0f, 0.0f, 0.0f, 1.0f}, originalTriVertexDivisor, "red0");
		Triangle redTri1 = Triangle.create(redVertices1, new float[]{1.0f, 0.0f, 0.0f, 1.0f}, originalTriVertexDivisor, "red1");
		
		Triangle newWhiteTri0 = Triangle.create(newVertices0, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, newTriVertexDivisor, "white0");
		Triangle newWhiteTri1 = Triangle.create(newVertices1, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, newTriVertexDivisor, "white1");

		Triangle newRedTri0 = Triangle.create(newVertices0, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, newTriVertexDivisor, "red0");
		Triangle newRedTri1 = Triangle.create(newVertices1, new float[]{0.9f, 0.9f, 0.9f, 1.0f}, newTriVertexDivisor, "red1");

		ModelObject redSquare = new ModelObject("redSquare");
        //redSquare.add( newRedTri0 );
        //redSquare.add( newRedTri1 );
        redSquare.add( redTri1 );
        redSquare.add( redTri0 );

		ModelObject whiteSquare = new ModelObject("whiteSquare");
        //whiteSquare.add( newWhiteTri0 );
        //whiteSquare.add( newWhiteTri1 );
        whiteSquare.add( whiteTri1 );
        whiteSquare.add( whiteTri0 );

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
