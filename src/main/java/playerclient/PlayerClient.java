package playerclient;

import javafx.application.Application;
import javafx.stage.Stage;



public class PlayerClient extends Application {

    public static double getWidth() {
        double width = 350;
        return width;
    }

    public static double getHeight() {
        double height = 561.5;
        return height;
    }

    @Override
    public void start(final Stage primaryStage) {

        //Already have logInScreen, lobbyEntryScreen, lobbyScreen, registerScreen, questionChoiceScreen, questionTextAreaScreen, waitingAfterQuestionScene
        //
        primaryStage.setScene(LogIn.change(primaryStage));
        primaryStage.setTitle("Player client");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
