package com.example.glttt;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;


public class GLTTTSurfaceRenderer implements GLSurfaceView.Renderer {
    public static final int FLOAT_BYTE_LENGTH = 4;

    private int mPositionHandle;
    private int mMVPMatrixHandle;
    private int mColorHandle;
    private Shader shader;

    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    
    private Resources resources;

    public GLTTTSurfaceRenderer( Resources resources )
    {
    	super();
    	
    	this.resources = resources;
    }
    
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        shader = Shader.create(
        		resources.getString(R.string.vertex_shader),
        		resources.getString(R.string.fragment_shader)
        		);
        mPositionHandle = GLES20.glGetAttribLocation(shader.getProgram(), "a_position");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shader.getProgram(), "u_VPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(shader.getProgram(), "a_color");

        mPositionHandle = GLES20.glGetAttribLocation(shader.getProgram(), "a_position");
        if (mPositionHandle == -1)
        {
        	throw new ShaderException("could not get position handle");
        }
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shader.getProgram(), "u_VPMatrix");
        if (mMVPMatrixHandle == -1)
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
        
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
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
        
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);        
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
     
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        //Matrix.orthoM(mProjectionMatrix, 0, 0.0f, 1000.0f, 0.0f, 1000.0f, -1.0f, 1.0f );
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
    	GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    	Matrix.setIdentityM(mModelMatrix, 0);
    	drawNewGame(gl);
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("opengl", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private void drawQuad(Quad quad)
    {
    	float[] vertexData = quad.getVertexData();
		ByteBuffer vertexBB = ByteBuffer.allocateDirect(vertexData.length * 4);
		vertexBB.order(ByteOrder.nativeOrder());
		FloatBuffer vertexFB = vertexBB.asFloatBuffer();
		vertexFB.put(vertexData);

		/*ByteBuffer indexBB = ByteBuffer.allocateDirect(indices.length * 2);
		indexBB.order(ByteOrder.nativeOrder());
		ShortBuffer indexSB = indexBB.asShortBuffer();
		indexSB.put(indices);
		indexSB.position(0);*/

		vertexFB.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 28, vertexFB);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		vertexFB.position(3);
		GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 28, vertexFB);
	    GLES20.glEnableVertexAttribArray(mColorHandle);
	    
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
	    
	    // Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

	    //Draw the shape
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
	    //GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_SHORT, indexSB);
    }
    
    private void drawNewGame(GL10 unused)
    {    	
		//gl.glColor4f( 1.0f, 0.0f, 0.0f, 1.0f );
		//draw_message( 0.5, 200, 200, "Select your colour:" );

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
		
		Quad whiteQuad0 = Quad.create(whiteVertices0, new float[]{0.9f, 0.9f, 0.9f, 1.0f});
		Quad whiteQuad1 = Quad.create(whiteVertices1, new float[]{0.9f, 0.9f, 0.9f, 1.0f});
		
		Quad redQuad0 = Quad.create(redVertices0, new float[]{1.0f, 0.0f, 0.0f, 1.0f});
		Quad redQuad1 = Quad.create(redVertices1, new float[]{1.0f, 0.0f, 0.0f, 1.0f});
		
		drawQuad(whiteQuad0);
		drawQuad(whiteQuad1);
		drawQuad(redQuad0);
		drawQuad(redQuad1);
	}
    
}
