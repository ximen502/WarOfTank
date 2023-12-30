package game

import game.map.Brick
import game.map.Iron
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.abs

/**
 * 炮弹类，按键后从坦克炮筒发出的炮弹
 * @Author xsc
 */
class Shells : GameObject() {
    private var speed = 2.5
    var direction = 0
    val mapArray = CP.mapArray
    val tileArray = CP.tileArray

    //前方障碍物瓦片的容器,size=2
    var tilePair = arrayOfNulls<GameObject>(2)
    //需要处理的8种情况
    //[brick,brick], [brick,iron], [iron,iron], [iron,brick]...
    //容器数据辅助判断
    var tilePairFlag = 0

    //炮弹级别
    var level = LEVEL0

    //1.级别默认0，速度慢，火力很弱，3发消灭胖子坦克，无法破坏铁块
    //2.级别1，速度快，火力很弱，3发消灭胖子坦克，消除1层砖块，无法破坏铁块
    //3.级别2，速度快，火力升级，2发消灭胖子坦克，消除2层砖块，无法破坏铁块
    //4.级别3，速度块，火力升级，2发消灭胖子坦克，消除2层砖块，可以破块铁块
    //道具星星，吃1个lv1，吃2个lv2，吃3个lv3
    var times = 1

    val d = 10
    val SIZE = CP.SIZE
    val SIZE_M = CP.SIZE_M
    //赋值要谨慎，不然影响碰撞逻辑处理
    var doCollision = false
    //var doCollision2 = 0
    var observer: GOObserver? = null
    // 4个方向的炮弹图片
    var imageN:BufferedImage
    var imageS:BufferedImage
    var imageW:BufferedImage
    var imageE:BufferedImage

    //缓存
    var hit = Hit()
    // 代表炮弹的矩形
    var rect: Rectangle = Rectangle(x, y, w, h)

    companion object {
        const val DIRECTION_EAST = 1
        const val DIRECTION_WEST = 2
        const val DIRECTION_SOUTH = 4
        const val DIRECTION_NORTH = 8

        const val LEVEL0 = 0
        const val LEVEL1 = 1
        const val LEVEL2 = 2
        const val LEVEL3 = 3

        //brick, iron, empty,两两组合(9-1=8)
        const val BB = 'B'.code shl 8 or 'B'.code
        const val BI = 'B'.code shl 8 or 'I'.code
        const val BE = 'B'.code shl 8 or 'E'.code
        const val IB = 'I'.code shl 8 or 'B'.code
        const val II = 'I'.code shl 8 or 'I'.code
        const val IE = 'I'.code shl 8 or 'E'.code
        const val EB = 'E'.code shl 8 or 'B'.code
        const val EI = 'E'.code shl 8 or 'I'.code
        //const val EE = 0x80 useless
    }

    init {
        w = d
        h = d
        level = LEVEL1
        isDestroyed = true
        val path = javaClass.getResource("image/shn.png")
        println("shells path:$path")
        imageN = ImageIO.read(path)
        imageS = ImageIO.read(javaClass.getResource("image/shs.png"))
        imageW = ImageIO.read(javaClass.getResource("image/shw.png"))
        imageE = ImageIO.read(javaClass.getResource("image/she.png"))
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        var color = g2.color
        g2.color = Color.YELLOW
        // 炮弹直径
//        g2.fillRect(x, y, d, d)
        g2.color = color
        when (direction) {
            DIRECTION_NORTH -> {
                g2.drawImage(imageN, x, y, null)
            }

            DIRECTION_SOUTH -> {
                g2.drawImage(imageS, x, y, null)
            }

            DIRECTION_WEST -> {
                g2.drawImage(imageW, x, y, null)
            }

            DIRECTION_EAST -> {
                g2.drawImage(imageE, x, y, null)
            }

            else -> {}
        }
    }

