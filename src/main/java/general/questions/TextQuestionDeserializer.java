package general.questions;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class TextQuestionDeserializer implements JsonDeserializer<TextQuestion> {

    @Override
    public TextQuestion deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject questionObject = jsonElement.getAsJsonObject();

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

        return new TextQuestion(QuestionType.TEXT, questionID, scoreDegradation, question, correctAnswers, potentialPoints, time);

    }

}
