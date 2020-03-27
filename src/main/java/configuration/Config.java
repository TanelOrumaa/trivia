package configuration;

import java.io.File;

public class Config {

    // SSH settings
    public static boolean DATABASE_IN_LOCALHOST = false;
    public static final String SSH_TUNNEL_USERNAME = "obfuscated";
    public static final String SSH_TUNNEL_HOST = "11.11.11.11";
    public static final String SSH_PRIVATE_KEY_LOCATION = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "keys" + File.separator + "trivia.ppk";
    public static final int SSH_PORT_FORWARDING_PORT = 3307;

    // DB settings
    public static final String DB_USERNAME = "obfuscated";
    public static final String DB_PASSWORD = "obfuscated";
    public static final String DB_NAME = "obfuscated";
    public static final String DB_CONNECT_STRING = "obfuscated:" + SSH_PORT_FORWARDING_PORT + "/" + DB_NAME;
    public static final int DB_CONNECTION_TIMEOUT_SECONDS = 10;

}
