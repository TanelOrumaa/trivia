package general;

import general.questions.*;

import java.util.ArrayList;
import java.util.List;

public class TriviaTest {

    public static void main(String[] args) {

        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer("siin", true, 1));
        answers.add(new Answer("seal", false, 2));
        answers.add(new Answer("Lible", true, 3));

        TextQuestion textQuestion = new TextQuestion(AnswerType.CHOICE, 1, true, "Kus sa oled", answers, 69, 420);
        ImageQuestion imageQuestion = new ImageQuestion(AnswerType.FREEFORM, 1, true, "Kus sa oled", answers, 69, 420, "teerada");
        AudioQuestion audioQuestion = new AudioQuestion(AnswerType.CHOICE, 1, false, "peeter", answers, 666, 420, "youtube");
        VideoQuestion videoQuestion = new VideoQuestion(AnswerType.CHOICE, 1, true, "Kus sa oled", answers, 69, 420, "twitter");
        List<Question> questions = new ArrayList<>();
        questions.add(textQuestion);
        questions.add(imageQuestion);
        questions.add(audioQuestion);
        questions.add(videoQuestion);

        TriviaSet triviaSet = new TriviaSet("Ülo Remmelgas", questions);
        System.out.println(triviaSet.getQuestionMap().toString());
        triviaSet.reorderQuestion(2, 2);
        System.out.println(triviaSet.getQuestionMap().toString());

    }

}
