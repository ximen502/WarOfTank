package game.tank

import game.CP
import game.GOObserver
import game.GameObject
import game.Ground
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D

/**
 * @Class Producing
 * @Description 坦克产生中的动画效果
 * @Author xsc
 * @Date 2023/12/18 下午2:52
 * @Version 1.0
 */
class Producing(ground: Ground, position: Int) : GameObject() {

    var r = 0.0
    var xNum = 0.0
    private val offset = CP.TANK_SIZE / 2
    var alpha = 0xff
    var color = Color(0xff, 0, 0, alpha)

    var ttl = 0
    var observer: GOObserver? = null

    companion object {
        const val READY = 36
        const val GONE = 67
    }

    init {
        xNum = x.toDouble()
        when (position) {
            CP.BORN_1 -> {
                x = (CP.SIZE - CP.TANK_SIZE) / 2
                y = (CP.SIZE - CP.TANK_SIZE) / 2

                println("cx:$cx, cy:$cy")
            }
            CP.BORN_2 -> {
                x = ground.width / 2 - CP.TANK_SIZE / 2
                y = (CP.SIZE - CP.TANK_SIZE) / 2
            }
            CP.BORN_3 -> {
                x = ground.width - CP.SIZE + (CP.SIZE - CP.TANK_SIZE) / 2
                y = (CP.SIZE - CP.TANK_SIZE) / 2
            }
            CP.BORN_4 -> {
                x = ground.width / 2 - CP.SIZE_M * 5 + (CP.SIZE - CP.TANK_SIZE) / 2
                y = ground.height - CP.SIZE_M * 2 + (CP.SIZE - CP.TANK_SIZE) / 2
            }
            else -> {}
        }
    }

    override fun onTick() {
        xNum += 0.14 //61*69
        r = Math.sin(xNum)
        if (xNum >= 2 * Math.PI) {
            xNum = 0.0
        }
        ttl++

        if (ttl >= GONE) {
            observer?.die(this)
        }
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        if (ttl >= READY) {
            alpha-=9
            if (alpha < 0) {
                alpha = 0
            }
            color = Color(0xff, 0,0, alpha)
        }
        g2.color = color
        val size = (10 + 15 * r)
        var shape = Ellipse2D.Double(x + offset - size / 2.0, y + offset - size / 2.0, size, size)
        g2.fill(shape)
    }
}