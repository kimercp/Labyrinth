package Labyrinth;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* import required classes/interfaces package which acts as a bridge
 * between Java application and database */


/* This form is intended to serve a new player's registration process.
 * Check if the user filled all text fields with data. 
 * Most fields passes validating process. 
 * My code checks if all mandatory fields are not empty.
 * This also checks each field with demanding pattern,
 * depending of type of data wanted using regular expressions.
 * It's create and open connection to remote MySQL database server 
 * and execute query. */
public class RegisterForm extends javax.swing.JFrame {
    
    /* All variables all necessary to connect with mySql database */
    // static final String variable (constant)
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String SERVER_ADDRESS = "jdbc:mysql://sql8.freemysqlhosting.net:3306/sql8141017";
    private static final String USER = "sql8141017";
    private static final String PASSWORD = "uF3rYuJ1YG";
    // object to connect with database
    private Connection con = null;
    // statement with sql query
    private Statement stmt = null;
    // statement with sql query
    private PreparedStatement preparedStatement = null;
    // result set received by executing sql query
    private ResultSet rs = null;
    // counter for children images keep in lblCharacterImage
    private int imageChildCounter = 1;

    /* Create new form */
    public RegisterForm() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblCharacter = new javax.swing.JLabel();
        lblArrowLeft = new javax.swing.JLabel();
        lblArrowRight = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        lblNick = new javax.swing.JLabel();
        txtNick = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblSurname = new javax.swing.JLabel();
        txtSurname = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblBirth = new javax.swing.JLabel();
        ftxtBirth = new javax.swing.JFormattedTextField();
        lblCountry = new javax.swing.JLabel();
        txtCountry = new javax.swing.JTextField();
        lblTown = new javax.swing.JLabel();
        txtTown = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        pfConfirmPassword = new javax.swing.JPasswordField();
        pfPassword = new javax.swing.JPasswordField();
        pnlCharacterHolder = new javax.swing.JPanel();
        lblCharacterImage = new javax.swing.JLabel();
        lblRegisterImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(500, 500));
        setName("frmRegisterMenu"); // NOI18N
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(500, 500));
        getContentPane().setLayout(null);

        lblCharacter.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCharacter.setForeground(new java.awt.Color(102, 102, 102));
        lblCharacter.setText("Character");
        getContentPane().add(lblCharacter);
        lblCharacter.setBounds(370, 390, 70, 24);

        lblArrowLeft.setIcon(new ImageIcon(getClass().getResource("/MyLabResources/arrowLeft25x25.jpg"))); // NOI18N
        lblArrowLeft.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblArrowLeftMouseClicked(evt);
            }
        });
        getContentPane().add(lblArrowLeft);
        lblArrowLeft.setBounds(330, 390, 30, 30);

        lblArrowRight.setIcon(new ImageIcon(getClass().getResource("/MyLabResources/arrowRight25x25.jpg"))); // NOI18N
        lblArrowRight.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblArrowRightMouseClicked(evt);
            }
        });
        getContentPane().add(lblArrowRight);
        lblArrowRight.setBounds(450, 390, 25, 30);

        btnCancel.setText("Cancel");
        btnCancel.setPreferredSize(new java.awt.Dimension(67, 32));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel);
        btnCancel.setBounds(400, 450, 80, 32);

        lblNick.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblNick.setForeground(new java.awt.Color(102, 102, 102));
        lblNick.setText("Game Nick");
        getContentPane().add(lblNick);
        lblNick.setBounds(240, 30, 80, 19);

        txtNick.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtNickFocusLost(evt);
            }
        });
        getContentPane().add(txtNick);
        txtNick.setBounds(330, 30, 150, 20);

        lblPassword.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblPassword.setForeground(new java.awt.Color(102, 102, 102));
        lblPassword.setText("Password");
        getContentPane().add(lblPassword);
        lblPassword.setBounds(250, 70, 70, 19);

        lblConfirmPassword.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblConfirmPassword.setForeground(new java.awt.Color(102, 102, 102));
        lblConfirmPassword.setText("Confirm Password");
        getContentPane().add(lblConfirmPassword);
        lblConfirmPassword.setBounds(190, 110, 130, 19);

        lblName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblName.setForeground(new java.awt.Color(102, 102, 102));
        lblName.setText("Name");
        getContentPane().add(lblName);
        lblName.setBounds(280, 150, 40, 19);
        getContentPane().add(txtName);
        txtName.setBounds(330, 150, 150, 20);

        lblSurname.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblSurname.setForeground(new java.awt.Color(102, 102, 102));
        lblSurname.setText("Surname");
        getContentPane().add(lblSurname);
        lblSurname.setBounds(260, 190, 70, 19);
        getContentPane().add(txtSurname);
        txtSurname.setBounds(330, 190, 150, 20);

        lblEmail.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(102, 102, 102));
        lblEmail.setText("Email");
        getContentPane().add(lblEmail);
        lblEmail.setBounds(280, 230, 50, 19);
        getContentPane().add(txtEmail);
        txtEmail.setBounds(330, 230, 150, 20);

        lblBirth.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblBirth.setForeground(new java.awt.Color(102, 102, 102));
        lblBirth.setText("Date of Birth");
        getContentPane().add(lblBirth);
        lblBirth.setBounds(230, 270, 90, 19);

        try {
            ftxtBirth.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        getContentPane().add(ftxtBirth);
        ftxtBirth.setBounds(330, 270, 150, 25);

        lblCountry.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblCountry.setForeground(new java.awt.Color(102, 102, 102));
        lblCountry.setText("Country");
        getContentPane().add(lblCountry);
        lblCountry.setBounds(260, 310, 70, 19);
        getContentPane().add(txtCountry);
        txtCountry.setBounds(330, 310, 150, 20);

        lblTown.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblTown.setForeground(new java.awt.Color(102, 102, 102));
        lblTown.setText("Town");
        getContentPane().add(lblTown);
        lblTown.setBounds(280, 350, 50, 19);
        getContentPane().add(txtTown);
        txtTown.setBounds(330, 350, 150, 20);

        btnSubmit.setText("Submit");
        btnSubmit.setPreferredSize(new java.awt.Dimension(67, 32));
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });
        getContentPane().add(btnSubmit);
        btnSubmit.setBounds(310, 450, 80, 32);
        getContentPane().add(pfConfirmPassword);
        pfConfirmPassword.setBounds(330, 110, 150, 20);
        getContentPane().add(pfPassword);
        pfPassword.setBounds(330, 70, 150, 20);

        pnlCharacterHolder.setBackground(new java.awt.Color(255, 255, 255));
        pnlCharacterHolder.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pnlCharacterHolder.setMaximumSize(new java.awt.Dimension(150, 200));

        lblCharacterImage.setIcon(new ImageIcon(getClass().getResource("/MyLabResources/child1.png"))); // NOI18N

        javax.swing.GroupLayout pnlCharacterHolderLayout = new javax.swing.GroupLayout(pnlCharacterHolder);
        pnlCharacterHolder.setLayout(pnlCharacterHolderLayout);
        pnlCharacterHolderLayout.setHorizontalGroup(
            pnlCharacterHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCharacterHolderLayout.createSequentialGroup()
                .addComponent(lblCharacterImage)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlCharacterHolderLayout.setVerticalGroup(
            pnlCharacterHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCharacterHolderLayout.createSequentialGroup()
                .addComponent(lblCharacterImage)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        getContentPane().add(pnlCharacterHolder);
        pnlCharacterHolder.setBounds(40, 280, 150, 200);

        lblRegisterImage.setIcon(new ImageIcon(getClass().getResource("/MyLabResources/register500x500.jpg"))); // NOI18N
        lblRegisterImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblRegisterImage.setMaximumSize(new java.awt.Dimension(500, 500));
        lblRegisterImage.setMinimumSize(new java.awt.Dimension(500, 500));
        lblRegisterImage.setPreferredSize(new java.awt.Dimension(500, 500));
        getContentPane().add(lblRegisterImage);
        lblRegisterImage.setBounds(0, 0, 500, 500);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // close the register form and back to login form
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Do You want to cancel the registration process",
                "Confirm to cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            //open the main menu form
            new LoginForm().setVisible(true);
            //close this form
            this.dispose();
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    /* Register new player in database
    Mandatory fields are:
    Nick, Password, Confirm Password
     */
    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        /* this part will check all fields using regular expressions
         * Does all text fields are vaild?
         * if one of the field isn't match regular expression,
         * submit is canceled and player is asked to correct input
         */

        // check Nick - first capital letter and no digits allowed
        String patternNick = "^[A-Z][a-z]*$";
        // if text field is empty
        if (txtNick.getText().isEmpty()) {
            // this will execute only when text field nick is empty
            JOptionPane.showMessageDialog(null, "Please type in Your nick.");
            txtNick.requestFocusInWindow();
            return;
        } else {
            // string is not valid (not match the regular expression)
            if (!isPattern(txtNick.getText(), patternNick)) {
                // txtNick has to start with capital letter and no digits allowed
                JOptionPane.showMessageDialog(null, "Nick has to start with capital"
                        + " letter and no digits allowed. Please correct Your nick.");
                txtNick.requestFocusInWindow();
                return;
            }
        }

        // check Password and confirm password are the same
        /* code inside two conditions will execute only if field is not empty
         * and string is not valid (not match the regular expression) */
        // convert password value which is char to string
        String tempPassword = new String(pfPassword.getPassword());
        String tempConfirmPassword = new String(pfConfirmPassword.getPassword());
        if ((tempPassword.isEmpty()) || (tempConfirmPassword.isEmpty())) {
            // if text field password or text field confirm password are empty
            JOptionPane.showMessageDialog(null, "Please type in Your passwords.");
            pfPassword.requestFocusInWindow();
            return;
        } else {
            // check if password are the same
            if (!tempPassword.equals(tempConfirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords does not match.");
                pfConfirmPassword.requestFocusInWindow();
                return;
            }
        }

        // check Name - first capital letter and no digits allowed
        String patternName = "^[A-Z][a-z]*$";
        /* code inside two conditions will execute only if field is not empty
         * and string is not valid (not match the regular expression) */
        if (!txtName.getText().isEmpty()) {
            if (!isPattern(txtName.getText(), patternName)) {
                // txtName has to start with capital letter and no digits allowed
                JOptionPane.showMessageDialog(null, "Name has to start with capital"
                        + " letter and no digits allowed. Please correct Your Name.");
                txtName.requestFocusInWindow();
                return;
            }
        }

        // check Surname - first capital letter and no digits allowed
        String patternSurname = "^[A-Z][\\-\\'\\sa-zA-Z]*[a-z]$";
        /* code inside two conditions will execute only if field is not empty
         * and string is not valid (not match the regular expression) */
        if (!txtSurname.getText().isEmpty()) {
            if (!isPattern(txtSurname.getText(), patternSurname)) {
                // txtSurname has to start with capital letter and no digits allowed
                JOptionPane.showMessageDialog(null, "Surname has to start with capital"
                        + " letter and no digits allowed. Please correct Your Surname.");
                txtSurname.requestFocusInWindow();
                return;
            }
        }

        // check Email - multiple combinations       
        String patternEmail = "^[\\w\\.-]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$";
        /* code inside two conditions will execute only if field is not empty
         * and string is not valid (not match the regular expression) */
        if (!txtEmail.getText().isEmpty()) {
            if (!isPattern(txtEmail.getText(), patternEmail)) {
                // txtEmail has to start with capital letter and no digits allowed
                JOptionPane.showMessageDialog(null, "Please correct Your Email.");
                txtEmail.requestFocusInWindow();
                return;
            }
        }

        /* checking the date format is correct 
         * try to convert string from formated text field to date
         * if the process ends succesfully, means the string is valid
         */
        String dateString = "  /  /    ";
        // create a new object of clas DateFormat
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        // check if date has been typed in to formatted field by user
        if (!dateString.equals(ftxtBirth.getText())) {
            try {
                String input = ftxtBirth.getText();
                // convert string to date
                Date dateOfBirth = dateFormat.parse(input);
                // convert date to string
                String output = dateFormat.format(dateOfBirth);
                // compare the date input and output (two strings)
                if (!input.equals(output)) {
                    JOptionPane.showMessageDialog(null, "It seems Your date of birth is invalid. Please correct this.");
                    return;
                }
            } catch (ParseException ex) {
                // show the message if date inputed by user is incorrect
                JOptionPane.showMessageDialog(null, "The date format is incorrect.");
                ftxtBirth.requestFocusInWindow();
                Logger.getLogger(RegisterForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // check Country - first capital letter and no digits allowed
        String patternCountry = "^[A-Z][\\-\\'\\sa-zA-Z]*[a-z]$";
        /* code inside two conditions will execute only if field is not empty
         * and string is not valid (not match the regular expression) */
        if (!txtCountry.getText().isEmpty()) {
            if (!isPattern(txtCountry.getText(), patternCountry)) {
                // txtCountry has to start with capital letter and no digits allowed
                JOptionPane.showMessageDialog(null, "Country has to start with capital"
                        + " letter and no digits allowed. Please correct Your Country.");
                txtCountry.requestFocusInWindow();
                return;
            }
        }

        // check Town - first capital letter and no digits allowed
        String patternTown = "^[A-Z][\\-\\'\\sa-zA-Z]*[a-z]$";
        /* code inside two conditions will execute only if field is not empty
         * and string is not valid (not match the regular expression) */
        if (!txtTown.getText().isEmpty()) {
            if (!isPattern(txtTown.getText(), patternTown)) {
                // txtCountry has to start with capital letter and no digits allowed
                JOptionPane.showMessageDialog(null, "Town has to start with capital"
                        + " letter and no digits allowed. Please correct Your Town.");
                txtTown.requestFocusInWindow();
                return;
            }
        }

        /* This part will execute only if there was no return exectuted in above code
         *
         * This connector JAR file needs to be included in the client project’s
	 * class path. The statement Class.forName (“com.mysql.jdbc.driver”)
	 * loads the MySQL Java driver class in memory.
         */
        try {
            // establish java mySQL connection
            Class.forName(DRIVER_CLASS).newInstance();
            con = DriverManager.getConnection(SERVER_ADDRESS, USER, PASSWORD);

            /* create variable String query as sql query to database
             * This query add new player's info (register) to playersinfo table.
             * I am using preparedStatement method to insert data */
            String query = "INSERT INTO `playersinfo` (`nick`, `password`, "
                    + "`name`, `surname`, `email`, `birth`, `country`, `town`, "
                    + "`character`) VALUES (?,?,?,?,?,?,?,?,?)";

            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, txtNick.getText());
            // pfPassword contains array of char, needs to convert to string
            String password = new String(pfPassword.getPassword());
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, txtName.getText());
            preparedStatement.setString(4, txtSurname.getText());
            preparedStatement.setString(5, txtEmail.getText());
            preparedStatement.setString(6, ftxtBirth.getText());
            preparedStatement.setString(7, txtCountry.getText());
            preparedStatement.setString(8, txtTown.getText());
            /* lblCharacterImage has the name of images as child(number).jpg            
             * ex. child4.jpg or child12.jpg
             * Variable imageChildCounter keeps the number of actual displayed image of child
             * Update this integer into the database as varchar */
            preparedStatement.setString(9, Integer.toString(imageChildCounter));
            // execute insert SQL stetement
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Communications link failure. Check the connection with Internet, please.", "Network error", 0);
            e.printStackTrace(System.out);
        } finally {
            /*
	 *  In the finally block, the result set, statement, and connection
	 * are all explicitly closed. This is a VERY good practice to follow
	 * so that database connections do not get leaked when you write
	 * JDBC code.
             */
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }

            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(System.out);
            }
            txtNick.setText("");
            pfPassword.setText("");
            pfConfirmPassword.setText("");
            txtName.setText("");
            txtSurname.setText("");
            txtEmail.setText("");
            ftxtBirth.setText("");
            txtCountry.setText("");
            txtTown.setText("");
            JOptionPane.showMessageDialog(null, "Your new player has been registered succesfully."
                    + " Please Login using Your new Nick and Password", "New Player Ready", 1);

            //open the main menu form
            new LoginForm().setVisible(true);
            //close this form
            this.dispose();
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void txtNickFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtNickFocusLost
        // txtNick field must contains any characters to check nick availability
        if (!txtNick.getText().isEmpty()) { //txtNick.getText().length()!=0
            /*
		 * Connector JAR file needs to be included in the client project’s
		 * class path. The statement Class.forName (“com.mysql.jdbc.driver”)
		 * loads the MySQL Java driver class in memory.
             */
            try {
                // establish java mySQL connection
                Class.forName(DRIVER_CLASS).newInstance();
                con = DriverManager.getConnection(SERVER_ADDRESS, USER, PASSWORD);
                stmt = con.createStatement();
                /* create variable String query as sql query to database
                         * This query check if in database is already register
                         * player's nick with the same name as in txtNick.
                 */
                String query = "Select * from playersinfo where nick = '"
                        + txtNick.getText() + "'";
                // execute INSERT query to database
                rs = stmt.executeQuery(query);
                // execute code in loop if there is at least one row in result
                while (rs.next()) {
                    JOptionPane.showMessageDialog(null, "This nick is taken. Type another nick name please.");
                    txtNick.requestFocusInWindow();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Communications link failure. Check the connection with Internet, please.", "Network error", 0);
                e.printStackTrace(System.out);
            } finally {
                /*
			 *  In the finally block, the result set, statement, and connection
			 * are all explicitly closed. This is a VERY good practice to follow
			 * so that database connections do not get leaked when you write
			 * JDBC code.
                 */
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }//GEN-LAST:event_txtNickFocusLost

    private void lblArrowRightMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArrowRightMouseClicked
        /* this method change an image of child character in the game 
         * when user clicks left button 
         * user can go left or right to choose the image
         * after submiting the data, database will holds the number for chosen image
         */
        // after 15 it starts display images from 1 
        imageChildCounter++;
        if (imageChildCounter > 15) {
            imageChildCounter = 1;
        }
        // set the path where I keep my images
        String temporaryImageURL = "MyLabResources/child" + Integer.toString(imageChildCounter) + ".png";
        URL imageURL = this.getClass().getClassLoader().getResource(temporaryImageURL);
        if (imageURL != null) {
            lblCharacterImage.setIcon(new ImageIcon(imageURL));
        }
    }//GEN-LAST:event_lblArrowRightMouseClicked

    private void lblArrowLeftMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblArrowLeftMouseClicked
        /* this method change an image of child character in the game
         * when user clicks left button
         * user can go left or right to choose the image
         * after submiting the data, database will holds the number for chosen image
         */
        imageChildCounter--;
        if (imageChildCounter < 1) {
            imageChildCounter = 15;
        }
        // set the path where I keep my images
        String temporaryImageURL = "MyLabResources/child" + Integer.toString(imageChildCounter) + ".png";
        URL imageURL = this.getClass().getClassLoader().getResource(temporaryImageURL);
        if (imageURL != null) {
            lblCharacterImage.setIcon(new ImageIcon(imageURL));
        }
    }//GEN-LAST:event_lblArrowLeftMouseClicked
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JFormattedTextField ftxtBirth;
    private javax.swing.JLabel lblArrowLeft;
    private javax.swing.JLabel lblArrowRight;
    private javax.swing.JLabel lblBirth;
    private javax.swing.JLabel lblCharacter;
    private javax.swing.JLabel lblCharacterImage;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblCountry;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNick;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRegisterImage;
    private javax.swing.JLabel lblSurname;
    private javax.swing.JLabel lblTown;
    private javax.swing.JPasswordField pfConfirmPassword;
    private javax.swing.JPasswordField pfPassword;
    private javax.swing.JPanel pnlCharacterHolder;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNick;
    private javax.swing.JTextField txtSurname;
    private javax.swing.JTextField txtTown;
    // End of variables declaration//GEN-END:variables

    /* this function return true if string match the pattern */
    private boolean isPattern(String text, String pattern) {
        // create a Pattern object
        Pattern regex = Pattern.compile(pattern);
        // create a Mathcer object
        Matcher matcher = regex.matcher(text);
        // is string match the regular expression pattern
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
