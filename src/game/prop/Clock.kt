package game.prop

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class Clock
 * @Description 钟表道具，玩家吃掉钟表，敌军坦克会停止移动
 * @Author xsc
 * @Date 2024/4/15 下午4:14
 * @Version 1.0
 */
class Clock : PropObject() {
    private var image: BufferedImage
    private var counter = 0
    private var imageArray = arrayOfNulls<BufferedImage>(3)
    private var index = 0

    init {
        val img0 = ImageIO.read(javaClass.getResource("/game/image/clock01.png"))
        val img1 = ImageIO.read(javaClass.getResource("/game/image/clock02.png"))
        val img2 = ImageIO.read(javaClass.getResource("/game/image/clock03.png"))
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