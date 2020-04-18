package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.net.Socket;

public class PlayerClientConnection extends ClientConnectionBase {

    // Logger
    static final Logger LOG = LoggerFactory.getLogger(PlayerClientConnection.class);

    public PlayerClientConnection(Socket socket, DataInputStream dataInputStream, String hash) {
        super(socket, dataInputStream, hash, "[PlayerClient: " + socket.getInetAddress() + ":" + socket.getPort() + "] ");
    }

}