package com.example.glttt.text;

import android.opengl.Matrix;
import android.util.Log;

import com.example.glttt.shader.ISpriteShader;
import com.example.glttt.shader.Sprite;

public class SpriteBatch {

    final static int VERTEX_SIZE = 5;                  // Vertex Size (in Components) ie. (X,Y,U,V,M), M is MVP matrix index
    final static int VERTICES_PER_SPRITE = 4;          // Vertices Per Sprite
    final static int INDICES_PER_SPRITE = 6;           // Indices Per Sprite
    private static final String TAG = "SpriteBatch";

    public void drawSprites(ISpriteShader spriteShader, Sprite[] sprites, float[] vpMatrix, int textureId, float[] colour)  {
        float[] vertexBuffer = new float[sprites.length * VERTICES_PER_SPRITE * VERTEX_SIZE];
        Vertices vertices = new Vertices(sprites.length * VERTICES_PER_SPRITE, sprites.length * INDICES_PER_SPRITE);
        int bufferIndex = 0;

        short[] indices = new short[sprites.length * INDICES_PER_SPRITE];
        int len = indices.length;
        //Log.d("SpriteBatch", "drawSprites(): drawing " + sprites.length + " sprites (" + len + " indices)");
        short j = 0;
        for ( int i = 0; i < len; i+= INDICES_PER_SPRITE, j += VERTICES_PER_SPRITE )  {  // FOR Each Index Set (Per Sprite)
            indices[i + 0] = (short)( j + 0 );           // Calculate Index 0
            indices[i + 1] = (short)( j + 1 );           // Calculate Index 1
            indices[i + 2] = (short)( j + 2 );           // Calculate Index 2
            indices[i + 3] = (short)( j + 2 );           // Calculate Index 3
            indices[i + 4] = (short)( j + 3 );           // Calculate Index 4
            indices[i + 5] = (short)( j + 0 );           // Calculate Index 5
        }
        vertices.setIndices( indices, 0, len );         // Set Index Buffer for Rendering

        float[] uMVPMatrices = new float[sprites.length*16];
        float[] mvpMatrix = new float[16];                // used to calculate MVP matrix of each sprite
        int count = 0;

        for (Sprite sprite : sprites) {
            float halfWidth = sprite.getWidth() / 2.0f;                 // Calculate Half Width
            float halfHeight = sprite.getHeight() / 2.0f;               // Calculate Half Height
            float x1 = sprite.getX() - halfWidth;                       // Calculate Left X
            float y1 = sprite.getY() - halfHeight;                      // Calculate Bottom Y
            float x2 = sprite.getX() + halfWidth;                       // Calculate Right X
            float y2 = sprite.getY() + halfHeight;                      // Calculate Top Y

            vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 0
            vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 0
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().u1;        // Add U for Vertex 0
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().v2;        // Add V for Vertex 0
            vertexBuffer[bufferIndex++] = count % 24;

            vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 1
            vertexBuffer[bufferIndex++] = y1;               // Add Y for Vertex 1
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().u2;        // Add U for Vertex 1
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().v2;        // Add V for Vertex 1
            vertexBuffer[bufferIndex++] = count % 24;

            vertexBuffer[bufferIndex++] = x2;               // Add X for Vertex 2
            vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 2
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().u2;        // Add U for Vertex 2
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().v1;        // Add V for Vertex 2
            vertexBuffer[bufferIndex++] = count % 24;

            vertexBuffer[bufferIndex++] = x1;               // Add X for Vertex 3
            vertexBuffer[bufferIndex++] = y2;               // Add Y for Vertex 3
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().u1;        // Add U for Vertex 3
            vertexBuffer[bufferIndex++] = sprite.getTextureRegion().v1;        // Add V for Vertex 3
            vertexBuffer[bufferIndex++] = count % 24;

            // add the sprite mvp matrix to uMVPMatrices array
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, sprite.getModelMatrix(), 0);

            for (int i = 0; i < 16; ++i) {
                int index = (count % 24) * 16 + i;
                //Log.d("SpriteBatch", "drawSprites(): uMVPMatrices["+index+"] = mvpMatrix["+i+"];");
                uMVPMatrices[index] = mvpMatrix[i];
            }

            count++;

            if ((count > 0 && count % 24 == 0) || count == sprites.length) {
                vertices.setVertices(vertexBuffer, 0, bufferIndex);
                spriteShader.draw(textureId, vertices.vertices, vertices.indices, uMVPMatrices, sprites.length, colour);

                bufferIndex = 0;
            }
        }
    }
}
