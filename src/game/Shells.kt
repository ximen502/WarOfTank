package game

import java.awt.Graphics
import java.awt.Graphics2D

/**
 * 炮弹类，按键后从坦克炮筒发出的炮弹
 */
class Shells : GameObject() {
    private var speed = 2 * 2 * 2 * 2
    var direction = 0

    companion object {
        const val DIRECTION_EAST = 1
        const val DIRECTION_WEST = 2
        const val DIRECTION_SOUTH = 4
        const val DIRECTION_NORTH = 8
    }

    override fun draw(g: Graphics?) {
        super.draw(g)
        var g2 = g as Graphics2D
//        g2.fillRect(x, y, 20, 20)
        // 炮弹直径
        val d = 10
        g2.fillOval(x, y, d, d)
    }

    override fun onTick() {
        super.onTick()
        when (direction) {
            DIRECTION_NORTH -> transfer(0, -speed)
            DIRECTION_SOUTH -> transfer(0, speed)
            DIRECTION_WEST -> transfer(-speed, 0)
            DIRECTION_EAST -> transfer(speed, 0)
            else -> transfer(0, 0)
        }
    }

}