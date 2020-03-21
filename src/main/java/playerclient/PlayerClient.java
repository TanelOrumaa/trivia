package playerclient;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;


public class PlayerClient extends Application {

    double width = 350;
    double height = 561.5;

    @Override
    public void start(final Stage primaryStage) {


        //Lobby scene, where player is waiting for start.
        BorderPane lobbyRoot = new BorderPane();
        final Scene lobbyScreen = new Scene(lobbyRoot, width, height);
        VBox lobby = new VBox(20);
        lobby.setStyle("-fx-background-color: ROYALBLUE;");

        lobby.setAlignment(Pos.CENTER);
        lobby.getChildren().addAll();

        lobbyRoot.setCenter(lobby);

        //It is a scene, where player has to enter code to join a lobby.
        BorderPane lobbyEntryRoot = new BorderPane();
        final Scene lobbyEntryScreen = new Scene(lobbyEntryRoot, width, height);
        VBox lobbyEntry = new VBox(20);
        lobbyEntry.setStyle("-fx-background-color: ROYALBLUE;");

        final Label lobbyEntryError = new Label("");

        final TextField lobbyEntryCodeInput = new TextField("Enter the code");
        HBox lobbyEntryCode = new HBox(lobbyEntryCodeInput);

        Button lobbyEntryButton = new Button("Enter to lobby");
        lobbyEntryButton.setOnAction(lobbyEntryButtonAction(lobbyEntryCodeInput.getText(), primaryStage, lobbyScreen, lobbyEntryError));
        HBox lobbyEntryButtons = new HBox(lobbyEntryButton);

        lobbyEntryButtons.setAlignment(Pos.CENTER);
        lobbyEntryCode.setAlignment(Pos.CENTER);
        lobbyEntry.setAlignment(Pos.CENTER);
        lobbyEntry.getChildren().addAll(lobbyEntryError, lobbyEntryCode, lobbyEntryButtons);

        lobbyEntryRoot.setCenter(lobbyEntry);

        //Log in scene. Player has option to log in or register.
        BorderPane logInRoot = new BorderPane();
        Scene logInScreen = new Scene(logInRoot, width, height);
        VBox logIn = new VBox(20);
        logIn.setStyle("-fx-background-color: ROYALBLUE;");

        final Label logInError = new Label("");

        TextField usernameInput = new TextField("Username");
        usernameInput.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/10);
        HBox username = new HBox(usernameInput);

        TextField passwordInput = new TextField("Password");
        passwordInput.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/10);
        HBox password = new HBox(passwordInput);

        Button logInButton = new Button("Log In");
        logInButton.setOnAction(logInButtonAction(primaryStage, lobbyEntryScreen, logInError));
        Button registerButton = new Button("Register");
        HBox logInScreenButtons = new HBox(20, logInButton, registerButton);

        logIn.setAlignment(Pos.CENTER);
        username.setAlignment(Pos.CENTER);
        password.setAlignment(Pos.CENTER);
        logInScreenButtons.setAlignment(Pos.CENTER);
        logIn.getChildren().addAll(logInError, username, password,logInScreenButtons);

        logInRoot.setCenter(logIn);

        //Multiple-choice question. Player choose one correct answer.
        ScrollPane questionChoiceScroller = new ScrollPane();
        questionChoiceScroller.setFitToWidth(true);

        Scene questionChoiceScreen = new Scene(questionChoiceScroller, width, height);
        VBox questionChoice = new VBox(20);

        Label questionChoiceQuestionText = new Label("Question");
        questionChoiceQuestionText.setWrapText(true);
        HBox questionChoiceQuestion = new HBox(questionChoiceQuestionText);
        questionChoiceQuestion.setPadding(new Insets(15));

        int questionChoiceNumber = 5;
        ArrayList<Button> questionChoiceOptions = new ArrayList<Button>();
        for (int i = 0; i < questionChoiceNumber; i++) {
            Button questionChoiceOptionButton = new Button(Integer.toString(i));
            questionChoiceOptionButton.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/10);
            questionChoiceOptionButton.setOnAction(questionChoiceOptionButtonAction(i));
            questionChoiceOptions.add(questionChoiceOptionButton);
        }

        questionChoice.getChildren().add(questionChoiceQuestion);
        questionChoice.getChildren().addAll(questionChoiceOptions);

        questionChoiceQuestion.setAlignment(Pos.CENTER);
        questionChoice.setAlignment(Pos.CENTER);

        questionChoiceScroller.setContent(questionChoice);


        //Text area question. Player writes answer.
        BorderPane questionTextAreaRoot = new BorderPane();
        Scene questionTextAreaScreen = new Scene(questionTextAreaRoot, width, height);
        VBox questionTextArea = new VBox(20);

        Label questionTextAreaQuestionText = new Label("Question");
        questionTextAreaQuestionText.setWrapText(true);
        HBox questionTextAreaQuestion = new HBox(questionTextAreaQuestionText);

        TextArea questionTextAreaInput = new TextArea();
        questionTextAreaInput.setPrefSize(logInScreen.getWidth()/4*3,logInScreen.getHeight()/5);
        questionTextAreaInput.setWrapText(true);
        HBox questionTextAreaInputBox = new HBox(questionTextAreaInput);

        Button questionTextAreaButton = new Button("Send");
        HBox questionTextAreaButtonBox = new HBox(questionTextAreaButton);


        questionTextArea.getChildren().addAll(questionTextAreaQuestion, questionTextAreaInputBox, questionTextAreaButtonBox);

        questionTextAreaQuestion.setAlignment(Pos.CENTER);
        questionTextAreaInputBox.setAlignment(Pos.CENTER);
        questionTextAreaButtonBox.setAlignment(Pos.CENTER);
        questionTextArea.setAlignment(Pos.CENTER);

        questionTextAreaRoot.setCenter(questionTextArea);

        primaryStage.setScene(logInScreen);
        primaryStage.setTitle("Player client");
        primaryStage.show();
    }


    //Event handler: clicking on log in button. Checking if username and password match.
    EventHandler<ActionEvent> logInButtonAction(final Stage primaryStage, final Scene lobbyEntryScreen, final Label logInError) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (true) {
                    primaryStage.setScene(lobbyEntryScreen);
                } else {
                    logInError.setText("Incorrect username/password");
                }
            }
        };
    }

    //Event handler: entering code to join a lobby. Check if the lobby with this code exists.
    EventHandler<ActionEvent> lobbyEntryButtonAction(String lobbycode, final Stage primaryStage, final Scene lobbyScreen, final Label lobbyEntryError) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if (true) {
                    primaryStage.setScene(lobbyScreen);
                } else {
                    lobbyEntryError.setText("Something went wrong");
                }
            }
        };
    }

    //Event handler: multiple-choice question's: get index of button that was clicked, and should send it to backend.
    EventHandler<ActionEvent> questionChoiceOptionButtonAction(final int i) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println(i);
            }
        };
    }


    public static void main(String[] args) {
        launch(args);
    }
}
