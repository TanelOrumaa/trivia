package style;

import baseclient.BaseClient;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import view.LogInScreen;

public class Styles {

    private static final int STANDARD_FONT_SIZE = 200;

    private double width;
    private double height;
    private double verticalPadding;
    private double horizontalPadding;


    // Fonts
    private final String BERLIN_FONT = "Berlin Sans FB Demi";

    // Insets
    private final Insets buttonInsets;
    private final Insets bottomMargin;
    private final Insets topMargin;
    private final Insets topAndBottomMargin;
    private final Insets thinTopAndBottomMargin;

    // Buttons
    private final String buttonRegular = "-fx-border-width: 2px; -fx-border-color: #0b0e07; -fx-background-color: #2dabf9; -fx-text-fill: #ffffff;";
    private final String buttonRegularHover = "-fx-border-width: 2px; -fx-border-color: #0b0e07; -fx-background-color: #0688fa; -fx-text-fill: #ffffff;";
    private final String buttonRegularPressed = "-fx-border-width: 2px; -fx-border-color: #0b0e07; -fx-background-color: #0061b5; -fx-text-fill: #ffffff;";

    private final String buttonNeutral = "-fx-border-width: 2px; -fx-border-color: #0b0e07; -fx-background-color: #4A525A; -fx-text-fill: #ffffff;";
    private final String buttonNeutralHover = "-fx-border-width: 2px; -fx-border-color: #0b0e07; -fx-background-color: #2dabf9; -fx-text-fill: #ffffff;";
    private final String buttonNeutralPressed = "-fx-border-width: 2px; -fx-border-color: #0b0e07; -fx-background-color: #0688fa; -fx-text-fill: #ffffff;";

    private final String buttonBorderless = "-fx-border-width: 0; -fx-background-color: #2dabf9; -fx-text-fill: #2dabf9; -fx-border-radius: 0;";
    private final String buttonBorderlessHover = "-fx-border-width: 0; -fx-background-color: #0688fa; -fx-text-fill: #2dabf9; -fx-border-radius: 0;";
    private final String buttonBorderlessPressed = "-fx-border-width: 0; -fx-background-color: #0061b5; -fx-text-fill: #0688fa; -fx-border-radius: 0;";

    // Text fields
    private final String textfieldRegular = "-fx-border-width: 2px; -fx-border-color: #2dabf9; -fx-background-color: #ffffff; -fx-text-fill: #4A525A;";
    private final String textfieldHover = "-fx-border-width: 2px; -fx-border-color: #0688fa; -fx-background-color: #ffffff; -fx-text-fill: #4A525A;";
    private final String textfieldPressed = "-fx-border-width: 2px; -fx-border-color: #0061b5; -fx-background-color: #ffffff; -fx-text-fill: #4A525A;";

    // Sliders
    private final String sliderRegular = "-fx-control-inner-background: #2dabf9;";

    // Labels
    private final String titleLabel = "-fx-border-width: 0; fx-text-fill: #0061b5;";

    // Background
    private final String background = "-fx-background: #EBEBEB; -fx-border-width: 0;";

    // Borderless VBox
    private final String borderlessTransparent = "-fx-border-width: 0; -fx-background-color: transparent;";

    public Styles(double width, double height) {
        this.width = width;
        this.height = height;
        this.verticalPadding = height / 50;
        this.horizontalPadding = height / 50;
        this.bottomMargin = new Insets(0, 0, height * 0.05, 0);
        this.topMargin = new Insets(height * 0.05, 0, 0, 0);
        this.topAndBottomMargin = new Insets(height * 0.05, 0, height * 0.05, 0);
        this.thinTopAndBottomMargin = new Insets(height * 0.02, 0, height * 0.02, 0);
        buttonInsets = new Insets(width * 0.01, width * 0.02, width * 0.01, width * 0.02);
    }

    public VBox getStandardVBox() {
        VBox vBox = new VBox(width * 0.02);
        vBox.setStyle(borderlessTransparent);
        return vBox;
    }

    public HBox getStandardHBox() {
        HBox hBox = new HBox(width * 0.02);
        hBox.setStyle(borderlessTransparent);
        return hBox;
    }

