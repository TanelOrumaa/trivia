package general.baseclient;

import general.questions.Question;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BaseClient extends Application {
    protected ClientType type;
    protected Stage guiStage;
    protected BlockingQueue<Question> questions = new ArrayBlockingQueue<Question>(10);


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
        guiStage = primaryStage;

        try (Scanner in = new Scanner(System.in)) {
            System.out.println("Enter client type: (1 - Presenter, 2 - Player , 3 - Host) :");
            String answer = in.next();
            switch (answer) {
                case "1":
                    type = ClientType.PRESENTER;
                case "2":
                    type = ClientType.PLAYER;
                case "3":
                    type = ClientType.HOST;
            }

        }


        BaseClientBackEnd backEnd = new BaseClientBackEnd(this, questions);
        Thread backEndThread = new Thread(backEnd);
        backEndThread.start();



        //Already have logInScreen, lobbyEntryScreen, lobbyScreen, registerScreen, questionChoiceScreen, questionTextAreaScreen, waitingAfterQuestionScreen

        guiStage.setScene(LogIn.change(guiStage, this));
        guiStage.setTitle("Base client");
        guiStage.show();



    }


    public static void main(String[] args) {

        launch(args);
    }


}