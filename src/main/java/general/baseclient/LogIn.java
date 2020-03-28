package general.baseclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

public class LogIn {

    public static Scene change(Stage primaryStage) {

        double width = PlayerClient.getWidth();
        double height = PlayerClient.getHeight();

        //Log in scene. Player has option to log in or register.
        BorderPane logInRoot = new BorderPane();
        Scene logInScreen = new Scene(logInRoot, width, height);
        VBox logIn = new VBox(20);
        logIn.setStyle("-fx-background-color: ROYALBLUE;");

        //If logging failed, then player will see text
        final Label logInError = new Label("");

        TextField usernameInput = new TextField("Username");
        usernameInput.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/10);
        HBox username = new HBox(usernameInput);

        TextField passwordInput = new TextField("Password");
        passwordInput.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/10);
        HBox password = new HBox(passwordInput);

        Button logInButton = new Button("Log In");
        logInButton.setOnAction(logInButtonAction(primaryStage, logInError));
        Button registrationButton = new Button("Register");
        registrationButton.setOnAction(registrationButtonAction(primaryStage));
        HBox logInScreenButtons = new HBox(20, logInButton, registrationButton);

        logIn.setAlignment(Pos.CENTER);
        username.setAlignment(Pos.CENTER);
        password.setAlignment(Pos.CENTER);
        logInScreenButtons.setAlignment(Pos.CENTER);
        logIn.getChildren().addAll(logInError, username, password, logInScreenButtons);

        logInRoot.setCenter(logIn);

        return logInScreen;

    }

    //Event handler: clicking on log in button. Checking if username and password match.
    static EventHandler<ActionEvent> logInButtonAction(final Stage primaryStage, final Label logInError) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //if username and password match
                if (true) {
                    primaryStage.setScene(LobbyEntry.change(primaryStage));
                } else {
                    logInError.setText("Incorrect username/password");
                }
            }
        };
    }

    //Event handler: clicking on "register" button
    static EventHandler<ActionEvent> registrationButtonAction(final Stage primaryStage) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                primaryStage.setScene(Register.change(primaryStage));
            }
        };
    }

}
