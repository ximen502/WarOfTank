package game

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.util.*
import javax.imageio.ImageIO

/**
 * 抽象的坦克类，一些公共行为或属性抽取
 *
 * @author ximen502
 */
abstract class AbstractTank : GameObject() {

    companion object {
        const val SIZE = CP.SIZE
    }
    // 坦克前进的方向
    var direction = 0

    //炮筒(需要根据行走方向调整指向)
    val ptRadius = 6
    val ptLength = 36
    val arc = 2

    // 炮弹初始位置坐标
    protected var shellsX = 0
    protected var shellsY = 0

    protected var speed = 2.5
    //炮弹速度倍数
    protected var times = 4
    // 炮弹容器
    var shellsList: ArrayList<Shells> = ArrayList()

    protected fun drawTank(g: Graphics?) {
        var g2 = g as Graphics2D

        val offset = SIZE / 2
        //车身
        g2?.drawRect(x /*- offset*/, y /*- offset*/, SIZE, SIZE)
        //炮台
        var offsetOval = offset / 2
        var sizeOval = SIZE / 2
        g2?.drawOval(x + offsetOval, y + offsetOval, sizeOval, sizeOval)

        //炮筒
        if (direction == Shells.DIRECTION_WEST) {
            shellsX = x + offset - ptLength
            shellsY = y + offset - ptRadius / 2
            g2?.fillRoundRect(x + offset - ptLength, y + offset - ptRadius / 2, ptLength, ptRadius, arc, arc)
        } else if (direction == Shells.DIRECTION_EAST) {
            shellsX = x + offset + ptLength
            shellsY = y + offset - ptRadius / 2
            g2?.fillRoundRect(x + offset, y + offset - ptRadius / 2, ptLength, ptRadius, arc, arc)
        } else if (direction == Shells.DIRECTION_NORTH) {
            shellsX = x + offset - ptRadius / 2
            shellsY = y + offset - ptLength
            g2?.fillRoundRect(x + offset - ptRadius / 2, y + offset - ptLength, ptRadius, ptLength, arc, arc)
        } else if (direction == Shells.DIRECTION_SOUTH) {
            shellsX = x + offset - ptRadius / 2
            shellsY = y + offset + ptLength
            g2?.fillRoundRect(x + offset - ptRadius / 2, y + offset, ptRadius, ptLength, arc, arc)
        }

    }

}