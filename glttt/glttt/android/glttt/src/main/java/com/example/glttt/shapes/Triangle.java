package com.example.glttt.shapes;

public class Triangle
{
	private final float[] vertexData;
    private final int mStride;
	
	Triangle( float[] vertexData, String id, int stride )
	{
		this.vertexData = vertexData;
		this.id = id;
        mStride = stride;
	}
	
	public float[] getVertexData()
	{
		return vertexData;
	}
	
	public float getX( int vertexNum )
	{
		return vertexData[(vertexNum * mStride)];
	}
	
	public float getY( int vertexNum )
	{
		return vertexData[(vertexNum * mStride) + 1];
	}
	
	public float getZ( int vertexNum )
	{
		return vertexData[(vertexNum * mStride) + 2];
	}

    @Override
	public String toString()
	{
		return id;
	}
	
	private String id;
}
