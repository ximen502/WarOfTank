package game

import java.awt.Graphics
import java.awt.Graphics2D
import java.util.*

/**
 * 抽象的坦克类，一些公共行为或属性抽取
 *
 * @author ximen502
 */
abstract class AbstractTank : GameObject() {

    companion object {
        const val SIZE = CP.SIZE
        const val TANK_SIZE = CP.TANK_SIZE
    }
    // 坦克前进的方向
    var direction = 0

    //炮筒(需要根据行走方向调整指向)
    val ptRadius = 4
    val ptLength = 25
    val arc = 2
    val halfSize = TANK_SIZE / 2
    var ptOffset = TANK_SIZE / 4

    // 炮弹初始位置坐标
    protected var shellsX = 0
    protected var shellsY = 0

    protected var speed = 1
    //炮弹速度倍数
    protected var times = 4
    // 炮弹容器
    var shellsList: ArrayList<Shells> = ArrayList()

    abstract fun drawTank(g: Graphics?)

}