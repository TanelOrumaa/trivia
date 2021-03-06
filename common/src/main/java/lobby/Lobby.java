package lobby;

import command.LobbyUpdateBase;
import configuration.GameSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import user.User;

import java.util.*;

//import server.Server;

// TODO: Add Triviaset reference and modify serializer/deserializer.
public class Lobby {

    private static final Logger LOG = LoggerFactory.getLogger(Lobby.class);

    // Monitor
    private final Object monitor = new Object();

    private Long triviaSetId;
    private String name;
    private int code;
    private List<User> connectedUsers;
    private User lobbyOwner;
    private TreeMap<Integer, LobbyUpdateBase> lobbyUpdates;
    private Map<Long, Map<User, String>> answersToQuestions = new HashMap<>();

    // Constructor for creating a lobby from existing data (client side).
    public Lobby(long triviaSetId, String name, int code, List<User> connectedUsers, User lobbyOwner) {
        this.triviaSetId = triviaSetId;
        this.name = name;
        this.code = code;
        this.connectedUsers = connectedUsers;
        this.lobbyOwner = lobbyOwner;
        this.lobbyUpdates = new TreeMap<Integer, LobbyUpdateBase>();
    }

    // Constructor for creating a new lobby with a name.
    public Lobby(long triviasetId, User user, int code) {
        this(triviasetId, "", code, new ArrayList<>(), user);
        connectedUsers.add(user);
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

    public int getAnswersForQuestion(long questionId) {
        return answersToQuestions.get(questionId).size();
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

    // TODO: Temporary solution.
    public String[] getLobbyCodeAndConnectedUserNamesAsArray() {
        synchronized (monitor) {
            String[] names = new String[connectedUsers.size() + 1];
            names[0] = String.valueOf(getCode());
            for (int i = 1; i < names.length; i++) {
                names[i] = connectedUsers.get(i - 1).getNickname(); // At one point makes sense to have something smarter here perhaps.
            }
            return names;
        }
    }

    public boolean canAddUserToLobby() {
        return GameSettings.MAX_PLAYERS_IN_LOBBY - this.connectedUsers.size() >= 1;
    }

    public long getLobbyOwnerId() {
        return lobbyOwner.getId();
    }

    public void addUserToLobby(User user) {
        connectedUsers.add(user);
    }

    public void addUserAnswerToLobby(User user, long questionId, String answer){
        synchronized (monitor){
            Map<User, String> usersAnswer = new HashMap<>();
            usersAnswer.put(user, answer);
            answersToQuestions.put(questionId, usersAnswer);
            LOG.debug("Added user's answer to memory: questionId = " + questionId + ", answer = " + answer);
        }
    }

    public void removeUserFromLobby(User user) {
        connectedUsers.remove(user);
    }

    public Long getTriviaSetId() {
        return triviaSetId;
    }

    public void setTriviaSetId(Long triviaSetId) {
        this.triviaSetId = triviaSetId;
    }
}
