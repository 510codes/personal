package com.example.glttt;

public class Math3d {
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
}
