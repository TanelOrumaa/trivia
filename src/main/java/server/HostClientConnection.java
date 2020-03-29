package server;

import java.net.Socket;

public class HostClientConnection extends ServerRunnableBase {

    private Socket socket;
    private String hash;

    public HostClientConnection(Socket socket, String hash) {
        this.socket = socket;
        this.hash = hash;
    }
}
