package baseclient;

import general.Command;
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

import static baseclient.BaseClientBackEnd.addCommand;

public class LogInScreen {

    public static Scene change(BaseClient frontEnd) {

        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //Log in scene. Player has option to log in or register.
        BorderPane logInRoot = new BorderPane();
        Scene logInScreen = new Scene(logInRoot, width, height);
        VBox logIn = new VBox(20);
        logIn.setStyle("-fx-background-color: ROYALBLUE;");

        //If logging failed, then player will see text
        final Label logInError = new Label("");

        TextField usernameInput = new TextField("Username");
        usernameInput.setPrefSize(width/4*3,height/10);
        HBox username = new HBox(usernameInput);

        TextField passwordInput = new TextField("Password");
        passwordInput.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/10);
        HBox password = new HBox(passwordInput);

        Button logInButton = new Button("Log In");
        //Event handler: clicking on log in button. Checking if username and password match.
        logInButton.setOnAction(actionEvent -> {
            // Send the command to client backend.
            // TODO: For some reason this gets called twice.
            addCommand(121, new String[]{usernameInput.getText(), passwordInput.getText()}, 0);
            // Wait for result

//                //if username and password match
//                if (true) {
//                    primaryStage.setScene(LobbyEntry.change(primaryStage, frontEnd));
//                } else {
//                    logInError.setText("Incorrect username/password");
//                }
        });

        HBox logInScreenButtons;
        if (frontEnd.type != ClientType.PRESENTER) {
            Button registrationButton = new Button("Register");
            //Event handler: clicking on "register" button
            registrationButton.setOnAction(actionEvent -> {
                // command: change to Register
                frontEnd.addCommandAndInvoke(new Command(136, new String[0]));
            });
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

        return logInScreen;

    }


}