    override fun onTick() {
        when (direction) {
            DIRECTION_NORTH -> {
                // column
                val xGrid = x / SIZE_M
                // row
                val yGrid = y / SIZE_M
                val yNext = if (yGrid <= 1) 0 else yGrid - 1
                /**************************************************************
                 * 炮弹消除瓦片的逻辑需要修改，由于坦克车身需要2块网格的宽度才能通行，
                 * 因此炮弹前进遇到瓦片障碍物，需要消除2块瓦片才行，如果炮弹无法消除钢铁，
                 * 那么炮弹就不能再前进。
                 *
                 * 2023-12-22 根据最近几天的测试发现，如果炮弹前方有砖块，不一定要处理
                 * 碰撞，因为砖块剩余的部分很可能已经不在炮弹的行走的路径上。
                 * ***********************************************************/
                // 炮弹前方有障碍物，准备碰撞检测和处理；只需要处理砖块和钢铁的碰撞
                if (doCollision) {
                    handleCollision(DIRECTION_NORTH)
                } else {
                    // 判断前进方向有没有障碍物，需要对前方的2个网格进行判断。
                    println("bullet x grd:$xGrid, y grd:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
                    println("bullet x:$x, y:$y")
                    // 前方可以通行
                    if (mapArray[yNext][xGrid].toInt() != CP.TILE_BRICK
                        && mapArray[yNext][xGrid].toInt() != CP.TILE_IRON
                        && mapArray[yNext][xGrid + 1].toInt() != CP.TILE_BRICK
                        && mapArray[yNext][xGrid + 1].toInt() != CP.TILE_IRON) {
                        if (y <= ground.t) {
                            println("bullet up 炮弹已到达顶部")
                            y = ground.t
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            //println("bullet up 444")
                            var yOffset = (-times * speed).toInt()
                            transfer(0, yOffset)
                        }
                    } else {// 有障碍物，无法通行
                        var flag = 0 //
                        if (mapArray[yNext][xGrid].toInt() == CP.TILE_BRICK) {//第一格发现障碍物
                            tilePair[0] = tileArray[yNext][xGrid]
                            doCollision = true
                            flag = 'B'.code shl 8
                        } else if (mapArray[yNext][xGrid].toInt() == CP.TILE_IRON) {
                            tilePair[0] = tileArray[yNext][xGrid]
                            doCollision = true
                            flag = 'I'.code shl 8
                        } else { //不处理
                            tilePair[0] = null
                            flag = 'E'.code shl 8
                        }

                        if(mapArray[yNext][xGrid + 1].toInt() == CP.TILE_BRICK) {//第二格发现障碍物
                            tilePair[1] = tileArray[yNext][xGrid + 1]
                            doCollision = true
                            flag = flag or 'B'.code
                        } else if (mapArray[yNext][xGrid + 1].toInt() == CP.TILE_IRON) {
                            tilePair[1] = tileArray[yNext][xGrid + 1]
                            doCollision = true
                            flag = flag or 'I'.code
                        } else {
                            tilePair[1] = null
                            flag = flag or 'E'.code
                        }
                        tilePairFlag = flag
                    }
                }
            }

            DIRECTION_SOUTH -> {
                val xGrid = x / SIZE_M
                val yGrid = y / SIZE_M
                val yNext = if (yGrid >= CP.R - 2) CP.R - 1 else yGrid + 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    handleCollision(DIRECTION_SOUTH)
                } else {
                    //前进方向有没有障碍物
                    //println("bullet x grd:$xGrid, y grd:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
                    //println("bullet x:$x, y:$y")
                    // 前方可以通行
                    if (mapArray[yNext][xGrid].toInt() != CP.TILE_BRICK
                        && mapArray[yNext][xGrid].toInt() != CP.TILE_IRON
                        && mapArray[yNext][xGrid + 1].toInt() != CP.TILE_BRICK
                        && mapArray[yNext][xGrid + 1].toInt() != CP.TILE_IRON) {
                        if (y >= ground.b) {
                            println("bullet down 炮弹已到达底部")
                            y = ground.b
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            var yOffset = (times * speed).toInt()
                            transfer(0, yOffset)
                        }
                    } else {// 有障碍物，无法通行
                        var flag = 0 //
                        if (mapArray[yNext][xGrid].toInt() == CP.TILE_BRICK) {//第一格发现障碍物
                            tilePair[0] = tileArray[yNext][xGrid]
                            doCollision = true
                            flag = 'B'.code shl 8
                        } else if (mapArray[yNext][xGrid].toInt() == CP.TILE_IRON) {
                            tilePair[0] = tileArray[yNext][xGrid]
                            doCollision = true
                            flag = 'I'.code shl 8
                        } else { //不处理
                            tilePair[0] = null
                            flag = 'E'.code shl 8
                        }

                        if(mapArray[yNext][xGrid + 1].toInt() == CP.TILE_BRICK) {//第二格发现障碍物
                            tilePair[1] = tileArray[yNext][xGrid + 1]
                            doCollision = true
                            flag = flag or 'B'.code
                        } else if (mapArray[yNext][xGrid + 1].toInt() == CP.TILE_IRON) {
                            tilePair[1] = tileArray[yNext][xGrid + 1]
                            doCollision = true
                            flag = flag or 'I'.code
                        } else {
                            tilePair[1] = null
                            flag = flag or 'E'.code
                        }
                        tilePairFlag = flag
                    }
                }
            }

            DIRECTION_WEST -> {
                val xGrid = x / SIZE_M
                val yGrid = y / SIZE_M
                val xNext = if (xGrid - 1 <= 0) 0 else xGrid - 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    handleCollision(DIRECTION_WEST)
                } else {
                    //前进方向有没有障碍物
                    println("bullet x grd:$xGrid, y grd:$yGrid, next xGrid:${xNext} :${mapArray[yGrid][xNext]}")
                    println("y:$y, maxY:$maxY")

                    // 前方可以通行
                    if (mapArray[yGrid][xNext].toInt() != CP.TILE_BRICK
                        && mapArray[yGrid][xNext].toInt() != CP.TILE_IRON
                        && mapArray[yGrid + 1][xNext].toInt() != CP.TILE_BRICK
                        && mapArray[yGrid + 1][xNext].toInt() != CP.TILE_IRON) {
                        if (x <= ground.l) {
                            x = ground.l
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            var xOffset = (-times * speed).toInt()
                            transfer(xOffset, 0)
                        }
                    } else {// 有障碍物，无法通行
                        var flag = 0 //
                        if (mapArray[yGrid][xNext].toInt() == CP.TILE_BRICK) {//第一格发现障碍物
                            tilePair[0] = tileArray[yGrid][xNext]
                            doCollision = true
                            //doCollision2++
                            flag = 'B'.code shl 8
                        } else if (mapArray[yGrid][xNext].toInt() == CP.TILE_IRON) {
                            tilePair[0] = tileArray[yGrid][xNext]
                            doCollision = true
                            flag = 'I'.code shl 8
                        } else { //不处理
                            tilePair[0] = null
                            flag = 'E'.code shl 8
                        }

                        if(mapArray[yGrid + 1][xNext].toInt() == CP.TILE_BRICK) {//第二格发现障碍物
                            tilePair[1] = tileArray[yGrid + 1][xNext]
                            doCollision = true
                            flag = flag or 'B'.code
                        } else if (mapArray[yGrid + 1][xNext].toInt() == CP.TILE_IRON) {
                            tilePair[1] = tileArray[yGrid + 1][xNext]
                            doCollision = true
                            flag = flag or 'I'.code
                        } else {
                            tilePair[1] = null
                            flag = flag or 'E'.code
                        }
                        tilePairFlag = flag
                    }
                }
            }

            DIRECTION_EAST -> {
                val xGrid = x / SIZE_M
                val yGrid = y / SIZE_M
                val xNext = if (xGrid >= CP.C - 2) CP.C - 1 else xGrid + 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    handleCollision(DIRECTION_EAST)
                } else {
                    //前进方向有没有障碍物
                    println("bullet x grd:$xGrid, y grd:$yGrid, next xGrid:${xNext} :${mapArray[yGrid][xNext]}")
                    println("y:$y, maxY:$maxY")

                    // 前方可以通行
                    if (mapArray[yGrid][xNext].toInt() != CP.TILE_BRICK
                        && mapArray[yGrid][xNext].toInt() != CP.TILE_IRON
                        && mapArray[yGrid + 1][xNext].toInt() != CP.TILE_BRICK
                        && mapArray[yGrid + 1][xNext].toInt() != CP.TILE_IRON) {
                        if (x >= ground.r) {
                            //println("bullet right 333")
                            x = ground.r
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            //println("bullet right 444")
                            var xOffset = (times * speed).toInt()
                            transfer(xOffset, 0)
                        }
                    } else {// 有障碍物，无法通行
                        var flag = 0 //
                        if (mapArray[yGrid][xNext].toInt() == CP.TILE_BRICK) {//第一格发现障碍物
                            tilePair[0] = tileArray[yGrid][xNext]
                            doCollision = true
                            flag = 'B'.code shl 8
                        } else if (mapArray[yGrid][xNext].toInt() == CP.TILE_IRON) {
                            tilePair[0] = tileArray[yGrid][xNext]
                            doCollision = true
                            flag = 'I'.code shl 8
                        } else { //不处理
                            tilePair[0] = null
                            flag = 'E'.code shl 8
                        }

                        if(mapArray[yGrid + 1][xNext].toInt() == CP.TILE_BRICK) {//第二格发现障碍物
                            tilePair[1] = tileArray[yGrid + 1][xNext]
                            doCollision = true
                            flag = flag or 'B'.code
                        } else if (mapArray[yGrid + 1][xNext].toInt() == CP.TILE_IRON) {
                            tilePair[1] = tileArray[yGrid + 1][xNext]
                            doCollision = true
                            flag = flag or 'I'.code
                        } else {
                            tilePair[1] = null
                            flag = flag or 'E'.code
                        }
                        tilePairFlag = flag
                    }
                }
            }

