package view;

import baseclient.BaseClient;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import popup.ErrorMessage;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class RegistrationScreen extends Scene {

    public RegistrationScreen(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        //Registration scene
        BorderPane registerRoot = new BorderPane();
        VBox register = new VBox(20);
        register.setStyle("-fx-background-color: ROYALBLUE;");

        //If registration failed, then player will see text with error
        Label conditions = new Label("Username must be at least 6 symbols long.\n" +
                "Password must: \n" +
                "be at least 8 symbols long.\n" +
                "have lower and uppercase letters.\n");

        Label usernameLabel = new Label("Enter username:");
        TextField newUsernameInput = new TextField();
        newUsernameInput.setPrefSize(width/4*3,height/12);
        VBox newUsernameBox = new VBox(usernameLabel, newUsernameInput);

        Label passwordLabel = new Label("Enter password:");
        PasswordField newPasswordInput = new PasswordField();
        newPasswordInput.setPrefSize(width/4*3,height/12);
        VBox newPasswordBox = new VBox(passwordLabel, newPasswordInput);

        Label confirmPasswordLabel = new Label("Confirm password:");
        PasswordField confirmPasswordInput = new PasswordField();
        confirmPasswordInput.setPrefSize(width/4*3, height/12);
        VBox confirmPasswordBox = new VBox(confirmPasswordLabel, confirmPasswordInput);

        Label nicknameLabel = new Label("Enter nickname (visible to other players):");
        TextField newNicknameInput = new TextField();
        newNicknameInput.setPrefSize(width/4*3, height/12);
        VBox newNicknameBox = new VBox(nicknameLabel, newNicknameInput);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(actionEvent -> {
            //Firstly check if username, password and nickname match criteria
            String newUsername = newUsernameInput.getText();
            String newPassword = newPasswordInput.getText();
            String confirmPassword = confirmPasswordInput.getText();
            String newNickname = newNicknameInput.getText();

            if (!newPassword.equals(confirmPassword)){
                ErrorMessage.popUp("Passwords don't match!");
                return;
            }

            if (newPassword.length() < 8){
                ErrorMessage.popUp("Password must be at least 8 symbols long!");
                return;
            }

            boolean hasUpper = false;
            boolean hasLower = false;
            for (char symbol : newPassword.toCharArray()) {
                if (Character.isUpperCase(symbol)) hasUpper = true;
                if (Character.isLowerCase(symbol)) hasLower = true;
            }
            if (!hasUpper || !hasLower){
                ErrorMessage.popUp("Password should have both lower and uppercase letters!");
                return;
            }

            if (newUsername.length() < 6){
                ErrorMessage.popUp("Username must be at least 6 symbols long!");
                return;
            }

            if (newNickname.length() < 1){
                ErrorMessage.popUp("Nickname can't be empty!");
                return;
            }

            // ask back-end to send the registration info to server
            addCommandToBackEnd(123, new String[] {newUsername, newPassword, newNickname}, 0);
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(actionEvent -> baseClient.setGuiStage(new LogInScreen(baseClient)));

        HBox buttonsBox = new HBox(20, backButton, registerButton);


        newUsernameBox.setAlignment(Pos.CENTER);
        newPasswordBox.setAlignment(Pos.CENTER);
        confirmPasswordBox.setAlignment(Pos.CENTER);
        newNicknameBox.setAlignment(Pos.CENTER);
        register.setAlignment(Pos.CENTER);
        buttonsBox.setAlignment(Pos.CENTER);
        register.getChildren().addAll(conditions, newUsernameBox, newPasswordBox, confirmPasswordBox, newNicknameBox, buttonsBox);

        registerRoot.setCenter(register);

        super.setRoot(registerRoot);

    }
}
