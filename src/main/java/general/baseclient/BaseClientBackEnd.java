package general.baseclient;

import general.questions.BaseQuestion;
import general.questions.Question;
import general.questions.QuestionType;
import javafx.scene.Scene;

import java.util.concurrent.BlockingQueue;

public class BaseClientBackEnd implements Runnable {
    protected BaseClient frontEnd;
    protected BlockingQueue<Question> questions;

    public BaseClientBackEnd(BaseClient frontEnd, BlockingQueue<Question> questions) {
        this.frontEnd = frontEnd;
        this.questions = questions;

    }

    public void run() {
        System.out.println("Back-end started");

        try{
            // kui back-end saab serverilt käsu näidata järgmist küsimust, siis paneb vajaliku info BlockingQueue-desse ära ja siis (mingil viisil) triggerib GUI threadi showNextQuestion() meetodi
            Thread.sleep(1000);
            BaseQuestion question1 = new BaseQuestion(QuestionType.CHOICE, true, "Nimeta riik Lõuna-Ameerikas", 1000, 60);
            //questions.add(question1);

            Thread.sleep(2000);
            BaseQuestion question2 = new BaseQuestion(QuestionType.CHOICE, false, "Kes on Teet Margna?", 420, 30);
            //questions.add(question2);



            // WORK IN PROGRESS
            Scene nextQuestion = QuestionChoice.change(frontEnd.guiStage, frontEnd);
            frontEnd.guiStage.setScene(nextQuestion);

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }


    }
}