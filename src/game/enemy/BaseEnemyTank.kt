package game.enemy

import game.AbstractTank
import game.CP
import game.Shells
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.util.*

/**
 * @Class BaseEnemyTank
 * @Description 敌军坦克基类
 * @Author xsc
 * @Date 2023/12/18 下午3:16
 * @Version 1.0
 */
open class BaseEnemyTank :AbstractTank() {

    var imgN: BufferedImage? = null
    var imgS: BufferedImage? = null
    var imgW: BufferedImage? = null
    var imgE: BufferedImage? = null

    //炮弹缓存
    val shells = Shells()

    //出现地点
    var position = CP.BORN_1

    protected var r = Random()

    private var keyList = ArrayList<Int>()
    // 模拟按键
    var key = KeyEvent.VK_DOWN

    var rect: Rectangle = Rectangle(x, y, w, h)

    val KEY_LIST = arrayListOf(
        KeyEvent.VK_UP,
        KeyEvent.VK_DOWN,
        KeyEvent.VK_LEFT,
        KeyEvent.VK_RIGHT
    )

    /**
     * 如果一个方向撞墙，直接在剩余的方向中随机选择一个方向，简单的进行处理
     */
    fun adjustDirection(key: Int): Unit {
        keyList.clear()
        keyList.addAll(KEY_LIST)
        keyList.remove(key)
        var index = r.nextInt(keyList.size)
        this.key = keyList.get(index)
        //println("enemy tank拐弯了，之前方向：${logstr(oldDir)}，新方向: ${logstr(this.direction)}")
    }

    /**
     * 掉头
     * @param key 当前方向
     */
    fun turnAround(key: Int) {
        when (key) {
            KeyEvent.VK_UP -> {
                this.key = KeyEvent.VK_DOWN
            }

            KeyEvent.VK_DOWN -> {
                this.key = KeyEvent.VK_UP
            }

            KeyEvent.VK_LEFT -> {
                this.key = KeyEvent.VK_RIGHT
            }

            KeyEvent.VK_RIGHT -> {
                this.key = KeyEvent.VK_LEFT
            }

            else -> {}
        }
    }

    /**
     * 水平方向行驶，纵向转弯后X坐标调整
     */
    protected fun adjustX() {
        //将y坐标重新进行设置，以对齐拐弯后的网格线
        x = x / SIZE_M * SIZE_M + (SIZE - TANK_SIZE) / 2
        //处理坦克中心点的坐标和炮弹发射起点坐标
        cx = x + w / 2
        shellsX = cx - shells.w / 2
        shellsY = cy
    }

    /**
     * 垂直方向行驶，横向转弯后Y坐标调整
     */
    protected fun adjustY() {
        //将y坐标重新进行设置，以对齐拐弯后的网格线
        y = y / SIZE_M * SIZE_M + (SIZE - TANK_SIZE) / 2
        //处理坦克中心点的坐标和炮弹发射起点坐标
        cy = y + h / 2
        shellsX = cx
        shellsY = cy - shells.h / 2
    }

    /**
     * 替代get方法
     */
    fun pickRect(): Rectangle {
        rect.x = x
        rect.y = y
        rect.width = w
        rect.height = h
        return rect
    }

    fun logstr(d: Int): String {
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
        return str1
        //println("new dir is ${str1}")
    }
    override fun drawTank(g: Graphics?) {
        var g2 = g as Graphics2D

        if (direction == Shells.DIRECTION_WEST) {
            g2.drawImage(imgW, x, y, null)
        } else if (direction == Shells.DIRECTION_EAST) {
            g2.drawImage(imgE, x, y, null)
        } else if (direction == Shells.DIRECTION_NORTH) {
            g2.drawImage(imgN, x, y, null)
        } else if (direction == Shells.DIRECTION_SOUTH) {
            g2.drawImage(imgS, x, y, null)
        }
    }

    override fun born() {
        
    }


