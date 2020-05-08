package presenterclient;

import baseclient.BaseClient;
import baseclient.ClientType;

public class PresenterClient extends BaseClient {

    public PresenterClient() {
        super(ClientType.PRESENTER);
    }

    public static void main(String[] args) {
        new PresenterClient();
        launch(args);
    }

}
