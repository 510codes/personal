package com.example.glttt.shapes;

import java.util.ArrayList;

public class ShapeFactory {

    private final boolean mIncludeNormalData;

    public ShapeFactory( boolean includeNormalData ) {
        mIncludeNormalData = includeNormalData;
    }

    public Triangle createTriangle( float[] vertices, float[] colour, float vertexDivideFactor, String id ) {
        int[] indices = new int[3];
        indices[0] = 0;
        indices[1] = 3;
        indices[2] = 6;
        return createTriangle( vertices, indices, colour, vertexDivideFactor, id );
    }

    public Triangle createTriangle( float[] vertices, int[] vertexIndices, float[] colour, float vertexDivideFactor, String id ) {
        int stride = (mIncludeNormalData ? 10 : 7);
        float[] vertexData = new float[stride * 3];

        float nvx, nvy, nvz;

        for (int i=0; i<3; ++i)
        {
            int k = vertexIndices[i];
            vertexData[i*stride] = (vertices[k] / vertexDivideFactor);
            vertexData[(i*stride) + 1] = (vertices[k + 1] / vertexDivideFactor);
            vertexData[(i*stride) + 2] = (vertices[k + 2] / vertexDivideFactor);

            for (int j=0; j<4; ++j)
            {
                vertexData[(i*stride) + 3 + j] = colour[j];
            }

            if (i == 2 && mIncludeNormalData) {
                float v1x = vertexData[1*stride] - vertexData[0*stride];
                float v1y = vertexData[(1*stride) + 1] - vertexData[(0*stride) + 1];
                float v1z = vertexData[(1*stride) + 2] - vertexData[(0*stride) + 2];

                float v2x = vertexData[2*stride] - vertexData[0*stride];
                float v2y = vertexData[(2*stride) + 1] - vertexData[(0*stride) + 1];
                float v2z = vertexData[(2*stride) + 2] - vertexData[(0*stride) + 2];

                float vx = v1y * v2z - v1z * v2y;
                float vy = v1z * v2x - v1x * v2z;
                float vz = v1x * v2y - v1y * v2x;

                float l = (float)Math.sqrt( (vx*vx) + (vy*vy) + (vz*vz));

                nvx = vx / l;
                nvy = vy / l;
                nvz = vz / l;

                for (int j=0; j<3; ++j) {
                    vertexData[(j * stride) + 7] = nvx;
                    vertexData[(j * stride) + 8] = nvy;
                    vertexData[(j * stride) + 9] = nvz;
                }
            }
        }

        return new Triangle( vertexData, id, stride );
    }

    public Triangle[] createRectangle( float[] vertices, float[] colour, float vertexDivideFactor, String id ) {
        Triangle[] t = new Triangle[2];
        int[] indices = new int[3];

        indices[0] = 0;
        indices[1] = 3;
        indices[2] = 6;
        t[0] = createTriangle(vertices, indices, colour, vertexDivideFactor, id+"_0");

        indices[0] = 0;
        indices[1] = 6;
        indices[2] = 9;
        t[1] = createTriangle(vertices, indices, colour, vertexDivideFactor, id+"_1");

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

        Triangle[] tri = new Triangle[rings*sectors*2];
        for (i=0; i<rings*sectors; ++i) {
            float[] quadVertices = new float[4*3];

            for (int j=0; j<4; ++j) {
                int index = indices[(i*4) + j];
                System.arraycopy(vertices, index*3, quadVertices, j*3, 3);
            }

            Triangle[] quadTris = createRectangle(quadVertices, colour, vertexDivideFactor, id+"_"+i);
            tri[(i*2)] = quadTris[0];
            tri[(i*2)+1] = quadTris[1];
        }

        return tri;
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
