package com.example.glttt;

public abstract class GLTTTException extends RuntimeException
{
	public GLTTTException()
	{
	}
	
	protected abstract String getMsg();
	
	public String toString()
	{
		return "GLTTTException: " + getMsg();
	}
}
