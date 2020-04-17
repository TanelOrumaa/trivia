package general.questions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class VideoQuestionSerializer implements JsonSerializer<VideoQuestion> {

    @Override
    public JsonElement serialize(VideoQuestion videoQuestion, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject questionObject = new JsonObject();

        questionObject.addProperty("address", videoQuestion.getVideoAddress());
        questionObject.addProperty("questionID", videoQuestion.getQuestionID());
        questionObject.addProperty("scoreDegradation", videoQuestion.isScoreDegradation());
        questionObject.addProperty("question", videoQuestion.getQuestion());
        questionObject.addProperty("potentialPoints", videoQuestion.getPotentialPoints());
        questionObject.addProperty("time", videoQuestion.getTime());

        Answer correctAnswers = videoQuestion.getCorrectAnswers();
        List<String> receivedAnswers = correctAnswers.getAnswers();
        String answers = "";
        switch (correctAnswers.getAnswerType()) {

            case CHOICE:
                questionObject.addProperty("answerType", "choice");
            case FREEFORM:
                questionObject.addProperty("answerType", "freeform");

        }
        answers += receivedAnswers.get(0);
        receivedAnswers.remove(0);
        for (String answer : receivedAnswers) {

            answers += "  S  " + answer;

        }
        questionObject.addProperty("answers", answers);

        return questionObject;

    }

}
