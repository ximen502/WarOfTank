package game.prop

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class Shield
 * @Description 无敌的护盾，当吃掉这个道具，将获得一段时间的无敌状态
 * @Author xsc
 * @Date 2024/4/10 下午10:12
 * @Version 1.0
 */
class Shield : PropObject() {
    private var image: BufferedImage
    private var counter = 0
    private var imageArray = arrayOfNulls<BufferedImage>(3)
    private var index = 0

    init {
        val img0 = ImageIO.read(javaClass.getResource("/game/image/shield01.png"))
        val img1 = ImageIO.read(javaClass.getResource("/game/image/shield02.png"))
        val img2 = ImageIO.read(javaClass.getResource("/game/image/shield03.png"))
        imageArray[0] = img0
        imageArray[1] = img1
        imageArray[2] = img2
        image = img0
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(image, x, y, null)
    }

    override fun onTick() {
        super.onTick()
        image = imageArray[index]!!
        if (counter >= 20) {
            index++
            counter = 0
            if (index >= imageArray.size) {
                index = 0
            }
        }
        counter++
    }
}