package baseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class LogInScreen extends Scene {

    public LogInScreen(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        //Log in scene. Player has option to log in or register.
        BorderPane logInRoot = new BorderPane();
        VBox logIn = new VBox(20);
        logIn.setStyle("-fx-background-color: ROYALBLUE;");

        //If logging failed, then player will see text
        final Label logInError = new Label("");

        TextField usernameInput = new TextField("Username");
        usernameInput.setPrefSize(width/4*3,height/10);
        HBox username = new HBox(usernameInput);

        TextField passwordInput = new TextField("Password");
        passwordInput.setPrefSize(super.getWidth()/4*3,super.getHeight()/10);
        HBox password = new HBox(passwordInput);

        Button logInButton = new Button("Log In");
        //Event handler: clicking on log in button. Checking if username and password match.
        logInButton.setOnAction(actionEvent -> {
            // Send the command to client backend.
            addCommandToBackEnd(121, new String[]{usernameInput.getText(), passwordInput.getText()}, 0);
        });

        HBox logInScreenButtons;
        if (baseClient.type != ClientType.PRESENTER) {
            Button registrationButton = new Button("Register");
            //Event handler: clicking on "register" button
            registrationButton.setOnAction(actionEvent -> BaseClient.guiStage.setScene(new RegistrationScreen(baseClient)));
            logInScreenButtons = new HBox(20, logInButton, registrationButton);
        }
        else {
            logInScreenButtons = new HBox(20, logInButton);
        }


        logIn.setAlignment(Pos.CENTER);
        username.setAlignment(Pos.CENTER);
        password.setAlignment(Pos.CENTER);
        logInScreenButtons.setAlignment(Pos.CENTER);
        logIn.getChildren().addAll(logInError, username, password, logInScreenButtons);

        logInRoot.setCenter(logIn);

        super.setRoot(logInRoot);

    }


}
