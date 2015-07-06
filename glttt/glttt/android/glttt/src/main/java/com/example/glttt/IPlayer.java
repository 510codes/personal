package com.example.glttt;

public interface IPlayer {
    int getMove(long currentTimeInNanos);
    int getDelayAfterMoveInMillis();
    String getName();
}
