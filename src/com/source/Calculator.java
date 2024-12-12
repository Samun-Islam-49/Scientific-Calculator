package com.source;

/**
 * @author SAMUN
 */
import com.source.History.History;
import com.source.History.HistoryController;
import java.math.BigDecimal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// This file is responsible for Controlling the calcultors main operations using other classes.
public class Calculator extends Application implements LayoutContoller.OnSpecialClick {

    private LayoutContoller layout;
    private ExpressionHandler handler;
    private HistoryController hiscon;

    /// Starting JavaFx Module
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        Image img = new Image(getClass().getResourceAsStream("assets/icon.png"));
        primaryStage.getIcons().add(img);

        layout = LayoutContoller.getInstance();      // Main Layout
        layout.SetOnSpecialClick(this);     // Special Clicks interface like Equal.
        
        hiscon = HistoryController.getInstance();

        Scene scene = layout.createScene();
        scene.getStylesheets().add(getClass().getResource("assets/styles.css").toExternalForm());
//        System.out.println("style ->" + getClass().getResource("assets/styles.css").toExternalForm());

        primaryStage.setTitle("Sem-Sci Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Special Clicks Handling ...
    @Override
    public void OnEqualClick(String exp) {
//        System.out.println(exp);
        handler = ExpressionHandler.getInstance();

        try {
            BigDecimal result = handler.calculate(exp);
            handler.setLastAns(result);

            String formated_result = handler.formatDecimal(result);

            layout.getDisplay().setText(formated_result);
            layout.setIsAns(true);

            History hist = new History(exp, formated_result);
            layout.addHistory(hist);
        } catch (Exception ex) {
            layout.ShowErrorAlert(ex.getMessage());
            ex.printStackTrace();
        }
    }

}
