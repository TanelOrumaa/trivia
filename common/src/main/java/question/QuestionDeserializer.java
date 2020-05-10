package question;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionDeserializer implements JsonDeserializer<Question> {

    @Override
    public Question deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject questionObject = jsonElement.getAsJsonObject();

        String questionType = questionObject.get("questionType").getAsString();
        String answerType = questionObject.get("answerType").getAsString();
        int questionID = questionObject.get("questionID").getAsInt();
        boolean scoreDegradation = questionObject.get("scoreDegradation").getAsBoolean();
        String question = questionObject.get("question").getAsString();
        int potentialPoints = questionObject.get("potentialPoints").getAsInt();
        int time = questionObject.get("time").getAsInt();
        JsonArray answersObject = questionObject.get("answerList").getAsJsonArray();
        List<Answer> answerList = new ArrayList<>();

        for (JsonElement answerElement : answersObject) {

            JsonObject answerObject = answerElement.getAsJsonObject();
            answerList.add(new Answer(answerObject.get("answerID").getAsLong(), answerObject.get("answerText").getAsString(),
                    answerObject.get("isCorrect").getAsBoolean()));

        }

        AnswerType answerTypeEnum = null;

        switch (answerType) {

            case "CHOICE":
                answerTypeEnum = AnswerType.CHOICE;
                break;
            case "FREEFORM":
                answerTypeEnum = AnswerType.FREEFORM;
                break;

        }

        switch (questionType) {

            case "TEXT":
                return new TextQuestion(answerTypeEnum, questionID, scoreDegradation, question, answerList, potentialPoints, time);
            case "IMAGE":
                return new ImageQuestion(answerTypeEnum, questionID, scoreDegradation, question, answerList, potentialPoints, time, questionObject.get("imagePath").getAsString());
            case "AUDIO":
                return new AudioQuestion(answerTypeEnum, questionID, scoreDegradation, question, answerList, potentialPoints, time, questionObject.get("audioAddress").getAsString());
            case "VIDEO":
                String videoAadress = questionObject.get("videoAddress").getAsString();
                return new VideoQuestion(answerTypeEnum, questionID, scoreDegradation, question, answerList, potentialPoints, time, videoAadress);
            default:
                throw new RuntimeException("Questiontype does not exist!");

        }

    }

}
