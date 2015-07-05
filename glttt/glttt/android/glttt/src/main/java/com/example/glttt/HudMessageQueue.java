package com.example.glttt;

import java.util.LinkedList;

public class HudMessageQueue {

    private final LinkedList<HudMessage> mMessages;
    private final long mMessageLifeInNanos;

    private static class HudMessage {
        private final String mMessage;
        private final long mRemoveTime;

        HudMessage(String msg, long removeTime) {
            mMessage = msg;
            mRemoveTime = removeTime;
        }
    }

    public HudMessageQueue( long messageLifeInNanos ) {
        mMessages = new LinkedList<HudMessage>();
        mMessageLifeInNanos = messageLifeInNanos;
    }

    public void add(String msg, long currentTimeInNanos) {
        mMessages.add(new HudMessage(msg, currentTimeInNanos + mMessageLifeInNanos));
    }

    public String[] getMessages(long currentTimeInNanos) {
        while (mMessages.size() > 0 && mMessages.getFirst().mRemoveTime < currentTimeInNanos) {
            mMessages.removeFirst();
        }

        String[] messages = new String[mMessages.size()];
        int i = 0;
        for (HudMessage m : mMessages) {
            messages[i] = m.mMessage;
            i++;
        }

        return messages;
    }
}
