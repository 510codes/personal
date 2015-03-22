package com.example.glttt;

public class Math3d {
    private static final float SMALL_NUM = 0.0000001f;

    private Math3d() {}

    public static float dotProduct( float[] vec0, float[] vec1 ) {
        float sum = 0;
        for(int i = 0; i < 3; i++){
            sum += vec0[i] * vec1[i];
        }
        return sum;
    }

    public static void vector( float[] a, float[] b, float[] c) {
        a[0] = b[0] - c[0];
        a[1] = b[1] - c[1];
        a[2] = b[2] - c[2];
    }

    public static void crossProduct( float[] out, float[] v1, float[] v2 ) {
        out[0] = v1[1] * v2[2] - v1[2] * v2[1];
        out[1] = v1[2] * v2[0] - v1[0] * v2[2];
        out[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }

    public static float innerProduct( float[] v, float[] q ) {
        return ((v)[0] * (q)[0] + (v)[1] * (q)[1] + (v)[2] * (q)[2]);
    }

    public static float vectorlength( float[] v ) {
        return (float)Math.sqrt( (v[0] * v[0]) + (v[1] * v[1]) + (v[2] * v[2]));
    }

    public static void normalize( float[] v ) {
        float l = vectorlength(v);
        v[0] /= l;
        v[1] /= l;
        v[2] /= l;
    }

    public static boolean getSurfaceNormal( float[] out, float[] v0, float[] v1, float[] v2 ) {
        float[] vec0 = new float[4];
        float[] vec1 = new float[4];

        Math3d.vector(vec0, v1, v0);
        Math3d.vector(vec1, v2, v0);
        Math3d.crossProduct(out, vec0, vec1);
        Math3d.normalize(out);
        if (Float.isNaN(out[0]) || Float.isNaN(out[1]) || Float.isNaN(out[2])) {
            return false;
        }

        return true;
    }

    public static boolean getPlaneIntersection( float[] planePos, float[] planeNormal, float[] rayOrigin, float[] rayDir, float[] outPos, float[] outDir ) {
        boolean found = false;

        float dp = Math3d.dotProduct(planeNormal, rayDir);
        if (Math.abs(dp) < SMALL_NUM) {
            return false;
        }

        if (dp <= 0.0f) {
            float t = 0.0f;
            for (int i=0; i<3; ++i) {
                t += (planeNormal[i] * rayOrigin[i]);
                t += (planeNormal[i] * -planePos[i]);
            }

            t /= -dp;

            if (t >= 0.0f) {
                outPos[0] = rayOrigin[0] + (rayDir[0] * t);
                outPos[1] = rayOrigin[1] + (rayDir[1] * t);
                outPos[2] = rayOrigin[2] + (rayDir[2] * t);
                outPos[3] = 1.0f;

                outDir[0] = -rayDir[0];
                outDir[1] = -rayDir[1];
                outDir[2] = -rayDir[2];
                outDir[3] = 0.0f;

                found = true;
            }
        }

        return found;
    }

    public static boolean getSphereIntersection( float[] sphereOrigin, float radius, float[] rayOrigin, float[] rayDir ) {
        float[] L = new float[4];
        Math3d.vector(L, sphereOrigin, rayOrigin);
        float tca = Math3d.dotProduct(L, rayDir);
        if (tca < 0) return false;
        float d2 = Math3d.dotProduct(L, L) - tca * tca;
        float radius2 = radius * radius;
        if (d2 > radius2) return false;
        return true;
    }
}
