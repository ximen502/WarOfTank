package game.tile

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
class Eagle : TileObject() {
    //1.实现基地老鹰绘制
    private var imgAlive: BufferedImage? = null
    private var imgNow: BufferedImage? = null
    private var imgDead: BufferedImage? = null

    init {
        val path = javaClass.getResource("/game/image/eagle.png")
        //println(path)
        imgAlive = ImageIO.read(path)
        imgDead = ImageIO.read(javaClass.getResource("/game/image/eagle_dead.png"))
        imgNow = imgAlive
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(imgNow, x, y, null)
    }

    override fun onTick() {
        imgNow = if (isDestroyed) {
            imgDead
        } else {
            imgAlive
        }
    }
}