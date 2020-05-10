package command;

public class NewUserConnectedUpdate extends LobbyUpdateBase {

    long userId;

    public NewUserConnectedUpdate(long userId) {
        super(LobbyUpdateType.NEW_USER_CONNECTED);
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
