package server;

import java.io.DataInputStream;
import java.net.Socket;

public class PresenterClientConnection extends ServerRunnableBase {

    private Socket socket;
    private String hash;
    private DataInputStream dataInputStream;

    public PresenterClientConnection(Socket socket, DataInputStream dataInputStream, String hash) {
        this.socket = socket;
        this.hash = hash;
        this.dataInputStream = dataInputStream;
    }
}
