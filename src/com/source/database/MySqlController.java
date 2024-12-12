package com.source.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SAMUN
 */
public class MySqlController {

    private Connection con;

    private static MySqlController instance;

    private final String DB_NAME = "jdbc:mysql://localhost/expressiondb";

    private MySqlController() {
        Connect();
    }

    public static MySqlController getInstance() {
        if (instance == null) {
            instance = new MySqlController();
        }

        return instance;
    }

    public Connection getConnection() {
        return con;
    }

    public final void Connect() {
        try {
            con = DriverManager.getConnection(DB_NAME, "root", "");
            System.out.println("MySQL Connection successfull.");
        } catch (SQLException ex) {
            Logger.getLogger(MySqlController.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
        }
    }

}
