package view;

import baseclient.BaseClient;
import baseclient.ClientType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class LogInScreen extends Scene {

    public LogInScreen(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        BorderPane logInRoot = new BorderPane();
        VBox logIn = new VBox(20);
        logIn.setStyle("-fx-background-color: ROYALBLUE;");

        Label usernameLabel = new Label("Username:");
        TextField usernameInput = new TextField();
        usernameInput.setPrefSize(width/4*3,height/12);
        VBox usernameBox = new VBox(usernameLabel, usernameInput);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPrefSize(super.getWidth()/4*3,super.getHeight()/12);
        VBox passwordBox = new VBox(passwordLabel, passwordInput);

        passwordInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                addCommandToBackEnd(121, new String[]{usernameInput.getText(), passwordInput.getText()}, 0);
            }
        });

        Button logInButton = new Button("Log In");
        //Event handler: clicking on log in button. Checking if username and password match.
        logInButton.setOnAction(actionEvent -> {
            // Send the command to client backend.
            addCommandToBackEnd(121, new String[]{usernameInput.getText(), passwordInput.getText()}, 0);
        });

        HBox logInScreenButtons;
        if (baseClient.getType() != ClientType.PRESENTER) {
            Button registrationButton = new Button("Register");
            //Event handler: clicking on "register" button
            registrationButton.setOnAction(actionEvent -> baseClient.setGuiStage(new RegistrationScreen(baseClient)));
            logInScreenButtons = new HBox(20, logInButton, registrationButton);
        }
        else {
            logInScreenButtons = new HBox(20, logInButton);
        }


        logIn.setAlignment(Pos.CENTER);
        usernameBox.setAlignment(Pos.CENTER);
        passwordBox.setAlignment(Pos.CENTER);
        logInScreenButtons.setAlignment(Pos.CENTER);
        logIn.getChildren().addAll(usernameBox, passwordBox, logInScreenButtons);

        logInRoot.setCenter(logIn);

        super.setRoot(logInRoot);

    }


}
