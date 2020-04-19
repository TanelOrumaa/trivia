package general;

import com.google.gson.*;
import general.questions.Question;
import general.questions.QuestionDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TriviaSetDeserializerFull implements JsonDeserializer<TriviaSet> {

    @Override
    public TriviaSet deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject triviaSetObject = jsonElement.getAsJsonObject();
        JsonArray questionListArray = triviaSetObject.get("questionList").getAsJsonArray();

        String name = triviaSetObject.get("name").getAsString();

        List<Question> questionList = new ArrayList<>();
        Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionDeserializer()).create();
        for (JsonElement element : questionListArray) {

            JsonObject questionObject = element.getAsJsonObject();
            questionList.add(gson.fromJson(gson.toJson(questionObject), Question.class));

        }

        return new TriviaSet(name, questionList);

    }

}
