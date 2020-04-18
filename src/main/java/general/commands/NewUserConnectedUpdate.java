package general.commands;

public class NewUserConnectedUpdate extends LobbyUpdateBase {

    int userId;

    public NewUserConnectedUpdate(int userId) {
        super(LobbyUpdateType.NEW_USER_CONNECTED);
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
