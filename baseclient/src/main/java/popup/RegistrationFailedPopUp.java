package popup;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class RegistrationFailedPopUp {

    public static Popup getPopup(){

        Popup popup = new Popup();
        VBox mainBox = new VBox();
        mainBox.setStyle("-fx-background-color: ROYALBLUE;");

        Label errorLabel = new Label("Registration failed - username already exists!");

        Button okButton = new Button("OK");
        okButton.setOnAction(actionEvent -> {
            popup.hide();
        });

        mainBox.getChildren().addAll(errorLabel, okButton);
        popup.getContent().add(mainBox);

        return popup;
    }
}
