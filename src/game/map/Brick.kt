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

    var observer: GOObserver? = null
    // 表示形状的数字，可以根据数字获得目前砖的形状
    // 每个小砖块可被炮弹打成4个部分
    // [1][2]
    // [4][8]
    // 00001111B
    var shape = 0x0F

    init {
        val path = javaClass.getResource("../image/wood_m.png")
        println(path)
        img = ImageIO.read(path)
    }

    companion object {
        const val SIZE_M = CP.SIZE_M
        // 如果尺寸/2得到的是偶数，下面这个变量可以去掉
        const val ONE2ND_F = CP.SIZE_M * 0.5f
        const val ONE2ND_I = CP.SIZE_M / 2
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        g2.drawImage(img, x, y, null)
    }

    override fun onTick() {
        shells?.let {
            when (it.direction) {
                Shells.DIRECTION_NORTH -> {
                    //炮弹向北，南侧被命中
                    if (it.level <= Shells.LEVEL1) {//只能消除1层
                        var newH = (h - ONE2ND_F).toInt()
                        println("=====h:$h, 1/4:$ONE2ND_I, newH:$newH")
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
                    } else {//可以消除2层
                        var newH = (h - SIZE_M).toInt()
                        println("=====oldH:$h, 1/2:$SIZE_M, newH:$newH")
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
                    }
                    shells = null
                }
                Shells.DIRECTION_SOUTH -> {
                    //炮弹向南，北侧被命中
                    if (it.level <= Shells.LEVEL1) {//只能消除1层
                        var newH = h - ONE2ND_I
                        println("=====h:$h, 1/4:$ONE2ND_I, newH:$newH, y:$y")
                        //砖块彻底消失(剩余高度不足1/4)
                        if (newH < ONE2ND_F) {
                            isDestroyed = true
                            h = 0
                            w = 0
                            observer?.die(this)
                        } else {
                            println("砖块w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                            img = img?.getSubimage(0, ONE2ND_I, w, newH)
                            h = newH
                            y += ONE2ND_I
                        }
                    } else {//可以消除2层
                        var newH = h - SIZE_M
                        println("=====oldH:$h, 1/2:$SIZE_M, newH:$newH, y:$y")
                        //彻底消失(剩余高度1/2)
                        if (newH < SIZE_M) {
                            isDestroyed = true
                            h = 0
                            w = 0
                            observer?.die(this)
                        } else {
                            println("钢铁w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                            img = img?.getSubimage(0, SIZE_M, w, newH)
                            h = newH
                            y += SIZE_M
                        }
                    }
                    shells = null
                }
                Shells.DIRECTION_WEST -> {
                    //炮弹向西，东侧被命中
                    if (it.level <= Shells.LEVEL1) {//只能消除1层
                        var newW = (w - ONE2ND_F).toInt()
                        println("=====w:$w, 1/4:$ONE2ND_I, newW:$newW")
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
                    } else {//可以消除2层
                        var newW = (w - SIZE_M).toInt()
                        println("=====oldW:$w, 1/2:$SIZE_M, newW:$newW")
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
                    }
                    shells = null
                }
                Shells.DIRECTION_EAST -> {
                    //炮弹向东，西侧被命中
                    if (it.level <= Shells.LEVEL1) {//只能消除1层
                        var newW = w - ONE2ND_I
                        println("=====w:$w, 1/4:$ONE2ND_I, newW:$newW")
                        //砖块彻底消失(剩余宽度不足1/4)
                        if (newW < ONE2ND_F) {
                            isDestroyed = true
                            w = 0
                            h = 0
                            observer?.die(this)
                        } else {
                            println("砖块w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                            img = img?.getSubimage(ONE2ND_I, 0, newW, h)
                            w = newW
                            x += ONE2ND_I
                        }
                    } else {//可以消除2层
                        var newW = w - SIZE_M
                        println("=====oldW:$w, 1/2:$SIZE_M, newW:$newW")
                        //钢铁彻底消失(剩余宽度不足1/2)
                        if (newW < SIZE_M) {
                            isDestroyed = true
                            w = 0
                            h = 0
                            observer?.die(this)
                        } else {
                            println("钢铁w:$w, h:$h, x:$x, y:$y, maxY:$maxY img w:${img?.width}, img h:${img?.height}")
                            img = img?.getSubimage(SIZE_M, 0, newW, h)
                            w = newW
                            x += SIZE_M
                        }
                    }
                    shells = null
                }
                else -> {
                }
            }
        }
    }
}