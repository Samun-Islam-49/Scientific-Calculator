package com.source;

/**
 * This file is responsible for handling Main Calculator layout.
 *
 * @author Samun Islam
 */
import com.source.History.CustomListCell;
import com.source.History.History;
import com.source.History.HistoryController;
import com.source.Layout.LayoutUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class LayoutContoller implements EventHandler<ActionEvent> {

    /**
     * All the operators available.
     */
    public final static String OPERATORS = "+-*/^%!";

    /**
     * Available braces.
     */
    public final static String BRACES = "()";

    private boolean isAns = false;                                  // Indicates if the display showing answer
    private boolean error = false;                                  // Indicates if occures any error
    private final String VALID_CHARS = "0123456789+-*/^%().";       // Valid characters available to input

    private GridPane grid;                                          // Grid Structure for the buttons    
    private final String[][] bList = { // Array list that creates Button Layout for the UI
        {"(", ")", "!", "C", "⌫"},
        {"sin", "cos", "tan", "log", "ln"},
        {"7", "8", "9", "%", "^"},
        {"4", "5", "6", "*", "/"},
        {"1", "2", "3", "+", "-"},
        {"0", ".", "Ans", "π", "="}
    };

    private ListView<History> histDisplay;      // Display to show histories
    private TextField display;                  // Main Display for the calculator
    private int cursor;                         // Cursor Index in display

    private final HistoryController hisCon;             // For using HistotyController Class
    private final ExpressionHandler expHndl;            // For using ExpressionController Class
    private final LayoutUtils utils;                    // For using LayoutUtils Class
    
    private OnSpecialClick clickListener;               // For using OnSpecialClick interface

    private static LayoutContoller instance;    // Static LayoutController instance, so that it never expires

    /**
     * Private Constructor so no external class can create new instance.
     */
    private LayoutContoller() {
        utils = LayoutUtils.getInstance();
        hisCon = HistoryController.getInstance();
        expHndl = ExpressionHandler.getInstance();
    }


    /**
     * Returns the existing instance. If no instance has created before, then
     * creates the instance and returns. Ensures no other instance is created
     * but one on whole run-time. The created instance is static so it will not
     * expire.
     *
     * @return self-class instance.
     */
    public static LayoutContoller getInstance() {
        if (instance == null) {
            instance = new LayoutContoller();
        }

        return instance;
    }


    /**
     * Gives the access of the display TextField.
     *
     * @return display
     */
    public TextField getDisplay() {
        return display;
    }


    /**
     * Sets if the display is showing answer.
     *
     * @param isAns if the display is showing answer or otherwise
     */
    public void setIsAns(boolean isAns) {
        this.isAns = isAns;
    }


    /**
     * Set OnSpecialClick for controlling some specific buttons.
     *
     * @param listener OnSpecialClick interface
     */
    public void SetOnSpecialClick(OnSpecialClick listener) {
        clickListener = listener;
    }


    /**
     * Renders all components in a scene and returns the scene for Primary
     * Stage.
     *
     * @return layout (Scene)
     */
    public Scene createScene() {
        createGrid();
        createHistoryDisplay(null);
        createDisplay();
        createButtons();

        return new Scene(grid, grid.getPrefWidth(), grid.getPrefHeight());
    }


    /**
     * Prepares grid for adding components on the UI.
     */
    private void createGrid() {
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.setPrefSize(GridPane.USE_COMPUTED_SIZE, GridPane.USE_COMPUTED_SIZE);
    }


    /**
     * Adds the given histDisplay on the History display and MySQL Database.
     *
     * @param hist History to be added
     */
    public void addHistory(History hist) {
        ObservableList<History> curList = histDisplay.getItems();
        curList.add(hist);

        histDisplay.scrollTo(curList.size() - 1);

        hisCon.addToDB(hist);
    }


    /**
     * Deletes the histDisplay from the History Display and the Database based on
 the ID.
     *
     * @param histID ID of the histDisplay needs to deleted
     */
    public void deleteHistory(long histID) {
        ObservableList<History> curList = histDisplay.getItems();

        History target = null;

        for (History x : curList) {
            if (x.getId() == histID) {
                target = x;
                break;
            }
        }

        if (target != null) {
            curList.remove(target);
            hisCon.removeFromDB(histID);
        }

        createHistoryDisplay(curList);
    }


    /**
     * Creates secondary display for showing history. Takes
     * <code> ObservableList<History> </code> and loads the History display
     * based on that list. If that list is null, loads the list from main
     * Database.
     *
     * @param olst default list to be loaded. Set <code> null </code> to load
     * from the Database.
     */
    private void createHistoryDisplay(ObservableList<History> olst) {

        if (olst != null) {
            histDisplay = new ListView<>(olst);
        } else {
            new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                ObservableList<History> nlist = hisCon.retainHistories();
                createHistoryDisplay(nlist);
            })).play();

            histDisplay = new ListView<>();
        }
        
        histDisplay.scrollTo(histDisplay.getItems().size() - 1);

        histDisplay.setCellFactory(param -> new CustomListCell() {
            @Override
            public void onLabelClicked(String txt) {
                display.insertText(cursor, txt);
                cursor += txt.length();
            }


            @Override
            public void onCrossClicked(long historyID) {
                deleteHistory(historyID);
            }


        });

        grid.add(histDisplay, 0, 0, bList[0].length, 1);
    }


    /**
     * Creates Main Display of the calculator.
     */
    private void createDisplay() {
        display = new TextField();
        display.setPadding(new Insets(10));

        // Calculator Input Filtering
        display.setOnKeyTyped((KeyEvent event) -> {
            if (!VALID_CHARS.contains(event.getCharacter())) {
                event.consume(); // Consume the event to prevent the character from being entered
            }
        });

        // Getting cursor postion dynamically
        display.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                cursor = display.getCaretPosition();
                //    System.out.println("Cursor : " + cursor);
            }
        });

        grid.add(display, 0, 1, bList[0].length, 1);
    }


    /**
     * Clears Display.
     */
    private void clearDisplay() {
        display.clear();
        cursor = 0;
    }


    /**
     * Creates the buttons layout using the <code>bList</code> Array button
     * structure.
     */
    private void createButtons() {
        for (int i = 0; i < bList.length; i++) {
            for (int j = 0; j < bList[0].length; j++) {
                Button bt = new Button(bList[i][j]);
                bt.setOnAction(this);
                grid.add(bt, j, i + 2);
            }
        }
    }


    /**
     * Shows Error Alert when encounters an error. Takes error string and shows
     * that string if that string is not empty.
     *
     * @param error Error String
     */
    public void ShowErrorAlert(String error) {
        if (error == null || error.isEmpty()) {
            display.setText("Syntax Error!");
        } else {
            display.setText(error);
        }

        this.error = true;
    }


    /**
     * Handles Mouse Click event on Buttons. Its a global
     * <code>ActionEvent</code> implementation. It handles all mouse click event
     * on buttons.
     *
     * @param event <code>ActionEvent</code> that is passed from the super
     * class.
     */
    @Override
    public void handle(ActionEvent event) {
        Button bt = (Button) event.getSource();
        String btxt = bt.getText().trim();

        if (error) {
            error = false;
            clearDisplay();
        }

        if (isAns) {
            isAns = false;
            clearDisplay();

            // Clicking Operator buttons after "=" button
            if (OPERATORS.contains(btxt)) {
                String lastAns = expHndl.getLastAns().toString();
                display.insertText(cursor, lastAns + btxt);
                cursor += lastAns.length() + 1;
                return;
            }

        }

        if (btxt.equals("=")) {                             // =
            clickListener.OnEqualClick(display.getText().trim());

        } else if (btxt.equals("⌫")) {                   // ⌫
            if (cursor != 0) {
                display.deleteText(cursor - 1, cursor--);
            }

        } else if (btxt.equals("C")) {                      // C
            display.clear();
            cursor = 0;

        } else if (btxt.equals("Ans")) {                  // Ans
            String lastAns = expHndl.getLastAns().toString();
            display.insertText(cursor, lastAns);
            cursor += lastAns.length();

        } else if (utils.checkMathTermLength(btxt) == 3) {                    // For 3 Char Math terms like sin, cos, tan
            display.insertText(cursor, btxt + "[]");
            cursor += 4;
        } else if (utils.checkMathTermLength(btxt) == 2) {                    // For 2 Char Math terms like ln
            display.insertText(cursor, btxt + "[]");
            cursor += 3;
        } else {                                                        // Others
            display.insertText(cursor++, btxt);
        }
    }


    /**
     * Interface for transferring controls for some specific buttons.
     */
    public interface OnSpecialClick {

        void OnEqualClick(String exp);


    }
}
