package com.example.glttt;

import android.opengl.GLES20;

public class Shader
{
	private int program;
	private int vertexShader;
	private int fragmentShader;
	
	private Shader()
	{
	}
	
	private void initialize( String strVertex, String strFragment )
	{
		vertexShader = load(GLES20.GL_VERTEX_SHADER, strVertex);
		fragmentShader = load(GLES20.GL_FRAGMENT_SHADER, strFragment);
		
		program = GLES20.glCreateProgram();
		if (program == 0)
		{
			throw new ShaderException("glCreateProgram() failed");
		}
		
		GLES20.glAttachShader(program, vertexShader);
		GLES20.glAttachShader(program, fragmentShader);
		
		GLES20.glBindAttribLocation(program, 0, "a_position");
	    GLES20.glBindAttribLocation(program, 1, "a_color");
		
		GLES20.glLinkProgram(program);
		int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] != GLES20.GL_TRUE)
		{
			GLES20.glDeleteProgram(program);
			throw new ShaderException("linking of shader program failed");
		}
	}
	
	private int load( int type, String src )
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
	
	public static Shader create( String strVertex, String strPixel )
	{
		Shader shader = new Shader();
		shader.initialize( strVertex, strPixel );
		
		return shader;
	}
	
	public int getProgram()
	{
		return program;
	}
}
