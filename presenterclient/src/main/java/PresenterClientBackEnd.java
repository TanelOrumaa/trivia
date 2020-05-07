import javafx.stage.Stage;

import java.util.concurrent.BlockingQueue;


public class PresenterClientBackEnd implements Runnable {
    Stage primaryStage;
    BlockingQueue<Integer> questionTypes;
    BlockingQueue<String> questions;

    public PresenterClientBackEnd(Stage primaryStage, BlockingQueue<Integer> questionTypes, BlockingQueue<String> questions) {
        this.primaryStage = primaryStage;
        this.questionTypes = questionTypes;
        this.questions = questions;

    }

    public void run() {
        System.out.println("Back-end started");

        try{
            // kui back-end saab serverilt käsu näidata järgmist küsimust, siis paneb vajaliku info BlockingQueue-desse ära ja siis (mingil viisil) triggerib GUI threadi showNextQuestion() meetodi
            Thread.sleep(1000);
            questionTypes.add(Integer.valueOf(0));
            questions.add("Nimeta riik Lõuna-Ameerikas");

            Thread.sleep(2000);
            questionTypes.add(1);
            questions.add("Mis objekti on kujutatud antud pildil?");

        }
        catch (InterruptedException e){
            e.printStackTrace();
        }


    }
}