    public ScrollPane getStandardScrollPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle(borderlessTransparent);
        return scrollPane;
    }

    public BorderPane getStandardBorderPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle(background);
        return borderPane;
    }

    public HBox getHeader(BaseClient baseClient) {
        double heightPercent = 4d/50;

        // Header area.
        HBox header = new HBox(height * heightPercent * 0.1);
        header.setPrefSize(width, height * heightPercent);

        header.setStyle("-fx-border-width: 0 0 2px 0; -fx-border-color: #0688fa; -fx-background-color: #2dabf9;");

        // Username label
        Label usernameLabel = new Label(baseClient.getUsername());
        usernameLabel.setFont(Font.font(BERLIN_FONT, STANDARD_FONT_SIZE * heightPercent * 1.5));

        VBox usernameArea = new VBox();
        Region verticalSpace = new Region();
        VBox.setVgrow(verticalSpace, Priority.ALWAYS);
        usernameLabel.setAlignment(Pos.BOTTOM_LEFT);
        usernameLabel.setPadding(buttonInsets);
        usernameArea.getChildren().addAll(verticalSpace, usernameLabel);

        // Space between username and logout button.
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        // Logout button
        Text icon = GlyphsDude.createIcon(FontAwesomeIcon.SIGN_OUT, (height * heightPercent) + "px");

        Button logout = getButton("", heightPercent, heightPercent, buttonBorderless, buttonBorderlessHover, buttonBorderlessPressed);
        logout.setGraphic(icon);
        logout.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(usernameArea, space, logout);

        // Event to log out.
        logout.setOnMouseClicked(mouseEvent -> {
            baseClient.setGuiStage(new LogInScreen(baseClient));
            baseClient.logout();
        });

        BorderPane.setMargin(header, bottomMargin);

        return header;

    }

    public Label getH1TitleLabel(String[] text, double widthPercent, double heightPercent) {
        Label label = getLabel(text, widthPercent, heightPercent, 2);
        label.setStyle(titleLabel);

        return label;
    }

    public Label getH2TitleLabel(String[] text, double widthPercent, double heightPercent) {
        Label label = getLabel(text, widthPercent, heightPercent, 1.6);
        label.setStyle(titleLabel);

        return label;
    }

    public Label getH3TitleLabel(String[] text, double widthPercent, double heightPercent) {
        Label label = getLabel(text, widthPercent, heightPercent, 1.2);
        label.setStyle(titleLabel);

        return label;
    }

    public Label getH4TitleLabel(String[] text, double widthPercent, double heightPercent) {
        Label label = getLabel(text, widthPercent, heightPercent, 1);
        label.setStyle(titleLabel);

        return label;
    }

    public Label getH5TitleLabel(String[] text, double widthPercent, double heightPercent) {
        Label label = getLabel(text, widthPercent, heightPercent, 0.8);
        label.setStyle(titleLabel);

        return label;
    }

    public Label getQuestionLabel(String questionText, double widthPercent, double heightPercent) {
        Label label = getLabel(new String[] {questionText}, widthPercent, heightPercent, 0.4);
        label.setWrapText(true);
        label.setTextAlignment(TextAlignment.LEFT);
        label.setStyle(titleLabel);
        label.setMinHeight(height * heightPercent);
        return label;
    }

    public HBox getSlider(double min, double max, double currentValue, double increments, double widthPercent, double heightPercent) {
        HBox sliderArea = getStandardHBox();
        sliderArea.setPrefSize(width * widthPercent - 2 * horizontalPadding, height * heightPercent - 2 * verticalPadding);

        Slider slider = new Slider(min, max, currentValue);
        slider.setMajorTickUnit(increments);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);
        slider.setPrefWidth(width * widthPercent * 0.7);
        slider.setMinHeight(height * heightPercent * 0.7);
        slider.setStyle(sliderRegular);

        TextField sliderValue = readOnlyTextField(getRoundedValue(currentValue, min, increments), 8d/10, 1d/10);
        sliderValue.setPrefWidth(width * widthPercent * 0.3);
        slider.valueProperty().addListener((observableValue, oldValue, newValue) -> sliderValue.setText(getRoundedValue(newValue.doubleValue(), min, increments)));

        sliderArea.getChildren().addAll(slider, sliderValue);

        return sliderArea;
    }

    private String getRoundedValue(double actualValue, double minValue, double incrementSize) {
        return Long.toString(Math.round(Math.round((actualValue - minValue) / incrementSize) * incrementSize + minValue));
    }

    public TextField readOnlyTextField(String startValue, double widthPercent, double heightPercent) {
        TextField readOnly = getRegularTextField("", widthPercent, heightPercent);
        readOnly.setStyle(textfieldRegular);
        readOnly.setText(startValue);
        readOnly.setDisable(true);

        return readOnly;
    }

    public VBox getOverlay() {
        VBox overlay = new VBox();
        overlay.setStyle("-fx-background-color: #4A525A; -fx-opacity: 50%;");
        overlay.setMinSize(width, height);
        return overlay;
    }

    public Label getFieldErrorLabel(String[] lines, double widthPercent, double heightPercent) {

        Label errorLabel = getLabel(lines, widthPercent, heightPercent, 0.8);
        errorLabel.setStyle("-fx-text-fill: #B0413E");
        return errorLabel;

    }

    public Label getInfoLabel(String[] lines, double widthPercent, double heightPercent) {
        Label infoLabel = getLabel(lines, widthPercent, heightPercent, 1.5);
        infoLabel.setStyle("-fx-border-width: 2px; -fx-border-color: #2dabf9; -fx-background-color: #ffffff; -fx-text-fill: #4A525A;");
        return infoLabel;
    }

    public TextField getAnswerTextField(String placeholder, double widthPercent, double heightPercent) {
        TextField textField = getTextField(placeholder, widthPercent, heightPercent);
        textField.setStyle(textfieldRegular);
        textField.setFont(Font.font(BERLIN_FONT, heightPercent * STANDARD_FONT_SIZE * 0.3));

        return textField;
    }

    public TextField getRegularTextField(String placeholder, double widthPercent, double heightPercent) {
        TextField textField = getTextField(placeholder, widthPercent, heightPercent);
        textField.setStyle(textfieldRegular);

        setMouseEvents(textField, textfieldRegular, textfieldHover, textfieldPressed);

        return textField;
    }

    public PasswordField getRegularPasswordField(String placeholder, double widthPercent, double heightPercent) {
        PasswordField passwordField = getPasswordField(placeholder, widthPercent, heightPercent);
        passwordField.setStyle(textfieldRegular);

        setMouseEvents(passwordField, textfieldRegular, textfieldHover, textfieldPressed);

        return passwordField;
    }

    public Button getAnswerButton(String text, double widthPercent, double heightPercent) {
        Button button = getButton(text, widthPercent, heightPercent, buttonRegular, buttonRegularHover, buttonRegularPressed);
        button.setFont(Font.font(BERLIN_FONT, heightPercent * STANDARD_FONT_SIZE * 0.5));
        button.setStyle(buttonRegular);
        VBox.setVgrow(button, Priority.ALWAYS);
        button.setMaxWidth(width * widthPercent);
        button.setWrapText(true);
        button.setTextAlignment(TextAlignment.CENTER);

        return button;
    }

    public Button getRegularButton(String text, double widthPercent, double heightPercent) {
        Button button = getButton(text, widthPercent, heightPercent, buttonRegular, buttonRegularHover, buttonRegularPressed);
        button.setStyle(buttonRegular);

        return button;
    }

    public Button getNeutralButton(String text, double widthPercent, double heightPercent) {
        Button button = getButton(text, widthPercent, heightPercent, buttonNeutral, buttonNeutralHover, buttonNeutralPressed);
        button.setStyle(buttonNeutral);

        return button;
    }

    public Button getBorderlessButton(Node value, double widthPercent, double heightPercent) {
        Button button = getButton("", widthPercent, heightPercent, buttonBorderless, buttonBorderlessHover, buttonBorderlessPressed);
        button.setGraphic(value);

        return button;
    }

    private Button getButton(String text, double widthPercent, double heightPercent, String styleRegular, String styleHover, String stylePressed) {
        Button button = new Button(text);
        button.setFont(Font.font(BERLIN_FONT, heightPercent * STANDARD_FONT_SIZE));
        button.setPrefSize(width * widthPercent - 2 * horizontalPadding, height * heightPercent - 2 * verticalPadding);

        button.setPadding(buttonInsets);

        button.setStyle(styleRegular);

        setMouseEvents(button, styleRegular, styleHover, stylePressed);

        return button;
    }

    private void setMouseEvents(Control object, String regular, String hover, String pressed) {
        object.setOnMouseEntered(actionEvent ->
                object.setStyle(hover)
        );
        object.setOnMouseExited(actionEvent ->
                object.setStyle(regular)
        );
        object.setOnMousePressed(actionEvent ->
                object.setStyle(pressed));
        object.setOnMouseReleased(actionEvent -> {
            if (object.isHover()) {
                object.setStyle(hover);
            } else {
                object.setStyle(regular);
            }
        });
    }

    private TextField getTextField(String placeholder, double widthPercent, double heightPercent) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setFont(Font.font(BERLIN_FONT, heightPercent * 1.2 * STANDARD_FONT_SIZE));
        textField.setMaxSize(width * widthPercent - 2 * horizontalPadding, height * heightPercent - 2 * verticalPadding);
        textField.setPadding(buttonInsets);
        return textField;
    }

    // TODO: Couldn't cast textfield to passwordfield, perhaps there's a way to use just one constructor?
    private PasswordField getPasswordField(String placeholder, double widthPercent, double heightPercent) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(placeholder);
        passwordField.setFont(Font.font(BERLIN_FONT, heightPercent * 1.2 * STANDARD_FONT_SIZE));
        passwordField.setMaxSize(width * widthPercent - 2 * horizontalPadding, height * heightPercent - 2 * verticalPadding);
        passwordField.setPadding(buttonInsets);
        return passwordField;
    }

    private Label getLabel(String[] lines, double widthPercent, double heightPercent, double fontMultiplier) {
        Label label = new Label(String.join("\n", lines));
        label.setFont(Font.font(BERLIN_FONT, STANDARD_FONT_SIZE * fontMultiplier * heightPercent / lines.length));
        label.setPrefWidth(width * widthPercent - 2 * horizontalPadding);
        return label;
    }

    public Insets getTopMargin() {
        return topMargin;
    }

    public Insets getBottomMargin() {
        return bottomMargin;
    }

    public Insets getTopAndBottomMargin() {
        return topAndBottomMargin;
    }

    public Insets getThinTopAndBottomMargin() {
        return thinTopAndBottomMargin;
    }
}


//-fx-border-width
//-fx-border-color
//-fx-background-color
//-fx-font-size
//-fx-text-fill