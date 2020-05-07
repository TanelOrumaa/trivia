package configuration;

public class Config {

    // SSH settings
    public static final int SSH_PORT_FORWARDING_PORT = 8889;

    // Server settings
    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int SERVER_LISTENING_PORT = 4242;
    public static final int SOCKET_POLL_INTERVAL = 100;

    // DB settings
    public static final String DB_USERNAME = "integrator";
    public static final String DB_PASSWORD = "76yx7xPQV56E39ISzDQDVAEd97glqBbc";
    public static final String DB_NAME = "trivia";
    public static final String DB_CONNECT_STRING = "jdbc:mysql://localhost:" + SSH_PORT_FORWARDING_PORT + "/" + DB_NAME;
    public static final int DB_CONNECTION_TIMEOUT_SECONDS = 10;
}