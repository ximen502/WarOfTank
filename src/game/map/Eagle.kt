package game.map

import game.GameObject
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class Eagle
 * @Description 基地老鹰
 * @Author xsc
 * @Date 2023/10/20 上午11:43
 * @Version 1.0
 */
class Eagle : GameObject() {
    //1.实现基地老鹰绘制
    var img: BufferedImage? = null

    init {
        val path = javaClass.getResource("../image/eagle.png")
        println(path)
        img = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(img, x, y, null)
    }

    override fun onTick() {

    }
}