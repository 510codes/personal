package com.example.glttt;

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
