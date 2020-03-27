package general.baseclient;

import general.questions.Question;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BaseClient extends Application {
    BlockingQueue<Question> questions = new ArrayBlockingQueue<Question>(10);


    public static double getWidth() {
        double width = 350;
        return width;
    }

    public static double getHeight() {
        double height = 561.5;
        return height;
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {


        BaseClientBackEnd backEnd = new BaseClientBackEnd(primaryStage, questions);
        Thread backEndThread = new Thread(backEnd);
        backEndThread.start();



        //Already have logInScreen, lobbyEntryScreen, lobbyScreen, registerScreen, questionChoiceScreen, questionTextAreaScreen, waitingAfterQuestionScreen

        primaryStage.setScene(LogIn.change(primaryStage));
        primaryStage.setTitle("Base client");
        primaryStage.show();



    }


    public static void main(String[] args) {

        launch(args);
    }

    static void showStartScreen(Stage primaryStage) {
        System.out.println("showStartScreen");
        Group startScreenRoot = new Group();

        Scene startScreenScene = new Scene(startScreenRoot, 800, 600, Color.GREEN);
        primaryStage.setScene(startScreenScene);
    }


    void showNextQuestion(Stage primaryStage) {
        Question question = questions.poll();
        /*
        Enum questionType = question.questionType;
        TextField questionField = new TextField(question);
        questionField.setEditable(false);
        switch (questionType){
            case 0:
                showTextQuestion(primaryStage, questionField);
                break;
            case 1:
                showImageQuestion(primaryStage, questionField);
                break;
            case 2:
                showVideoQuestion(primaryStage, questionField);
                break;
        }

         */

    }

    void showTextQuestion(Stage primaryStage, TextField questionField) {
        Group root = new Group();
        questionField.setLayoutX(200);
        questionField.setLayoutY(50);
        root.getChildren().add(questionField);
        Scene scene = new Scene(root, 800, 600, Color.BLANCHEDALMOND);
        primaryStage.setScene(scene);
    }

    void showImageQuestion(Stage primaryStage, TextField questionField) {
        Group root = new Group();
        questionField.setLayoutX(200);
        questionField.setLayoutY(50);
        root.getChildren().add(questionField);

        Rectangle image = new Rectangle(100, 100, 300, 300);
        root.getChildren().add(image);
        Scene scene = new Scene(root, 800, 600, Color.BLANCHEDALMOND);
        primaryStage.setScene(scene);
    }

    void showVideoQuestion(Stage primaryStage, TextField questionField) {

    }

    void showLeaderboard(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.BLUE);
        primaryStage.setScene(scene);
    }

}