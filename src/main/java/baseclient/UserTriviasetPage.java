package baseclient;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class UserTriviasetPage extends Scene {

    static final Logger LOG = LoggerFactory.getLogger(UserTriviasetPage.class);

    private static final Font textFont = Font.font("Berlin Sans FB Demi", 20);
    private static ObservableList<String> triviasetsList;

    public UserTriviasetPage(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        //It is a scene, where player has to enter code to join a lobby.
        BorderPane triviasetsPageRoot = new BorderPane();
        VBox triviasetsPageLayout = new VBox(20);
        triviasetsPageLayout.setStyle("-fx-background-color: ROYALBLUE;");


        final Label usernameLabel = new Label(BaseClient.username);
        usernameLabel.setFont(textFont);
        HBox usernameLabelArea = new HBox(usernameLabel);
        usernameLabelArea.setAlignment(Pos.TOP_RIGHT);
        usernameLabelArea.setPadding(new Insets(width * 0.05, width * 0.05, width * 0.05, width * 0.05));

        triviasetsList = FXCollections.observableArrayList(BaseClient.triviasets);

        VBox triviaSetsArea = new VBox();
        ArrayList<HBox> triviasetsRows = new ArrayList<>();

        updateTriviasetsArea(triviasetsRows);

        triviaSetsArea.getChildren().addAll(triviasetsRows);

        triviasetsList.addListener((ListChangeListener<String>) observable -> {
            updateTriviasetsArea(triviasetsRows);
            if (triviaSetsArea.getChildren().size() == 0) {
                triviaSetsArea.getChildren().addAll(triviasetsRows);
            }
        });


        Button back = new Button("Back");
        back.setOnMouseReleased(mouseEvent -> {
            BaseClient.guiStage.setScene(new UserMainPage(baseClient));
        });

        HBox buttonArea = new HBox(back);
        buttonArea.setAlignment(Pos.CENTER);

        triviasetsPageLayout.getChildren().addAll(usernameLabelArea, triviaSetsArea, buttonArea);

        triviasetsPageRoot.setTop(triviasetsPageLayout);

        super.setRoot(triviasetsPageRoot);
    }

    private void updateTriviasetsArea(ArrayList<HBox> triviasetsRows) {
        for (String triviaset : BaseClient.triviasets) {
            Label triviasetName = new Label(triviaset);
            // TODO: Remove hardcoded values.
            triviasetName.setPadding(new Insets(0, 0, 0, 10));
            Button host = new Button("Host");
            host.setOnMouseReleased(mouseEvent -> {
                addCommandToBackEnd(133, new String[] {triviaset}, 0);
            });

            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);

            HBox triviasetRow = new HBox(triviasetName, region, host);
            triviasetName.setAlignment(Pos.CENTER_LEFT);
            host.setAlignment(Pos.CENTER_RIGHT);
            triviasetRow.setAlignment(Pos.CENTER);
            triviasetRow.setBorder(new Border(new BorderStroke(Paint.valueOf("black"), BorderStrokeStyle.SOLID, new CornerRadii(0.5), BorderWidths.DEFAULT)));

            triviasetsRows.add(triviasetRow);
        }

    }

    public static void updateTriviasetsList() {
        triviasetsList.setAll(BaseClient.triviasets);
        LOG.debug("Participants list updated.");
    }}
