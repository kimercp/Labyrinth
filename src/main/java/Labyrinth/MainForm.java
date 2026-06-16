package Labyrinth;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class MainForm extends javax.swing.JFrame {

    // object to connect with database
    private MyConnection myConnection;    
    // user's nick
    private String nick;
    // user's character
    private String character;
    // rules form 
    private RulesForm rules;
    // form with records the best results
    private WallForm wall;
            
    // first constructor
    public MainForm() {
        initComponents();
    }

    // second constructor with passing nick and character value from LoginForm
    public MainForm(String userNickFromDatabase, String userCharacterFromDatabase) {
        // get player's nick and character from passed attributes
        nick = userNickFromDatabase;
        character = userCharacterFromDatabase;
        
        initComponents();
        
        // display players name as greetings when form shown
        lblWelcome.setText("Welcome " + nick);

        /* code for display player's character next to "Welcome `player's nick` 
         * image is half size of the original image from MyLabResources
         * this code execute after initComponents() method
         * which means post-creation code of the lblWelcome object
         */
      URL imageURL = this.getClass().getClassLoader().getResource("MyLabResources/child" + character + ".png");
      //URL imageURL = RegisterForm.class.getResource("../MyLabResources/child" + character + ".png");
        if (imageURL != null) {
            // resize the image to 75,100 pxl
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageURL).getImage().getScaledInstance(75, 100, Image.SCALE_DEFAULT));
            // set the image on label
            lblWelcome.setIcon(imageIcon);
        }

        try {
            /* this code create new variable using class Connection
             * the code inside the class constructor will connect with database
             * execute the sql query, counts rows and columns of data
             * create table model.
             * The size of table (rows and columns) depends from data set that
             * sql query return. It means that table is resizeable dynamically.
             */
            myConnection = new MyConnection("select level,points from bestscore where nick='"+ nick +"'");
            // player can select only one row
            tblPlayerScores.setSelectionMode(SINGLE_SELECTION);
            // set model to application table
            tblPlayerScores.setModel(myConnection.getTableModel());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Can't fill up tha table with score data.", "Communications link failure", 0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnExit = new javax.swing.JButton();
        lblWelcome = new javax.swing.JLabel();
        lblYourBestScores = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();
        lblWinnersImage = new javax.swing.JLabel();
        spTableCointainer = new javax.swing.JScrollPane();
        tblPlayerScores = new javax.swing.JTable();
        pnlMain = new javax.swing.JPanel();
        lblRules = new javax.swing.JLabel();
        btnRules = new javax.swing.JButton();
        lblRules1 = new javax.swing.JLabel();
        lblMainBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));
        setMaximumSize(new java.awt.Dimension(750, 650));
        setMinimumSize(new java.awt.Dimension(750, 650));
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(750, 650));
        getContentPane().setLayout(null);

        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit);
        btnExit.setBounds(620, 590, 70, 23);

        lblWelcome.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(102, 102, 102));
        lblWelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWelcome.setText("Welcome");
        getContentPane().add(lblWelcome);
        lblWelcome.setBounds(30, 90, 310, 200);

        lblYourBestScores.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblYourBestScores.setForeground(new java.awt.Color(113, 19, 19));
        lblYourBestScores.setText("Your Best Scores");
        getContentPane().add(lblYourBestScores);
        lblYourBestScores.setBounds(50, 240, 180, 19);

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });
        getContentPane().add(btnStart);
        btnStart.setBounds(50, 590, 70, 23);

        lblWinnersImage.setIcon(new ImageIcon(getClass().getResource("/MyLabResources/winners100x75.png"))); // NOI18N
        lblWinnersImage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblWinnersImageMouseClicked(evt);
            }
        });
        getContentPane().add(lblWinnersImage);
        lblWinnersImage.setBounds(330, 570, 100, 75);

        spTableCointainer.setViewportView(tblPlayerScores);

        getContentPane().add(spTableCointainer);
        spTableCointainer.setBounds(50, 260, 640, 300);

        pnlMain.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));

        lblRules.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblRules.setForeground(new java.awt.Color(102, 102, 102));
        lblRules.setText("?");

        btnRules.setText("Rules");
        btnRules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRulesActionPerformed(evt);
            }
        });

        lblRules1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        lblRules1.setForeground(new java.awt.Color(102, 102, 102));
        lblRules1.setText("Do You want to read the ");

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap(366, Short.MAX_VALUE)
                .addComponent(lblRules1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRules)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblRules)
                .addGap(16, 16, 16))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnRules, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(lblRules1))
                    .addComponent(lblRules, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(pnlMain);
        pnlMain.setBounds(50, 170, 640, 50);

        lblMainBackground.setIcon(new ImageIcon(getClass().getResource("/MyLabResources/loginImage750x650blank.jpg"))); // NOI18N
        lblMainBackground.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblMainBackground.setMaximumSize(new java.awt.Dimension(750, 650));
        lblMainBackground.setMinimumSize(new java.awt.Dimension(750, 650));
        getContentPane().add(lblMainBackground);
        lblMainBackground.setBounds(0, 0, 750, 650);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // close the main form and back to login form
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        if (JOptionPane.showConfirmDialog(null, "Do You want to quit the game?",
                "Confirm to cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            //open the main menu form
            new LoginForm().setVisible(true);
            //close this form
            this.dispose();
        } else {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        // 1.method of passing user's nick to the constructor of MainForm
        // new MainForm(userNickFromDatabase,userCharacterFromDatabase).setVisible(true);

        /* 2.method of passing user's nick to MainForm class 
         * by method setUser
         * create new main form, pass the nick value, display new form
        example:                 
        MainForm mainForm = new MainForm();
        MainForm.setUser(userNickFromDatabase);
        MainForm.setVisible(true);
         */
        
        // take an integer value of selected table row
        int selectedRow = tblPlayerScores.getSelectedRow();
        // no row selected a value is -1
        if (selectedRow!=-1) {         
            // close the main form
            this.dispose();            
            // start game with selected level and default character
            try {
                // added 1 because first position has value 0 of selected index in tblPlayerScore JTable
                new GameForm(selectedRow+1, nick, character);//.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "MainForm.Start.Something goes wrong. Please try to start the game again, sorry.", "Game error", 0);
                JOptionPane.showMessageDialog(null, e);
                e.printStackTrace(System.out);
            }
        }       
        // start game from level 1 and default character
        else {
            // close this form
            this.dispose();
            // start game from level 1 and default character
            new GameForm(1, nick, character);
        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnRulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRulesActionPerformed
        if (rules == null) {
            rules = new RulesForm(character);
            rules.setVisible(true);
        } else rules.setVisible(true);
    }//GEN-LAST:event_btnRulesActionPerformed

    private void lblWinnersImageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWinnersImageMouseClicked
        if (wall == null) {
            wall = new WallForm();
            wall.setVisible(true);
        } else wall.setVisible(true);
    }//GEN-LAST:event_lblWinnersImageMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnRules;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel lblMainBackground;
    private javax.swing.JLabel lblRules;
    private javax.swing.JLabel lblRules1;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JLabel lblWinnersImage;
    private javax.swing.JLabel lblYourBestScores;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JScrollPane spTableCointainer;
    private javax.swing.JTable tblPlayerScores;
    // End of variables declaration//GEN-END:variables
}
