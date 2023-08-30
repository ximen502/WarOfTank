package game.map

import game.CP
import game.GOObserver
import game.GameObject
import game.Shells
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * 木头或砖块类，炮弹可以打碎的方块
 *
 * @author xsc
 */
class Brick : GameObject() {
    //1.实现砖块绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var brick: BufferedImage? = null
    var shells: Shells? = null
    val ONE4TH = CP.SIZE * 0.25f
    var observer: GOObserver? = null

    init {
        val path = javaClass.getResource("../image/wood.png")
        println(path)
        brick = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        g2.drawImage(brick, x, y, null)
    }

    override fun onTick() {
        when (shells?.direction) {
            Shells.DIRECTION_NORTH -> {
                //炮弹向北，南侧被命中
                var newH = (h - ONE4TH).toInt()
                println("=====h:$h, 1/4:${ONE4TH.toInt()}, newH:$newH")
                //砖块彻底消失
                if (newH <= 0) {
                    isDestroyed = true
                    h = 0
                    w = 0
                    observer?.die(this)
                } else {
                    brick = brick?.getSubimage(0, 0, w, newH)
                    h = newH
                }
                shells = null
            }
            else -> {
            }
        }
    }
}