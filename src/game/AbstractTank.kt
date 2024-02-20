package game

import java.awt.Graphics

import java.util.*

/**
 * 抽象的坦克类，一些公共行为或属性抽取
 *
 * @author ximen502
 */
abstract class AbstractTank : GameObject() {

    companion object {
        const val SIZE = CP.SIZE
        const val SIZE_M = CP.SIZE_M
        const val TANK_SIZE = CP.TANK_SIZE
        const val TANK_W = CP.TANK_W
        const val TANK_H = CP.TANK_H
        const val FAST_B = CP.FAST_B
        const val FAST_S = CP.FAST_S
    }
    // 坦克前进的方向
    protected var direction = 0

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
    var times = 4

    val mapArray = CP.mapArray

    // 在网格中的行
    var row = 0
    // 在网格中的列
    var col = 0

    // 方便记录遇到障碍物后4个方向的边界
    var west = -1
    var east = -1
    var north = -1
    var south = -1

    var observer: GOObserver? = null

    abstract fun drawTank(g: Graphics?)

    /**
     * 出生动画
     */
    abstract fun born()

    /**
     * 在地图中行走
     */
    abstract fun walk()

    /**
     * 发射炮弹
     */
    abstract fun fire()

}