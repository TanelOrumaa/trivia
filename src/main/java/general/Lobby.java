package general;

import configuration.Config;
import server.Server;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private String name;
    private int code;
    private List<User> connectedUsers;
    private User lobbyOwner;

    // Constructor for creating a lobby from existing data (client side).
    public Lobby(String name, int code, List<User> connectedUsers, User lobbyOwner) {
        this.name = name;
        this.code = code;
        this.connectedUsers = connectedUsers;
        this.lobbyOwner = lobbyOwner;
    }

    // Constructor for creating a new lobby with a name.
    public Lobby(String name, User user) {
        this.name = name;
        this.connectedUsers = new ArrayList<>();
        this.connectedUsers.add(user);
        this.lobbyOwner = user;
        this.code = Server.addLobby(this);
    }

    // Constructor for creating a new lobby without a name.
    public Lobby(User user) {
        new Lobby("", user);
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
        // TODO: Needs to be threadsafe.
        String[] names = new String[connectedUsers.size()];
        for (int i = 0; i < connectedUsers.size(); i++) {
            names[i] = connectedUsers.get(i).getFirstName() + " " + connectedUsers.get(i).getLastName(); // At one point makes sense to have something smarter here perhaps.
        }
        return names;
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
