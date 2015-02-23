package com.example.glttt.shader;

import android.content.res.Resources;

import com.example.glttt.R;

public class ShaderFactory
{
    private Resources mResources;

	public ShaderFactory( Resources resources )
	{
        mResources = resources;
	}
	
    public IShader createSimpleShader()
	{
        String vertexShader = mResources.getString(R.string.simple_vertex_shader);
        String fragmentShader = mResources.getString(R.string.simple_fragment_shader);

        return new SimpleShader(new ShaderProgram(vertexShader, fragmentShader));
	}

    public IShader createPerFragShader() {
        String vertexShader = mResources.getString(R.string.perfrag_vertex_shader);
        String fragmentShader = mResources.getString(R.string.perfrag_fragment_shader_2);

        return new PerFragmentShader(new ShaderProgram(vertexShader, fragmentShader));
    }
}
