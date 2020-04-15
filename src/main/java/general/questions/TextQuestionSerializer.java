package general.questions;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TextQuestionSerializer implements JsonSerializer<TextQuestion> {

    @Override
    public JsonElement serialize(TextQuestion textQuestion, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject questionObject = new JsonObject();

        questionObject.addProperty("questionID", textQuestion.getQuestionID());
        questionObject.addProperty("scoreDegradation", textQuestion.isScoreDegradation());
        questionObject.addProperty("question", textQuestion.getQuestion());
        questionObject.addProperty("potentialPoints", textQuestion.getPotentialPoints());
        questionObject.addProperty("time", textQuestion.getTime());

        Answers correctAnswers = textQuestion.getCorrectAnswers();
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
