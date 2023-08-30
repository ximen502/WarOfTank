package game

import game.map.Brick
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import kotlin.math.abs

/**
 * 炮弹类，按键后从坦克炮筒发出的炮弹
 */
class Shells : GameObject() {
    private var speed = 2 * 2
    var direction = 0
    val mapArray = CP.mapArray
    val tileArray = CP.tileArray
    var brick: Brick? = null
    var times = 1

    val d = 10
    val SIZE = CP.SIZE
    var doCollision = false
    var observer: GOObserver? = null

    companion object {
        const val DIRECTION_EAST = 1
        const val DIRECTION_WEST = 2
        const val DIRECTION_SOUTH = 4
        const val DIRECTION_NORTH = 8
    }

    init {
        w = d
        h = d
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        var color = g2.color
        g2.color = Color.YELLOW
        // 炮弹直径
        g2.fillRect(x, y, d, d)
        g2.color = color
    }

    override fun onTick() {
        when (direction) {
            DIRECTION_NORTH -> {
                direction = Shells.DIRECTION_NORTH
                var xGrid = x / SIZE
                var yGrid = y / SIZE
                var yNext = if (yGrid - 1 <= 0) 0 else yGrid - 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    var bcx = brick!!.cx
                    var bcy = brick!!.cy
                    var wOf2 = (w + brick!!.w) / 2
                    var hOf2 = (h + brick!!.h) / 2
                    println("炮弹x:$x, y:$y, $w, $h")
                    println("brick x:${brick!!.x}, y:${brick!!.y}, w:${brick!!.w}, h:${brick!!.h}brickCX:$bcx, brickCY:$bcy")
                    if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                        println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickCX:$bcx, brickCY:$bcy")
                        //通知砖块碰撞消息
                        brick!!.shells = this

                        //炮弹消失，数据相关数据重置
                        doCollision = false
                        isDestroyed = true
                        observer?.die(this)
                    } else {
                        println("还没有碰撞，正在接近")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }

                //前进方向有没有障碍物
                // 1.没有跨网格
                println("bullet x grd:$xGrid, y grd:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
                println("bullet x:$x, y:$y")
                if (mapArray[yNext][xGrid].toInt() == 1) {//发现砖块障碍物
                    if (tileArray[yNext][xGrid] is Brick) {
                        brick = tileArray[yNext][xGrid] as Brick
                    }
                    doCollision = true
                } else {
                    if (y <= ground.t) {
                        //println("bullet up 333")
                        y = ground.t
                        transfer(0, 0)
                    } else {
                        //println("bullet up 444")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }
            }
            DIRECTION_SOUTH -> transfer(0, speed)
            DIRECTION_WEST -> transfer(-speed, 0)
            DIRECTION_EAST -> transfer(speed, 0)
            else -> transfer(0, 0)
        }
    }

}