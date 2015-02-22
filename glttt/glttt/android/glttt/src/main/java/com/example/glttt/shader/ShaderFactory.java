package com.example.glttt.shader;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import com.example.glttt.R;

public class ShaderFactory
{
	private ShaderFactory()
	{
	}
	
	private static int createProgram( String strVertex, String strFragment )
	{
		int vertexShader = load(GLES20.GL_VERTEX_SHADER, strVertex);
		int fragmentShader = load(GLES20.GL_FRAGMENT_SHADER, strFragment);
		
		int program = GLES20.glCreateProgram();
		if (program == 0)
		{
			throw new ShaderException("glCreateProgram() failed");
		}
		
		GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, fragmentShader);
        checkGlError("glAttachShader");

        GLES20.glBindAttribLocation(program, 0, "a_position");
        checkGlError("glBindAttribLocation");
	    GLES20.glBindAttribLocation(program, 1, "a_color");
        checkGlError("glBindAttribLocation");

		GLES20.glLinkProgram(program);
		int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GLES20.GL_TRUE)
		{
			GLES20.glDeleteProgram(program);
			throw new ShaderException("linking of shader program failed");
		}

        return program;
	}
	
	private static int load( int type, String src )
	{
		int shader = GLES20.glCreateShader(type);
		if (shader == 0)
		{
			throw new ShaderException("glCreateShader() failed");
		}
		
		GLES20.glShaderSource(shader, src);
        GLES20.glCompileShader(shader);
		int[] compiled = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
		if (compiled[0] == 0)
		{
			GLES20.glDeleteShader(shader);
			throw new ShaderException("Shader of type " + type + " failed to compile");
		}
		
		return shader;
	}

    private static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("opengl", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public static IShader createSimpleShader( Resources resources )
	{
        String vertexShader = resources.getString(R.string.simple_vertex_shader);
        String fragmentShader = resources.getString(R.string.simple_fragment_shader);

        int program = createProgram(vertexShader, fragmentShader);

        return new SimpleShader(program);
	}
}
