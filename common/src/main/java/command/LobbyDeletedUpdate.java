package command;

public class LobbyDeletedUpdate extends LobbyUpdateBase {

    private int lobbyCode;

    public LobbyDeletedUpdate(int lobbyCode) {
        super(LobbyUpdateType.LOBBY_DELETED);
        this.lobbyCode = lobbyCode;
    }

    public int getLobbyCode() {
        return lobbyCode;
    }
}
