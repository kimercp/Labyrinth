package Labyrinth;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.*;

/* This class will handle all SELECT * queries data into application table.
 * Establish connection with database. 
 * It will create two arrays: 
 * 1st two dimension array with row data extracted from database table using sql query
 * passed as string in constructor. 
 * 2nd one dimension array with column names from database table. 
 * Will count rows and data columns. 
 * Create TableModel using those two arrays
 */
public class MyConnection {

    /* All variables all necessary to connect with mySql database */
    // static final String variable (constant)
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String SERVER_ADDRESS = "jdbc:mysql://sql8.freemysqlhosting.net:3306/sql8141017";
    private static final String USER = "sql8141017";
    private static final String PASSWORD = "uF3rYuJ1YG";

    // object to connect with database
    private Connection con = null; 
    // statement with sql query
    private PreparedStatement preparedStatement = null; 
    // result set received by executing sql query
    private ResultSet rs = null; 
    // meta data received from database
    private ResultSetMetaData metaData; 
    // number of result rows received back from database by sql queries
    private int rows = 0; 
    // number of result columns received back from database by sql queries
    private int columns = 0; 
        
    // declares an array of objects
    private Object[][] array; 
    // declares an array of strings which keeps columns names
    private String[] columnsNames; 
    // my model to display data in the data
    private TableModel myModel; 

    // constructor
    MyConnection(String sqlQuery) {

        /* Connector JAR file needs to be included in the client project’s
	* class path. The statement Class.forName (“com.mysql.jdbc.driver”)
	* loads the MySQL Java driver class in memory.
         */
        try {
            // MySql Java driver loaded in to memory
            Class.forName(DRIVER_CLASS).newInstance();
            // establish java mySQL connection
            con = DriverManager.getConnection(SERVER_ADDRESS, USER, PASSWORD);
            // create variable String query as sql query to database
            preparedStatement = con.prepareStatement(sqlQuery);
            // execute insert SQL stetement
            rs = preparedStatement.executeQuery();
            // get columns number from metaData
            metaData = rs.getMetaData();
            // set new value to columns (number of columns)
            columns = metaData.getColumnCount(); 
            // allocates memory for strings array
            columnsNames = new String[columns];

            // fill up the columnsNames array with the columns name from metaData
            for (int i = 0; i < columnsNames.length; i++) {
                // getColumnName(i+1) because columns starts from 1 instead 0 like in arrays
                columnsNames[i] = metaData.getColumnName(i+1);
            }
            
            // check numbers of rows
            if (rs.last()) { // moves the cursor to the last row
                // retrieves the current row number. Row stars from 1.
                rows = rs.getRow(); 
            }
            // moves the cursor to the front of this ResultSet object, just before the first row. 
            rs.beforeFirst();

            // fill array with data from result set
            if (rows != 0) {
                // allocates memory for objects array
                array = new Object[rows][columns];
                int rowsCounter = 0;
                // execute this code bellow for every row from data set
                while (rs.next()) {
                    for (int i = 0; i < columns; i++) {
                        // getColumnName(i+1) because columns starts from 1 instead 0 like in arrays
                        array[rowsCounter][i] = rs.getObject(i+1);
                    }
                    rowsCounter++;
                }
            }
            
            // create new instance for table model using array
            myModel = new DefaultTableModel(array, columnsNames) {
                // disable edit cells of table by user
                @Override
                public boolean isCellEditable(int i, int j) {
                    return false;
                }
            };

        } catch (HeadlessException | ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Communications link failure. Check the connection with Internet, please.", "Network error", 0);
            System.out.println("Error message: " + e.getMessage());
        } finally {

            /* In the finally block, the result set, statement, and connection
             * are all explicitly closed. This is a VERY good practice to follow
             * so that database connections do not get leaked when you write
             * JDBC code.
             */
            try {
                if (rs != null) {
                    rs.close();
                }

                if (preparedStatement != null) {
                    preparedStatement.close();
                }

                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                System.out.println("Result set, prepared statement or connection not closed. " + e.getMessage());
            }
        } //end of finally
    } // end of constructor

    // this will return myModel created in this class
    public TableModel getTableModel() {
        return myModel;
    }
}
