package view;

import baseclient.BaseClient;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class RegistrationSuccessfulScreen {
    static final Font font = Font.font("Berlin Sans FB Demi", 20);

    public static Scene change(BaseClient baseClient){

        double width = baseClient.getWidth();
        double heigth = baseClient.getHeight();


        VBox mainBox = new VBox();

        Label successLabel = new Label("Registration successful!");
        successLabel.setFont(font);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(actionEvent -> baseClient.setGuiStage(new LogInScreen(baseClient)));

        HBox loginButtonBox = new HBox(loginButton);
        mainBox.getChildren().addAll(successLabel, loginButtonBox);

        mainBox.setAlignment(Pos.CENTER);
        successLabel.setAlignment(Pos.CENTER);
        loginButtonBox.setAlignment(Pos.BOTTOM_CENTER);

        return new Scene(mainBox, width, heigth);
    }
}
