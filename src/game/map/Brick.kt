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
    var img: BufferedImage? = null
    var shells: Shells? = null
    val ONE4TH = CP.SIZE * 0.25f
    val ONE4THINT = ONE4TH.toInt()
    var observer: GOObserver? = null

    init {
        val path = javaClass.getResource("../image/wood.png")
        println(path)
        img = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        g2.drawImage(img, x, y, null)
    }

    override fun onTick() {
        when (shells?.direction) {
            Shells.DIRECTION_NORTH -> {
                //炮弹向北，南侧被命中
                var newH = (h - ONE4TH).toInt()
                println("=====h:$h, 1/4:$ONE4THINT, newH:$newH")
                //砖块彻底消失
                if (newH <= 0) {
                    isDestroyed = true
                    h = 0
                    w = 0
                    observer?.die(this)
                } else {
                    img = img?.getSubimage(0, 0, w, newH)
                    h = newH
                }
                shells = null
            }
            Shells.DIRECTION_SOUTH -> {
                //炮弹向南，北侧被命中
                var newH = h - ONE4THINT
                println("=====h:$h, 1/4:$ONE4THINT, newH:$newH, y:$y")
                //砖块彻底消失(剩余高度不足1/4)
                if (newH < ONE4TH) {
                    isDestroyed = true
                    h = 0
                    w = 0
                    observer?.die(this)
                } else {
                    println("砖块w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                    img = img?.getSubimage(0, ONE4THINT, w, newH)
                    h = newH
                    y += ONE4THINT
                }
                shells = null
            }
            Shells.DIRECTION_WEST -> {
                //炮弹向西，东侧被命中
                var newW = (w - ONE4TH).toInt()
                println("=====w:$w, 1/4:$ONE4THINT, newW:$newW")
                //砖块彻底消失
                if (newW <= 0) {
                    isDestroyed = true
                    w = 0
                    h = 0
                    observer?.die(this)
                } else {
                    img = img?.getSubimage(0, 0, newW, h)
                    w = newW
                }
                shells = null
            }
            Shells.DIRECTION_EAST -> {
                //炮弹向东，西侧被命中
                var newW = w - ONE4THINT
                println("=====w:$w, 1/4:$ONE4THINT, newW:$newW")
                //砖块彻底消失(剩余宽度不足1/4)
                if (newW < ONE4TH) {
                    isDestroyed = true
                    w = 0
                    h = 0
                    observer?.die(this)
                } else {
                    println("砖块w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                    img = img?.getSubimage(ONE4THINT, 0, newW, h)
                    w = newW
                    x += ONE4THINT
                }
                shells = null
            }
            else -> {
            }
        }
    }
}