package configuration;

public class Config {

    // Game settings
    public static final int MAX_PLAYERS_IN_LOBBY = 10;
    public static final long POLL_INTERVAL_MS = 1000;


    // Server settings
    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int SERVER_LISTENING_PORT = 4242;

    // SSH settings
    public static boolean DATABASE_IN_LOCALHOST = false;
    public static final String SSH_TUNNE_USERNAME = "obfuscated";
    public static final String SSH_TUNNEL_HOST = "obfuscated";
    public static final String SSH_PRIVATE_KEY_LOCATION = "obfuscated";
    public static final int SSH_PORT_FORWARDING_PORT = 3307;

    // DB settings
    public static final String DB_USERNAME = "obfuscated";
    public static final String DB_PASSWORD = "obfuscated";
    public static final String DB_NAME = "obfuscated";
    public static final String DB_CONNECT_STRING = "obfuscated";
    public static final int DB_CONNECTION_TIMEOUT_SECONDS = 10;

}
