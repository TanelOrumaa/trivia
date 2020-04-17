package general.questions;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ImageQuestionDeserializer implements JsonDeserializer<ImageQuestion> {

    @Override
    public ImageQuestion deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject questionObject = jsonElement.getAsJsonObject();

        Path imagePath = Paths.get(questionObject.get("path").getAsString());
        int questionID = questionObject.get("questionID").getAsInt();
        boolean scoreDegradation = questionObject.get("scoreDegradation").getAsBoolean();
        String question = questionObject.get("question").getAsString();
        int potentialPoints = questionObject.get("potentialPoints").getAsInt();
        int time = questionObject.get("time").getAsInt();

        AnswerType answerType = null;
        switch (questionObject.get("answerType").getAsString()) {

            case "choice":
                answerType = AnswerType.CHOICE;
            case "freeform":
                answerType = AnswerType.FREEFORM;

        }
        List<String> answers = Arrays.asList(questionObject.get("answers").getAsString().split(" {2}S {2}"));
        Answer correctAnswers = new Answer(answers, answerType);

        return new ImageQuestion(QuestionType.TEXT, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time, imagePath);

    }

}
