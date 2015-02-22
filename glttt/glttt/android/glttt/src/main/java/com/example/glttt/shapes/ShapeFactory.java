package com.example.glttt.shapes;

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
            vertexData[i*7] = (vertices[k] / vertexDivideFactor);
            vertexData[(i*7) + 1] = (vertices[k + 1] / vertexDivideFactor);
            vertexData[(i*7) + 2] = (vertices[k + 2] / vertexDivideFactor);

            for (int j=0; j<4; ++j)
            {
                vertexData[(i*7) + 3 + j] = colour[j];
            }
        }

        return new Triangle( vertexData, id );
    }

    public Triangle[] createRectangle( float[] vertices, float[] colour, float vertexDivideFactor, String id ) {
        Triangle[] t = new Triangle[2];
        int[] indices = new int[3];

        indices[0] = 0;
        indices[1] = 3;
        indices[2] = 6;
        t[0] = createTriangle(vertices, indices, colour, vertexDivideFactor, id+"0");

        indices[0] = 0;
        indices[1] = 6;
        indices[2] = 9;
        t[1] = createTriangle(vertices, indices, colour, vertexDivideFactor, id+"1");

        return t;
    }

    public Triangle[] createSphere( String id, float radius, int rings, int sectors, float[] colour, float vertexDivideFactor ) {
        float[] vertices;
        float[] normals;
        float[] texcoords;
        int[] indices;
        final float R = 1.0f/(float)(rings-1);
        final float S = 1.0f/(float)(sectors-1);
        int r, s;

        vertices = new float[(rings * sectors * 3)];
        normals = new float[(rings * sectors * 3)];
        texcoords = new float[(rings * sectors * 2)];
        int t = 0;
        int v = 0;
        int n = 0;
        for(r = 0; r < rings; r++) for(s = 0; s < sectors; s++) {
            final float y = (float)Math.sin( -(Math.PI / 2.0f) + Math.PI * r * R );
            final float x = (float)Math.cos(2*Math.PI * s * S) * (float)Math.sin( Math.PI * r * R );
            final float z = (float)Math.sin(2*Math.PI * s * S) * (float)Math.sin( Math.PI * r * R );

            texcoords[t] = s*S;
            t++;

            texcoords[t] = r*R;
            t++;

            vertices[v] = x * radius;
            v++;

            vertices[v] = y * radius;
            v++;

            vertices[v] = z * radius;
            v++;

            normals[n] = x;
            n++;

            normals[n] = y;
            n++;

            normals[n] = z;
            n++;
        }

        indices = new int[(rings * sectors * 4)];
        int i = 0;
        for (r=0; r < rings - 1; r++) for(s = 0; s < sectors-1; s++) {
            indices[i] = r * sectors + s;
            i++;

            indices[i] = r * sectors + (s+1);
            i++;

            indices[i] = (r+1) * sectors + (s+1);
            i++;

            indices[i] = (r+1) * sectors + s;
            i++;
        }

        return null;
    }

    /*void draw(GLfloat x, GLfloat y, GLfloat z)
    {
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glTranslatef(x,y,z);

        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

        glVertexPointer(3, GL_FLOAT, 0, &vertices[0]);
        glNormalPointer(GL_FLOAT, 0, &normals[0]);
        glTexCoordPointer(2, GL_FLOAT, 0, &texcoords[0]);
        glDrawElements(GL_QUADS, indices.size(), GL_UNSIGNED_SHORT, &indices[0]);
        glPopMatrix();
    }*/
}
