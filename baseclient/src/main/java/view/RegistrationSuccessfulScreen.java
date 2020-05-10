package view;

import baseclient.BaseClient;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class RegistrationSuccessfulScreen extends Scene {

    static final Font font = Font.font("Berlin Sans FB Demi", 20);

    public RegistrationSuccessfulScreen(BaseClient baseClient, String type){
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double heigth = baseClient.getHeight();


        VBox mainBox = new VBox();


        Label successLabel = null;
        Button continueButton = null;


        switch (type){
            case "user":
                successLabel = new Label("User registration successful!");
                continueButton = new Button("Log in");
                continueButton.setOnAction(actionEvent -> baseClient.setGuiStage(new LogInScreen(baseClient)));
                break;
            case "trivia set":
                successLabel = new Label("Trivia set registration successful!");
                continueButton = new Button("Continue");
                continueButton.setOnAction(actionEvent -> baseClient.setGuiStage(new TriviaSetMenu(baseClient)));
        }
        successLabel.setFont(font);

        HBox continueButtonBox = new HBox(continueButton);
        mainBox.getChildren().addAll(successLabel, continueButtonBox);

        mainBox.setAlignment(Pos.CENTER);
        successLabel.setAlignment(Pos.CENTER);
        continueButtonBox.setAlignment(Pos.BOTTOM_CENTER);

        super.setRoot(mainBox);
    }
}
