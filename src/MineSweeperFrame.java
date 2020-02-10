
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/*

 */

public class MineSweeperFrame extends javax.swing.JFrame implements ActionListener {

    private JButton[][] gameTiles;
    private Board gameBoard;
    private int markType = 0; //Aligns with Board.
    private ImageIcon[] tNumImg = new ImageIcon[13]; //array of images
    private int size = 24; // default board size
    private int bombCount = 99; //default bomb count
    private int imgScale = 39; //default image size
    private boolean clickable = true; //if the board can be clicked

    /**
     * Creates new form MineSweeperFrame
     */
    public MineSweeperFrame() {
        initComponents();
        protectFirst.setSelected(true);
        prepImages();
        generateGame();
    }

    public void prepImages() {
        //PREPARE IMAGES AT START TO MINIMIZE LAG DURING GAME - CREATES AN ADDITIONAL 1-2 SECOND DELAY IN INITAL LOAD
        //Far better than resizing each image during the loop, which took up to 15 additional seconds just to load the board
        //Then that took another 2-3 seconds after every click, longer on open tiles.
        imgScale = (int) Math.floor(950 / size);
        tNumImg[0] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile0.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));  //BLANK TILE - REVEALED
        tNumImg[1] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile1.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[2] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile2.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[3] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile3.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[4] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile4.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[5] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile5.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[6] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile6.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[7] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile7.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[8] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile8.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[9] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tile.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));   //BLANK TILE - NOT REVEALED
        tNumImg[10] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tileBOMB.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[11] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tileFLAG.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH));
        tNumImg[12] = new ImageIcon(new ImageIcon(getClass().getResource("/resource/tileQM.png")).getImage().getScaledInstance(imgScale, imgScale, Image.SCALE_SMOOTH)); //QUESTION MARK
        flagBTN.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/resource/tileFLAG.png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        flagBTN.setBackground(new Color(222, 222, 222));
        questionBTN.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/resource/tileQM.png")).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
        flagBTN.setBackground(new Color(222, 222, 222));
    }

    public void generateGame() {
        try {
            size = Integer.parseInt(sizeTF.getText());      //Try-catch to prevent NullPointerExceptions
        } catch (Exception Ignore) {
            size = 24;                                      //if unable to parse, set size to default
        }
        try {
            bombCount = Integer.parseInt(bCountTF.getText());
        } catch (Exception Ignore) {                    //if unable to parse, set bomb count to default
            bombCount = 99;
        }
        if (bombCount >= (size * size)) {                     //if bomb count is too large, reset
            bombCount = (int) Math.floor((size * size) / 2);
        }
        gameBoard = new Board(size, bombCount);
        gameTiles = new JButton[size][size];
        tilePanel.setLayout(new GridLayout(size, size));
        for (int y = 0; y < size; y++) {                    //initialize main board
            for (int x = 0; x < size; x++) {
                gameTiles[y][x] = new JButton();
                gameTiles[y][x].addActionListener(this);
                tilePanel.add(gameTiles[y][x]);
                gameTiles[y][x].setContentAreaFilled(false);
                gameTiles[y][x].setBorder(null);
                gameTiles[y][x].setIcon(tNumImg[9]);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        boolean firstProtect = protectFirst.isSelected();   //Protect first turn, first turn will not be a bomb if true
        if (clickable) {
            if (markType == 0) {                //if not marking
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        if (ae.getSource() == gameTiles[y][x]) {
                            gameBoard.reveal(y, x, firstProtect);
                            if (gameBoard.getLayout()[y][x]) {          //if bomb at tile
                                letEmKnowTheyLost();                //shows all bombs
                            }
                            gameBoard.reveal(y, x, firstProtect);   //set tile as revealed
                        }
                    }
                }
            } else if (markType == 1) {                 //if marking with flag
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        if (ae.getSource() == gameTiles[y][x] && !gameBoard.getRevealedTiles()[y][x]) {
                            if (gameBoard.getMark(y, x) != 0) {
                                gameBoard.markTile(y, x, 0);
                                gameTiles[y][x].setIcon(tNumImg[9]);    //reset to default tile if tile already marked
                            } else {
                                gameBoard.markTile(y, x, 1);        //mark with flag
                            }
                        }
                    }
                }
            } else {                                        //if marking with ?
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        if (ae.getSource() == gameTiles[y][x] && !gameBoard.getRevealedTiles()[y][x]) {
                            if (gameBoard.getMark(y, x) != 0) {
                                gameBoard.markTile(y, x, 0);
                                gameTiles[y][x].setIcon(tNumImg[9]);    //reset to default if tile already marked
                            } else {
                                gameBoard.markTile(y, x, 2);
                            }
                        }
                    }
                }
            }
            showAllRevealed();              //update the game for the user
        }
    }

    public void showAllRevealed() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (gameBoard.getRevealedTiles()[y][x]) {
                    if (gameBoard.getMark(y, x) == 0) {
                        if (gameBoard.getLayout()[y][x]) {
                            gameTiles[y][x].setIcon(tNumImg[10]);                       //if bombed, display a bomb there
                        } else {
                            gameTiles[y][x].setIcon(tNumImg[gameBoard.getNums()[y][x]]);//set tile to adjacent bomb count
                        }
                    }
                } else if (gameBoard.getMark(y, x) == 1) {          //sets tile image to its marker if it is marked
                    gameTiles[y][x].setIcon(tNumImg[11]);
                } else if (gameBoard.getMark(y, x) == 2) {
                    gameTiles[y][x].setIcon(tNumImg[12]);
                }
            }
        }
    }

    public void revealAll() {                           //reveals all tiles w/o protecting the first reveal
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                gameBoard.reveal(y, x, false);
            }
        }
    }

    public void letEmKnowTheyLost() {               //displays all bombs
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (gameBoard.getLayout()[y][x]) {
                    gameTiles[y][x].setIcon(tNumImg[10]);
                }
            }
        }
        clickable = false;                      //disables interaction with the board
    }

    public void newGame() {
        protectFirst.setSelected(true);                //default to protecting first turn
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                gameTiles[y][x].setIcon(tNumImg[9]);
            }
        }                                               //set new size and bomb count
        try {
            size = Integer.parseInt(sizeTF.getText());
        } catch (Exception Ignore) {
            size = 24;
        }
        try {
            bombCount = Integer.parseInt(bCountTF.getText());
        } catch (Exception Ignore) {
            bombCount = 99;
        }
        if (bombCount >= (size * size)) {                     //if bomb count is too large, reset
            bombCount = (int) Math.floor((size * size) / 2);
        }
        tilePanel.invalidate();
        tilePanel.setVisible(false);
        tilePanel.removeAll();
        tilePanel.setVisible(true);
        prepImages();
        tilePanel.setMaximumSize(new java.awt.Dimension(950, 950));     //new game, has to redeclare tilePanel as it is removed first
        tilePanel.setMinimumSize(new java.awt.Dimension(1000, 1000));
        tilePanel.setPreferredSize(new java.awt.Dimension(950, 950));
        javax.swing.GroupLayout tilePanelLayout = new javax.swing.GroupLayout(tilePanel);
        tilePanel.setLayout(tilePanelLayout);
        tilePanelLayout.setHorizontalGroup(
                tilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1000, Short.MAX_VALUE)
        );
        tilePanelLayout.setVerticalGroup(
                tilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1000, Short.MAX_VALUE)
        );
        getContentPane().add(tilePanel);
        tilePanel.setBounds(20, 10, 1000, 1000); //done redeclaring tilePanel
        gameBoard.resetBombCount();             //sets bomb count to 0
        gameBoard = new Board(size, bombCount);     //sets board to size and bomb count
        gameTiles = new JButton[size][size];        //creates new JButton array
        tilePanel.setLayout(new GridLayout(size, size));        //new Grid
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {            //initializes JButtons w/ icons
                gameTiles[y][x] = new JButton();
                gameTiles[y][x].addActionListener(this);
                tilePanel.add(gameTiles[y][x]);
                gameTiles[y][x].setContentAreaFilled(false);
                gameTiles[y][x].setBorder(null);
                gameTiles[y][x].setIcon(tNumImg[9]);
            }
        }
        gameBoard.setFirstTurn();           //sets to first turn
        clickable = true;                   //user can click
        System.out.println(gameBoard.toString());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tilePanel = new javax.swing.JPanel();
        showAllBT = new javax.swing.JButton();
        protectFirst = new javax.swing.JCheckBox();
        flagBTN = new javax.swing.JButton();
        questionBTN = new javax.swing.JButton();
        resetBTN = new javax.swing.JButton();
        bCountTF = new javax.swing.JTextField();
        sizeTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1920, 1080));
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setPreferredSize(new java.awt.Dimension(1920, 1080));
        getContentPane().setLayout(null);

        tilePanel.setMaximumSize(new java.awt.Dimension(950, 950));
        tilePanel.setMinimumSize(new java.awt.Dimension(1000, 1000));
        tilePanel.setPreferredSize(new java.awt.Dimension(950, 950));

        javax.swing.GroupLayout tilePanelLayout = new javax.swing.GroupLayout(tilePanel);
        tilePanel.setLayout(tilePanelLayout);
        tilePanelLayout.setHorizontalGroup(
            tilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        tilePanelLayout.setVerticalGroup(
            tilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );

        getContentPane().add(tilePanel);
        tilePanel.setBounds(20, 10, 1000, 1000);

        showAllBT.setFont(new java.awt.Font("Counter-Strike", 0, 14)); // NOI18N
        showAllBT.setText("Show");
        showAllBT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllBTActionPerformed(evt);
            }
        });
        getContentPane().add(showAllBT);
        showAllBT.setBounds(1400, 210, 90, 40);

        protectFirst.setFont(new java.awt.Font("Counter-Strike", 0, 14)); // NOI18N
        protectFirst.setText("Protect First Turn");
        protectFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                protectFirstActionPerformed(evt);
            }
        });
        getContentPane().add(protectFirst);
        protectFirst.setBounds(1400, 260, 190, 30);

        flagBTN.setFont(new java.awt.Font("Counter-Strike", 1, 24)); // NOI18N
        flagBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flagBTNActionPerformed(evt);
            }
        });
        getContentPane().add(flagBTN);
        flagBTN.setBounds(1400, 70, 90, 90);

        questionBTN.setFont(new java.awt.Font("Counter-Strike", 1, 24)); // NOI18N
        questionBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                questionBTNActionPerformed(evt);
            }
        });
        getContentPane().add(questionBTN);
        questionBTN.setBounds(1490, 70, 90, 90);

        resetBTN.setFont(new java.awt.Font("Counter-Strike", 0, 14)); // NOI18N
        resetBTN.setText("Reset");
        resetBTN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBTNActionPerformed(evt);
            }
        });
        getContentPane().add(resetBTN);
        resetBTN.setBounds(1490, 210, 90, 40);

        bCountTF.setText("99");
        getContentPane().add(bCountTF);
        bCountTF.setBounds(1490, 340, 90, 30);

        sizeTF.setText("24");
        sizeTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeTFActionPerformed(evt);
            }
        });
        getContentPane().add(sizeTF);
        sizeTF.setBounds(1400, 340, 90, 30);

        jLabel1.setText("Bomb Count");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(1490, 320, 70, 15);

        jLabel2.setText("Size");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(1410, 320, 70, 15);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void showAllBTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllBTActionPerformed
        revealAll();
        showAllRevealed();
        letEmKnowTheyLost();
    }//GEN-LAST:event_showAllBTActionPerformed

    private void protectFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_protectFirstActionPerformed
    }//GEN-LAST:event_protectFirstActionPerformed

    private void flagBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flagBTNActionPerformed
        if (markType != 1) {                        //alters button color when active
            flagBTN.setBackground(Color.GREEN);
            questionBTN.setBackground(new Color(222, 222, 222));
            markType = 1;                           //sets to flag marker
        } else {
            flagBTN.setBackground(new Color(222, 222, 222));
            questionBTN.setBackground(new Color(222, 222, 222));
            markType = 0;                           //sets to no marker
        }
    }//GEN-LAST:event_flagBTNActionPerformed

    private void questionBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_questionBTNActionPerformed
        if (markType != 2) {                        //alters button color when active
            questionBTN.setBackground(Color.GREEN);
            flagBTN.setBackground(new Color(222, 222, 222));
            markType = 2;                           //sets to ? marker
        } else {
            questionBTN.setBackground(new Color(222, 222, 222));
            flagBTN.setBackground(new Color(222, 222, 222));
            markType = 0;                           //sets to no marker
        }
    }//GEN-LAST:event_questionBTNActionPerformed

    private void resetBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBTNActionPerformed
        newGame();
    }//GEN-LAST:event_resetBTNActionPerformed

    private void sizeTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sizeTFActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MineSweeperFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MineSweeperFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MineSweeperFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MineSweeperFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MineSweeperFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bCountTF;
    private javax.swing.JButton flagBTN;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JCheckBox protectFirst;
    private javax.swing.JButton questionBTN;
    private javax.swing.JButton resetBTN;
    private javax.swing.JButton showAllBT;
    private javax.swing.JTextField sizeTF;
    private javax.swing.JPanel tilePanel;
    // End of variables declaration//GEN-END:variables

}
