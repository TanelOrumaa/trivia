package server;

import java.io.DataInputStream;
import java.net.Socket;

public class HostClientConnection extends ServerRunnableBase {

    private Socket socket;
    private String hash;
    private DataInputStream dataInputStream;

    public HostClientConnection(Socket socket, DataInputStream dataInputStream, String hash) {
        this.socket = socket;
        this.hash = hash;
        this.dataInputStream = dataInputStream;
    }
}
