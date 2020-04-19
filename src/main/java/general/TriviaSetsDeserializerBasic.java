package general;

import com.google.gson.*;
import general.questions.Question;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TriviaSetsDeserializerBasic implements JsonDeserializer<List<TriviaSet>>{

    @Override
    public List<TriviaSet> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonArray triviaSetListArray = jsonElement.getAsJsonArray();
        List<TriviaSet> triviaSetList = new ArrayList<>();

        for (JsonElement element : triviaSetListArray) {

            JsonObject triviaSetObject = element.getAsJsonObject();
            triviaSetList.add(new TriviaSet(triviaSetObject.get("name").getAsString(),
                    triviaSetObject.get("numberOfQuestions").getAsInt()));

        }

        return triviaSetList;

    }

}
