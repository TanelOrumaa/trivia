package general.baseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import playerclient.PlayerClient;

public class Register {

    public static Scene change(Stage primaryStage) {

        double width = PlayerClient.getWidth();
        double height = PlayerClient.getHeight();

        //Registration scene. Player make new account.
        BorderPane registerRoot = new BorderPane();
        Scene registerScreen = new Scene(registerRoot, width, height);
        VBox register = new VBox(20);
        register.setStyle("-fx-background-color: ROYALBLUE;");

        //If registration failed, then player will see text with error
        Label registerError = new Label("");

        TextField newUsernameInput = new TextField("Be creative with your username");
        newUsernameInput.setPrefSize(width/4*3,height/10);
        HBox newUsername = new HBox(newUsernameInput);

        TextField newPasswordInput = new TextField("Password should be strong");
        newPasswordInput.setPrefSize(width/4*3,height/10);
        HBox newPassword = new HBox(newPasswordInput);

        Button registerButton = new Button("Register");

        HBox registerButtons = new HBox(20, registerButton);


        newUsername.setAlignment(Pos.CENTER);
        newPassword.setAlignment(Pos.CENTER);
        register.setAlignment(Pos.CENTER);
        registerButtons.setAlignment(Pos.CENTER);
        register.getChildren().addAll(registerError, newUsername, newPassword, registerButtons);

        registerRoot.setCenter(register);

        return registerScreen;

    }
}
