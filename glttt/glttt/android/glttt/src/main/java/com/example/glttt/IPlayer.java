package com.example.glttt;

public interface IPlayer {
    int getMove(long currentTimeInNanos);
    void opponentMove( int peg );
    int getDelayAfterMoveInMillis();
    String getName();
}
