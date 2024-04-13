package game.prop

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class Bomb
 * @Description 炸弹道具类，吃掉此道具后，地图上的敌军坦克全部被摧毁
 * @Author xsc
 * @Date 2024/4/13 上午8:36
 * @Version 1.0
 */
class Bomb : BaseGameObject() {
    private var image: BufferedImage
    private var counter = 0
    private var imageArray = arrayOfNulls<BufferedImage>(4)
    private var index = 0

    init {
        val img0 = ImageIO.read(javaClass.getResource("/game/image/bomb01.png"))
        val img1 = ImageIO.read(javaClass.getResource("/game/image/bomb02.png"))
        val img2 = ImageIO.read(javaClass.getResource("/game/image/bomb03.png"))
        val img3 = ImageIO.read(javaClass.getResource("/game/image/bomb04.png"))
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