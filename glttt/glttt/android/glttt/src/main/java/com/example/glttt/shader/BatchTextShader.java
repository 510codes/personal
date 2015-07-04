package com.example.glttt.shader;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class BatchTextShader implements ISpriteShader {

    private static final int BATCH_TEXT_STRIDE = 5;
    private final static int TEXCOORD_CNT = 2;                 // Number of Components in Vertex Texture Coords
    private static final int MVP_MATRIX_INDEX_CNT = 1; // Number of Components in MVP matrix index
    final static int INDICES_PER_SPRITE = 6;           // Indices Per Sprite
    public final static int CHAR_BATCH_SIZE = 24;     // Number of Characters to Render Per Batch

    private final ShaderProgram mProgram;
    private int m_aTextureCoordinateHandle;
    private int m_aMVPIndexHandle;
    private int m_aPositionHandle;
    private int m_uMVPMatricesHandle;
    private int m_uColorHandle;
    private int m_uTextureUniformHandle;


    BatchTextShader( ShaderProgram program ) {
        mProgram = program;
    }

    @Override
    public void initialize() {
        mProgram.initialize();

        switchTo();

        int program = mProgram.getProgramHandle();

        // initialize the shader attribute handles
        m_aTextureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate");
        if (m_aTextureCoordinateHandle == -1) {
            throw new ShaderException("could not get texture coordinate handle");
        }
        m_aMVPIndexHandle = GLES20.glGetAttribLocation(program, "a_MVPMatrixIndex");
        if (m_aMVPIndexHandle == -1) {
            throw new ShaderException("could not get mvp matrix index handle");
        }
        m_aPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
        if (m_aPositionHandle == -1) {
            throw new ShaderException("could not get position handle");
        }
        m_uMVPMatricesHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
        if (m_uMVPMatricesHandle == -1) {
            throw new ShaderException("could not get MVP matrices handle");
        }
        m_uColorHandle = GLES20.glGetUniformLocation(program, "u_Color");
        if (m_uColorHandle == -1) {
            throw new ShaderException("could not get color handle");
        }
        m_uTextureUniformHandle = GLES20.glGetUniformLocation(program, "u_Texture");
        if (m_uTextureUniformHandle == -1) {
            throw new ShaderException("could not get texture uniform handle");
        }
    }

    @Override
    public void draw(int textureId, IntBuffer vertexFB, ShortBuffer indexFB, float[] mvpMatrices, int numSprites, float[] colour) {
        // GLText.initDraw() starts

        GLES20.glUniform4fv(m_uColorHandle, 1, colour , 0);
        checkError("glUniform4fv");
        GLES20.glEnableVertexAttribArray(m_uColorHandle);
        checkError("glEnableVertexAttribArray");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);  // Set the active texture unit to texture unit 0
        checkError("glActiveTexture");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId); // Bind the texture to this unit
        checkError("glBindTexture");

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0
        GLES20.glUniform1i(m_uTextureUniformHandle, 0);
        checkError("glUniform1i");

        // GLText.initDraw() ends



        // SpriteBatch.endBatch()
        GLES20.glUniformMatrix4fv(m_uMVPMatricesHandle, numSprites, false, mvpMatrices, 0);
        checkError("glUniformMatrix4fv");
        GLES20.glEnableVertexAttribArray(m_uMVPMatricesHandle);
        checkError("glEnableVertexAttribArray");




        // Vertices.bind() starts
        // bind vertex position pointer
        vertexFB.position( 0 );                         // Set Vertex Buffer to Position
        GLES20.glVertexAttribPointer(m_aPositionHandle, 2, GLES20.GL_FLOAT, false, BATCH_TEXT_STRIDE * 4, vertexFB);
        checkError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(m_aPositionHandle);
        checkError("glEnableVertexAttribArray");

        // bind texture position pointer
        vertexFB.position(2);  // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
        GLES20.glVertexAttribPointer(m_aTextureCoordinateHandle, TEXCOORD_CNT,
                GLES20.GL_FLOAT, false, BATCH_TEXT_STRIDE * 4, vertexFB);
        checkError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(m_aTextureCoordinateHandle);
        checkError("glEnableVertexAttribArray");

        // bind MVP Matrix index position handle
        vertexFB.position(2 + TEXCOORD_CNT);
        GLES20.glVertexAttribPointer(m_aMVPIndexHandle, MVP_MATRIX_INDEX_CNT,
                GLES20.GL_FLOAT, false, BATCH_TEXT_STRIDE * 4, vertexFB);
        checkError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(m_aMVPIndexHandle);
        checkError("glEnableVertexAttribArray");
        // Vertices.bind() ends




        int numVertices = numSprites * INDICES_PER_SPRITE;
        // Vertices.draw() starts
        if (indexFB != null)  {
            indexFB.position(0);
            //draw indexed
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, numVertices, GLES20.GL_UNSIGNED_SHORT, indexFB);
            checkError("glDrawElements");
        }
        else  {
            //draw direct
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numVertices);
            checkError("glDrawArrays");
        }
        // Vertices.draw() ends




        // Vertices.unbind()
        GLES20.glDisableVertexAttribArray(m_aTextureCoordinateHandle);
        checkError("glDisableVertexAttribArray");

        // SpriteBatch.endBatch() ends

        // GLText.end()
        GLES20.glDisableVertexAttribArray(m_uColorHandle);
        checkError("glDisableVertexAttribArray");
    }

    public int loadTexture(Bitmap bitmap)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        checkError("glGenTextures");

        if (textureHandle[0] != 0)
        {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            checkError("glBindTexture");

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            checkError("glTexParameteri");
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            checkError("glTexParameteri");
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );  // Set U Wrapping
            checkError("glTexParameterf");
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );  // Set V Wrapping
            checkError("glTexParameterf");

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            checkError("texImage2D");

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    @Override
    public void switchTo() {
        GLES20.glUseProgram(mProgram.getProgramHandle());
        checkError("glUseProgram");
    }

    private void checkError( String s ) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.d("BatchTextShader", "checkError( " + s + " ): " + error);
        }
    }
}
