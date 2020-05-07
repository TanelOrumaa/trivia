import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class PresenterClient extends Application {
    BlockingQueue<Integer> questionTypes = new ArrayBlockingQueue<Integer>(1);
    BlockingQueue<String> questions = new ArrayBlockingQueue<String>(100);

    @Override
    public void start(final Stage primaryStage) throws Exception {

        /*
        TextField connection = new TextField();

        connection.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                System.out.println(s);
                System.out.println(t1);
                int orderType = Integer.parseInt(t1);
                switch (orderType) {
                    case 0:
                        System.out.println("order 0");
                        showStartScreen(primaryStage);
                        break;
                    case 1:
                        System.out.println("order 1");
                        showNextQuestion(primaryStage);
                        break;
                    case 2:
                        System.out.println("order 2");
                        showLeaderboard(primaryStage);
                        break;
                }
            }
        });

         */



        PresenterClientBackEnd backEnd = new PresenterClientBackEnd(primaryStage, questionTypes, questions);
        Thread backEndThread = new Thread(backEnd);
        backEndThread.start();



        Group root = new Group();
        Circle ajutine = new Circle(100, 100, 20, Color.RED);
        ajutine.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {
                showNextQuestion(primaryStage);
            }
        });
        root.getChildren().add(ajutine);
        Scene scene = new Scene(root, 800, 600, Color.BLANCHEDALMOND);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Presenter client");
        primaryStage.show();





    }


    public static void main(String[] args) {

        Application.launch(args);
    }

    static void showStartScreen(Stage primaryStage) {
        System.out.println("showStartScreen");
        Group startScreenRoot = new Group();

        Scene startScreenScene = new Scene(startScreenRoot, 800, 600, Color.GREEN);
        primaryStage.setScene(startScreenScene);
    }


    void showNextQuestion(Stage primaryStage) {
        Integer questionType = questionTypes.poll();
        String question = questions.poll();
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
