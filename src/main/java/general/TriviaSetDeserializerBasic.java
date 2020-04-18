package general;

import com.google.gson.*;
import general.questions.Question;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TriviaSetDeserializerBasic implements JsonDeserializer<TriviaSet>{

    @Override
    public TriviaSet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject triviaSetObject = jsonElement.getAsJsonObject();
        String name = triviaSetObject.get("name").getAsString();
        int numberOfQuestions = triviaSetObject.get("numberOfQuestions").getAsInt();

        return new TriviaSet(name, new ArrayList<>());

    }

}
