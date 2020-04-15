package general.questions;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class VideoQuestionDeserializer implements JsonDeserializer<VideoQuestion> {

    @Override
    public VideoQuestion deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject questionObject = jsonElement.getAsJsonObject();

        String videoAddress = questionObject.get("address").getAsString();
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
        Answers correctAnswers = new Answers(answers, answerType);

        return new VideoQuestion(QuestionType.TEXT, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time, videoAddress);

    }

}
