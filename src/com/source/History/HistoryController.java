package com.source.History;

import com.source.database.MySqlController;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.util.Duration;

/**
 *
 * @author SAMUN
 */
public class HistoryController {

    private static HistoryController instance;
    private final MySqlController sql;

    private PreparedStatement pst;

    private HistoryController() {
        sql = MySqlController.getInstance();
    }

    public static HistoryController getInstance() {
        if (instance == null) {
            instance = new HistoryController();
        }

        return instance;
    }

    public void addToDB(History hist) {
        try {
            pst = sql.getConnection().prepareStatement("insert into expressiondb(ID,Expression,Answer)values(?,?,?)");
            pst.setLong(1, hist.getId());
            pst.setString(2, hist.getExp());
            pst.setString(3, hist.getAns());
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeFromDB(long histID) {

        try {
            pst = sql.getConnection().prepareStatement("delete from expressiondb where ID = ? ");
            pst.setLong(1, histID);
            pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<History> retainHistories() {
        ObservableList<History> lst = FXCollections.observableArrayList();

        try {
            pst = sql.getConnection().prepareStatement("select ID,Expression,Answer from expressiondb");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                History hist = new History();
                hist.setId(rs.getLong("ID"));
                hist.setExp(rs.getString("Expression"));
                hist.setAns(rs.getString("Answer"));
                lst.add(hist);
            }
        } catch (SQLException ex) {
            Logger.getLogger(HistoryController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lst;
    }
}
