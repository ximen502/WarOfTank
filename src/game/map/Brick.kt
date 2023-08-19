package game.map

import game.GameObject
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import javax.imageio.ImageIO

/**
 * 木头或砖块类，炮弹可以打碎的方块
 *
 * @author xsc
 */
class Brick : GameObject() {
    //1.实现砖块绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var brick: Image? = null

    init {
        val path = javaClass.getResource("image/wood.png")
        println(path)
        brick = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        g2.drawImage(brick, x, y, null)
    }

    override fun onTick() {

    }
}