package general.questions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class ImageQuestionSerializer implements JsonSerializer<ImageQuestion> {

    @Override
    public JsonElement serialize(ImageQuestion imageQuestion, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject questionObject = new JsonObject();

        questionObject.addProperty("path", imageQuestion.getImagePath().toString());
        questionObject.addProperty("questionID", imageQuestion.getQuestionID());
        questionObject.addProperty("scoreDegradation", imageQuestion.isScoreDegradation());
        questionObject.addProperty("question", imageQuestion.getQuestion());
        questionObject.addProperty("potentialPoints", imageQuestion.getPotentialPoints());
        questionObject.addProperty("time", imageQuestion.getTime());

        Answers correctAnswers = imageQuestion.getCorrectAnswers();
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
