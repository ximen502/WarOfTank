package game;

import javax.swing.SwingUtilities;

public class LaunchGame {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            GameWindow gameWindow = new GameWindow(800, 600, "坦克大战[ximen502]");
        };
        SwingUtilities.invokeLater(runnable);
        System.out.println(System.currentTimeMillis());
    }
}