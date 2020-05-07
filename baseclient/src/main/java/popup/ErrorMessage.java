package popup;

import javafx.scene.control.Alert;

public class ErrorMessage {
    public static void popUp(String errorMessage) {
        Alert a =  new Alert(Alert.AlertType.ERROR);
        a.setContentText(errorMessage);
        a.show();
    }
}
