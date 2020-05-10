package view;

import baseclient.BaseClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import style.Styles;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class LogInScreen extends Scene {

    public LogInScreen(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        // Create styles file depending on window size.
        Styles style = new Styles(width, height);

        BorderPane logInRoot = style.getStandardBorderPane();
        VBox logIn = style.getStandardVBox();

        // Textfields
        TextField usernameInput = style.getRegularTextField("Username", 8d/10, 1d/10);

        PasswordField passwordInput = style.getRegularPasswordField("Password", 8d/10, 1d/10);

        passwordInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addCommandToBackEnd(121, new String[]{usernameInput.getText(), passwordInput.getText()}, 0);
            }
        });

        // Buttons
        HBox logInScreenButtons;

        final Button logInButton = style.getRegularButton("Log in", 2d/5, 2d/15);

        logInButton.setOnAction(actionEvent -> {
            // Send the command to client backend.
            addCommandToBackEnd(121, new String[]{usernameInput.getText(), passwordInput.getText()}, 0);
        });

        Button registrationButton = style.getRegularButton("Register", 2d/5, 2d/15);
        //Event handler: clicking on "register" button
        registrationButton.setOnAction(actionEvent -> baseClient.setGuiStage(new RegistrationScreen(baseClient)));
        logInScreenButtons = new HBox(20, logInButton, registrationButton);

        // Alignments.
        logIn.setAlignment(Pos.CENTER);
        usernameInput.setAlignment(Pos.CENTER);
        passwordInput.setAlignment(Pos.CENTER);
        logInScreenButtons.setAlignment(Pos.CENTER);
        logIn.getChildren().addAll(usernameInput, passwordInput, logInScreenButtons);

        logInRoot.setCenter(logIn);

        // Remove focus from username field at application start.
        Platform.runLater(logInRoot::requestFocus);

        super.setRoot(logInRoot);

    }


}
