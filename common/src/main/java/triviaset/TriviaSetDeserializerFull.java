package triviaset;

import com.google.gson.*;
import question.Question;
import question.QuestionDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TriviaSetDeserializerFull implements JsonDeserializer<TriviaSet> {

    @Override
    public TriviaSet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject triviaSetObject = jsonElement.getAsJsonObject();
        JsonArray questionListArray = triviaSetObject.get("questionList").getAsJsonArray();

        long id = triviaSetObject.get("id").getAsLong();
        String name = triviaSetObject.get("name").getAsString();
        long ownerId = triviaSetObject.get("ownerId").getAsLong();

        List<Question> questionList = new ArrayList<>();
        Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();

        for (JsonElement element : questionListArray) {
            Question question = gson.fromJson(gson.toJson(element.getAsJsonObject()), Question.class);
            questionList.add(question);

        }

        return new TriviaSet(id, name, questionList, ownerId);

    }

}
