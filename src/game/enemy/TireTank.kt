package game.enemy

import game.CP
import game.Ground
import game.Shells
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * @Class TireTank
 * @Description 轮胎式坦克
 * @Author xsc
 * @Date 2023-12-13 上午09:55
 */
class TireTank(ground: Ground) : BaseEnemyTank() {
    var imgN: BufferedImage
    var imgS: BufferedImage
    var imgW: BufferedImage
    var imgE: BufferedImage


    init {
        x = 0
        y = 5
        this.ground = ground
        direction = Shells.DIRECTION_SOUTH
        times = 1

        imgN = ImageIO.read(javaClass.getResource("../image/fastn.png"))
        imgS = ImageIO.read(javaClass.getResource("../image/fasts.png"))
        imgW = ImageIO.read(javaClass.getResource("../image/fastw.png"))
        imgE = ImageIO.read(javaClass.getResource("../image/faste.png"))
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
        when (direction) {
            Shells.DIRECTION_NORTH -> {
//                println("old direction:" + this.direction)
//                if (this.direction == Shells.DIRECTION_NORTH) {
//                    println("直行")
//                } else {
//                    //拐弯就只改变一下方向，不需要移动
//                    println("拐弯了，拐弯前的方向是${this.direction}")
//                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
//                    if (this.direction == Shells.DIRECTION_WEST) {
//                        adjustX()
//                    } else if (this.direction == Shells.DIRECTION_EAST) {
//                        adjustX()
//                    }
//                    this.direction = Shells.DIRECTION_NORTH
//                    return
//                }
//                this.direction = Shells.DIRECTION_NORTH

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
                            adjustDirection(direction)
                        }
                    }
                } else if (row == 0) {
                    if (y > 0 + (SIZE - TANK_SIZE) / 2) {
                        var yOffset = -times * speed
                        transfer(0, yOffset)
                    } else {
                        y = 0 + (SIZE - TANK_SIZE) / 2
                        adjustDirection(direction)
                    }
                }
                println("move over new x:$x, y:$y")
                //炮弹初始位置
                shellsX = cx - shells.w / 2
                shellsY = cy
//                if (y <= Ground.TITLE_H) {
//                    y = Ground.TITLE_H
//                    transfer(0, 0)
//                } else {
//                    var yOffset = 0
//                    yOffset = if (y - Ground.TITLE_H < times * speed) {
//                        y - Ground.TITLE_H
//                    } else {
//                        (-times * speed).toInt()
//                    }
//                    transfer(0, yOffset)
//                }
//                adjustDirection(direction)
            }

            Shells.DIRECTION_SOUTH -> {
//                println("old direction:" + this.direction)
//                if (this.direction == Shells.DIRECTION_SOUTH) {
//                    println("直行")
//                } else {
//                    //拐弯就只改变一下方向，不需要移动
//                    println("拐弯了，拐弯前的方向是${this.direction}")
//                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
//                    if (this.direction == Shells.DIRECTION_WEST) {
//                        adjustX()
//                    } else if (this.direction == Shells.DIRECTION_EAST) {
//                        adjustX()
//                    }
//                    this.direction = Shells.DIRECTION_SOUTH
//                    return
//                }
//                this.direction = Shells.DIRECTION_SOUTH
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
                            adjustDirection(direction)
                        }
                    }
                } else if (row == CP.R - 2) {
                    if (y < ground.height - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                        var yOffset = times * speed
                        transfer(0, yOffset)
                    } else {
                        y = ground.height - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                        adjustDirection(direction)
                    }
                }

                //炮弹初始位置
                shellsX = cx - shells.w / 2
                shellsY = cy

            }

            Shells.DIRECTION_WEST -> {
//                println("old direction:" + this.direction)
//                if (this.direction == Shells.DIRECTION_WEST) {
//                    println("直行")
//                } else {
//                    //拐弯就只改变一下方向，不需要移动
//                    println("拐弯了，拐弯前的方向是${this.direction}")
//                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
//                    if (this.direction == Shells.DIRECTION_NORTH) {
//                        adjustY()
//                    } else if (this.direction == Shells.DIRECTION_SOUTH) {
//                        adjustY()
//                    }
//                    this.direction = Shells.DIRECTION_WEST
//                    return
//                }
//                this.direction = Shells.DIRECTION_WEST

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
                            adjustDirection(direction)
                        }
                    }
                } else if (col == 0) {
                    if (x > 0 + (SIZE - TANK_SIZE) / 2) {
                        var xOffset = -times * speed
                        transfer(xOffset, 0)
                    } else {
                        x = 0 + (SIZE - TANK_SIZE) / 2
                        adjustDirection(direction)
                    }
                }
                println("move over new x:$x, y:$y")

                //炮弹初始位置
                shellsX = cx
                shellsY = cy - shells.h / 2
            }

            Shells.DIRECTION_EAST -> {
//                println("old direction:" + this.direction)
//                if (this.direction == Shells.DIRECTION_EAST) {
//                    println("直行")
//                } else {
//                    //拐弯就只改变一下方向，不需要移动
//                    println("拐弯了，拐弯前的方向是${this.direction}")
//                    // 如果是垂直方向的行驶拐弯，需要处理对齐网格线的问题
//                    if (this.direction == Shells.DIRECTION_NORTH) {
//                        adjustY()
//                    } else if (this.direction == Shells.DIRECTION_SOUTH) {
//                        adjustY()
//                    }
//                    this.direction = Shells.DIRECTION_EAST
//                    return
//                }
//                this.direction = Shells.DIRECTION_EAST

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
                            adjustDirection(direction)
                        }
                    }

                } else if(col == CP.C - 2) {//抵达边界
                    if (x < ground.width - TANK_SIZE - (SIZE - TANK_SIZE) / 2) {
                        var xOffset = times * speed
                        transfer(xOffset, 0)
                    } else {
                        x = ground.width - TANK_SIZE - (SIZE - TANK_SIZE) / 2
                        adjustDirection(direction)
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
        drawTank(g)
    }
    override fun onTick() {
        walk()
//        //移动位置、发射炮弹、碰撞检测
//        //方向随机，碰到墙壁的处理、碰到队友的处理
//        when (direction) {
//            Shells.DIRECTION_NORTH -> {
//                // 撞墙处理
//                // 1创建方向list,size=4,
//                // 2当一个方向撞墙，移除这个方向，在剩下的方向中随机，
//                // 3如果新的方向还撞墙，重复步骤2，直到找到可以通行的方向(必然有可通行方向)
//                direction = adjustDirection(direction)
//                if (y <= Ground.TITLE_H) {
//                    y = Ground.TITLE_H
//                    transfer(0, 0)
//                } else {
//                    var yOffset = 0
//                    yOffset = if (y - Ground.TITLE_H < times * speed) {
//                        y - Ground.TITLE_H
//                    } else {
//                        (-times * speed).toInt()
//                    }
//                    transfer(0, yOffset)
//                }
//            }
//
//            Shells.DIRECTION_SOUTH -> {
//                direction = adjustDirection(direction)
////                transfer(0, 1 * speed)
//                if (y >= ground.height) {
//                    y = ground.height
//                    transfer(0, 0)
//                } else {
//                    var yOffset = 0
//                    yOffset = if (ground.height - SIZE - y < times * speed) {
//                        ground.height - SIZE - y
//                    } else {
//                        (times * speed).toInt()
//                    }
//                    transfer(0, yOffset)
//                }
//            }
//
//            Shells.DIRECTION_WEST -> {
//                direction = adjustDirection(direction)
////                transfer(-1 * speed, 0)
//                if (x <= 0) {
//                    x = 0
//                    transfer(0, 0)
//                } else {
//                    var xOffset = 0
//                    //当前坐标与边界的距离不足一步(取决于速度)的距离
//                    xOffset = if (x < times * speed) {
//                        -x
//                    } else {
//                        (-times * speed).toInt()
//                    }
//                    transfer(xOffset, 0)
//                }
//            }
//
//            Shells.DIRECTION_EAST -> {
//                direction = adjustDirection(direction)
////                transfer(1 * speed, 0)
//                if (x >= ground.width) {
//                    x = ground.width
//                    transfer(0, 0)
//                } else {
//                    var xOffset = 0
//                    //注意车身的尺寸不能忽略
//                    xOffset = if ((ground.width - SIZE - x) < (times * speed)) {
//                        ground.width - SIZE - x
//                    } else {
//                        (times * speed).toInt()
//                    }
//                    transfer(xOffset, 0)
//                }
//            }
//        }
    }

}