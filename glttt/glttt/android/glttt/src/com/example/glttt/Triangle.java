package com.example.glttt;

public class Triangle
{
	private float[] vertexData;
	
	private Triangle()
	{	
	}
	
	private Triangle( float[] vertexData )
	{
		this.vertexData = vertexData;
	}
	
	public float[] getVertexData()
	{
		return vertexData;
	}
	
	public static Triangle create( float[] vertices, float[] colour )
	{
		float[] vertexData = new float[21];
		for (int i=0; i<3; ++i)
		{
			vertexData[i*7] = (vertices[i*3] / 500.0f) - 1.0f;
			vertexData[(i*7) + 1] = (vertices[(i*3) + 1] / 500.0f) - 1.0f;
			vertexData[(i*7) + 2] = vertices[(i*3) + 2];
			
			for (int j=0; j<4; ++j)
			{
				vertexData[(i*7) + 3 + j] = colour[j];
			}
		}
		
		return new Triangle( vertexData );
	}
}
