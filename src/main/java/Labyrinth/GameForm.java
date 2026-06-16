package Labyrinth;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameForm extends JFrame implements KeyListener {

    /* Variables and Constants Section */
    
    // to work with graphics card display modes
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice d = ge.getDefaultScreenDevice();

    // number of level and number of rows are the same (level 5 has 5 rows, lvl
    // 9 has 9 rows, etc)
    private int numberOfRows;
    // number of labyrinth's columns
    private int numberOfColumns;
    // user's nick
    private String nick;
    // user's character
    private String character;
    // number of seconds to finish the level
    private int seconds;
    // players score
    private int playerScore = 0;
    // boolean to define if game is running
    private boolean isGameRunning = true;
    // player's position x
    private int playerTilePositionColumn = 1;
    // player's position y
    private int playerTilePositionRow = 1;
    // width of tile
    private int tileWidth;
    // height of tile
    private int tileHeight;
    // size of border around the frame
    private int thickOfBorder = 4;
    // teleport status true if player stand on any teleport false if not

    // array list for dead ends coordinate
    private ArrayList<Coordinate> blindAlleyCoordinate = new ArrayList<>();
    // array list for objects coordinate
    private ArrayList<Coordinate> graphicsDetailsObjectsOnMapCoordinate = new ArrayList<>();
    // array list for structure objects coordinate
    private ArrayList<Coordinate> obstaclesPathOnMapCoordinate = new ArrayList<>();
    // array list for structure for windmill coordinate
    private ArrayList<Coordinate> windmillOnMapCoordinate = new ArrayList<>();
    // array list for gold coins coordinate
    private ArrayList<Coordinate> goldCoinOnMapCoordinate = new ArrayList<>();

    // first teleport x and y coordinates (position)
    private Coordinate firstTeleport = null;
    // second teleport x and y coordinates (position)
    private Coordinate secondTeleport = null;
    // windmill coordinate (position)
    private Coordinate windmill;

    // sprite sheets for sprite animation
    private SpriteSheet teleportAnimation;
    private SpriteSheet windmillAnimation;
    private SpriteSheet coinAnimation;

    // graphics object for drawing
    private Graphics2D g2d;
    // To prevent the screen from flickering buffered images objects
    private BufferedImage backbuffer, homeImage, playerImage,
            // hedge images
            leftBottomHedgeCorner, leftTopHedgeCorner, rightBottomeHedgeCorner, rightTopHedgeCorner, leftHedge,
            rightHedge, topHedge, bottomHedge, topAndBottomHedge, leftAndRightHedge, leftTopBottom, bottomLeftRight,
            rightTopBottom, topLeftRight, topLeftRightDown;

    /* This section will keep variables needs to generate maze */
    private boolean[][] north; // is there a wall to north of cell i, j
    private boolean[][] east;
    private boolean[][] south;
    private boolean[][] west;
    private boolean[][] visitedTile;
    // the field which will have a random object draw ex. tree, bush or rock
    private boolean[][] assignedObjectToTile;

    // count down of seconds to finish the game
    private Timer countdownTimer = new Timer();
    private MyTimerTask myTaskToCountDownSeconds = new MyTimerTask();
    // this timer is for running game loop
    private Timer gameTimer;
        
    /* All variables all necessary to connect with mySql database */
    // static final String variable (constant)
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String SERVER_ADDRESS = "jdbc:mysql://sql8.freemysqlhosting.net:3306/sql8141017";
    private static final String USER = "sql8141017";
    private static final String PASSWORD = "uF3rYuJ1YG";
    // object to connect with database
    private Connection con = null;
    // statement with sql query
    private PreparedStatement preparedStatementCheckPoints = null;
    private PreparedStatement preparedStatementUpdatePoints = null;
    // result set received by executing sql query
    private ResultSet rs = null;
    
    /* End of Variables and Constants Section */
    
    // constructor
    public GameForm(int selectedRow, String nick, String character) {

        // calling to the constructor of superclass
        super("Level " + selectedRow);

        // selected row on the table is an integer number and number of level
        // either
        this.numberOfRows = selectedRow+2;
        this.nick = nick != null ? nick : "No name";
        this.character = character != null ? character : "1";

        // check the display mode (width, height of the screen)
        DisplayMode myDisplayMode = d.getDisplayMode();
        System.out.println("Display mode: " + myDisplayMode.getWidth() + " " + myDisplayMode.getHeight());

        // set the frame size
        setSize(myDisplayMode.getWidth() - 100, myDisplayMode.getHeight() - 100);
        // or fixed size
        // setSize(300, 300);
        System.out.println("Size of frame: " + getSize().width + " " + getSize().height);

        // number of seconds to finish the level (level to power of 2)
        // It might be numberOfRows*numberOfRows but using Math.pow gives me
        // posibility to power to any real number etc. 1.5 or 1.9
        seconds = (int) Math.pow(numberOfRows, 1.8) + 10;
        

        // execute count down of seconds timer
        countdownTimer.schedule(myTaskToCountDownSeconds, 0, 1000);

        // set the size of tile
        tileHeight = ((getSize().height - thickOfBorder * 2) / numberOfRows);
        tileWidth = tileHeight;
        System.out.println("Size of tile: " + tileWidth + " " + tileHeight);

        // count the number of columns (how many tile in one row) by dividing
        // the screen over width of tile
        numberOfColumns = ((getSize().width - thickOfBorder * 2) / tileWidth);

        // adjust the size of frame ( to avoid empty space on the right side of frame)
        setSize((numberOfColumns * tileWidth + thickOfBorder * 2), getSize().height);

        // create the back buffer for smooth graphics
        backbuffer = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
        // creates a Graphics2D, which can be used to draw into this
        // BufferedImage.
        g2d = backbuffer.createGraphics();

        // do not allow to resize the window
        setResizable(false);
        // display frame in the middle of screen
        setLocationRelativeTo(null);
        // choose the closing operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // set the window as undecorated
        setUndecorated(true);
        // show form
        setVisible(true);
        
        // initialise arrays
        initializeBoardArrays();
        // create bounds around labyrinth
        defineBoundsOfLabyrinth();
        // define tile with different image instead path of labyrinth
        drawObstaclePathObjects();
        // draw windmill on the board
        drawWindmillObjects();
        // set true all walls to make this ready for labyrinth algorythm
        setWallsTrue();
        // load sprite's images into memory
        loadSpriteImages();

        // generate the maze
        /*
		 * implement try..catch structure for stack over flow errors to make the
		 * game more robust
         */
        try {
            generateMaze(numberOfRows, numberOfColumns);
        } catch (StackOverflowError e) {
            JOptionPane.showMessageDialog(null, "GameForm.generateMaze.Something goes wrong during the genereating the maze. Please try to start the game again, sorry.",
                    "Game error", 0);
            this.dispose();
        }
        // add key listener to handle a pressed keys
        addKeyListener(this);
        // check how many "dead ends" labyrinth have and add desired tile
        // coordinates to the list
        lookingForDeadEnds();
        // display teleport in random "dead ends"
        randomTeleportsPointsOnBoard();
        // random trees position
        randomTreesPointsOnBoard();
        // random gold coins position
        randomCoinsOnBoard();
        // this method will run loop game
        startGameLoop();
    }

    // this method start the timer task with instructions to do in equal time intervals
    private void startGameLoop() {
        gameTimer = new Timer();
        gameTimer.schedule(new toDoInGameLoop(), 0, 1000 / 60); //new timer at 60 fps, the timing mechanism
        // 1 second is 1000ms / 60fps gives 16.666 ms,
    }

    // define tile with different image instead path of labyrinth
    private void drawObstaclePathObjects() {
        for (int i = 0; i < numberOfRows - (numberOfRows / 2); i++) {
            // set up random position for row
            int row = (int) (Math.random() * (numberOfRows - 2) + 2);
            // set up random position for column
            int column = (int) (Math.random() * (numberOfColumns - 2) + 2);

            // set up random image to draw in random tile
            int tempObjectNumber = (int) (Math.random() * 16 + 1);
            String imageObject = "MyLabResources/tile" + tempObjectNumber + ".png";
            BufferedImage imageTile = null;
            try {
                imageTile = ImageIO.read(this.getClass().getClassLoader().getResource(imageObject));
            } catch (IOException e) {
                System.out.println("Error. Image tile" + tempObjectNumber + ".png not found.");
            }

            // add position and image to array list whch will be draw in paint
            // mehtod
            obstaclesPathOnMapCoordinate.add(new Coordinate(row, column, imageTile));
            // mark the tile as visited before labyrinth is generate in purpose
            // to bypass the tile
            visitedTile[row][column] = true;
            // add this object position to array list
            assignedObjectToTile[row][column] = true;
        }
    }
    
    // set the windmill position
    private void drawWindmillObjects() {
        // number of windmills is calculate by dividing the number of columns by 4
        while (windmillOnMapCoordinate.size() != numberOfColumns / 4) {
            // set up random position for row
            int row = (int) (Math.random() * (numberOfRows - 2) + 2);
            // set up random position for column
            int column = (int) (Math.random() * (numberOfColumns - 2) + 2);

            // set windmill position only on even rows
            if ((row % 2 == 0) && (assignedObjectToTile[row][column] == false)) {
                BufferedImage imageWindmill = null;
                try {
                    imageWindmill = ImageIO.read(this.getClass().getClassLoader().getResource("MyLabResources/windmill.png"));
                } catch (IOException e) {
                    System.out.println("Error. Image tile windmill.png not found.");
                }
                // add position and image to array list whch will be draw in paint
                // mehtod
                windmill = new Coordinate(row, column, imageWindmill);
                windmillOnMapCoordinate.add(windmill);
                // mark the tile as visited before labyrinth is generate in purpose to
                // bypass the tile
                visitedTile[row][column] = true;
                visitedTile[row + 1][column] = true;
                // add this windmill position and row+1 position to array list
                assignedObjectToTile[row][column] = true;
                assignedObjectToTile[row + 1][column] = true;
            }
        }
    }

    // set tree at random position
    private void randomTreesPointsOnBoard() {
        int i = 0;

        do {
            // random the tree position (row and column)
            int z = (int) ((Math.random() * (numberOfRows - 1)) + 1);
            int k = (int) ((Math.random() * (numberOfColumns - 1)) + 1);
            // compare if z and k is not equal to windmill coordinates
            if ((z == windmill.getCoordinateRowX()) && (k == windmill.getCoordinateColumnY())) {
                System.out.println("Random genereated coordinates are equal to windmill coordinates " + z + " "
                        + k);
            } else {
                // random number of image (object) to draw
                int tempTreeRockBushNumber = (int) (Math.random() * 6 + 1);
                // create the string with random image
                String imageTreeRockBush = "MyLabResources/object" + tempTreeRockBushNumber + ".png";
                // load the image
                BufferedImage u = null;
                try {
                    u = ImageIO.read(this.getClass().getClassLoader().getResource(imageTreeRockBush));
                } catch (IOException e) {
                    System.out.println("Error. Image object" + tempTreeRockBushNumber + ".png not found.");
                }

                // adding object to array list only if previously non other graphics has been assigned to this tile
                if (assignedObjectToTile[z][k] == false) {
                    graphicsDetailsObjectsOnMapCoordinate.add(new Coordinate(z, k, u));
                    assignedObjectToTile[z][k] = true;
                    i++;
                }
            }
        } while (i < numberOfColumns);
    }

    // set random position for coins
    private void randomCoinsOnBoard() {
        int i = 0;
        int numberOfTriesToRandomACoinPosition = 0;
        do {
            // random the gold coin position (row and column)
            int z = (int) ((Math.random() * (numberOfRows)) + 1);
            int k = (int) ((Math.random() * (numberOfColumns)) + 1);
            // compare if z and k is not equal to windmill coordinates
            // the coin can't be displayed on start tile and finish tile
            if ((z == numberOfRows) && (k == numberOfColumns) || (z == 1) && (k == 1)) {
                System.out.println("The random coin can't appear on home image tile. " + z + " " + k);
            } else {
                // random number of image (object) to draw
                int coinTypeNumber = (int) ((Math.random() * 3) + 1);
                // create the string with random image
                String tempImageOfCoin = "MyLabResources/coin_" + coinTypeNumber + ".png";
                // load the image
                BufferedImage u = null;
                try {
                    u = ImageIO.read(this.getClass().getClassLoader().getResource(tempImageOfCoin));
                } catch (IOException e) {
                    System.out.println("Error. Image coin_" + coinTypeNumber + ".png not found.");
                }
                
                // adding coin to array list only if previously non other graphics has been assigned to this tile
                if (assignedObjectToTile[z][k] == false) {
                    goldCoinOnMapCoordinate.add(new Coordinate(z, k, u, coinTypeNumber));
                    assignedObjectToTile[z][k] = true;
                    i++;
                }
                numberOfTriesToRandomACoinPosition++;
                System.out.println("Coins "+i+" columns"+numberOfColumns+"    Number of tries to find a coin position: "+numberOfTriesToRandomACoinPosition);
            }
        } while ((i < numberOfColumns) && (numberOfTriesToRandomACoinPosition!=100));
    }

    // find all dead ends (blind alleys) in labyrinth
    private void lookingForDeadEnds() {
        for (int x = 0; x <= numberOfRows + 1; x++) {
            for (int y = 0; y <= numberOfColumns + 1; y++) {
                // check every wall in tile, at least three walls have to be
                // true and one false
                /* if statement to check if the tile has dead end (blind alley)
                if ((north[x][y] == false && east[x][y] == true && south[x][y] == true && west[x][y] == true)
                        || (north[x][y] == true && east[x][y] == false && south[x][y] == true && west[x][y]
                        == true)
                        || (north[x][y] == true && east[x][y] == true && south[x][y] == false && west[x][y]
                        == true)
                        || (north[x][y] == true && east[x][y] == true && south[x][y] == true && west[x][y]
                        == false)) // add coordinates to the list
                */
                // if statement to check if the tile has dead end (blind alley)
                // in this if statement one variable has been used
                // but the code is more clear to understand than above if statement
                int wallTrue = 0;
                if (north[x][y] == true) wallTrue++;
                if (south[x][y] == true) wallTrue++;
                if (east[x][y] == true) wallTrue++;
                if (west[x][y] == true) wallTrue++;
                if (wallTrue == 3)
                {
                    // adding the found blind alley to array list as new Coordinate x,y
                    blindAlleyCoordinate.add(new Coordinate(x, y));
                }
            }
        }
        // display size of array list
        System.out.println("Dead ends coordinate: " + blindAlleyCoordinate.size());

        // display coordinate from list in console (this is only for testing)
        // get each coordinate object from array list
        for (Coordinate item : blindAlleyCoordinate) {
            System.out.println(
                    "	(row) x= " + item.getCoordinateRowX() + "   (column) y= "
                    + item.getCoordinateColumnY());
        }
        /*
		 * another way to display coordinates for (int i=0;
		 * i<deadEndsCoordinate.size(); i++){ // get coordinate object from
		 * array list and assign it to point variable of coordinate class
		 * Coordinate point = deadEndsCoordinate.get(i); System.out.println(
		 * "(row) x= "+point.getCoordinateRowX()+"   (column) y= "
		 * +point.getCoordinateColumnY()); }
         */
    }

    // random teleports coordinate in two different places and set the
    // coordinates for them
    private void randomTeleportsPointsOnBoard() {
        
        // do teleports only when there is more than 3 blind alleys on board
        if (blindAlleyCoordinate.size() > 3) {
            do {
                // choose the coordinate from array list randomly for first
                // teleport
                int r = (int) (Math.random() * blindAlleyCoordinate.size());
                // get coordinate class object from array list with given index
                // number
                firstTeleport = blindAlleyCoordinate.get(r);
                assignedObjectToTile[firstTeleport.getCoordinateRowX()][firstTeleport.getCoordinateColumnY()]
                        = true;
            } 
            // check if coordinates are not beginning point and exit point
            while (((firstTeleport.getCoordinateRowX() == 1) && (firstTeleport.getCoordinateColumnY() == 1))
                    || ((firstTeleport.getCoordinateRowX() == numberOfRows)
                    && (firstTeleport.getCoordinateColumnY() == numberOfColumns)));

            do {
                // choose the coordinate from array list randomly for second
                // teleport
                int r = (int) (Math.random() * blindAlleyCoordinate.size());
                // get coordinate class object from array list with given index
                // number
                secondTeleport = blindAlleyCoordinate.get(r);
                assignedObjectToTile[secondTeleport.getCoordinateRowX()][secondTeleport.getCoordinateColumnY()]
                        = true;
            } 
            // check if coordinates are not beginning point and exit point
            while (((secondTeleport.getCoordinateRowX() == 1) && (secondTeleport.getCoordinateColumnY() == 1))
                    || ((secondTeleport.getCoordinateRowX() == numberOfRows)
                    && (secondTeleport.getCoordinateColumnY() == numberOfColumns))
                    // or secondTeleport is equal firstTeleport
                    || (secondTeleport.getCoordinateRowX() == firstTeleport.getCoordinateRowX()
                    && secondTeleport.getCoordinateColumnY()
                    == firstTeleport.getCoordinateColumnY()));
        }
    }

    // check if players position is equal with any coin position
    private void checkIfCoin() {
        Coordinate coinToRemove = null;
        // this loop check each coin position with the actual player position
        for (Coordinate item : goldCoinOnMapCoordinate) {
            if ((playerTilePositionRow == item.getCoordinateRowX())
                    && (playerTilePositionColumn == item.getCoordinateColumnY())) {
                // depend from type of coin, add some amounts of points to player's score
                // 3 is gold which equal 30 extra points for collecting this coin
                // 2 is silver 20 extra points
                // 1 is copper 10 extra points
                playerScore+= item.getCoinType()*10;
                coinToRemove = item;
                new PlaySound("coins.wav");
            }
        }
        // remove the coin from array list, which in effect won't be draw on board any more
        goldCoinOnMapCoordinate.remove(coinToRemove);
    }

    // check if players position is equal with any teleport position
    private void checkIfTeleport() {
        // check if first teleport and second teleport has been initialized
        if ((firstTeleport != null) && (secondTeleport != null)) {
            // if player stands on the teleport it will move his position to other teleport on the board
            if (playerTilePositionColumn == firstTeleport.getCoordinateColumnY()
                    && playerTilePositionRow == firstTeleport.getCoordinateRowX()) {
                playerTilePositionColumn = secondTeleport.getCoordinateColumnY();
                playerTilePositionRow = secondTeleport.getCoordinateRowX();
                new PlaySound("teleport.wav");
            } else if (playerTilePositionColumn == secondTeleport.getCoordinateColumnY()
                    && playerTilePositionRow == secondTeleport.getCoordinateRowX()) {
                playerTilePositionColumn = firstTeleport.getCoordinateColumnY();
                playerTilePositionRow = firstTeleport.getCoordinateRowX();
                new PlaySound("teleport.wav");
            }
        }
    }

    // check if players position is equal with exit position
    private void checkIfExit() {
        if ((playerTilePositionColumn == numberOfColumns) && (playerTilePositionRow == numberOfRows)) {
            new PlaySound("applause1.wav");
            repaint();
            // cancel the timer task and timer
            myTaskToCountDownSeconds.cancel();
            countdownTimer.cancel();
            countdownTimer.purge();
            JOptionPane.showMessageDialog(null, "Congratulations "+nick+"! You have finished this level successfully and earn "+playerScore+" points.",
                    "You won", 1);
            // set false value to stop the game loop (timer)
            isGameRunning = false;
            
            // update the players point into the database if score is better than previous one
            updateScore();
            
            // close this store
            this.dispose();
            
            // run next level
            try {
                /* 
                   During creating new instance GameForm the number of level is passed
                   Then in constructor of GameForm class to passed number value 2 is added, to make level with more rows.
                   Can't generate the level 1 with 1 row and 1 column. Therefor level 1 has 3 rows, level 2 has 4 rows.
                   In code below I had to substract 2 and add 1 as a next level 'numberOfRows-2+1'.
                   To simplify this, I can write 'numberOfRows-1', so if currently player finished level 2 which has 4 rows,
                    the program is passing number 3 as an one of the argument and constructor adds 2 to the number of rows.
                    Therefore level 3 has 5 rows.
                */ 
                new GameForm(numberOfRows-1, nick, character);//.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "GameForm.checkIfExit. Something goes wrong. Please try to start the game again, sorry.", "Game error", 0);
                e.printStackTrace(System.out);
                // open main form if the new level does not create
                new MainForm(nick, character).setVisible(true);
            }         
        }
    }
    
    // update the players points into the database,
    // if score is better than previous one on the same level
    private void updateScore() {
        int scoreFromDatabase = 0;
        int rows = 0;
        try {
                // establish java mySQL connection
                Class.forName(DRIVER_CLASS).newInstance();
                con = DriverManager.getConnection(SERVER_ADDRESS, USER, PASSWORD);

                // create variable String query as sql query to database
                String query = "select points from bestscore where level=? and nick=?;";

                preparedStatementCheckPoints = con.prepareStatement(query);
                // parameter used to avoid data breach using sql injections method
                preparedStatementCheckPoints.setString(1, Integer.toString(numberOfRows-2));
                preparedStatementCheckPoints.setString(2, nick);

                // execute insert SQL stetement
                rs = preparedStatementCheckPoints.executeQuery();
                /* extract data from result and assign in to the variables 
             * There supposed to be only one record in results if login
             * is already in database. If not 0 rows in effect.
             * This is my validation if login is in database.
                 */
                while (rs.next()) {
                    scoreFromDatabase = Integer.parseInt(rs.getString("points"));
                    rows++;
                }
                
                // check if score from database is less than player score
                if ( scoreFromDatabase < playerScore ) {
                    JOptionPane.showMessageDialog(null, "This time Your score is higher. The database will be updated.", "Congrats", 1);
                    /* create variable String query as sql query to database
                    * This query update points attribute to bestscore table in database
                     * I am using preparedStatement method to modify the record */
                    
                    // insert new record in to the database
                    String queryToInsertPoints = "INSERT INTO `bestscore`(`points`, `level`, `nick`) VALUES (?,?,?);";
                    // update record with new value of points
                    String queryToUpdatePoints = "UPDATE `bestscore` SET `points`=? WHERE level=? and nick=?;";

                    if (rows==1){
                        // if there was one record from database, with older points
                        preparedStatementUpdatePoints = con.prepareStatement(queryToUpdatePoints);
                    } else{
                        // if there is no record with actual level, insert new record in to the database
                        preparedStatementUpdatePoints = con.prepareStatement(queryToInsertPoints);
                    }                    
                    preparedStatementUpdatePoints.setString(1, Integer.toString(playerScore));
                    preparedStatementUpdatePoints.setString(2, Integer.toString(numberOfRows-2));
                    preparedStatementUpdatePoints.setString(3, nick);
                    // execute insert SQL stetement
                    preparedStatementUpdatePoints.executeUpdate();
                    
                } else JOptionPane.showMessageDialog(null, "Your score is lower than the previous records.", "Sorry", 1);
        } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Communications link failure. Can't compare Your result with the results from database. Sorry.", "Network error", 0);
        } finally {

                /* In the finally block, the result set, statement, and connection
             * are all explicitly closed. This is a VERY good practice to follow
             * so that database connections do not get leaked when you write
             * JDBC code.
                 */
                // dispose result set 'rs'
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                // dispose prepared statement
                try {
                    if (preparedStatementCheckPoints != null) {
                        preparedStatementCheckPoints.close();
                    }
                    if (preparedStatementUpdatePoints != null) {
                        preparedStatementUpdatePoints.close();
                    }                    
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }

                // dispose connection 'con'
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
        }
    }
    
    // // Initialise arrays
    private void initializeBoardArrays() {
        // Initialise arrays
        visitedTile = new boolean[numberOfRows + 2][numberOfColumns + 2];
        assignedObjectToTile = new boolean[numberOfRows + 2][numberOfColumns + 2];
        north = new boolean[numberOfRows + 2][numberOfColumns + 2];
        east = new boolean[numberOfRows + 2][numberOfColumns + 2];
        south = new boolean[numberOfRows + 2][numberOfColumns + 2];
        west = new boolean[numberOfRows + 2][numberOfColumns + 2];
    }

    // definde bounds of laybrinth
    private void defineBoundsOfLabyrinth() {
        // define bounds of labyrinth (value true in some border cells as
        // visited)
        for (int x = 0; x <= numberOfRows + 1; x++) {
            visitedTile[x][0] = true; // x rows left border
            visitedTile[x][numberOfColumns + 1] = true; // right border
        }
        for (int y = 0; y <= numberOfColumns + 1; y++) {
            visitedTile[0][y] = true; // y columns top border
            visitedTile[numberOfRows + 1][y] = true; // bottom border
        }
    }

    // set every wall true, before the maze is created
    private void setWallsTrue() {
        /*
         * Set up walls as true in every cell of labyrinth. It starts from 1
         * because row and column 0 and N+1 are borders,
         */
        for (int x = 0; x <= numberOfRows + 1; x++) {
            for (int y = 0; y <= numberOfColumns + 1; y++) {
                north[x][y] = true;
                east[x][y] = true;
                south[x][y] = true;
                west[x][y] = true;
            }
        }
    }
    
    // this method generate labyrinth by removing the walls 
    // which create only one path to exit from any point on the maze
    private void generateMaze(int x, int y) {
        // set the tile as visited on x y position
        visitedTile[x][y] = true;
        // while there is an unvisited neighbour tiles
        while (!visitedTile[x][y + 1] || !visitedTile[x + 1][y] || !visitedTile[x][y - 1] || !visitedTile[x - 1][y]) {

            // the loop do{}while (true) is always true,
            // exit from this loop is followed after the break mehtod called.
            // When "mole" returns, checks if the field has any unvisited neighbours tiles
            // if there is no more unvisited tiles around the "mole", it quit the method
            do {
                // choose the random direction
                int r = (int) (Math.random() * 4); 
                
                // check the direction and if next tile has't been visited
                if (r == 0 && !visitedTile[x][y + 1]) {
                    // remove border on east wall
                    east[x][y] = false;
                    // remove border on west wall
                    west[x][y + 1] = false;
                    // recur the method (recursive function) 
                    generateMaze(x, y + 1);
                    break; // stop do..while loop ("return" quit method)
                }
                if (r == 1 && !visitedTile[x + 1][y]) {
                    // remove border on south wall
                    south[x][y] = false;
                    // remove border on north wall
                    north[x + 1][y] = false;
                    // recur the method (recursive function) 
                    generateMaze(x + 1, y);
                    break;
                }
                if (r == 2 && !visitedTile[x][y - 1]) {
                    // remove border on west wall
                    west[x][y] = false;
                    // remove border on east wall
                    east[x][y - 1] = false;
                    // recur the method (recursive function) 
                    generateMaze(x, y - 1);
                    break;
                }
                if (r == 3 && !visitedTile[x - 1][y]) {
                    // remove border on north wall
                    north[x][y] = false;
                    // remove border on south wall
                    south[x - 1][y] = false;
                    // recur the method (recursive function) 
                    generateMaze(x - 1, y);
                    break;
                }
            } while (true);
        }
    }

    /* key listener section */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // get key code of released key and assign it to variable keycode
        int keycode = e.getKeyCode();
        // choose the action depends from key released
        switch (keycode) {
            // serve the arrow right key released action
            case KeyEvent.VK_RIGHT:
                // move player right when there is no wall
                if ((!east[playerTilePositionRow][playerTilePositionColumn])) {
                    System.out.println("MOVE X+1 " + playerTilePositionColumn + " " + playerTilePositionRow);
                    playerTilePositionColumn += 1;
                    // check if on the new tile is coin , teleport or exit point
                    checkIfCoin();
                    checkIfTeleport();
                    checkIfExit();
                } else // sound announcing the wall is on the way
                {
                    new PlaySound("wallblocked.wav");
                }
                break;

            // serve the arrow left key released action    
            case KeyEvent.VK_LEFT:
                // move player left if there is no wall
                if ((!west[playerTilePositionRow][playerTilePositionColumn])) {
                    System.out.println("MOVE X-1 " + playerTilePositionColumn + " " + playerTilePositionRow);
                    playerTilePositionColumn -= 1;
                    // check if on the new tile is coin , teleport or exit point
                    checkIfCoin();
                    checkIfTeleport();
                    checkIfExit();
                } else // sound announcing the wall is on the way
                {
                    new PlaySound("wallblocked.wav");
                }
                break;

            // serve the arrow up key released action
            case KeyEvent.VK_UP:
                // move player up if there is no wall
                if ((!north[playerTilePositionRow][playerTilePositionColumn])) {
                    System.out.println("MOVE Y-1 " + playerTilePositionColumn + " " + playerTilePositionRow);
                    playerTilePositionRow -= 1;
                    // check if on the new tile is coin , teleport or exit point
                    checkIfCoin();
                    checkIfTeleport();
                    checkIfExit();
                } else // sound announcing the wall is on the way
                {
                    new PlaySound("wallblocked.wav");
                }
                break;

            // serve the arrow down key released action
            case KeyEvent.VK_DOWN:
                // move player down if there is no wall
                if ((!south[playerTilePositionRow][playerTilePositionColumn])) {
                    System.out.println("MOVE Y+1 " + playerTilePositionColumn + " " + playerTilePositionRow);
                    playerTilePositionRow += 1;
                    // check if on the new tile is coin , teleport or exit point
                    checkIfCoin();
                    checkIfTeleport();
                    checkIfExit();
                } else // sound announcing the wall is on the way
                {
                    new PlaySound("wallblocked.wav");
                }
                break;

            // serve the escape key released action
            case KeyEvent.VK_ESCAPE:
                new PlaySound("exit2.wav");

                // leave game and back to main form
                if (JOptionPane.showConfirmDialog(null, "Do You want to stop the game?", "Exit",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    // stop the game loop
                    isGameRunning = false;

                    // cancel the timer task and timer
                    myTaskToCountDownSeconds.cancel();
                    countdownTimer.cancel();
                    countdownTimer.purge();
                    
                    // close this form
                    this.dispose();
                    // open main form when player pressed escape key
                    new MainForm(nick, character).setVisible(true);
                } else {
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
                break;
        }
    }
    /* end of key listener section */

    @Override
    public void paint(Graphics g) {

        // set colour for drawing and background, colour picked from HSBColor
        // picker tool online
        g2d.setColor(Color.getHSBColor(0.27f, 0.9f, 0.5f));

        // erase the background
        g2d.fillRect(0, 0, getSize().width, getSize().height);

        // draw the game graphic borders beetwen tiles
        // painting the maze, swapped x and y to achieve desired effect
        // during drawing the walls
        for (int column = 1; column <= numberOfColumns; column++) {
            for (int row = 1; row <= numberOfRows; row++) {

                // draw the black lines (border)
                if (north[row][column]) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder),
                            (((column + 1) * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder));
                }
                
                if (south[row][column]) {
                    g2d.drawLine(((column * tileWidth) - tileWidth + thickOfBorder),
                            (((row + 1) * tileHeight) - tileWidth + thickOfBorder),
                            (((column + 1) * tileWidth) - tileWidth + thickOfBorder),
                            (((row + 1) * tileHeight) - tileWidth + thickOfBorder));
                }
                
                if (west[row][column]) {
                    g2d.drawLine(((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder),
                            ((column * tileWidth) - tileWidth + thickOfBorder),
                            (((row + 1) * tileHeight) - tileWidth + thickOfBorder));
                }
                
                if (east[row][column]) {
                    g2d.drawLine((((column + 1) * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder),
                            (((column + 1) * tileWidth) - tileWidth + thickOfBorder),
                            (((row + 1) * tileHeight) - tileWidth + thickOfBorder));
                }

                // draw the images of path tiles on specific location 
                // depends on opened borders between tiles
                if ((west[row][column]) && (south[row][column]) && !(east[row][column]) && !(north[row][column])) {
                    g2d.drawImage(leftBottomHedgeCorner, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((west[row][column]) && (!south[row][column]) && !(east[row][column]) && (north[row][column])) {
                    g2d.drawImage(leftTopHedgeCorner, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (!south[row][column]) && (east[row][column]) && (north[row][column])) {
                    g2d.drawImage(rightTopHedgeCorner, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (south[row][column]) && (east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(rightBottomeHedgeCorner, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((west[row][column]) && (!south[row][column]) && (!east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(leftHedge, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (!south[row][column]) && (east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(rightHedge, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (south[row][column]) && (!east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(bottomHedge, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (!south[row][column]) && (!east[row][column]) && (north[row][column])) {
                    g2d.drawImage(topHedge, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((west[row][column]) && (!south[row][column]) && (east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(leftAndRightHedge, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (south[row][column]) && (!east[row][column]) && (north[row][column])) {
                    g2d.drawImage(topAndBottomHedge, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((west[row][column]) && (south[row][column]) && (!east[row][column]) && (north[row][column])) {
                    g2d.drawImage(leftTopBottom, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((west[row][column]) && (south[row][column]) && (east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(bottomLeftRight, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((!west[row][column]) && (south[row][column]) && (east[row][column]) && (north[row][column])) {
                    g2d.drawImage(rightTopBottom, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }

                if ((west[row][column]) && (!south[row][column]) && (east[row][column]) && (north[row][column])) {
                    g2d.drawImage(topLeftRight, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }
                if ((!west[row][column]) && (!south[row][column]) && (!east[row][column]) && (!north[row][column])) {
                    g2d.drawImage(topLeftRightDown, ((column * tileWidth) - tileWidth + thickOfBorder),
                            ((row * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                            this);
                }
            }
        }

        // draw game status information like time, score, player's lives
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Serif", 0, 25));
        g2d.drawString("Score: " + playerScore, thickOfBorder, 30);
        if (seconds <= 10) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLACK);
        }
        g2d.setFont(new Font("Serif", 1, 30));
        g2d.drawString("Time: " + seconds, 300, 30);

        // draw first teleport sprite animation
        if (firstTeleport != null) {
            g2d.drawImage(teleportAnimation.image,
                    ((firstTeleport.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder),
                    ((firstTeleport.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder),
                    (((firstTeleport.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder)
                    + tileWidth),
                    (((firstTeleport.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder)
                    + tileHeight),
                    0, teleportAnimation.getCurrentFrame() * teleportAnimation.frameHeight,
                    teleportAnimation.frameWidth,
                    (teleportAnimation.getCurrentFrame() * teleportAnimation.frameHeight)
                    + teleportAnimation.frameHeight,
                    this);
        }

        // draw second teleport sprite animation
        if (secondTeleport != null) {
            g2d.drawImage(teleportAnimation.image,
                    ((secondTeleport.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder),
                    ((secondTeleport.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder),
                    (((secondTeleport.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder)
                    + tileWidth),
                    (((secondTeleport.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder)
                    + tileHeight),
                    0, teleportAnimation.getCurrentFrame() * teleportAnimation.frameHeight,
                    teleportAnimation.frameWidth,
                    (teleportAnimation.getCurrentFrame() * teleportAnimation.frameHeight)
                    + teleportAnimation.frameHeight,
                    this);
        }

        // draw exit (image of house)
        g2d.drawImage(homeImage, ((numberOfColumns * tileWidth) - tileWidth + thickOfBorder),
                ((numberOfRows * tileHeight) - tileWidth + thickOfBorder), tileWidth, tileHeight,
                this);

        // draw player sprite
        g2d.drawImage(playerImage, ((playerTilePositionColumn * tileWidth) - tileWidth + thickOfBorder),
                ((playerTilePositionRow * tileHeight) - tileWidth + thickOfBorder), tileWidth,
                tileHeight, this);

        // draw structure (object on empty tile, no path)
        for (int i = 0; i < obstaclesPathOnMapCoordinate.size(); i++) {
            g2d.drawImage(obstaclesPathOnMapCoordinate.get(i).getTempImage(),
                    ((obstaclesPathOnMapCoordinate.get(i).getCoordinateColumnY() * tileWidth) - tileWidth
                    + thickOfBorder),
                    ((obstaclesPathOnMapCoordinate.get(i).getCoordinateRowX() * tileHeight) - tileWidth
                    + thickOfBorder),
                    // added 10% of tile heigth to make the tree image lower
                    tileWidth, tileHeight, null);
        }

        // draw big (2 tiles) structure windmill animation
        for (Coordinate item : windmillOnMapCoordinate) {
            if (windmillAnimation != null) {
                g2d.drawImage(item.getTempImage(),
                        ((item.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder),
                        ((item.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder),
                        (((item.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder)
                        + tileWidth),
                        (((item.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder) + tileHeight
                        * 2),
                        windmillAnimation.col * windmillAnimation.frameWidth,
                        windmillAnimation.row * windmillAnimation.frameHeight,
                        (windmillAnimation.col * windmillAnimation.frameWidth)
                        + windmillAnimation.frameWidth,
                        (windmillAnimation.row * windmillAnimation.frameHeight)
                        + windmillAnimation.frameHeight,
                        null);
            }
        }

        // draw tree, bush or rock loop, on specific position set in array list
        for (Coordinate item : graphicsDetailsObjectsOnMapCoordinate) {
            g2d.drawImage(item.getTempImage(),
                    ((item.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder),
                    ((item.getCoordinateRowX() * tileHeight) - tileWidth + thickOfBorder
                    + (10 * tileHeight / 100)),
                    // added 10% of tile heigth to make the tree image lower
                    tileWidth, tileHeight, null);
        }

        // draw the coin animation on specific position,
        // it takes coordinates and image path from array list 
        if (coinAnimation != null) {
            for (Coordinate item : goldCoinOnMapCoordinate) {
                g2d.drawImage(item.getTempImage(),
                        (((item.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder)
                        + tileWidth / 4),
                        (((item.getCoordinateRowX() * tileHeight) - tileHeight + thickOfBorder)
                        + tileHeight / 4),
                        (((item.getCoordinateColumnY() * tileWidth) - tileWidth + thickOfBorder)
                        + (tileWidth * 75 / 100)),
                        (((item.getCoordinateRowX() * tileHeight) - tileHeight + thickOfBorder)
                        + (tileWidth * 75 / 100)),
                        coinAnimation.col * coinAnimation.frameWidth, coinAnimation.row
                        * coinAnimation.frameHeight,
                        (coinAnimation.col * coinAnimation.frameWidth) + coinAnimation.frameWidth,
                        (coinAnimation.row * coinAnimation.frameHeight) + coinAnimation.frameHeight, null);
            }
        }

        // draw the back buffer image (whole screen at once)
        g.drawImage(backbuffer, 0, 0, this);
    }// end of paint method

    // class Coordinate to keep x position and y position
    public class Coordinate {

        private int row;
        private int column;
        private BufferedImage temporaryImage;
        private int coinType;

        // constructor with two integer position numbers
        public Coordinate(int x, int y) {
            this.row = x;
            this.column = y;
        }

        // constructor with two integer position numbers and image
        public Coordinate(int x, int y, BufferedImage temporaryImage) {
            this.row = x;
            this.column = y;
            this.temporaryImage = temporaryImage;
        }

        // constructor with two integer position numbers and image and 
        // number which determines the type of coin (type of coin is important 
        // during the collecting coins as each coin has different amount of points)
        public Coordinate(int x, int y, BufferedImage temporaryImage, int coinTypeColor) {
            this.row = x;
            this.column = y;
            this.temporaryImage = temporaryImage;
            this.coinType = coinTypeColor;
        }

        // getters
        public int getCoordinateRowX() {
            return row;
        }

        public int getCoordinateColumnY() {
            return column;
        }

        public BufferedImage getTempImage() {
            return temporaryImage;
        }

        public int getCoinType() {
            return coinType;
        }
    }

    // class spriteSheet
    public class SpriteSheet {

        private int frameWidth;
        private int frameHeight;
        private int frameSpeed;
        private int startFrame;
        private int endFrame;
        private int framesPerRow;
        private int framesPerColumn;
        private BufferedImage image;

        private int currentFrame; // the current frame to draw
        private int counter; // keep track of frame rate

        private int row;
        private int col;

        // first constructor
        public SpriteSheet(int frameWidth, int frameHeight, int frameSpeed, int startFrame, int endFrame,
                String pathImage) {
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
            this.frameSpeed = frameSpeed;
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            // set the first frame into current frame variable just once in
            // constructor, later the value of current frame change
            this.currentFrame = startFrame;

            // reading the image as sprite sheet
            try {
                this.image = ImageIO.read(this.getClass().getClassLoader().getResource(pathImage)); // graphics/teleport.png
            } catch (IOException e) {
                System.err.println("Error. Cannot read the '" + pathImage + "' image.");
                return;
            }
            
            // calculate how many sprites are in the image by dividing width of image by frame width
            this.framesPerRow = image.getWidth() / frameWidth;
            // calculate how many rows are in the image by dividing height of image by frame height
            this.framesPerColumn = image.getHeight() / frameHeight;
        }

        // getters
        public int getFramesPerRow() {
            return framesPerRow;
        }

        public int getFramesPerColumn() {
            return framesPerColumn;
        }

        public int getCurrentFrame() {
            return currentFrame;
        }

        // update the animation
        public void updateFrame() {
            // update to the next frame
            if (counter == (frameSpeed - 1)) {
                counter = 0;
                currentFrame++;
            }

            // update the counter
            if (currentFrame == endFrame) {
                currentFrame = startFrame;
            }
            counter++;
            // calculate the row where the current frame is on the sprite sheets
            this.row = currentFrame / framesPerRow;
            // calculate the column where the current frame is on the sprite sheets
            this.col = currentFrame % framesPerRow;
        }
    }

    // thie method will load all images needed to run a game
    private void loadSpriteImages() {
        try {
            String playerImagePath = "MyLabResources/child"+character+".png";
            playerImage = ImageIO.read(this.getClass().getClassLoader().getResource(playerImagePath));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'child"+character+".png' image.");
        }

        // reading the image needed to display of point of exit in labyrinth
        try {
            homeImage = ImageIO.read(this.getClass().getClassLoader().getResource("MyLabResources/home.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'chest.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            leftBottomHedgeCorner = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/leftBottomHedgeCorner.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'LeftBottomHedgeCorner.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            leftTopHedgeCorner = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/leftTopHedgeCorner.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'leftTopHedgeCorner.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            rightBottomeHedgeCorner = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/rightBottomHedgeCorner.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'rightBottomHedgeCorner.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            rightTopHedgeCorner = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/rightTopHedgeCorner.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'rightTopHedgeCorner.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            bottomHedge = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/bottomHedge.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'bottomHedge.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            topHedge = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/topHedge.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'topHedge.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            leftHedge = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/leftHedge.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'leftHedge.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            rightHedge = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/rightHedge.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'rightHedge.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            topAndBottomHedge = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/topAndBottomHedge.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'topAndBottomHedge.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            leftAndRightHedge = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/leftAndRightHedge.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'leftAndRightHedge.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            leftTopBottom = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/leftTopBottom.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'leftTopBottom.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            bottomLeftRight = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/bottomLeftRight.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'bottomLeftRight.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            rightTopBottom = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/rightTopBottom.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'rightTopBottom.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            topLeftRight = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/topLeftRight.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'topLeftRight.png' image.");
        }

        // reading the image needed to display of background in labyrinth
        try {
            topLeftRightDown = ImageIO.read(getClass().getResource("/MyLabResources/Hedge/topLeftRightDown.png"));
        } catch (IOException e) {
            System.err.println("Error. Cannot read the 'topLeftRightDown.png' image.");
        }

        // create new instance teleportAnimation of sprite sheet class
        teleportAnimation = new SpriteSheet(256, 256, 10, 1, 4, "MyLabResources/teleport.png");

        // create new instance of sprite sheet class for player
        windmillAnimation = new SpriteSheet(128, 256, 10, 0, 3, "MyLabResources/windmill.png");

        // create new instance of sprite sheet class coins to pickup for player
        coinAnimation = new SpriteSheet(32, 32, 10, 0, 8, "MyLabResources/coin_1.png");
    }
    
    // this is for running all updates and paint the graphics
    private class toDoInGameLoop extends TimerTask {
        @Override
        public void run() //this becomes the loop
        {
            // update frame animation
            windmillAnimation.updateFrame();
            teleportAnimation.updateFrame();
            coinAnimation.updateFrame();
            // paint all graphics
            repaint();
            // cancel timer if the game has no running status
            if (!isGameRunning) {
                gameTimer.cancel();
            }
        }
    } // end of toDoInGameLoop class
    
    // this class is created to make task for count down
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            // sound announcing the wall is on the way
            if (seconds < 11) {
                new PlaySound("seconds.wav");
            }
            if (seconds < 1) {

                // cancel the timer task and timer
                myTaskToCountDownSeconds.cancel();
                countdownTimer.cancel();
                countdownTimer.purge();

                // stop the game loop
                isGameRunning = false;
                 new PlaySound("timeout.wav");
                // open the main menu form
                // LoginForm().setVisible(true);
                JOptionPane.showMessageDialog(null, "Sorry, time's up. Try this level again.", "Game over", 0);
                dispose();
                new MainForm(nick, character).setVisible(true);
            } else {
                seconds--;
                System.out.println(seconds);
            }
        }
    } // end of MyTimerTask class
}
