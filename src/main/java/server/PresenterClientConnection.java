package server;

import java.net.Socket;

public class PresenterClientConnection extends ServerRunnableBase {

    private Socket socket;
    private String hash;

    public PresenterClientConnection(Socket socket, String hash) {
        this.socket = socket;
        this.hash = hash;
    }
}
