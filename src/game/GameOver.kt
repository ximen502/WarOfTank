package game

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class GameOver
 * @Description 游戏结束
 * @Author xsc
 * @Date 2024/1/2 下午5:58
 * @Version 1.0
 */
class GameOver(ground: Ground) : GameObject() {
    private var img: BufferedImage? = null
    var showing = false

    init {
        img = ImageIO.read(javaClass.getResource("image/gameover.png"))
        w = img?.width!!
        h = img?.height!!
        x = (ground.width - w) / 2
        y = (ground.height - h) / 2
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(img, x, y, null)
    }

    override fun onTick() {

    }
}