import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Name:       Garvin Stewart
// Date:       2026-08-16
// Assignment: CSD420 - Module 1 - JavaFX Random Card Display
// Purpose:    Show four randomly-selected cards from a 52-card deck and let
//             the user draw four new ones with a Refresh button.

public class CardShuffleApp extends Application {

    /** Total number of cards in a standard deck. */
    private static final int DECK_SIZE = 52;

    /** Number of cards displayed on screen at one time. */
    private static final int CARDS_SHOWN = 4;

    /** Relative path to the directory containing the card image files. */
    private static final String CARDS_DIR = "cards";

    /** Pixel width used to render each card image. */
    private static final double CARD_WIDTH = 120;

    /** Shared random-number source used when shuffling the deck. */
    private final Random random = new Random();

    /** The four ImageView nodes that display the currently drawn cards. */
    private final ImageView[] cardViews = new ImageView[CARDS_SHOWN];

    /**
     * Lambda expression (a {@link Supplier}) that builds a fresh list of
     * the integers 1..52 and shuffles it, producing a new random draw
     * order each time it is invoked. Using a Supplier lets the shuffle
     * logic be defined once and re-run on every click of Refresh.
     */
    private final Supplier<List<Integer>> newShuffledDeck = () -> {
        List<Integer> deck = IntStream.rangeClosed(1, DECK_SIZE)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(deck, random);
        return deck;
    };

    /**
     * Lambda expression (a {@link Function}) that maps a card number
     * (1..52) to the {@link Image} loaded from its corresponding PNG
     * file in the {@code cards} subdirectory.
     */
    private final Function<Integer, Image> loadCardImage = cardNumber -> {
        File file = new File(CARDS_DIR, cardNumber + ".png");
        return new Image(file.toURI().toString(), CARD_WIDTH, 0, true, true);
    };

    /**
     * Builds and shows the primary stage: a row of four card ImageViews
     * above a "Refresh" button, then performs the initial draw.
     *
     * @param primaryStage the primary stage supplied by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {

        // --- Card display row: four empty ImageView slots side by side ---
        HBox cardRow = new HBox(15);
        cardRow.setAlignment(Pos.CENTER);
        cardRow.setPadding(new Insets(20));

        // Lambda expression used inside forEach to initialize and add
        // each of the four ImageView placeholders to the row.
        IntStream.range(0, CARDS_SHOWN).forEach(i -> {
            ImageView view = new ImageView();
            view.setFitWidth(CARD_WIDTH);
            view.setPreserveRatio(true);
            cardViews[i] = view;
            cardRow.getChildren().add(view);
        });

        // --- Refresh button: redraws four new random cards on click ---
        Button refreshButton = new Button("Refresh");
        // Lambda expression supplying the button's EventHandler in place
        // of an anonymous inner class.
        refreshButton.setOnAction(event -> drawFourCards());

        HBox controls = new HBox(refreshButton);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(0, 0, 20, 0));

        // --- Title label shown above the cards ---
        Label title = new Label("Random Card Draw");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // --- Assemble the layout: title on top, cards in the middle, ---
        // --- Refresh button along the bottom                        ---
        BorderPane root = new BorderPane();
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setCenter(cardRow);
        root.setBottom(controls);

        // --- Initial draw so the window opens with four cards showing ---
        drawFourCards();

        // --- Build and show the window ---
        Scene scene = new Scene(root, 620, 300);
        primaryStage.setTitle("CSD420 Module 1 - Card Shuffle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Draws four distinct cards at random from a freshly shuffled deck
     * (via {@link #newShuffledDeck}) and updates each ImageView with the
     * corresponding card image (via {@link #loadCardImage}).
     */
    private void drawFourCards() {
        // Shuffle a fresh deck and take the first four cards, without replacement.
        List<Integer> deck = newShuffledDeck.get();
        List<Integer> draw = deck.subList(0, CARDS_SHOWN);

        // Lambda expression used to apply the loaded image to each
        // ImageView slot.
        IntStream.range(0, CARDS_SHOWN)
                .forEach(i -> cardViews[i].setImage(loadCardImage.apply(draw.get(i))));
    }

    /**
     * Application entry point; hands control to the JavaFX runtime.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
