package view;

import baseclient.BaseClient;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import style.Styles;
import triviaset.TriviaSet;

import static baseclient.BaseClientBackEnd.addCommandToBackEnd;

public class TriviaSetDetailView extends Scene {

    public TriviaSetDetailView(BaseClient baseClient, TriviaSet triviaSet) {
        super(new BorderPane(), baseClient.getWidth(), baseClient.getHeight());

        double width = baseClient.getWidth();
        double height = baseClient.getHeight();

        Styles style = new Styles(width, height);

        // Header
        HBox header = style.getHeader(baseClient);

        BorderPane triviaSetDetailsRoot = style.getStandardBorderPane();
        final VBox triviaSetDetails = style.getStandardVBox();

        // Trivia set title
        Label triviaSetTitleLabel = style.getH3TitleLabel(new String[] {triviaSet.getName()}, 8d/10, 2d/15);

        // Buttons below the title
        Button host = style.getRegularButton("Host", 4d/10, 1d/10);
        host.setOnMouseClicked(mouseEvent -> addCommandToBackEnd(133, new String[] {Long.toString(triviaSet.getId())}, 0));

        Button edit = style.getRegularButton("Edit", 4d/10, 1d/10);
        edit.setOnMouseClicked(mouseEvent -> baseClient.setGuiStage(new AddTriviaSetFX(baseClient, triviaSet)));

        HBox buttonsArea = style.getStandardHBox();
        buttonsArea.getChildren().addAll(host, edit);

        // Questions list.
        VBox questionsList = style.getStandardVBox();
        ScrollPane questionsScrollable = style.getStandardScrollPane();
        triviaSet.getQuestionMap().forEach(((id, question) -> {
            Label questionLabel = style.getH5TitleLabel(new String[] {question.getQuestion()}, 8d/10, 1d/10);
            questionsList.getChildren().add(questionLabel);
        }));

        questionsScrollable.setContent(questionsList);
        HBox questionsPanel = style.getStandardHBox();

        questionsPanel.getChildren().add(questionsScrollable);

        // Back button
        Button backButton = style.getNeutralButton("Back", 4d/15, 1d/10);
        backButton.setOnMouseClicked(mouseEvent -> baseClient.setGuiStage(new UserTriviasetPage(baseClient)));
        HBox bottomButtons = style.getStandardHBox();
        bottomButtons.getChildren().addAll(backButton);

        // Alignments
        triviaSetTitleLabel.setAlignment(Pos.CENTER);
        buttonsArea.setAlignment(Pos.CENTER);
        questionsPanel.setAlignment(Pos.CENTER);
        bottomButtons.setAlignment(Pos.CENTER);

        triviaSetDetails.getChildren().addAll(triviaSetTitleLabel, buttonsArea, questionsPanel);

        triviaSetDetailsRoot.setTop(header);
        triviaSetDetailsRoot.setCenter(triviaSetDetails);
        BorderPane.setMargin(bottomButtons, style.getTopAndBottomMargin());
        triviaSetDetailsRoot.setBottom(bottomButtons);

        super.setRoot(triviaSetDetailsRoot);
    }
}
