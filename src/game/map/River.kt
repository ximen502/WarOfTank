package game.map

import game.GameObject
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import javax.imageio.ImageIO

/**
 * @Class River
 * @Description 河流方块，这个坦克无法穿越，子弹可以通过
 * @Author xsc
 * @Date 2023/8/19 上午10:54
 * @Version 1.0
 */
class River : GameObject() {
    //1.实现河流绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var river: Image? = null

    init {
        val path = javaClass.getResource("../image/river.png")
        println(path)
        river = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(river, x, y, null)
    }

    override fun onTick() {

    }
}