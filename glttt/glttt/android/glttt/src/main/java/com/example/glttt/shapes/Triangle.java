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

    public int getStride() {
        return mStride;
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

    public float[] getVertex( int vertexNum ) {
        float[] v = new float[4];
        v[0] = vertexData[(vertexNum * mStride)];
        v[1] = vertexData[(vertexNum * mStride) + 1];
        v[2] = vertexData[(vertexNum * mStride) + 2];
        v[3] = 1.0f;

        return v;
    }

    @Override
	public String toString()
	{
		return id;
	}
	
	private String id;
}
