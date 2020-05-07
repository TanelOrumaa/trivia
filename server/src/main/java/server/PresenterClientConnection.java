package server;

import java.io.DataInputStream;
import java.net.Socket;

public class PresenterClientConnection extends ClientConnectionBase {

    public PresenterClientConnection(Socket socket, DataInputStream dataInputStream, String hash) {
        super(socket, dataInputStream, hash, "[PlayerClient: " + socket.getInetAddress() + ":" + socket.getPort() + "] ");
    }
}
