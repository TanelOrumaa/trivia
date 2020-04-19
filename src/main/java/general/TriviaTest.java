package general;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import general.questions.*;

import java.util.ArrayList;
import java.util.List;

public class TriviaTest {

    public static void main(String[] args) {

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("siin", true, 1));
        answers.add(new Answer("seal", false, 2));
        answers.add(new Answer("Lible", true, 3));

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

        TriviaSet triviaSet1 = new TriviaSet("Ülo Remmelgas", questions1);
        TriviaSet triviaSet2 = new TriviaSet("Alo Ritsing", questions2);
        List<TriviaSet> triviaSets = new ArrayList<>();
        triviaSets.add(triviaSet1);
        triviaSets.add(triviaSet2);

        Gson gsonSend = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetSerializerFull()).create();
        System.out.println("Triviaset" + gsonSend.toJson(triviaSet1));
        Gson gsonReceive = new GsonBuilder().registerTypeAdapter(TriviaSet.class, new TriviaSetDeserializerFull()).create();
        TriviaSet receivedSet = gsonReceive.fromJson(gsonSend.toJson(triviaSet1), TriviaSet.class);
        System.out.println(receivedSet.getQuestionMap().toString());

    }

}
