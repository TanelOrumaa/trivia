package general;

import com.google.gson.*;
import general.questions.Question;
import general.questions.QuestionSerializer;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class TriviaSetSerializerFull implements JsonSerializer<TriviaSet> {

    @Override
    public JsonElement serialize(TriviaSet triviaSet, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject triviaSetObject = new JsonObject();

        triviaSetObject.addProperty("name", triviaSet.getName());
        triviaSetObject.addProperty("numberOfQuestions", triviaSet.getNumberOfQuestions());

        JsonArray questionMapObject = new JsonArray();
        LinkedHashMap<Integer, Question> questionMap = triviaSet.getQuestionMap();

        Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionSerializer()).create();
        questionMap.forEach((integer, question) -> questionMapObject.add(gson.toJson(question)));
        triviaSetObject.add("questionMap", questionMapObject);

        return triviaSetObject;

    }

}
