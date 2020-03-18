package playerclient;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PlayerClient extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.ROYALBLUE);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Player client");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
