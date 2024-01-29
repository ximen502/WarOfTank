package game

import java.applet.Applet
import java.applet.AudioClip
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * 玩家坦克，根据给定的坐标绘制一个坦克的俯视图
 * 矩形车身+圆形炮台+矩形炮筒
 * @Author xsc
 */
class Tank(input: Input, ground: Ground) : AbstractTank(), MoveListener {
    private var image: Image = Toolkit.getDefaultToolkit().createImage("image/Snow.png")

    private var input: Input

    var fireAC: AudioClip? = null

    var imgN: BufferedImage
    var imgS: BufferedImage
    var imgW: BufferedImage
    var imgE: BufferedImage


    //炮弹缓存
    val shells = Shells()

    var rect: Rectangle = Rectangle(x, y, w, h)

    init {
        this.ground = ground
        println("w:${ground.width}")
        x = ground.width / 2 - (SIZE * 2.5).toInt() + (SIZE - CP.TANK_SIZE) / 2
        y = ground.height - SIZE + (SIZE - CP.TANK_SIZE) / 2
        w = TANK_SIZE
        h = TANK_SIZE
        shellsX = cx - shells.w / 2
        shellsY = cy - shells.h / 2
        direction = Shells.DIRECTION_NORTH
        println("tank born position x:$x, y:$y, cx:$cx, cy:$cy, shells x:$shellsX, y:$shellsY")
        this.input = input
        times = 7
//        println(javaClass.toString())
//        var resource = javaClass.getResource("")
//        println(resource)
//        var tankPath = javaClass.getResource("image/tank.png")
//        println(tankPath)
//        img = ImageIO.read(tankPath)
        imgN = ImageIO.read(javaClass.getResource("/game/image/tkn2.png"))
        imgS = ImageIO.read(javaClass.getResource("/game/image/tks2.png"))
        imgW = ImageIO.read(javaClass.getResource("/game/image/tkw2.png"))
        imgE = ImageIO.read(javaClass.getResource("/game/image/tke2.png"))
//        imgX = (w - img.width) / 2
//        imgY = (h - img.height) / 2
//        println(resource2)
        fireAC = Applet.newAudioClip(javaClass.getResource("/game/sound/Gunfire.wav"))
    }

    override fun draw(g: Graphics?) {
        g?.color = Color.YELLOW
        drawTank(g)
    }

    override fun onTick() {
        //shells born
        if (input.getKeyDown(KeyEvent.VK_CONTROL) == true) {
            println("control is pressed, fire in the hole. shellsList size:${shellsList.size}")
            fire()
        }
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

    /**
     * 相当于keyPressed(key)
     */
    override fun begin(key: Int) {
        println("begin:${key}")
        when (key) {
            KeyEvent.VK_UP -> {
                /*************************************************
                 * 地图瓦片进一步缩小为1/4。坦克能不能前进的条件如下
                 * 能：空地；可以通过的瓦片(草地、雪地)；没有抵达游戏窗口边界；
                 * 不能：不可通过的瓦片(砖头、钢铁、河流)；抵达游戏窗口边界；
                 * ***********************************************/
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
                println("move up row:$row, col:$col, x:$x, y:$y")
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
                            transfer(0, 0)
                            println("1-2 y:$y")
                        }
                    }
                } else if (row == 0) {
                    if (y > 0 + (SIZE - TANK_SIZE) / 2) {
                        var yOffset = -times * speed
                        transfer(0, yOffset)
                    } else {
                        y = 0 + (SIZE - TANK_SIZE) / 2
                        transfer(0, 0)
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
                println("move down row:$row, col:$col, x:$x, y:$y")
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
                            transfer(0, 0)
                        }
                    }

                } else if(row == CP.R - 2) {
                    if (y < ground.height - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                        var yOffset = times * speed
                        transfer(0, yOffset)
                    } else {
                        y = ground.height - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                        transfer(0, 0)
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
                            transfer(0, 0)
                        }
                    }
                } else if (col == 0) {
                    if (x > 0 + (SIZE - TANK_SIZE) / 2) {
                        var xOffset = -times * speed
                        transfer(xOffset, 0)
                    } else {
                        x = 0 + (SIZE - TANK_SIZE) / 2
                        transfer(0, 0)
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
                            transfer(0, 0)
                        }
                    }

                } else if(col == CP.C - 2) {//抵达边界
                    if (x < ground.width - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                        var xOffset = times * speed
                        transfer(xOffset, 0)
                    } else {
                        x = ground.width - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                        transfer(0, 0)
                    }
                }
                println("move over new x:$x, y:$y")
                //炮弹初始位置
                shellsX = cx
                shellsY = cy - shells.h / 2
            }
        }
    }

    override fun end(direction: Int) {
        println("end")

    }

    /**
     * 水平方向行驶，纵向转弯后X坐标调整
     */
    private fun adjustX() {
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
    private fun adjustY() {
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

    override fun born() {

    }

    override fun walk() {

    }

    override fun fire() {
        // 简化炮弹是否可以发射的判断逻辑
        if (shells.isDestroyed) {
            val sh = shells
            sh.times = 2
            sh.id = (CP.PLAYER shl 8 or id.toInt()).toLong()
            sh.observer = observer
            sh.ground = ground
            sh.setPosition(shellsX, shellsY)
            sh.direction = direction
            sh.isDestroyed = false
            shellsList.add(sh)
            observer?.born(sh)
            fireAC?.play()
        }
    }

}