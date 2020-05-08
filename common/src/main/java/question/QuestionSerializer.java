package question;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class QuestionSerializer implements JsonSerializer<Question> {

    @Override
    public JsonElement serialize(Question question, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject questionObject = new JsonObject();

        switch (question.getQuestionType()) {

            case TEXT:
                questionObject.addProperty("questionType", "TEXT");
                break;
            case IMAGE:
                questionObject.addProperty("questionType", "IMAGE");
                questionObject.addProperty("imagePath", question.getMediaPath());
                break;
            case AUDIO:
                questionObject.addProperty("questionType", "AUDIO");
                questionObject.addProperty("audioAddress", question.getMediaPath());
                break;
            case VIDEO:
                questionObject.addProperty("questionType", "VIDEO");
                questionObject.addProperty("videoAddress", question.getMediaPath());
                break;

        }

        switch (question.getAnswerType()) {

            case CHOICE:
                questionObject.addProperty("answerType", "CHOICE");
                break;
            case FREEFORM:
                questionObject.addProperty("answerType", "FREEFORM");
                break;

        }

        questionObject.addProperty("questionID", question.getQuestionID());
        questionObject.addProperty("scoreDegradation", question.isScoreDegradation());
        questionObject.addProperty("question", question.getQuestion());
        questionObject.addProperty("potentialPoints", question.getPotentialPoints());
        questionObject.addProperty("time", question.getTime());

        JsonArray answersObject = new JsonArray();
        List<Answer> answerList = question.getAnswerList();

        for (Answer answer : answerList) {

            JsonObject answerObject = new JsonObject();
            answerObject.addProperty("answerID", answer.getAnswerID());
            answerObject.addProperty("answerText", answer.getAnswerText());
            answerObject.addProperty("isCorrect", answer.isCorrect());
            answersObject.add(answerObject);

        }
        questionObject.add("answerList", answersObject);
        
        return questionObject;
        
    }
    
}
