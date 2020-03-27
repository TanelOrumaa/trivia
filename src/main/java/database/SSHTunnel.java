package database;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import configuration.Config;

public class SSHTunnel {

    private Session session;
    private int port;

    public SSHTunnel() {
        this.session = createSSHTunnel();
    }


    // Creates a SSH tunnel object with port forwarding and returns it.
    private Session createSSHTunnel() {
        try {
            JSch jsch = new JSch();

            // Insecure, only for testing.
            JSch.setConfig("StrictHostKeyChecking", "no");

            // Add the private key.
            jsch.addIdentity(Config.SSH_PRIVATE_KEY_LOCATION);

            // Create the SSH session.
            Session session = jsch.getSession(Config.SSH_TUNNE_USERNAME, Config.SSH_TUNNEL_HOST);

            // Connect to the session.
            session.connect();

            // Set up port forwarding.
            this.port = session.setPortForwardingL(Config.SSH_PORT_FORWARDING_PORT, "localhost", 3306);

            return session;
        } catch (
                JSchException e) {
            throw new RuntimeException("Unable to create the SSH session.", e);
        }
    }


    // Checks if the tunnel is active.
    public boolean isSSHTunnelActive() {
        return this.session.isConnected();
    }

    public int getPort() {
        return this.port;
    }

    // Closes this SSH tunnel.
    public void closeSSHTunne() {
        this.session.disconnect();
    }
}
