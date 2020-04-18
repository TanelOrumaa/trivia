package general;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class TriviaSetSerializerBasic implements JsonSerializer<TriviaSet> {

    @Override
    public JsonElement serialize(TriviaSet triviaSet, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject triviaSetObject = new JsonObject();

        triviaSetObject.addProperty("name", triviaSet.getName());
        triviaSetObject.addProperty("numberOfQuestions", triviaSet.getNumberOfQuestions());

        return triviaSetObject;

    }

}
