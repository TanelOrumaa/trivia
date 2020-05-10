package server;

import configuration.Configuration;
import exception.LobbyDoesNotExistException;
import lobby.Lobby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

;

public class Server {

    // Logger
    static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final String hashAllowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@£$€&%";
    private static HashSet<String> allHashes = new HashSet<>();
    private static HashSet<Integer> allLobbyCodes = new HashSet<>();
    private static List<Lobby> lobbies = new ArrayList<>();

    private static final Object monitor = new Object();

    public static void main(String[] args) {

        // Read the config first.
        try {
            Configuration.readConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read configuration file, make sure you have one in resources/sensitive", e);
        }

        int portNumber = Configuration.SERVER_LISTENING_PORT;
        final ExecutorService executorService = Executors.newCachedThreadPool();

        // Create a new serversocket.
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            LOG.info("Listening...");
            while (true) {
                try {
                    // Accept incoming connection.
                    Socket socket = ss.accept();
                    socket.setKeepAlive(true);

                    // Check the type of incoming client
                    try {
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                        // Read the message, should be 101 (contact message)
                        int code = dataInputStream.readInt();
                        if (code == 101) {
                            LOG.debug("Client sent a \"Hello\" message.");

                            // Read client type
                            int clientType = dataInputStream.readInt();
                            LOG.debug("Client type is " + clientType);
                            switch (clientType) {
                                case 1:
                                    executorService.submit(new PresenterClientConnection(socket, dataInputStream, generateUniqueHash()));
                                    break;
                                case 2:
                                    executorService.submit(new PlayerClientConnection(socket, dataInputStream, generateUniqueHash()));
                                    break;
                                default:
                                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                    dataOutputStream.writeInt(404); // Incorrect client number.
                                    dataOutputStream.close();
                            }

                        } else {
                            LOG.warn("Server expected 101 from client, received " + code);
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            dataOutputStream.writeInt(402); // Unexpected message code.
                            dataOutputStream.close();
                        }
                    } catch (IOException e) {
                        System.out.println("Unable to open datainputstream for socket.");
                    }

                } catch (IOException e) {
                    throw new RuntimeException("I/O error occurred while waiting for connection.", e);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException("Startup failed.");
        }
    }

    public static String generateUniqueHash() {
        String hash = generateHash();
        while (allHashes.contains(hash)) {
            hash = generateHash();
        }
        allHashes.add(hash);
        return hash;
    }

    public static void addLobby(Lobby lobby) {
        synchronized (monitor) {
            lobbies.add(lobby);
        }
    }

    public static boolean removeLobby(Lobby lobby) {
        synchronized (monitor) {
            int lobbyCode = lobby.getCode();
            allLobbyCodes.remove(lobbyCode);
            return lobbies.remove(lobby);
        }
    }

    public static boolean isLobbyAvailable(int lobbyCode, boolean isPresenter) {
        synchronized (monitor) {
            Lobby lobby = getLobbyByCode(lobbyCode);
            if (lobby != null) {
                return lobby.canAddUserToLobby() || isPresenter;
            } else {
                throw new LobbyDoesNotExistException(lobbyCode);
            }
        }
    }

    public static Lobby addUserToLobby(User user, int lobbyCode) {
        synchronized (monitor) {
            Lobby lobby = getLobbyByCode(lobbyCode);
            if (lobby != null) {
                lobby.addUserToLobby(user);
                return lobby;
            } else {
                throw new LobbyDoesNotExistException(lobbyCode);
            }
        }
    }

    public static Lobby addPresenterToLobby(int lobbyCode) {
        synchronized (monitor) {
            Lobby lobby = getLobbyByCode(lobbyCode);
            if (lobby != null) {
                return lobby;
            } else {
                throw new LobbyDoesNotExistException(lobbyCode);
            }
        }
    }

    private static Lobby getLobbyByCode(int lobbyCode) {
        Lobby lobby = null;
        for (Lobby lobbyFromList : lobbies) {
            if (lobbyFromList.getCode() == lobbyCode) {
                lobby = lobbyFromList;
            }
        }
        return lobby;
    }

    private static String generateHash() {
        // Create a hash
        char[] chars = new char[32];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = hashAllowedChars.charAt(ThreadLocalRandom.current().nextInt(0, hashAllowedChars.length() - 1));
        }
        return new String(chars);
    }

    public static int generateLobbyCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 999999);
        while (allLobbyCodes.contains(code)) {
            code = ThreadLocalRandom.current().nextInt(100000, 999999);
        }
        allLobbyCodes.add(code);
        return code;
    }
}