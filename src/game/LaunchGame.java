package game;

import javax.swing.*;

public class LaunchGame {
    public static void main(String[] args) {
        Runnable runnable = () -> {
            GameWindow gameWindow = new GameWindow(800, 600, "Tank War");
        };
        SwingUtilities.invokeLater(runnable);
        System.out.println(System.currentTimeMillis());
    }
}