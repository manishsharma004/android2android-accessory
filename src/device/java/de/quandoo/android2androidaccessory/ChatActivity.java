package de.quandoo.android2androidaccessory;

import android.os.Bundle;

public class ChatActivity extends BaseChatActivity {

    private AccessoryCommunicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        communicator = new AccessoryCommunicator(this) {

            @Override
            public void onReceive(byte[] msg, int len) {
                printLineToUI("host> " + new String(msg, 0, len));
            }

            @Override
            public void onError(String msg) {
                printLineToUI("notify" + msg);
            }

            @Override
            public void onConnected() {
                printLineToUI("connected");

            }

            @Override
            public void onDisconnected() {
                printLineToUI("disconnected");
            }
        };
    }


    @Override
    protected void sendString(String string) {
        communicator.send(string.getBytes());
    }
}
