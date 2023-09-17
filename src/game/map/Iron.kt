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
 * @Class Iron
 * @Description 钢铁方块类，炮弹升级到一定级别才可以打烂
 * @Author xsc
 * @Date 2023/8/19 上午10:52
 * @Version 1.0
 */
class Iron : GameObject() {
    //1.实现钢铁绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var img: BufferedImage? = null
    var shells: Shells? = null
    var observer: GOObserver? = null
    val ONE2ND = CP.SIZE / 2

    init {
        val path = javaClass.getResource("../image/iron.png")
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
                shells?.let {
                    if (it.level >= Shells.LEVEL3) {//炮弹可以破坏铁块
                        var newH = (h - ONE2ND).toInt()
                        println("=====oldH:$h, 1/2:$ONE2ND, newH:$newH")
                        //彻底消失
                        if (newH <= 0) {
                            isDestroyed = true
                            h = 0
                            w = 0
                            observer?.die(this)
                        } else {
                            img = img?.getSubimage(0, 0, w, newH)
                            h = newH
                        }
                    } else {//炮弹不可以破坏铁块
                        println("炮弹级别不够，无法消除钢铁")
                    }
                }
                shells = null
            }
            Shells.DIRECTION_SOUTH -> {
                //炮弹向南，北侧被命中
                shells?.let {
                    if (it.level >= Shells.LEVEL3) {//炮弹可以破坏铁块
                        var newH = h - ONE2ND
                        println("=====oldH:$h, 1/2:$ONE2ND, newH:$newH, y:$y")
                        //彻底消失(剩余高度1/2)
                        if (newH < ONE2ND) {
                            isDestroyed = true
                            h = 0
                            w = 0
                            observer?.die(this)
                        } else {
                            println("钢铁w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                            img = img?.getSubimage(0, ONE2ND, w, newH)
                            h = newH
                            y += ONE2ND
                        }
                    } else {//炮弹不可以破坏铁块
                        println("炮弹级别不够，无法消除钢铁")
                    }
                }
                shells = null
            }
            Shells.DIRECTION_WEST -> {
                //炮弹向西，东侧被命中
                shells?.let {
                    if (it.level >= Shells.LEVEL3) {//炮弹可以破坏铁块
                        var newW = (w - ONE2ND).toInt()
                        println("=====oldW:$w, 1/2:$ONE2ND, newW:$newW")
                        //钢铁彻底消失
                        if (newW <= 0) {
                            isDestroyed = true
                            w = 0
                            h = 0
                            observer?.die(this)
                        } else {
                            img = img?.getSubimage(0, 0, newW, h)
                            w = newW
                        }
                    } else {//炮弹不可以破坏铁块
                        println("炮弹级别不够，无法消除钢铁")
                    }
                }
                shells = null
            }
            Shells.DIRECTION_EAST -> {
                //炮弹向东，西侧被命中
                shells?.let {
                    if (it.level >= Shells.LEVEL3) {//炮弹可以破坏铁块
                        var newW = w - ONE2ND
                        println("=====oldW:$w, 1/2:$ONE2ND, newW:$newW")
                        //钢铁彻底消失(剩余宽度不足1/2)
                        if (newW < ONE2ND) {
                            isDestroyed = true
                            w = 0
                            h = 0
                            observer?.die(this)
                        } else {
                            println("钢铁w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                            img = img?.getSubimage(ONE2ND, 0, newW, h)
                            w = newW
                            x += ONE2ND
                        }
                    } else {//炮弹不可以破坏铁块
                        println("炮弹级别不够，无法消除钢铁")
                    }
                }
                shells = null
            }
            else -> {
            }
        }
    }
}