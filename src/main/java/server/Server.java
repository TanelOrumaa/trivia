package server;

import Exceptions.LobbyDoesNotExistException;
import configuration.Config;
import general.Lobby;
import general.User;

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

public class Server {

    private static final String hashAllowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@£$€&%";
    private static HashSet<String> allHashes = new HashSet<>();
    private static HashSet<Integer> allLobbyCodes = new HashSet<>();
    private static List<Lobby> lobbies = new ArrayList<>();

    private static final Object monitor = new Object();

    public static void main(String[] args) {

        int portNumber = Config.SERVER_LISTENING_PORT;
        final ExecutorService executorService = Executors.newCachedThreadPool();

        // Create a new serversocket.
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            System.out.println("Listening...");
            while (true) {
                try {
                    // Accept incoming connection.
                    Socket socket = ss.accept();
                    socket.setKeepAlive(true);

                    // Check the type of incoming client
                    try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

                        // Read the message, should be 101 (contact message)
                        int code = dataInputStream.readInt();
                        if (code == 101) {
                            System.out.println("Client sent a \"Hello\" message.");

                            // Read client type
                            int clientType = dataInputStream.readInt();
                            switch (clientType) {
                                case 1:
                                    executorService.submit(new PresenterClientConnection(socket, generateUniqueHash()));
                                    break;
                                case 2:
                                    executorService.submit(new PlayerClientConnection(socket, generateUniqueHash()));
                                    // For some reason the socket keeps closing if not using it constantly. Uncomment this
                                    // to keep the socket alive, but this blocks any other sockets from connecting.
                                    // Temporary solution for testing until I figure out how to keep the socket alive.
                                    while (true) {
                                        if (socket.isClosed()) {
                                            System.out.println("Socket is now closed.");
                                            break;
                                        }
                                    }
                                    break;
                                case 3:
                                    executorService.submit(new HostClientConnection(socket, generateUniqueHash()));
                                    break;
                                default:
                                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                                    dataOutputStream.writeInt(404); // Incorrect client number.
                                    dataOutputStream.close();
                            }

                        } else {
                            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                            dataOutputStream.writeInt(402); // Unexpected message code.
                            dataOutputStream.close();
                        }
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

    public static int addLobby(Lobby lobby) {
        synchronized (monitor) {
            int lobbyCode = generateLobbyCode();
            lobbies.add(lobby);
            return lobbyCode;
        }
    }

    public static boolean removeLobby(Lobby lobby) {
        synchronized (monitor) {
            int lobbyCode = lobby.getCode();
            allLobbyCodes.remove(lobbyCode);
            return lobbies.remove(lobby);
        }
    }

    public static boolean isLobbyAvailable(int lobbyCode) {
        synchronized (monitor) {
            Lobby lobby = getLobbyByCode(lobbyCode);
            if (lobby != null) {
                return lobby.canAddUserToLobby();
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

    private static int generateLobbyCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 999999);
        while (allLobbyCodes.contains(code)) {
            code = ThreadLocalRandom.current().nextInt(100000, 999999);
        }
        allLobbyCodes.add(code);
        return code;
    }
}