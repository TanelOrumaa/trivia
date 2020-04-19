package general.commands;

public class DisplayNextQuestionUpdate extends LobbyUpdateBase {

    long questionId;

    public DisplayNextQuestionUpdate(long questionId) {
        super(LobbyUpdateType.DISPLAY_NEXT_QUESTION);
        this.questionId = questionId;
    }

    public long getQuestionId() {
        return questionId;
    }
}
