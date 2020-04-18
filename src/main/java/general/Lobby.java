package general;

import configuration.Config;
import general.commands.LobbyUpdateBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Lobby {

    private static final Logger LOG = LoggerFactory.getLogger(Lobby.class);

    // Monitor
    private final Object monitor = new Object();

    private String name;
    private int code;
    private List<User> connectedUsers;
    private User lobbyOwner;
    private TreeMap<Integer, LobbyUpdateBase> lobbyUpdates;

    // Constructor for creating a lobby from existing data (client side).
    public Lobby(String name, int code, List<User> connectedUsers, User lobbyOwner) {
        this.name = name;
        this.code = code;
        this.connectedUsers = connectedUsers;
        this.lobbyOwner = lobbyOwner;
        this.lobbyUpdates = new TreeMap<Integer, LobbyUpdateBase>();
    }

    // Constructor for creating a new lobby with a name.
    public Lobby(String name, User user) {
        this.name = name;
        this.connectedUsers = new ArrayList<>();
        this.connectedUsers.add(user);
        this.lobbyOwner = user;
        this.code = Server.addLobby(this);
        this.lobbyUpdates = new TreeMap<Integer, LobbyUpdateBase>();
    }

    // Constructor for creating a new lobby without a name.
    public Lobby(User user) {
        new Lobby("", user);
    }

    /**
     * Checks if there's a new update available for a client aka if the last update client ran is not the last one available.
     * @param clientLastUpdateId ID for the last update client ran.
     * @return boolean value true if there's a newer update than the one last ran by the client.
     */
    public boolean newUpdateAvailable(int clientLastUpdateId) {
        synchronized (monitor) {
            return lobbyUpdates.containsKey(clientLastUpdateId + 1);
        }
    }

    public void addNewLobbyUpdate(LobbyUpdateBase lobbyUpdate) {
        synchronized (monitor) {
            int nextId = getNextUpdateKey();
            lobbyUpdates.put(nextId, lobbyUpdate);
            LOG.debug("New update for lobby " + code + " added with ID " + nextId);
        }
    }

    /**
     * Method for getting the next available key for updates.
     * @return Integer value for the next key.
     */
    private int getNextUpdateKey() {
        synchronized (monitor) {
            if (lobbyUpdates.size() == 0) {
                return 1;
            }
            return lobbyUpdates.lastKey() + 1;
        }
    }

    public LobbyUpdateBase getLobbyUpdate(int updateKey) {
        return lobbyUpdates.get(updateKey);
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public List<User> getConnectedUsers() {
        return connectedUsers;
    }

    public String[] getConnectedUserNamesAsArray() {
        synchronized (monitor) {
            String[] names = new String[connectedUsers.size()];
            for (int i = 0; i < connectedUsers.size(); i++) {
                names[i] = connectedUsers.get(i).getFirstName() + " " + connectedUsers.get(i).getLastName(); // At one point makes sense to have something smarter here perhaps.
            }
            return names;
        }
    }

    public boolean canAddUserToLobby() {
        return Config.MAX_PLAYERS_IN_LOBBY - this.connectedUsers.size() >= 1;
    }

    public int getLobbyOwnerId() {
        return lobbyOwner.getId();
    }

    public void addUserToLobby(User user) {
        connectedUsers.add(user);
    }

    public void removeUserFromLobby(User user) {
        connectedUsers.remove(user);
    }
}
