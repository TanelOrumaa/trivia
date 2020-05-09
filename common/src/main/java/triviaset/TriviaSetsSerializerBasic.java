package triviaset;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class TriviaSetsSerializerBasic implements JsonSerializer<List<TriviaSet>> {

    @Override
    public JsonElement serialize(List<TriviaSet> triviaSetList, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonArray triviaSetListArray = new JsonArray();

        for (TriviaSet triviaSet : triviaSetList) {

            JsonObject triviaSetObject = new JsonObject();
            triviaSetObject.addProperty("id", triviaSet.getId());
            triviaSetObject.addProperty("name", triviaSet.getName());
            triviaSetObject.addProperty("numberOfQuestions", triviaSet.getNumberOfQuestions());
            triviaSetListArray.add(triviaSetObject);

        }

        return triviaSetListArray;

    }

}