    /**
     * 坦克行走
     * 1.前方是否可以通行
     * 1)创建方向list, size=4,
     * 2)当前进方向撞墙或遇到无法通行的障碍物，移除这个方向，在剩下的方向中随机选择一个方向继续前进，
     * 3)如果新的方向还无法通行，重复步骤2，直到找到可以通行的方向(必然有可通行方向)
     */
    override fun walk() {
        when (key) {
            KeyEvent.VK_UP -> {
                println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_NORTH) {
                    println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_WEST) {
                        adjustX()
                    } else if (this.direction == Shells.DIRECTION_EAST) {
                        adjustX()
                    }
                    this.direction = Shells.DIRECTION_NORTH
                    return
                }
                this.direction = Shells.DIRECTION_NORTH

                row = y / SIZE_M
                col = x / SIZE_M
                println("${this::javaClass.get().simpleName} move up row:$row, col:$col, x:$x, y:$y")
                //没有抵达边界并且前方可通行
                if (row > 0) {
                    if ((mapArray[row - 1][col].toInt() == 0
                                || mapArray[row - 1][col].toInt() == CP.TILE_GRASS
                                || mapArray[row - 1][col].toInt() == CP.TILE_SNOW)
                        && (mapArray[row - 1][col + 1].toInt() == 0
                                || mapArray[row - 1][col + 1].toInt() == CP.TILE_GRASS
                                || mapArray[row - 1][col + 1].toInt() == CP.TILE_SNOW)
                    ) {
                        println("up222")
                        var yOffset = -times * speed
                        transfer(0, yOffset)
                    } else {
                        //逻辑不太好处理，需要多加一个网格的高度，务必注意，困扰我好久
                        north = (row - 1) * SIZE_M + SIZE_M
                        println("发现障碍物row:${row-1}, north:$north")
                        if (y > north + (SIZE - TANK_SIZE) / 2) {
                            var yOffset = -times * speed
                            transfer(0, yOffset)
                            println("1-1 y:$y")
                        } else {
                            y = north + (SIZE - TANK_SIZE) / 2
                            //println("1-2 y:$y")
                            adjustDirection(key)
                        }
                    }
                } else if (row == 0) {
                    if (y > 0 + (SIZE - TANK_SIZE) / 2) {
                        var yOffset = -times * speed
                        transfer(0, yOffset)
                    } else {
                        y = 0 + (SIZE - TANK_SIZE) / 2
                        adjustDirection(key)
                    }
                }
                println("move over new x:$x, y:$y")
                //炮弹初始位置
                shellsX = cx - shells.w / 2
                shellsY = cy
            }

