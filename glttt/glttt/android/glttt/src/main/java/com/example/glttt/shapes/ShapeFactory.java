package com.example.glttt.shapes;

import com.example.glttt.ModelObject;

public class ShapeFactory {

    public static Triangle createTriangle( float[] vertices, float[] colour, float vertexDivideFactor, String id ) {
        int[] indices = new int[3];
        indices[0] = 0;
        indices[1] = 3;
        indices[2] = 6;
        return createTriangle( vertices, indices, colour, vertexDivideFactor, id );
    }

    public static Triangle createTriangle( float[] vertices, int[] vertexIndices, float[] colour, float vertexDivideFactor, String id ) {
        float[] vertexData = new float[21];
        for (int i=0; i<3; ++i)
        {
            int k = vertexIndices[i];
            vertexData[i*7] = (vertices[k] / vertexDivideFactor) - 1.0f;
            vertexData[(i*7) + 1] = (vertices[k + 1] / vertexDivideFactor) - 1.0f;
            vertexData[(i*7) + 2] = vertices[k + 2];

            for (int j=0; j<4; ++j)
            {
                vertexData[(i*7) + 3 + j] = colour[j];
            }
        }

        return new Triangle( vertexData, id );
    }

    public static Triangle[] createRectangle( float[] vertices, float[] colour, float vertexDivideFactor, String id ) {
        Triangle[] t = new Triangle[2];
        int[] indices = new int[3];

        indices[0] = 0;
        indices[1] = 3;
        indices[2] = 6;
        t[0] = createTriangle(vertices, indices, colour, vertexDivideFactor, id+"0");

        indices[0] = 0;
        indices[1] = 6;
        indices[2] = 9;
        t[1] = createTriangle(vertices, indices, colour, vertexDivideFactor, id+"0");

        return t;
    }
}
