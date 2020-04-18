package baseclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import configuration.Config;
import exceptions.MixedServerMessageException;
import general.*;
import general.questions.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class BaseClientBackEnd implements Runnable {

    static final Logger LOG = LoggerFactory.getLogger(BaseClientBackEnd.class);

    protected BaseClient frontEnd;
    protected ClientType clientType;
    protected BlockingQueue<Question> questions;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    static CommandQueue commandQueue;

    // Client properties.
    private static String hash;
    private static User user;
    private static Lobby currentLobby;

    // Sync variables.
    private static long serverTimeDelta;
    private static long smallestPing;

    public BaseClientBackEnd(BaseClient frontEnd, BlockingQueue<Question> questions) {
        this.frontEnd = frontEnd;
        this.questions = questions;
        this.clientType = frontEnd.type;

        smallestPing = Long.MAX_VALUE;
        commandQueue = new CommandQueue();
    }

    public void run() {
        // Create connection with backend.
        try (Socket socket = new Socket(Config.SERVER_IP_ADDRESS, Config.SERVER_LISTENING_PORT)) {
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeInt(101); // Hello message.
            switch (this.clientType) { // Type of client
                case PRESENTER:
                    dataOutputStream.writeInt(1);
                    break;
                case PLAYER:
                    dataOutputStream.writeInt(2);
                    break;
                case HOST:
                    dataOutputStream.writeInt(3);
                    break;
            }

            LOG.debug("Sent the hello message.");

            // Receive acknowledgement + hash
            int code = dataInputStream.readInt();
            if (code == 102) {
                hash = dataInputStream.readUTF();
                LOG.debug("Server ackowledged our \"Hello\" message. Our hash is now " + hash);
            } else {
                handleError(code);
            }

            // Do the first sync
            try {
                sync();
            } catch (IOException e) {
                LOG.warn("Sync with server failed.");
            }

            LOG.debug("Back-end started");

            // Run in loop until quit command is sent.
            Command command;
            try {
                while (true) {
                    if (commandQueue.size() > 0 && (command = commandQueue.poll(now())) != null) {
                        LOG.debug("Received a new command from front-end: " + command);
                        if (command.code == 199) {
                            break;
                        } else {
                            handleCommand(command);
                        }
                    } else {
                        LOG.trace("No commands right now. Trying to fetch messages from server.");
                        // Try to read commands from server for sleep time. Since this is internal, we don't need to use
                        // servertime here.
                        long sleepUntil = System.currentTimeMillis() + Config.POLL_INTERVAL_MS;
                        while (System.currentTimeMillis() < sleepUntil) {
                            if (dataInputStream.available() > 0) {
                                LOG.debug("There's a message incoming.");
                                code = dataInputStream.readInt();
                                handleIncoming(code);
                            }
                            // Wait for a small amount so we don't waste CPU resources for nothing.
                            Thread.sleep(100);
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOG.warn("Thread was interrupted.");
            }
        } catch (IOException e) {
            // TODO: Let the client know that connection to the server failed.
        } finally {
            try {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (IOException e) {
                LOG.warn("Unable to close input/output streams.");
            }
        }
    }

    public static void addCommand(int code, String[] commands, int delay) {
        commandQueue.add(new Command(code, commands, now() + delay));
        LOG.debug("Added a new command " + code + " to queue.");
    }

    private void handleCommand(Command command) throws IOException {
        int commandCode = command.code;
        switch (commandCode) {
            case 111: // Sync command.
                sync();
                break;
            case 121: // Login command
                if (command.args != null && command.args.length == 2) {
                    login(command.args[0], command.args[1]);
                }
                break;
            case 131: // Connect to lobby
                if (command.args != null && command.args.length == 1) {
                    connectToLobby(Integer.parseInt(command.args[0]));
                }
                break;
            case 133: // Create lobby
                String lobbyName = "";
                if (command.args != null && command.args.length == 1) {
                    lobbyName = command.args[0];
                }
                createLobby(lobbyName);
                break;
            case 123: // Register new user
                if (command.args != null && command.args.length == 3) {
                    registerUser(command.args[0], command.args[1], command.args[2]);

                }
            default:
                // For testing.
                LOG.debug("Unknown command " + command + ". Sending to server.");
                dataOutputStream.writeInt(commandCode);
                dataOutputStream.writeInt(command.args.length);
                for (int i = 0; i < command.args.length; i++) {
                    dataOutputStream.writeUTF(command.args[i]);
                }
                LOG.debug("Sent command to backend");
                int incomingCode = dataInputStream.readInt();
                LOG.debug("Incoming code was " + incomingCode);
                int argsAmount = dataInputStream.readInt();
                LOG.debug("Server will reply with " + argsAmount + " pieces of data.");

                String[] incomingParams = new String[argsAmount];
                for (int i = 0; i < argsAmount; i++) {
                    incomingParams[i] = dataInputStream.readUTF();
                }
                LOG.debug("Received all pieces of data.");
                LOG.debug("Server sent us:");
                for (int i = 0; i < incomingParams.length; i++) {
                    LOG.debug(incomingParams[i]);
                }
                LOG.debug("Command finished.");
        }
    }

    private void handleIncoming(int commandCode) throws IOException {
        // TODO: WIP
        LOG.debug("Incoming message code: " + commandCode);
        if (commandCode == 136) {
            String receivedHash = dataInputStream.readUTF();
            if (hash.equals(receivedHash)) {
                String lobbyAsJson = dataInputStream.readUTF();
                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbyDeserializer()).create();
                currentLobby = gson.fromJson(lobbyAsJson, Lobby.class);

                LOG.debug("New lobby version received " + currentLobby.getCode());
                this.frontEnd.addCommandAndInvoke(new Command(132, currentLobby.getConnectedUserNamesAsArray(), System.currentTimeMillis()));

                dataOutputStream.writeInt(137);
                dataOutputStream.writeUTF(hash);
            } else {
                throw new MixedServerMessageException(hash, receivedHash);
            }
        } else {
            // Just for testing.
            int paramsAmount = dataInputStream.readInt();
            String[] params = new String[paramsAmount];
            for (int i = 0; i < paramsAmount; i++) {
                params[i] = dataInputStream.readUTF();
            }
            this.frontEnd.incomingCommands.add(new Command(commandCode, params, System.currentTimeMillis()));
            LOG.debug("Added a new command to incoming queue.");
            this.frontEnd.listenEvent();
        }
    }

    private void handleError(int errorCode) throws IOException {
        // TODO: WIP
        System.out.println("ERROR: " + errorCode);
        // TODO: Currently just for testing, need to implement actual events for different errors.
        switch (errorCode) {
            case 402:
                LOG.debug("Unexpected message code.");
                break;
            case 404:
                LOG.debug("We sent an incorrect hash.");
                break;
            case 422:
                LOG.debug("Invalid login data.");
                break;
            case 423:
                LOG.debug("New user registration failed because username already exists");
                // Send error code to front-end to display error to user
                this.frontEnd.addCommandAndInvoke(new Command(423, new String[0], System.currentTimeMillis()));
                break;
            case 432:
                LOG.debug("Lobby does not exist.");
                break;
            case 434:
                LOG.debug("Lobby is full.");
                break;
        }
    }

    private void sync() throws IOException {
        // Send sync intialization message.
        LOG.debug("Sending sync message to server.");
        long syncStart = System.currentTimeMillis();
        dataOutputStream.writeInt(111);
        dataOutputStream.writeUTF(hash);

        int code = dataInputStream.readInt();
        if (code == 112) {
            LOG.debug("Server accepted our sync message and responded.");
            String resonseHash = dataInputStream.readUTF();
            if (hash.equals(resonseHash)) {
                long serverTime = dataInputStream.readLong();
                long syncEnd = System.currentTimeMillis();
                long ping = syncEnd - syncStart;
                if (ping <= smallestPing) {
                    serverTimeDelta = syncEnd - serverTime - ping / 2;
                    smallestPing = ping;
                }
                LOG.debug("Sync complete. New ping was " + ping + " and smallestPing is " + smallestPing);
            } else {
                throw new MixedServerMessageException(hash, resonseHash);
            }
        } else if (code >= 400 && code < 500) {
            handleError(code);
        } else {
            handleIncoming(code);
        }

        // Queue next sync
        addCommand(111, new String[0], 60000);
    }

    private void login(String username, String password) throws IOException {
        // Send login message.
        LOG.debug("Sending a login message to server.");
        dataOutputStream.writeInt(121);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeUTF(username);
        dataOutputStream.writeUTF(password);

        // Receive response.
        int responseCode = dataInputStream.readInt();
        if (responseCode == 122) {
            LOG.debug("Server accepted our login message.");
            // Login was successful. Check if hash matches.
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read and deserialize the user object.
                String userJson = dataInputStream.readUTF();
                Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserDeserializer()).create();
                user = gson.fromJson(userJson, User.class);

                // Add the command to front end.
                this.frontEnd.addCommandAndInvoke(new Command(122, new String[]{user.getUsername(), user.getFirstName(), user.getLastName()}, System.currentTimeMillis()));

            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }


    private void connectToLobby(int lobbyCode) throws IOException {
        // Send connect to lobby message.
        LOG.debug("Sending a connect to lobby message with code " + lobbyCode);
        dataOutputStream.writeInt(131);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeInt(lobbyCode);

        // Read the response
        int responseCode = dataInputStream.readInt();

        if (responseCode == 132) {
            LOG.debug("Server accepted the lobby connection message.");
            // Connecting to lobby was successful. Check if hash matches.
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the lobby object and deserialize it.
                String lobbyJson = dataInputStream.readUTF();

                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbyDeserializer()).create();
                currentLobby = gson.fromJson(lobbyJson, Lobby.class);
                LOG.debug("Connected to lobby " + currentLobby.getCode());

                this.frontEnd.addCommandAndInvoke(new Command(132, currentLobby.getConnectedUserNamesAsArray(), System.currentTimeMillis()));
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void createLobby(String lobbyName) throws IOException {
        // Send lobby creation mesage.
        LOG.debug("Sending a create lobby message.");
        dataOutputStream.writeInt(133);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeUTF(lobbyName);

        // Read the response.
        int responseCode = dataInputStream.readInt();
        if (responseCode == 134) {
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)) {
                // Read the lobby object and deserialize it.
                String lobbyAsJson = dataInputStream.readUTF();

                Gson gson = new GsonBuilder().registerTypeAdapter(Lobby.class, new LobbyDeserializer()).create();
                currentLobby = gson.fromJson(lobbyAsJson, Lobby.class);
                LOG.debug("Created lobby with code " + currentLobby.getCode());

                this.frontEnd.addCommandAndInvoke(new Command(134, currentLobby.getConnectedUserNamesAsArray(), System.currentTimeMillis()));
            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }
        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private void registerUser(String username, String password, String nickname) throws IOException {
        // Send register new user message.
        LOG.debug("Sending a register new user message");
        dataOutputStream.writeInt(123);
        dataOutputStream.writeUTF(hash);
        dataOutputStream.writeUTF(username);
        dataOutputStream.writeUTF(password);
        dataOutputStream.writeUTF(nickname);

        //Receive response
        int responseCode = dataInputStream.readInt();
        if (responseCode == 124){
            LOG.debug("Server responded positively - registration successful");
            String responseHash = dataInputStream.readUTF();
            if (hash.equals(responseHash)){
                this.frontEnd.addCommandAndInvoke(new Command(124, new String[0], System.currentTimeMillis()));

            } else {
                throw new MixedServerMessageException(hash, responseHash);
            }

        } else if (responseCode >= 400 && responseCode < 500) {
            handleError(responseCode);
        } else {
            handleIncoming(responseCode);
        }
    }

    private static long now() {
        return System.currentTimeMillis() + serverTimeDelta;
    }
}