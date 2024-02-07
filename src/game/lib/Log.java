package game.lib;

/**
 * Class:Log.java
 * Author:xsc
 * Date:2024-01-29 21:48
 */
public class Log {
    public static final boolean debug = true;

    public static void i(String info) {
        if (debug) {
            System.out.println(info);
        }
    }
}
