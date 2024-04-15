package game.prop

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class Star
 * @Description 星星道具，用于增强玩家坦克火力，改变坦克外观
 * @Author xsc
 * @Date 2023/12/28 上午11:13
 * @Version 1.0
 */
class Star : PropObject() {
    private var image: BufferedImage
    private var counter = 0
    var images = arrayOfNulls<BufferedImage>(4)
    var index = 0

    init {
        var img0 = ImageIO.read(javaClass.getResource("/game/image/star01.png"))
        var img1 = ImageIO.read(javaClass.getResource("/game/image/star02.png"))
        var img2 = ImageIO.read(javaClass.getResource("/game/image/star03.png"))
        var img3 = ImageIO.read(javaClass.getResource("/game/image/star04.png"))
        images[0] = img0
        images[1] = img1
        images[2] = img2
        images[3] = img3
        image = img0
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        g2.drawImage(image, x, y, null)
    }

    override fun onTick() {
        super.onTick()
        image = images[index]!!
        if (counter >= 20) {
            index++
            counter = 0
            if (index >= images.size) {
                index = 0
            }
        }
        counter++
    }
}