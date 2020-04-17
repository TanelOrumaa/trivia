package baseclient;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static baseclient.BaseClientBackEnd.addCommand;

public class RegistrationScreen {

    public static Scene change(BaseClient frontEnd) {

        double width = frontEnd.getWidth();
        double height = frontEnd.getHeight();

        //Registration scene
        BorderPane registerRoot = new BorderPane();
        Scene registerScreen = new Scene(registerRoot, width, height);
        VBox register = new VBox(20);
        register.setStyle("-fx-background-color: ROYALBLUE;");

        //If registration failed, then player will see text with error
        Label registerError = new Label("");

        TextField newUsernameInput = new TextField("Enter username:");
        newUsernameInput.setPrefSize(width/4*3,height/10);
        HBox newUsernameBox = new HBox(newUsernameInput);

        TextField newPasswordInput = new TextField("Enter password:");
        newPasswordInput.setPrefSize(width/4*3,height/10);
        HBox newPasswordBox = new HBox(newPasswordInput);

        TextField newNicknameInput = new TextField("Enter nickname (visible to other players):");
        newNicknameInput.setPrefSize(width/4*3, height/10);
        HBox newNicknameBox = new HBox(newNicknameInput);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(actionEvent -> {
            //Firstly check if username, password and nickname match criteria
            String newUsername = newUsernameInput.getText();
            String newPassword = newPasswordInput.getText();
            String newNickname = newNicknameInput.getText();
            if (newPassword.length() < 8){
                registerError.setText("Password must be at least 8 symbols long!");
                return;
            }

            boolean hasUpper = false;
            boolean hasLower = false;
            for (char symbol : newPassword.toCharArray()) {
                if (Character.isUpperCase(symbol)) hasUpper = true;
                if (Character.isLowerCase(symbol)) hasLower = true;
            }
            if (!hasUpper || !hasLower){
                registerError.setText("Password should have both lower and uppercase letters!");
                return;
            }

            if (newUsername.length() < 6){
                registerError.setText("Username must be at least 6 symbols long!");
                return;
            }

            if (newNickname.length() < 1){
                registerError.setText("Nickname can't be empty!");
                return;
            }

            // ask back-end to send the registration info to server
            addCommand(144, new String[] {newUsername, newPassword, newNickname}, 0);
        });

        HBox registerButtonBox = new HBox(20, registerButton);


        newUsernameBox.setAlignment(Pos.CENTER);
        newPasswordBox.setAlignment(Pos.CENTER);
        newNicknameBox.setAlignment(Pos.CENTER);
        register.setAlignment(Pos.CENTER);
        registerButtonBox.setAlignment(Pos.CENTER);
        register.getChildren().addAll(registerError, newUsernameBox, newPasswordBox, newNicknameBox, registerButtonBox);

        registerRoot.setCenter(register);

        return registerScreen;

    }
}
