package triviaset;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import question.*;

import java.util.ArrayList;
import java.util.List;

public class TriviaTest {

    public static void main(String[] args) {

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(1L, "siin", true));
        answers.add(new Answer(2L, "seal", false));
        answers.add(new Answer(3L, "Lible", true));

        TextQuestion textQuestion = new TextQuestion(AnswerType.CHOICE, 1, true, "Kus sa oled?", answers, 69, 420);
        ImageQuestion imageQuestion = new ImageQuestion(AnswerType.FREEFORM, 1, true, "siin ma olen?", answers, 69, 420, "teerada");
        AudioQuestion audioQuestion = new AudioQuestion(AnswerType.CHOICE, 1, false, "kullaketrajad?", answers, 666, 420, "youtube");
        VideoQuestion videoQuestion = new VideoQuestion(AnswerType.CHOICE, 1, true, "salajast vyrtsi ei olegi?", answers, 69, 420, "twitter");
        List<Question> questions1 = new ArrayList<>();
        List<Question> questions2 = new ArrayList<>();
        questions1.add(textQuestion);
        questions1.add(imageQuestion);
        questions2.add(audioQuestion);
        questions2.add(videoQuestion);

        TriviaSet triviaSet1 = new TriviaSet(-1L, "Ülo Remmelgas", questions1, 1L);
        TriviaSet triviaSet2 = new TriviaSet(-1L, "Alo Ritsing", questions2, 1L);
        List<TriviaSet> triviaSets = new ArrayList<>();
        triviaSets.add(triviaSet1);
        triviaSets.add(triviaSet2);

        Gson gsonSend = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetSerializerFull()).create();
        System.out.println("Triviaset" + gsonSend.toJson(triviaSet1));
        Gson gsonReceive = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetDeserializerFull()).create();
        String json = gsonSend.toJson(triviaSet2);
        System.out.println(json);
        TriviaSet receivedSet = gsonReceive.fromJson(json, TriviaSet.class);
        System.out.println(receivedSet.getQuestionMap().toString());
        receivedSet.getQuestionMap().forEach(((integer, question) -> System.out.println(question.getQuestion())));

    }

}
