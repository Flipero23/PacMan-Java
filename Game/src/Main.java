import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception{
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;                          //1 tile is 32x32 pixels
        int boardWidth = columnCount * tileSize;    //Operations to get the total size
        int boardHeight = rowCount * tileSize;      //of the height and width

        JFrame frame = new JFrame("Pac Man");
        frame.setVisible(true);                    //Creation of the game window,making it visible and
        frame.setSize(boardWidth,boardHeight);     //setting the size to the previously initialized width and height
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);                 //The user is not allowed to resize the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);               //Instantiate object and add panel to the window
        frame.pack();
        pacmanGame.requestFocus();
    }
}