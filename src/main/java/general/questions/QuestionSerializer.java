package general.questions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class QuestionSerializer implements JsonSerializer<Question> {

    @Override
    public JsonElement serialize(Question question, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject questionObject = new JsonObject();

        questionObject.addProperty("questionID", question.getQuestionID());
        questionObject.addProperty("scoreDegradation", question.isScoreDegradation());
        questionObject.addProperty("question", question.getQuestion());
        questionObject.addProperty("potentialPoints", question.getPotentialPoints());
        questionObject.addProperty("time", question.getTime());
        
        return questionObject;
        
    }
    
}
