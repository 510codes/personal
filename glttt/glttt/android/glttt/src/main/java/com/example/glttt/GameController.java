package com.example.glttt;

public class GameController
{
	private GameModel model;
	private GLTTT view;
	
	public GameController( GameModel model, GLTTT view )
	{
		this.model = model;
		this.view = view;
		view.setController(this);
	}
	
	public void actionStart( int xp, int yp )
	{
		if (model.actionStart(xp, yp))
		{
			view.mouseChoose(xp, yp);
		}
	}
}
