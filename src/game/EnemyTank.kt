package game

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.util.Random
import kotlin.math.pow

/**
 * 坦克，根据给定的坐标绘制一个坦克的俯视图
 * 矩形车身+圆形炮台+矩形炮筒
 */
class EnemyTank(ground: Ground) : AbstractTank() {
//    private var speed = 2 * 2

    //    private var input: Input
    var observer: GOObserver? = null

    private var r = Random()

    init {
        x = 0
        y = 80
//        this.input = input
        this.ground = ground
        var d = r.nextInt(4)
        direction = when (d) {
            0 -> Shells.DIRECTION_NORTH
            1 -> Shells.DIRECTION_SOUTH
            2 -> Shells.DIRECTION_WEST
            3 -> Shells.DIRECTION_EAST
            else -> {
                Shells.DIRECTION_EAST
            }
        }
        direction = 2.0.pow(d.toDouble()).toInt()
        times = 1
    }

    override fun draw(g: Graphics?) {
        g?.color = Color.CYAN
        drawTank(g)
    }

    override fun onTick() {
        //移动位置、发射炮弹、碰撞检测
        //方向随机，碰到墙壁的处理、碰到队友的处理
        when (direction) {
            Shells.DIRECTION_NORTH -> {
                // 撞墙处理
                // 1创建方向list,size=4,
                // 2当一个方向撞墙，移除这个方向，在剩下的方向中随机，
                // 3如果新的方向还撞墙，重复步骤2，直到找到可以通行的方向(必然有可通行方向)
                direction = adjustDirection(direction)
                if (y <= Ground.TITLE_H) {
                    y = Ground.TITLE_H
                    transfer(0, 0)
                } else {
                    var yOffset = 0
                    yOffset = if (y - Ground.TITLE_H < times * speed) {
                        y - Ground.TITLE_H
                    } else {
                        (-times * speed).toInt()
                    }
                    transfer(0, yOffset)
                }
            }

            Shells.DIRECTION_SOUTH -> {
                direction = adjustDirection(direction)
//                transfer(0, 1 * speed)
                if (y >= ground.height) {
                    y = ground.height
                    transfer(0, 0)
                } else {
                    var yOffset = 0
                    yOffset = if (ground.height - SIZE - y < times * speed) {
                        ground.height - SIZE - y
                    } else {
                        (times * speed).toInt()
                    }
                    transfer(0, yOffset)
                }
            }

            Shells.DIRECTION_WEST -> {
                direction = adjustDirection(direction)
//                transfer(-1 * speed, 0)
                if (x <= 0) {
                    x = 0
                    transfer(0, 0)
                } else {
                    var xOffset = 0
                    //当前坐标与边界的距离不足一步(取决于速度)的距离
                    xOffset = if (x < times * speed) {
                        -x
                    } else {
                        (-times * speed).toInt()
                    }
                    transfer(xOffset, 0)
                }
            }

            Shells.DIRECTION_EAST -> {
                direction = adjustDirection(direction)
//                transfer(1 * speed, 0)
                if (x >= ground.width) {
                    x = ground.width
                    transfer(0, 0)
                } else {
                    var xOffset = 0
                    //注意车身的尺寸不能忽略
                    xOffset = if ((ground.width - SIZE - x) < (times * speed)) {
                        ground.width - SIZE - x
                    } else {
                        (times * speed).toInt()
                    }
                    transfer(xOffset, 0)
                }
            }
        }
    }

    override fun isOut(): Boolean {
        if (x <= 0 || x + SIZE >= ground.width) {
            return true
        }

        if (y <= Ground.TITLE_H || y + SIZE >= ground.height) {
            return true
        }

        return false
    }

    /**
     * 如果一个方向撞墙，直接在剩余的方向中随机选择一个方向，简单的进行处理
     */
    private fun adjustDirection(direction: Int): Int {
        return if (isOut()) {
            var str = logstr(direction)
            //println("^-^ is out, dir is $direction, $str x is $x, y is $y")
            var directionList = arrayListOf(
                Shells.DIRECTION_NORTH,
                Shells.DIRECTION_SOUTH,
                Shells.DIRECTION_WEST,
                Shells.DIRECTION_EAST
            )
            if (x <= 0) {
                directionList.remove(Shells.DIRECTION_WEST)
            }
            if (x + SIZE >= ground.width) {
                directionList.remove(Shells.DIRECTION_EAST)
            }
            if (y <= Ground.TITLE_H) {
                directionList.remove(Shells.DIRECTION_NORTH)
            }
            if (y + SIZE > ground.height) {
                directionList.remove(Shells.DIRECTION_SOUTH)
            }

            var index = r.nextInt(directionList.size)

            logstr(directionList[index])
            directionList[index]
        } else {
            //println("in the ground, dir is $direction, x is $x, y is $y")
            direction
        }
    }

    private fun logstr(d: Int) {
        var str1 = ""
        when (d) {
            Shells.DIRECTION_NORTH -> {
                str1 = "north"
            }

            Shells.DIRECTION_SOUTH -> {
                str1 = "south"
            }

            Shells.DIRECTION_WEST -> {
                str1 = "west"
            }

            Shells.DIRECTION_EAST -> {
                str1 = "east"
            }

            else -> {
            }
        }
        //println("new dir is ${str1}")
    }

    override fun drawTank(g: Graphics?) {
        var g2 = g as Graphics2D

        val offset = SIZE / 2
        //车身
        g2?.drawRect(x /*- offset*/, y /*- offset*/, SIZE, SIZE)
        //炮台
        var offsetOval = offset / 2
        var sizeOval = SIZE / 2
        g2?.drawOval(x + offsetOval, y + offsetOval, sizeOval, sizeOval)

        //炮筒(需要根据行走方向调整指向)
        var ptRadius = 6
        var ptLength = 36
        var arc = 2

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