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
    private var speed = 2 * 2 * 2 * 2
    var direction = 0
    val mapArray = CP.mapArray
    val tileArray = CP.tileArray
    var brick: Brick? = null
    var times = 1

    val d = 10
    val SIZE = CP.SIZE
    var doCollision = false

    companion object {
        const val DIRECTION_EAST = 1
        const val DIRECTION_WEST = 2
        const val DIRECTION_SOUTH = 4
        const val DIRECTION_NORTH = 8
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
                //transfer(0, -speed)
                direction = Shells.DIRECTION_NORTH
                var xGrid = x / SIZE
                var yGrid = y / SIZE
                var yNext = if (yGrid - 1 <= 0) 0 else yGrid - 1

                if (doCollision) {
//                    mapArray
                    var bcx = brick!!.cx
                    var bcy = brick!!.cy
                    var wOf2 = (w + brick!!.w) / 2
                    var hOf2 = (h + brick!!.h) / 2
                    if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                        println("--碰撞--x:$x, y:$y, brickX:$bcx, brickY:$bcy")
                        //通知砖块碰撞消息
                        brick!!.shells = this

                        //炮弹消失，数据相关数据重置
                        doCollision = false
                    } else {
                        println("没有碰撞")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
//                    if (Math.abs(cx - ) <= tpwhalf && Math.abs(tcy - pcy) <= tphhalf) {
//                        when (player.direction) {
//                            Shells.DIRECTION_NORTH -> {
//                                println("上面撞墙了，边界坐标y:${tile.y + tile.h}")
//                            }
//                            Shells.DIRECTION_SOUTH -> {
//                                println("下面撞墙了，边界坐标y:${tile.y}")
//                            }
//                            Shells.DIRECTION_WEST -> {
//                                println("左面撞墙了，边界坐标x:${tile.x + tile.w}")
//                            }
//                            Shells.DIRECTION_EAST -> {
//                                println("右面撞墙了，边界坐标x:${tile.x}")
//                            }
//                            else -> {}
//                        }
//                    }
                }

                //前进方向有没有障碍物
                // 分2种情况，判断x有没有跨网格
                // 1.没有跨网格
                // 2.有跨网格
//                println(mapArray[yGrid].contentToString())
                println("bullet x grd:$xGrid, y grd:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
                println("bullet x:$x, y:$y")
                var mod = x % SIZE
//                if (mod == 0) {//1
                if (mapArray[yNext][xGrid].toInt() == 1) {//发现砖块障碍物
                    if (tileArray[yNext][xGrid] is Brick) {
                        brick = tileArray[yNext][xGrid] as Brick
//                        println("bx:${brick.x}, by:${brick.y}")
                    }

                    doCollision = true


//                        if (y <= yGrid * SIZE) {
//                            println("bullet up 111")
//                            y = yGrid * SIZE
//                            transfer(0, 0)
//                        } else {
//                            println("bullet up 222")
//                            var yOffset = (-times * speed).toInt()
//                            transfer(0, yOffset)
//                        }
                } else {
                    if (y <= Ground.TITLE_H) {
                        println("bullet up 333")
                        y = Ground.TITLE_H
                        transfer(0, 0)
                    } else {
                        println("bullet up 444")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }
//                } else {//2
//                    if (mapArray[yNext][xGrid].toInt() in 1..3 || mapArray[yNext][xGrid + 1].toInt() in 1..3) {
//                        if (y <= yGrid * SIZE) {
//                            //println("up111")
//                            y = yGrid * SIZE
//                            transfer(0, 0)
//                        } else {
//                            //println("up222")
//                            var yOffset = (-times * speed).toInt()
//                            transfer(0, yOffset)
//                        }
//                    } else {
//                        if (y <= Ground.TITLE_H) {
//                            //println("up333")
//                            y = Ground.TITLE_H
//                            transfer(0, 0)
//                        } else {
//                            //println("up444")
//                            var yOffset = (-times * speed).toInt()
//                            transfer(0, yOffset)
//                        }
//                    }
//                }
            }
            DIRECTION_SOUTH -> transfer(0, speed)
            DIRECTION_WEST -> transfer(-speed, 0)
            DIRECTION_EAST -> transfer(speed, 0)
            else -> transfer(0, 0)
        }
    }

}