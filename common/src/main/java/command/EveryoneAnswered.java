package command;

public class EveryoneAnswered extends LobbyUpdateBase {

    long nextQuestionId;
    long ownerId;

    public EveryoneAnswered(long nextQuestionId, long ownerId) {
        super(LobbyUpdateType.EVERYONE_ANSWERED);
        this.nextQuestionId = nextQuestionId;
        this.ownerId = ownerId;
    }

    public long getNextQuestionId() {
        return nextQuestionId;
    }

    public long getOwnerId() {
        return ownerId;
    }
}
