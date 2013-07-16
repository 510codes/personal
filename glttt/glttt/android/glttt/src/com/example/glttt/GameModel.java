package com.example.glttt;

public class GameModel
{
	private enum GameState
	{
		NEW_GAME, MOVE_FIRST, IN_GAME, GAME_OVER;
	}
	
	private GameState gameState;
	
	private int mxLeftDown;
	private int mxRightDown;
	private int mxHover;
	private int myHover;
	
	public GameModel()
	{
		gameState = GameState.NEW_GAME;		
	}
	
	public boolean actionStart( int xp, int yp )
	{
		mxLeftDown = mxHover;
		mxRightDown = mxHover;
		
		boolean returnVal = false;
		
		switch (gameState)
		{
			case NEW_GAME:
			case MOVE_FIRST:
				returnVal = true;
		}
		
		return returnVal;
	}
}
