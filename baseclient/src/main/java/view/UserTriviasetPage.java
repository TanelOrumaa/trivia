package view;

import baseclient.BaseClient;
import baseclient.BaseClientBackEnd;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import style.Styles;
import triviaset.TriviaSet;

import java.util.ArrayList;
import java.util.List;

public class UserTriviasetPage extends Scene {

    static final Logger LOG = LoggerFactory.getLogger(UserTriviasetPage.class);

    private static ObservableList<TriviaSet> triviasetsList;

    public UserTriviasetPage(BaseClient baseClient) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);

        HBox header = style.getHeader(baseClient);

        BorderPane triviasetsPageRoot = style.getStandardBorderPane();

        triviasetsList = FXCollections.observableArrayList(baseClient.getBasicTriviaSets());

        VBox triviaSetsArea = style.getStandardVBox();
        ArrayList<Button> triviasetsRows = new ArrayList<>();

        updateTriviasetsArea(baseClient, style, baseClient.getBasicTriviaSets(), triviasetsRows);

        triviaSetsArea.getChildren().addAll(triviasetsRows);
        HBox triviaSetsPanel = new HBox();

        // Regions for alignment.
        Region leftRegion = new Region();
        Region rightRegion = new Region();
        HBox.setHgrow(leftRegion, Priority.ALWAYS);
        HBox.setHgrow(rightRegion, Priority.ALWAYS);

        // Scrollpane with triviasets.
        ScrollPane scrollPane = style.getStandardScrollPane();
        scrollPane.setContent(triviaSetsArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        triviaSetsPanel.getChildren().addAll(leftRegion, scrollPane, rightRegion);

        triviasetsList.addListener((ListChangeListener<TriviaSet>) observable -> {
            updateTriviasetsArea(baseClient, style, baseClient.getBasicTriviaSets(), triviasetsRows);
            if (triviaSetsArea.getChildren().size() == 0) {
                triviaSetsArea.getChildren().addAll(triviasetsRows);
            }
        });

        Button createNewTriviaset = style.getRegularButton("Create new", 8d/10, 1d/10);
//        createNewTriviaset.setOnMouseClicked(mouseEvent -> baseClient.setGuiStage());

        Button backButton = style.getNeutralButton("Back", 8d/10, 1d/10);
        backButton.setOnMouseReleased(mouseEvent -> baseClient.setGuiStage(new UserMainPage(baseClient)));

        VBox buttonArea = style.getStandardVBox();
        buttonArea.getChildren().addAll(createNewTriviaset, backButton);

        buttonArea.setAlignment(Pos.CENTER);

        BorderPane.setMargin(buttonArea, style.getTopAndBottomMargin());

        triviasetsPageRoot.setTop(header);
        triviasetsPageRoot.setCenter(triviaSetsPanel);
        triviasetsPageRoot.setBottom(buttonArea);

        super.setRoot(triviasetsPageRoot);
    }

    private void updateTriviasetsArea(BaseClient baseClient, Styles style, List<TriviaSet> triviasets, ArrayList<Button> triviasetsRows) {
        for (TriviaSet triviaset : triviasets) {
            Button triviasetName = style.getRegularButton(triviaset.getName(), 8d/10, 1d/10);
            triviasetName.setOnMouseReleased(mouseEvent -> {
                BaseClientBackEnd.addCommandToBackEnd(213, new String[] {Long.toString(triviaset.getId())}, 0);
            });

//            Region region = new Region();
//            HBox.setHgrow(region, Priority.ALWAYS);
//
//            HBox triviasetRow = new HBox(triviasetName, region, host);
//            triviasetName.setAlignment(Pos.CENTER_LEFT);
//            host.setAlignment(Pos.CENTER_RIGHT);
//            triviasetRow.setAlignment(Pos.CENTER);
//            triviasetRow.setBorder(new Border(new BorderStroke(Paint.valueOf("black"), BorderStrokeStyle.SOLID, new CornerRadii(0.5), BorderWidths.DEFAULT)));

            triviasetsRows.add(triviasetName);
        }

    }

    public static void updateTriviasetsList(List<TriviaSet> triviasets) {
        triviasetsList.setAll(triviasets);
        LOG.debug("Triviasets list updated.");
    }}
