package com.example.glttt.shader;

import android.opengl.GLES20;

import com.example.glttt.shapes.Triangle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PerFragmentShader implements IShader {
    private final ShaderProgram mProgram;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mPositionHandle;
    private int mColourHandle;
    private int mNormalHandle;
    private int mLightPos;

    PerFragmentShader( ShaderProgram program ) {
        mProgram = program;
    }

    @Override
    public int getProgramHandle() {
        return mProgram.getProgramHandle();
    }

    @Override
    public boolean requiresNormalData() {
        return true;
    }

    @Override
    public void initialize() {
        mProgram.initialize();

        int program = mProgram.getProgramHandle();

        mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
        if (mMVPMatrixHandle == -1) {
            throw new ShaderException("could not get MVP matrix handle");
        }
        mMVMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVMatrix");
        if (mMVMatrixHandle == -1) {
            throw new ShaderException("could not get MV matrix handle");
        }
        mPositionHandle = GLES20.glGetAttribLocation(program, "a_position");
        if (mPositionHandle == -1) {
            throw new ShaderException("could not get position handle");
        }
        mColourHandle = GLES20.glGetAttribLocation(program, "a_colour");
        if (mColourHandle == -1) {
            throw new ShaderException("could not get color handle");
        }
        mNormalHandle = GLES20.glGetAttribLocation(program, "a_normal");
        if (mNormalHandle == -1) {
            throw new ShaderException("could not get normal handle");
        }

        mLightPos = GLES20.glGetUniformLocation(program, "u_lightPos");
        if (mLightPos == -1) {
            throw new ShaderException("could not get lightPos handle");
        }
    }

    @Override
    public void draw( float[] mvMatrix, float[] mvpMatrix, Iterable<Triangle> tris ) {
        GLES20.glUniform3f(mLightPos, 0.0f, 5.0f, 5.0f);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);

        for (Triangle t : tris)
        {
            drawTriangle(t);
        }
    }

    private void drawTriangle(Triangle tri) {
        float[] vertexData = tri.getVertexData();
        ByteBuffer vertexBB = ByteBuffer.allocateDirect(vertexData.length * 4);
        vertexBB.order(ByteOrder.nativeOrder());
        FloatBuffer vertexFB = vertexBB.asFloatBuffer();
        vertexFB.put(vertexData);

        vertexFB.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 40, vertexFB);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        vertexFB.position(3);
        GLES20.glVertexAttribPointer(mColourHandle, 4, GLES20.GL_FLOAT, false, 40, vertexFB);
        GLES20.glEnableVertexAttribArray(mColourHandle);

        vertexFB.position(7);
        GLES20.glVertexAttribPointer(mNormalHandle, 3, GLES20.GL_FLOAT, false, 40, vertexFB);
        GLES20.glEnableVertexAttribArray(mNormalHandle);

        //Draw the shape
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