            else -> transfer(0, 0)
        }
    }

    /**
     * 处理炮弹与砖块和钢铁的碰撞，需要同时处理2个网格
     * 8种情况
     */
    private fun handleCollision(direction: Int) {
        val tile0: GameObject? = tilePair[0]
        val tile1: GameObject? = tilePair[1]

        when(tilePairFlag) {
            BB -> {
                val brick0 = tile0 as Brick
                val brick1 = tile1 as Brick
                val bcx = brick0.cx
                val bcy = brick0.cy
                val wOf2 = (w + brick0.w) / 2
                val hOf2 = (h + brick0.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")
                // (1)2个砖块，如果炮弹击中了一个砖块，那么另一个砖块肯定也碰撞了，直接处理碰撞逻辑即可
                // 如上(1)所述，这样是不够严谨的，有bug。
                // 2个障碍物的情况，要分别处理碰撞逻辑，而且必须讲究先后顺序，距离近的优先处理
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${brick0.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    brick0.shells = this
                    brick1.shells = this

                    reset()
                } else {
                    move()
                }
            }
            BI -> {
                val brick0 = tile0 as Brick
                val iron1 = tile1 as Iron
                val bcx = brick0.cx
                val bcy = brick0.cy
                val wOf2 = (w + brick0.w) / 2
                val hOf2 = (h + brick0.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")
                // 2个砖块，如果炮弹击中了一个砖块，那么另一个砖块肯定也碰撞了，直接处理碰撞逻辑即可
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${brick0.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    brick0.shells = this
                    iron1.shells = this

                    reset()
                } else {
                    move()
                }
            }
            BE -> {
                val brick0 = tile0 as Brick
                val bcx = brick0.cx
                val bcy = brick0.cy
                val wOf2 = (w + brick0.w) / 2
                val hOf2 = (h + brick0.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")

                //直接创建一个未来的矩形和砖头碰撞检测，碰不到就不需要再处理了
                //前方虽然有障碍物，但是被炮弹击中消除了一部分，剩余部分不会和炮弹发生碰撞
                //暂时注释，不够成熟
//                val gh = Rectangle()
//                gh.x = x
//                gh.y = brick0.y
//                gh.width = w / 2
//                gh.height = brick0.h
//                val collision = gh.intersects(Rectangle(brick0.x, brick0.y, brick0.w, brick0.h))
//                println("collision:$collision")
//                if (!collision) {
//                    doCollision = false
//                    move()
//                }

                // 如果炮弹已经不在砖块网格可碰撞范围那么，没必要再处理了
                mayStop(direction, brick0)

                // 2个障碍物的情况，要分别处理碰撞逻辑，而且必须讲究先后顺序，距离近的优先处理
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${brick0.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    brick0.shells = this

                    reset()
                } else {
                    move()
                }
            }
            IB -> {
                val iron0 = tile0 as Iron
                val brick1 = tile1 as Brick
                val bcx = iron0.cx
                val bcy = iron0.cy
                val wOf2 = (w + iron0.w) / 2
                val hOf2 = (h + iron0.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")
                // 2个砖块，如果炮弹击中了一个砖块，那么另一个砖块肯定也碰撞了，直接处理碰撞逻辑即可
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${iron0.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    iron0.shells = this
                    brick1.shells = this

                    reset()
                } else {
                    move()
                }
            }
            II -> {
                val iron0 = tile0 as Iron
                val iron1 = tile1 as Iron
                val bcx = iron0.cx
                val bcy = iron0.cy
                val wOf2 = (w + iron0.w) / 2
                val hOf2 = (h + iron0.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")
                // 2个砖块，如果炮弹击中了一个砖块，那么另一个砖块肯定也碰撞了，直接处理碰撞逻辑即可
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${iron0.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    iron0.shells = this
                    iron1.shells = this

                    reset()
                } else {
                    move()
                }
            }
            IE -> {
                val iron0 = tile0 as Iron
                val bcx = iron0.cx
                val bcy = iron0.cy
                val wOf2 = (w + iron0.w) / 2
                val hOf2 = (h + iron0.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")
                // 2个砖块，如果炮弹击中了一个砖块，那么另一个砖块肯定也碰撞了，直接处理碰撞逻辑即可
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${iron0.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    iron0.shells = this

                    reset()
                } else {
                    move()
                }
            }
            EB -> {
                val brick1 = tile1 as Brick
                val bcx = brick1.cx
                val bcy = brick1.cy
                val wOf2 = (w + brick1.w) / 2
                val hOf2 = (h + brick1.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")

                // 如果炮弹已经不在砖块网格可碰撞范围那么，没必要再处理了
                mayStop(direction, brick1)

                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${brick1.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    brick1.shells = this

                    reset()
                } else {
                    move()
                }
            }
            EI -> {
                val iron1 = tile1 as Iron
                val bcx = iron1.cx
                val bcy = iron1.cy
                val wOf2 = (w + iron1.w) / 2
                val hOf2 = (h + iron1.h) / 2
                //println("炮弹x:$x, y:$y, $w, $h")
                //println("brick x:${brick.x}, y:${brick.y}, w:${brick.w}, h:${brick.h}brickCX:$bcx, brickCY:$bcy")
                // 2个砖块，如果炮弹击中了一个砖块，那么另一个砖块肯定也碰撞了，直接处理碰撞逻辑即可
                if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
                    hitEffect()

                    println("--碰撞--炮弹击中了砖块 炮弹x:$x, y:$y, brickID: ${iron1.id} brickCX:$bcx, brickCY:$bcy")
                    //通知砖块碰撞消息
                    iron1.shells = this

                    reset()
                } else {
                    move()
                }
            }
        }
    }

    private fun reset() {
        val observer = this.observer
        //炮弹消失，数据相关数据重置
        doCollision = false
        isDestroyed = true
        observer?.die(this)
    }

    /**
     * 炮弹击中目标的效果
     */
    private fun hitEffect() {
        val hit = this.hit
        val observer = this.observer
        //增加击中爆破效果
        hit.reset()
        hit.observer = observer
        hit.x = x - w / 2
        hit.y = y - h
        observer?.born(hit)
    }

    /**
     * 在即将接近障碍物的最后一个网格，继续进行移动
     */
    private fun move() {
        when (direction) {
            DIRECTION_NORTH -> {
                println("还没有碰撞，正在接近")
                var yOffset = (-times * speed).toInt()
                transfer(0, yOffset)
            }
            DIRECTION_SOUTH -> {
                println("还没有碰撞，正在接近")
                var yOffset = (times * speed).toInt()
                transfer(0, yOffset)
            }
            DIRECTION_WEST -> {
                println("还没有碰撞，正在接近")
                var xOffset = (-times * speed).toInt()
                transfer(xOffset, 0)
            }
            DIRECTION_EAST -> {
                println("还没有碰撞，正在接近")
                var xOffset = (times * speed).toInt()
                transfer(xOffset, 0)
            }
            else -> {}
        }
        // 有时候出现一个bug，炮弹跑到障碍物上面并飞出游戏窗口了，可是炮弹没消失，还在前进
        if (x < ground.l || x > ground.r) {
            println("水平方向已经出界")
//            observer?.die(this)
        } else if (y < ground.t || y > ground.b) {
            println("垂直方向已经出界")
//            observer?.die(this)
        }

    }

    /**
     * 判断是否要停止处理碰撞
     */
    private fun mayStop(direction: Int, br: Brick) {
        when (direction) {
            DIRECTION_NORTH -> {
                if (y <= br.y + SIZE_M / 2) {
                    doCollision = false
                }
            }

            DIRECTION_SOUTH -> {
                if (y > br.y + SIZE_M / 2) {
                    doCollision = false
                }
            }

            DIRECTION_WEST -> {
                if (x <= br.x + SIZE_M / 2) {
                    doCollision = false
                }
            }

            DIRECTION_EAST -> {
                if (x > br.x + SIZE_M / 2) {
                    doCollision = false
                }
            }

            else -> {}
        }
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

}