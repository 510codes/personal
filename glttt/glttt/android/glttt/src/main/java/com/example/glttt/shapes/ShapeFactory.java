package com.example.glttt.shapes;

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

    private static float[] getFlatTriangleVertexNormals( float[] vertices ) {
        float v1x = vertices[3] - vertices[0];
        float v1y = vertices[4] - vertices[1];
        float v1z = vertices[5] - vertices[2];

        float v2x = vertices[6] - vertices[0];
        float v2y = vertices[7] - vertices[1];
        float v2z = vertices[8] - vertices[2];

        float vx = v1y * v2z - v1z * v2y;
        float vy = v1z * v2x - v1x * v2z;
        float vz = v1x * v2y - v1y * v2x;

        float l = (float)Math.sqrt( (vx*vx) + (vy*vy) + (vz*vz));

        float nvx = vx / l;
        float nvy = vy / l;
        float nvz = vz / l;

        float[] normal = new float[3];
        normal[0] = nvx;
        normal[1] = nvy;
        normal[2] = nvz;

        return normal;
    }

    public Triangle createTriangle( float[] vertices, int[] vertexIndices, float[] colour, float vertexDivideFactor, String id ) {
        float[] normal = getFlatTriangleVertexNormals(vertices);
        float[] normals = new float[9];
        for (int i=0; i<3; ++i) {
            normals[(i*3)] = normal[i];
            normals[(i*3)+1] = normal[i+1];
            normals[(i*3)+2] = normal[i+2];
        }
        return createTriangle( vertices, vertexIndices, normals, colour, vertexDivideFactor, id );
    }

    public Triangle createTriangle( float[] vertices, int[] vertexIndices, float[] vertexNormals, float[] colour, float vertexDivideFactor, String id ) {
        int stride = (mIncludeNormalData ? 10 : 7);
        float[] vertexData = new float[stride * 3];

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

            if (mIncludeNormalData) {
                vertexData[(i * stride) + 7] = vertexNormals[k];
                vertexData[(i * stride) + 8] = vertexNormals[k + 1];
                vertexData[(i * stride) + 9] = vertexNormals[k + 2];
            }
        }

        return new Triangle( vertexData, id, stride );
    }

    public Triangle[] createRectangle( float[] vertices, float[] colour, float vertexDivideFactor, String id ) {
        float[] normal = getFlatTriangleVertexNormals(vertices);
        float[] normals = new float[12];
        for (int i=0; i<4; ++i) {
            normals[(i*3)] = normal[0];
            normals[(i*3)+1] = normal[1];
            normals[(i*3)+2] = normal[2];
        }
        return createRectangle(vertices, normals, colour, vertexDivideFactor, id );
    }

    public Triangle[] createRectangle( float[] vertices, float[] vertexNormals, float[] colour, float vertexDivideFactor, String id ) {
        Triangle[] t = new Triangle[2];
        int[] indices = new int[3];

        indices[0] = 0;
        indices[1] = 3;
        indices[2] = 6;
        t[0] = createTriangle(vertices, indices, vertexNormals, colour, vertexDivideFactor, id+"_0");

        indices[0] = 0;
        indices[1] = 6;
        indices[2] = 9;
        t[1] = createTriangle(vertices, indices, vertexNormals, colour, vertexDivideFactor, id+"_1");

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

            // TODO: i had to make the normals negative in order to get the sphere
            // to shade properly.  i don't know why, need to figure it out
            normals[n] = -x;
            n++;

            normals[n] = -y;
            n++;

            normals[n] = -z;
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
            float[] quadVertexNormals = new float[4*3];

            for (int j=0; j<4; ++j) {
                int index = indices[(i*4) + j];
                System.arraycopy(vertices, index*3, quadVertices, j*3, 3);
                System.arraycopy(normals, index*3, quadVertexNormals, j*3, 3);
            }

            Triangle[] quadTris = createRectangle(quadVertices, quadVertexNormals, colour, vertexDivideFactor, id+"_"+i);
            tri[(i*2)] = quadTris[0];
            tri[(i*2)+1] = quadTris[1];
        }

        return tri;
    }
}
