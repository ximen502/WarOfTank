package game.prop

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class Castle
 * @Description 城堡道具类，当玩家吃掉这个道具，老巢周围的砖头会变成钢铁，20s后恢复砖头
 * @Author xsc
 * @Date 2024/4/14 下午2:41
 * @Version 1.0
 */
class Castle : BaseGameObject(){
    private var image: BufferedImage
    private var counter = 0
    private var imageArray = arrayOfNulls<BufferedImage>(3)
    private var index = 0

    init {
        val img0 = ImageIO.read(javaClass.getResource("/game/image/castle01.png"))
        val img1 = ImageIO.read(javaClass.getResource("/game/image/castle02.png"))
        val img2 = ImageIO.read(javaClass.getResource("/game/image/castle03.png"))
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