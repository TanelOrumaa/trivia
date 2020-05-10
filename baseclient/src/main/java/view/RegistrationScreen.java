package view;

import baseclient.BaseClient;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import style.Styles;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class RegistrationScreen extends Scene {

    public RegistrationScreen(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);

        //Registration scene
        BorderPane registerRoot = style.getStandardBorderPane();
        VBox register = style.getStandardVBox();

        // Label with username/password requirements.
        Label conditions = style.getInfoLabel(new String[] {"Username must be:", "at least 6 symbols long.", "Password must:", "\tbe at least 8 symbols long", "\thave lower and uppercase letters", "Nickname is visible to other players."}, 8d/10, 3d/10);

        // Username field.
        Label usernameLengthError = style.getFieldErrorLabel(new String[] {"Must be at least 6 symbols."}, 8d/10, 1d/10);
        Label usernameFormatError = style.getFieldErrorLabel(new String[] {"Username can only contain", "letters and numbers."}, 8d/10, 2d/10);
        TextField newUsernameInput = style.getRegularTextField("Enter username", 8d/10, 1d/10);

        VBox usernameArea = new VBox();
        usernameArea.getChildren().addAll(newUsernameInput);

        // Password field.
        Label passwordLengthError = style.getFieldErrorLabel(new String[] {"Must be at least 8 symbols."}, 8d/10, 1d/10);
        Label passwordFormatError = style.getFieldErrorLabel(new String[] {"Must contain both upper", "and lowercase letters."}, 8d/10, 2d/10);
        PasswordField newPasswordInput = style.getRegularPasswordField("Enter password", 8d/10, 1d/10);

        VBox passwordArea = new VBox();
        passwordArea.getChildren().addAll(newPasswordInput);


        // Password confirmation field
        Label passwordsDontMatchError = style.getFieldErrorLabel(new String[] {"Passwords don't match."}, 8d/10, 1d/10);
        PasswordField confirmPasswordInput = style.getRegularPasswordField("Confirm password", 8d/10, 1d/10);

        VBox passwordConfirmationArea = new VBox();
        passwordConfirmationArea.getChildren().addAll(confirmPasswordInput);

        // Nickname field.
        Label nicknameLengthError = style.getFieldErrorLabel(new String[] {"Nickname can't be empty."}, 8d/10, 1d/10);
        TextField newNicknameInput = style.getRegularTextField("Enter nickname", 8d/10, 1d/10);

        VBox nicknameArea = new VBox();
        nicknameArea.getChildren().addAll(newNicknameInput);

        // Buttons
        Button registerButton = style.getRegularButton("Create", 2d/5, 2d/15);

        Button backButton = style.getNeutralButton("Back", 2d/5, 2d/15);

        HBox buttonsBox = new HBox(20, backButton, registerButton);

        registerButton.setOnAction(actionEvent -> {
            //Firstly check if username, password and nickname match criteria
            String newUsername = newUsernameInput.getText();
            String newPassword = newPasswordInput.getText();
            String confirmPassword = confirmPasswordInput.getText();
            String newNickname = newNicknameInput.getText();
            boolean canRegister = true;

            usernameArea.getChildren().clear();

            // Check username length.
            if (newUsername.length() < 6){
                usernameArea.getChildren().add(usernameLengthError);
                canRegister = false;
            }

            // Check username format
            boolean usernameIsValid = true;
            for (char letter : newUsername.toCharArray()) {
                if (!Character.isAlphabetic(letter) && !Character.isDigit(letter)) {
                    usernameIsValid = false;
                    break;
                }
            }
            if (!usernameIsValid) {
                usernameArea.getChildren().add(usernameFormatError);
                canRegister = false;
            }

            usernameArea.getChildren().add(newUsernameInput);

            passwordArea.getChildren().clear();

            // Check password length.
            if (newPassword.length() < 8){
                passwordArea.getChildren().add(passwordLengthError);
                canRegister = false;
            }

            // Check password format.
            boolean hasUpper = false;
            boolean hasLower = false;
            for (char symbol : newPassword.toCharArray()) {
                if (Character.isUpperCase(symbol)) hasUpper = true;
                if (Character.isLowerCase(symbol)) hasLower = true;
            }
            if (!hasUpper || !hasLower){
                passwordArea.getChildren().add(passwordFormatError);
                canRegister = false;
            }

            passwordArea.getChildren().add(newPasswordInput);

            passwordConfirmationArea.getChildren().clear();

            // Check that passwords match.
            if (!newPassword.equals(confirmPassword)){
                passwordConfirmationArea.getChildren().add(passwordsDontMatchError);
                canRegister = false;
            }

            passwordConfirmationArea.getChildren().add(confirmPasswordInput);

            nicknameArea.getChildren().clear();

            // Check nickname length.
            if (newNickname.length() < 1){
                nicknameArea.getChildren().add(nicknameLengthError);
                canRegister = false;
            }

            nicknameArea.getChildren().add(newNicknameInput);

            if (canRegister) {
                // Ask back-end to send the registration info to server
                addCommandToBackEnd(123, new String[] {newUsername, newPassword, newNickname}, 0);
            }
        });

        backButton.setOnAction(actionEvent -> baseClient.setGuiStage(new LogInScreen(baseClient)));


        usernameArea.setAlignment(Pos.CENTER);
        passwordArea.setAlignment(Pos.CENTER);
        passwordConfirmationArea.setAlignment(Pos.CENTER);
        nicknameArea.setAlignment(Pos.CENTER);
        register.setAlignment(Pos.CENTER);
        buttonsBox.setAlignment(Pos.CENTER);
        register.getChildren().addAll(conditions, usernameArea, passwordArea, passwordConfirmationArea, nicknameArea, buttonsBox);

        registerRoot.setCenter(register);

        // Remove focus from username field at application start.
        Platform.runLater(registerRoot::requestFocus);

        super.setRoot(registerRoot);

    }
}
