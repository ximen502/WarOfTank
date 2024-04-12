package game.prop

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class PropTank
 * @Description 道具坦克，吃掉后可以增加一条生命
 * @Author xsc
 * @Date 2024/4/12 上午10:38
 * @Version 1.0
 */
class PropTank : BaseGameObject() {
    private var image: BufferedImage
    private var counter = 0
    private var imageArray = arrayOfNulls<BufferedImage>(4)
    private var index = 0

    init {
        val img0 = ImageIO.read(javaClass.getResource("/game/image/proptank01.png"))
        val img1 = ImageIO.read(javaClass.getResource("/game/image/proptank02.png"))
        val img2 = ImageIO.read(javaClass.getResource("/game/image/proptank03.png"))
        val img3 = ImageIO.read(javaClass.getResource("/game/image/proptank04.png"))
        imageArray[0] = img0
        imageArray[1] = img1
        imageArray[2] = img2
        imageArray[3] = img3
        image = img0
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(image, x, y, null)
    }

    override fun onTick() {
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