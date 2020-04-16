package exceptions;

public class MixedServerMessageException extends RuntimeException {

    public MixedServerMessageException(String requiredHash, String actualHash) {
        super("Invalid hash received. Client expected " + requiredHash + " but received " + actualHash);
    }
}

