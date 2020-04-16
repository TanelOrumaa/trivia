package Exceptions;

public class LobbyDoesNotExistException extends RuntimeException {

    public LobbyDoesNotExistException(int code) {
        super("Lobby with code \"" + code + "\" does not exist.");
    }
}
