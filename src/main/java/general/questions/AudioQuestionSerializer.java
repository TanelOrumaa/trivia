package general.questions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class AudioQuestionSerializer implements JsonSerializer<AudioQuestion> {

    @Override
    public JsonElement serialize(AudioQuestion audioQuestion, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject questionObject = new JsonObject();

        questionObject.addProperty("address", audioQuestion.getAudioAddress());
        questionObject.addProperty("questionID", audioQuestion.getQuestionID());
        questionObject.addProperty("scoreDegradation", audioQuestion.isScoreDegradation());
        questionObject.addProperty("question", audioQuestion.getQuestion());
        questionObject.addProperty("potentialPoints", audioQuestion.getPotentialPoints());
        questionObject.addProperty("time", audioQuestion.getTime());

        Answer correctAnswers = audioQuestion.getCorrectAnswers();
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