            KeyEvent.VK_DOWN -> {
                println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_SOUTH) {
                    println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_WEST) {
                        adjustX()
                    } else if (this.direction == Shells.DIRECTION_EAST) {
                        adjustX()
                    }
                    this.direction = Shells.DIRECTION_SOUTH
                    return
                }
                this.direction = Shells.DIRECTION_SOUTH
                row = y / SIZE_M
                col = x / SIZE_M
                //println("${this::javaClass.get().simpleName} move down row:$row, col:$col, x:$x, y:$y")
                //没有抵达边界并且前方可通行
                if (row < CP.R - 2) {
                    if ((mapArray[row + 2][col].toInt() == 0
                                || mapArray[row + 2][col].toInt() == CP.TILE_GRASS
                                || mapArray[row + 2][col].toInt() == CP.TILE_SNOW)
                        && (mapArray[row + 2][col + 1].toInt() == 0
                                || mapArray[row + 2][col + 1].toInt() == CP.TILE_GRASS
                                || mapArray[row + 2][col + 1].toInt() == CP.TILE_SNOW)){
                        var yOffset = times * speed
                        transfer(0, yOffset)
                    } else {
                        south = (row + 2) * SIZE_M
                        if (y < south - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                            var yOffset = times * speed
                            transfer(0, yOffset)
                        } else {
                            y = south - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                            adjustDirection(key)
                        }
                    }
                } else if (row == CP.R - 2) {
                    if (y < ground.height - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                        var yOffset = times * speed
                        transfer(0, yOffset)
                    } else {
                        y = ground.height - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                        adjustDirection(key)
                    }
                }

                //炮弹初始位置
                shellsX = cx - shells.w / 2
                shellsY = cy

            }

            KeyEvent.VK_LEFT -> {
                println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_WEST) {
                    println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_NORTH) {
                        adjustY()
                    } else if (this.direction == Shells.DIRECTION_SOUTH) {
                        adjustY()
                    }
                    this.direction = Shells.DIRECTION_WEST
                    return
                }
                this.direction = Shells.DIRECTION_WEST

                row = y / SIZE_M
                col = x / SIZE_M
                println("move left row:$row, col:$col, x:$x, y:$y")
                //没有抵达边界并且前方可通行
                if (col > 0) {
                    if ((mapArray[row][col-1].toInt() == 0
                                || mapArray[row][col - 1].toInt() == CP.TILE_GRASS
                                || mapArray[row][col - 1].toInt() == CP.TILE_SNOW)
                        && (mapArray[row + 1][col - 1].toInt() == 0
                                || mapArray[row + 1][col - 1].toInt() == CP.TILE_GRASS
                                || mapArray[row + 1][col - 1].toInt() == CP.TILE_SNOW)
                    ) {
                        var xOffset = -times * speed
                        transfer(xOffset, 0)
                    } else {//遇到障碍物
                        west = (col - 1) * SIZE_M + SIZE_M
                        if (x > west + (SIZE - TANK_SIZE) / 2) {
                            var xOffset = -times * speed
                            transfer(xOffset, 0)
                        } else {
                            x = west + (SIZE - TANK_SIZE) / 2
                            adjustDirection(key)
                        }
                    }
                } else if (col == 0) {
                    if (x > 0 + (SIZE - TANK_SIZE) / 2) {
                        var xOffset = -times * speed
                        transfer(xOffset, 0)
                    } else {
                        x = 0 + (SIZE - TANK_SIZE) / 2
                        adjustDirection(key)
                    }
                }
                println("move over new x:$x, y:$y")

                //炮弹初始位置
                shellsX = cx
                shellsY = cy - shells.h / 2
            }

            KeyEvent.VK_RIGHT -> {
                println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_EAST) {
                    println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_NORTH) {
                        adjustY()
                    } else if (this.direction == Shells.DIRECTION_SOUTH) {
                        adjustY()
                    }
                    this.direction = Shells.DIRECTION_EAST
                    return
                }
                this.direction = Shells.DIRECTION_EAST

                row = y / SIZE_M
                col = x / SIZE_M
                println("move right row:$row, col:$col, x:$x, y:$y")

                //没有抵达边界并且前方可通行
                if (col < CP.C - 2) {
                    if ((mapArray[row][col + 2].toInt() == 0
                                || mapArray[row][col + 2].toInt() == CP.TILE_GRASS
                                || mapArray[row][col + 2].toInt() == CP.TILE_SNOW)
                        && (mapArray[row + 1][col + 2].toInt() == 0
                                || mapArray[row + 1][col + 2].toInt() == CP.TILE_GRASS
                                || mapArray[row + 1][col + 2].toInt() == CP.TILE_SNOW)){
                        var xOffset = times * speed
                        transfer(xOffset, 0)
                    } else {//有障碍物
                        //记住这个障碍物的边界，到达边界再停止
                        east = (col + 2) * SIZE_M
                        if (x < east - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                            var xOffset = times * speed
                            transfer(xOffset, 0)
                        } else {
                            x = east - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                            adjustDirection(key)
                        }
                    }

                } else if(col == CP.C - 2) {//抵达边界
                    if (x < ground.width - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                        var xOffset = times * speed
                        transfer(xOffset, 0)
                    } else {
                        x = ground.width - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                        adjustDirection(key)
                    }
                }
                println("move over new x:$x, y:$y")
                //炮弹初始位置
                shellsX = cx
                shellsY = cy - shells.h / 2
            }

            else -> {}
        }
    }

    override fun fire() {
        
    }

    override fun draw(g: Graphics?) {
        
    }

    override fun onTick() {
        
    }
}