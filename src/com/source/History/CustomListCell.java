package com.source.History;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 *
 * @author SAMUN
 */
public abstract class CustomListCell extends ListCell<History> {

    private final Label exp;    // For showing expression
    private final Label ans;    // For showing answer
    private final Button delete;
    
    private final GridPane gridPane; // Layout for components
    
    private final Image cross;    // Cross Image
    private final ImageView del;    // Cross image for delete
    private long histID;
    

    public CustomListCell() {
        exp = new Label();
        ans = new Label();
        delete = new Button();
        
        gridPane = new GridPane();
        
        del = new ImageView();
        cross = new Image(getClass().getResourceAsStream("assets/crossmark.png"));
        
        initComponents();

        setGraphic(gridPane);
    }

    final void initComponents() {
        del.setFitHeight(35);
        del.setFitWidth(35);
        del.setImage(cross);

        exp.setOnMouseClicked(this::onLabelClickListener);
        ans.setOnMouseClicked(this::onLabelClickListener);
        
        delete.setOnMouseClicked(this::onCrossClickListener);
        delete.getStyleClass().add("cell-button");
        delete.setGraphic(del);
        delete.setVisible(false);
 
        gridPane.add(exp, 0, 0);
        gridPane.add(ans, 0, 1);
        gridPane.add(delete, 1, 0, 1, 2);
    }

    @Override
    protected void updateItem(History item, boolean empty) {
        super.updateItem(item, empty);
        
        if (!empty && item != null) {
            exp.setText(item.getExp());
            ans.setText(item.getAns());
            
            delete.setVisible(true);
            
            histID = item.getId();
        }

    }

    private void onLabelClickListener(MouseEvent event) {
        Label lb = (Label) event.getSource();
        onLabelClicked(lb.getText());
    }
    
    private void onCrossClickListener(MouseEvent event) {
        onCrossClicked(histID);
    }

    public abstract void onLabelClicked(String txt);
    public abstract void onCrossClicked(long historyID);
}
