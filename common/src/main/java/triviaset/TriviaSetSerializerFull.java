package triviaset;

import com.google.gson.*;
import question.Question;
import question.QuestionSerializer;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

public class TriviaSetSerializerFull implements JsonSerializer<TriviaSet> {

    @Override
    public JsonElement serialize(TriviaSet triviaSet, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject triviaSetObject = new JsonObject();

        triviaSetObject.addProperty("id", triviaSet.getId());
        triviaSetObject.addProperty("name", triviaSet.getName());

        JsonArray questionListArray = new JsonArray();
        LinkedHashMap<Integer, Question> questionMap = triviaSet.getQuestionMap();

        Gson gson = new GsonBuilder().registerTypeAdapter(Question.class, new QuestionSerializer()).create();
        questionMap.forEach((integer, question) -> {
            questionListArray.add(gson.toJsonTree(question)); });
        triviaSetObject.add("questionList", questionListArray);
        return triviaSetObject;

    }

}
