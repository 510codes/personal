package com.example.glttt.shader;

import com.example.glttt.GLTTTException;

public class ShaderException extends GLTTTException
{
	private ShaderException()
	{
	}
	
	public ShaderException( String cause )
	{
		this();
		this.msg = cause;
	}
	
	protected String getMsg()
	{
		return "Shader exception: " + this.msg;
	}
	
	private String msg;
}
