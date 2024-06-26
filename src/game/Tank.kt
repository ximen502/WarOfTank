package game

import game.lib.Log
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

    var imgN: BufferedImage
    var imgS: BufferedImage
    var imgW: BufferedImage
    var imgE: BufferedImage


    //炮弹缓存
    val shells = Shells()

    var gameOver = false
    //是否可以发射炮弹
    var fireCounter = 0
    //炮弹发射间隔时间
    private var fireInterval = 0
    // 吃了多少个星星,对应于火力级别、外观、移动速度、发射间隔
    var hasStar = 0
    private var fireLevel = Shells.LEVEL0
    private var fireSpeed = Shells.MOVE_LV0

    companion object {
        const val MOVE_LV0 = 3
        const val MOVE_LV1 = 4
        const val INTERVAL_LV0 = 8
        const val INTERVAL_LV1 = 6
    }

    init {
        this.ground = ground
        Log.println("w:${ground.width}")
        x = ground.width / 2 - (SIZE_M * 5) + (SIZE - CP.TANK_SIZE) / 2
        y = ground.height - SIZE + (SIZE - CP.TANK_SIZE) / 2
        w = TANK_SIZE
        h = TANK_SIZE
        shellsX = cx - shells.w / 2
        shellsY = cy - shells.h / 2
        direction = Shells.DIRECTION_NORTH
        Log.println("tank born position x:$x, y:$y, cx:$cx, cy:$cy, shells x:$shellsX, y:$shellsY")
        this.input = input
        times = MOVE_LV0
        fireInterval = INTERVAL_LV0
        //fire init:3,1star:4,2 or 3 star:6
        //player move init:3, highest:4
        //fire interval init:8, star>=2:6
        //当吃掉2颗或更多星星的时候，发射速度是最快的，间隔时间是最短的
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
    }

    override fun draw(g: Graphics?) {
        g?.color = Color.YELLOW
        drawTank(g)
    }

    override fun onTick() {
        if (gameOver)
            return
        //with stars, fire get more powerful
        if (hasStar == 1) {
            fireLevel = Shells.LEVEL1
        } else if (hasStar == 2) {
            fireLevel = Shells.LEVEL2
        } else if (hasStar >= 3) {
            fireLevel = Shells.LEVEL3
        }
        //shells born
        if (input.getKeyDown(KeyEvent.VK_CONTROL) == true) {
            Log.println("control is pressed, fire in the hole")
            fire()
        }

        // player walk
        var key = 0
        if(input.getKeyDown(KeyEvent.VK_UP) == true) {
            key = KeyEvent.VK_UP
        } else if (input.getKeyDown(KeyEvent.VK_DOWN) == true) {
            key = KeyEvent.VK_DOWN
        } else if (input.getKeyDown(KeyEvent.VK_LEFT) == true) {
            key = KeyEvent.VK_LEFT
        } else if (input.getKeyDown(KeyEvent.VK_RIGHT) == true) {
            key = KeyEvent.VK_RIGHT
        }
        playerWalk(key)

        invincibleCounter--
        invincible = invincibleCounter > 0
        if (invincibleCounter < 0) {
            invincibleCounter = 0
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

        if (invincible) {
            g2.color = shieldColor
            g2.stroke = BasicStroke(4F)
            g2.drawOval(x - 6, y - 6, w + 12, h + 12)
        }

        //debug
        if (input.debug) {
            g2.color = Color.WHITE
            g2.drawString("hasStar:$hasStar", 48, 400)
            g2.drawString("times:$times", 48, 400+30)
            g2.drawString("fireTimes:$fireSpeed", 48, 400+60)
            g2.drawString("fireCounter:${fireCounter}", 48, 400+90)
            g2.drawString("invincibleCounter:$invincibleCounter", 48, 400+120)
        }

        // star show
        drawStars(g2)
    }

    private fun drawStars(g2: Graphics2D) {
        var tempX = x
        var tempY = y
        var i = 0
        if (direction == Shells.DIRECTION_WEST) {
            tempX = x + 30
            tempY = y + 6
            while (i < hasStar) {
                g2.color = Color.YELLOW
                g2.fillOval(tempX, tempY, 6, 6)
                tempY+=(5+6)
                i++
                if (i == 3) {//最多三颗星
                    break
                }
            }
        } else if (direction == Shells.DIRECTION_EAST) {
            tempX = x + 6
            tempY = y + 6
            while (i < hasStar) {
                g2.color = Color.YELLOW
                g2.fillOval(tempX, tempY, 6, 6)
                tempY+=(5+6)
                i++
                if (i == 3) {//最多三颗星
                    break
                }
            }
        } else if (direction == Shells.DIRECTION_NORTH) {
            tempX = x + 6
            tempY = y + 30
            while (i < hasStar) {
                g2.color = Color.YELLOW
                g2.fillOval(tempX, tempY, 6, 6)
                tempX+=(5+6)
                i++
                if (i == 3) {//最多三颗星
                    break
                }
            }
        } else if (direction == Shells.DIRECTION_SOUTH) {
            tempX = x + 6
            tempY = y
            while (i < hasStar) {
                g2.color = Color.YELLOW
                g2.fillOval(tempX, tempY, 6, 6)
                tempX+=(5+6)
                i++
                if (i == 3) {//最多三颗星
                    break
                }
            }
        }

    }

    /**
     * 相当于keyPressed(key)
     */
    override fun begin(key: Int) {
        Log.println("begin:${key}")
    }

    override fun end(direction: Int) {
        Log.println("end")

    }

    /**
     * 水平方向行驶，纵向转弯后X坐标调整，left->up, left->down
     */
    private fun adjustX() {
        //将x坐标重新进行设置，以对齐拐弯后的网格线
        x = x / SIZE_M * SIZE_M + (SIZE - TANK_SIZE) / 2
        //处理坦克中心点的坐标和炮弹发射起点坐标
        cx = x + w / 2
        shellsX = cx - shells.w / 2
        shellsY = cy
    }

    /**
     * 水平方向行驶，纵向转弯后X坐标调整，right->up, right->down
     */
    private fun adjustX2() {
        //如果是向右行驶，得到最右面的网格gridRightCol，再得到最右面网格左面一个网格gridLeftCol
        //gridLeftCol的x坐标就作为拐弯后的x坐标。
        //将x坐标重新进行设置，以对齐拐弯后的网格线
        val east = x + w
        var gridRightCol = east / SIZE_M
        if (gridRightCol > CP.C - 1) {
            gridRightCol = CP.C - 1
        }
        var gridLeftCol = gridRightCol - 1
        if (gridLeftCol < 0) {
            gridLeftCol = 0
        }
        x = gridLeftCol * SIZE_M + (SIZE - TANK_SIZE) / 2

        //x = x / SIZE_M * SIZE_M + (SIZE - TANK_SIZE) / 2
        //处理坦克中心点的坐标和炮弹发射起点坐标
        cx = x + w / 2
        shellsX = cx - shells.w / 2
        shellsY = cy
    }

    /**
     * 垂直方向行驶，横向转弯后Y坐标调整，up->left, up->right
     */
    private fun adjustY() {
        //如果坦克向上行驶
        //将y坐标重新进行设置，以对齐拐弯后的网格线
        y = y / SIZE_M * SIZE_M + (SIZE - TANK_SIZE) / 2
        //处理坦克中心点的坐标和炮弹发射起点坐标
        cy = y + h / 2
        shellsX = cx
        shellsY = cy - shells.h / 2
    }

    /**
     * 垂直方向行驶，横向转弯后Y坐标调整，down->left, down->right
     */
    private fun adjustY2() {
        //如果是向下行驶，得到最下面的网格gridDownRow，再得到最下面网格上面一个网格gridUpRow
        //gridUpRow的y坐标就作为拐弯后的y坐标。
        //将y坐标重新进行设置，以对齐拐弯后的网格线
        val south = y + h
        var gridDownRow = south / SIZE_M
        //边界检查
        if (gridDownRow > CP.R - 1){
            gridDownRow = CP.R - 1
        }
        var gridUpRow = gridDownRow - 1
        if (gridUpRow < 0) {
            gridUpRow = 0
        }

        y = gridUpRow * SIZE_M + (SIZE - TANK_SIZE) / 2

        //处理坦克中心点的坐标和炮弹发射起点坐标
        cy = y + h / 2
        shellsX = cx
        shellsY = cy - shells.h / 2
    }

    override fun born() {

    }


    override fun walk() {

    }

    private fun playerWalk(key: Int) {
        when (key) {
            KeyEvent.VK_UP -> {
                /*************************************************
                 * 地图瓦片进一步缩小为1/4。坦克能不能前进的条件如下
                 * 能：空地；可以通过的瓦片(草地、雪地)；没有抵达游戏窗口边界；
                 * 不能：不可通过的瓦片(砖头、钢铁、河流)；抵达游戏窗口边界；
                 * ***********************************************/
                Log.println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_NORTH) {
                    Log.println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    Log.println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_WEST) {
                        adjustX()
                    } else if (this.direction == Shells.DIRECTION_EAST) {
                        adjustX2()
                    }
                    this.direction = Shells.DIRECTION_NORTH
                    return
                }
                this.direction = Shells.DIRECTION_NORTH

                row = y / SIZE_M
                col = x / SIZE_M
                Log.println("move up row:$row, col:$col, x:$x, y:$y")
                //没有抵达边界并且前方可通行
                if (row > 0) {
                    if ((mapArray[row - 1][col].toInt() == 0
                                || mapArray[row - 1][col].toInt() == CP.TILE_GRASS
                                || mapArray[row - 1][col].toInt() == CP.TILE_SNOW)
                        && (mapArray[row - 1][col + 1].toInt() == 0
                                || mapArray[row - 1][col + 1].toInt() == CP.TILE_GRASS
                                || mapArray[row - 1][col + 1].toInt() == CP.TILE_SNOW)
                    ) {
                        Log.println("up222")
                        var yOffset = -times * speed
                        transfer(0, yOffset)
                    } else {
                        //逻辑不太好处理，需要多加一个网格的高度，务必注意，困扰我好久
                        north = (row - 1) * SIZE_M + SIZE_M
                        Log.println("发现障碍物row:${row-1}, north:$north")
                        if (y > north + (SIZE - TANK_SIZE) / 2) {
                            var yOffset = -times * speed
                            transfer(0, yOffset)
                            Log.println("1-1 y:$y")
                        } else {
                            y = north + (SIZE - TANK_SIZE) / 2
                            transfer(0, 0)
                            Log.println("1-2 y:$y")
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
                Log.println("move over new x:$x, y:$y")
                //炮弹初始位置
                shellsX = cx - shells.w / 2
                shellsY = cy
            }

            KeyEvent.VK_DOWN -> {
                Log.println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_SOUTH) {
                    Log.println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    Log.println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_WEST) {
                        adjustX()
                    } else if (this.direction == Shells.DIRECTION_EAST) {
                        adjustX2()
                    }
                    this.direction = Shells.DIRECTION_SOUTH
                    return
                }
                this.direction = Shells.DIRECTION_SOUTH
                row = y / SIZE_M
                col = x / SIZE_M
                Log.println("move down row:$row, col:$col, x:$x, y:$y")
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
                Log.println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_WEST) {
                    Log.println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    Log.println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_NORTH) {
                        adjustY()
                    } else if (this.direction == Shells.DIRECTION_SOUTH) {
                        adjustY2()
                    }
                    this.direction = Shells.DIRECTION_WEST
                    return
                }
                this.direction = Shells.DIRECTION_WEST

                row = y / SIZE_M
                col = x / SIZE_M
                Log.println("move left row:$row, col:$col, x:$x, y:$y")
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
                Log.println("move over new x:$x, y:$y")

                //炮弹初始位置
                shellsX = cx
                shellsY = cy - shells.h / 2
            }

            KeyEvent.VK_RIGHT -> {
                Log.println("old direction:" + this.direction)
                if (this.direction == Shells.DIRECTION_EAST) {
                    Log.println("直行")
                } else {
                    //拐弯就只改变一下方向，不需要移动
                    Log.println("拐弯了，拐弯前的方向是${this.direction}")
                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
                    if (this.direction == Shells.DIRECTION_NORTH) {
                        adjustY()
                    } else if (this.direction == Shells.DIRECTION_SOUTH) {
                        adjustY2()
                    }
                    this.direction = Shells.DIRECTION_EAST
                    return
                }
                this.direction = Shells.DIRECTION_EAST

                row = y / SIZE_M
                col = x / SIZE_M
                Log.println("move right row:$row, col:$col, x:$x, y:$y")

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
                Log.println("move over new x:$x, y:$y")
                //炮弹初始位置
                shellsX = cx
                shellsY = cy - shells.h / 2
            }
        }
    }

    fun reset() {
        x = ground.width / 2 - (SIZE_M * 5) + (SIZE - CP.TANK_SIZE) / 2
        y = ground.height - SIZE + (SIZE - CP.TANK_SIZE) / 2
        w = TANK_SIZE
        h = TANK_SIZE
        shellsX = cx - shells.w / 2
        shellsY = cy - shells.h / 2
        row = y / SIZE_M
        col = x / SIZE_M
        direction = Shells.DIRECTION_NORTH
        isDestroyed = true
    }

    override fun fire() {
        // 简化炮弹是否可以发射的判断逻辑
        if (shells.isDestroyed) {
            if (fireCounter % fireInterval == 0) {
                val sh = shells
                sh.times = fireSpeed
                sh.level = fireLevel
                sh.id = ID.ID_P1_SHELL
                sh.observer = observer
                sh.ground = ground
                sh.setPosition(shellsX, shellsY)
                sh.direction = direction
                sh.isDestroyed = false
                observer?.born(sh)
                AC.soundManagerGF?.play(AC.gunfire)
                fireCounter = 0
            }
            fireCounter++
        } else {
            // 兜底解决玩家偶尔无法发射炮弹的问题
            if (shells.x < 0 - shells.w) {
                shells.isDestroyed = true
                Log.println("shells position: x:${shells.x}, y:${shells.y}")
            }
            if (shells.x > ground.width + shells.w) {
                shells.isDestroyed = true
                Log.println("shells position: x:${shells.x}, y:${shells.y}")
            }

            if (shells.y < 0 - shells.h) {
                shells.isDestroyed = true
                Log.println("shells position: x:${shells.x}, y:${shells.y}")
            }

            if (shells.y > ground.height + shells.h) {
                shells.isDestroyed = true
                Log.println("shells position: x:${shells.x}, y:${shells.y}")
            }
        }
    }

    fun initSpeed() {
        //fire interval
        fireInterval = INTERVAL_LV0
        //speed up tank move
        if (hasStar > 0) {
            times = MOVE_LV1
        }
        //speed up fire shells move
        if (hasStar == 1) {
            fireSpeed = Shells.MOVE_LV1
        } else if (hasStar >= 2) {
            fireSpeed = Shells.MOVE_LV2
            fireInterval = INTERVAL_LV1
        }
    }

    fun eatStar() {
        hasStar++
        initSpeed()
    }

}