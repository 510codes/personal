package com.example.glttt;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TransformationTest {

    @Test
    public void defaultTransformation() {
        Transformation transformation = new Transformation();
        float[] result = new float[16];
        transformation.calculateTransformationMatrix(result);

        assertArrayEquals(new float[]{1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f},
                result, 0.0f);
    }
}
