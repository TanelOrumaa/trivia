package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    // SSH settings
    public static int SSH_PORT_FORWARDING_PORT = 0;

    // Server settings
    public static String SERVER_IP_ADDRESS = "";
    public static int SERVER_LISTENING_PORT = 0;
    public static int SOCKET_POLL_INTERVAL = 0;

    // DB settings
    public static String DB_USERNAME = "";
    public static String DB_PASSWORD = "";
    public static String DB_NAME = "";
    public static String DB_CONNECT_STRING = "";
    public static int DB_CONNECTION_TIMEOUT_SECONDS = 0;

    public static void readConfiguration() throws IOException {

        // TODO: Maybe find a more sensible solution for this?
        String configPath =
                Thread.currentThread().getContextClassLoader().getResource("").getPath() + // The server/target/classes/server folder
                        File.separator + ".." + File.separator + ".." + File.separator + ".." + File.separator + // Go up 3 folders.
                        "common" + File.separator + "target" + File.separator + "classes" + File.separator + "sensitive" + // Go to common/target/classes/sensitive.
                        File.separator + "trivia.properties"; // Property file.

        Properties triviaProperties = new Properties();
        triviaProperties.load(new FileInputStream(configPath));

        SSH_PORT_FORWARDING_PORT = Integer.parseInt(triviaProperties.getProperty("ssh_port_forwarding_port"));
        SERVER_IP_ADDRESS = triviaProperties.getProperty("server_ip_address");
        SERVER_LISTENING_PORT = Integer.parseInt(triviaProperties.getProperty("server_listening_port"));
        SOCKET_POLL_INTERVAL = Integer.parseInt(triviaProperties.getProperty("socket_poll_interval"));
        DB_USERNAME = triviaProperties.getProperty("db_username");
        DB_PASSWORD = triviaProperties.getProperty("db_password");
        DB_NAME = triviaProperties.getProperty("db_name");
        DB_CONNECT_STRING = triviaProperties.getProperty("db_connect_string_start") + SERVER_IP_ADDRESS + ":" + SSH_PORT_FORWARDING_PORT + "/" + DB_NAME;
        DB_CONNECTION_TIMEOUT_SECONDS = Integer.parseInt(triviaProperties.getProperty("db_connection_timeout_seconds"));
    }

}
