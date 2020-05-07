package exception;

public class LobbyDoesNotExistException extends RuntimeException {

    public LobbyDoesNotExistException(int code) {
        super("lobby.Lobby with code \"" + code + "\" does not exist.");
    }
}
