package com.example.glttt.shapes;

public class Triangle
{
	private float[] vertexData;
	
	Triangle( float[] vertexData, String id )
	{
		this.vertexData = vertexData;
		this.id = id;
	}
	
	public float[] getVertexData()
	{
		return vertexData;
	}
	
	public float getX( int vertexNum )
	{
		return vertexData[(vertexNum*7)];
	}
	
	public float getY( int vertexNum )
	{
		return vertexData[(vertexNum*7) + 1];
	}
	
	public float getZ( int vertexNum )
	{
		return vertexData[(vertexNum*7) + 2];
	}
	
	public String toString()
	{
		return id;
	}
	
	private String id;
}
