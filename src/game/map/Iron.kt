package game.map

import game.GameObject
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import javax.imageio.ImageIO

/**
 * @Class Iron
 * @Description 钢铁方块类，炮弹升级到一定级别才可以打烂
 * @Author xsc
 * @Date 2023/8/19 上午10:52
 * @Version 1.0
 */
class Iron : GameObject() {
    //1.实现砖块绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var iron: Image? = null

    init {
        val path = javaClass.getResource("../image/iron.png")
        println(path)
        iron = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        g2.drawImage(iron, x, y, null)
    }

    override fun onTick() {

    }
}