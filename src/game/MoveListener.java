package game;

/**
 * 坦克移动监听，开始移动，停止移动
 *
 * @date: 2023-10-30 10:15
 * @author: xsc
 */
public interface MoveListener {
    void begin(int direction);

    void end(int direction);
}
