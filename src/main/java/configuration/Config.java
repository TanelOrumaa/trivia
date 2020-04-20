package configuration;

public class Config {

    // Game settings
    public static final int MAX_PLAYERS_IN_LOBBY = 10;
    public static final long SOCKET_POLL_INTERVAL = 100;

    // SSH settings
    public static final int SSH_PORT_FORWARDING_PORT = 8889;

    // Server settings
    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int SERVER_LISTENING_PORT = 4242;

    // DB settings
    public static final String DB_USERNAME = "obfuscated";
    public static final String DB_PASSWORD = "obfuscated";
    public static final String DB_NAME = "obfuscated";
    public static final String DB_CONNECT_STRING = "jdbc:mysql://localhost:" + SSH_PORT_FORWARDING_PORT + "/" + DB_NAME;
    public static final int DB_CONNECTION_TIMEOUT_SECONDS = 10;

}
