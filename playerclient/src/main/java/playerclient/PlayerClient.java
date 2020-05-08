package playerclient;

import baseclient.BaseClient;
import baseclient.ClientType;

public class PlayerClient extends BaseClient {

    public PlayerClient() {
        super(ClientType.PLAYER);
    }

    public static void main(String[] args) {
        new PlayerClient();
        launch(args);
    }

}
