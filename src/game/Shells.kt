package game

import game.map.Brick
import game.map.Iron
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import kotlin.math.abs

/**
 * 炮弹类，按键后从坦克炮筒发出的炮弹
 */
class Shells : GameObject() {
    private var speed = 2.5
    var direction = 0
    val mapArray = CP.mapArray
    val tileArray = CP.tileArray
    var brick: Brick? = null
    var iron: Iron? = null

    //地图方块类型
    var tile = 0
    var level = LEVEL0

    //1.级别默认0，速度慢，火力很弱，3发消灭胖子坦克，无法破坏铁块
    //2.级别1，速度快，火力很弱，3发消灭胖子坦克，消除1层砖块，无法破坏铁块
    //3.级别2，速度快，火力升级，2发消灭胖子坦克，消除2层砖块，无法破坏铁块
    //4.级别3，速度块，火力升级，2发消灭胖子坦克，消除2层砖块，可以破块铁块
    //道具星星，吃1个lv1，吃2个lv2，吃3个lv3
    var times = 2

    val d = 10
    val SIZE = CP.SIZE
    var doCollision = false
    var observer: GOObserver? = null

    //缓存
    var hit = Hit()

    companion object {
        const val DIRECTION_EAST = 1
        const val DIRECTION_WEST = 2
        const val DIRECTION_SOUTH = 4
        const val DIRECTION_NORTH = 8

        const val LEVEL0 = 0
        const val LEVEL1 = 1
        const val LEVEL2 = 2
        const val LEVEL3 = 3
    }

    init {
        w = d
        h = d
        level = LEVEL1
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
                var xGrid = x / SIZE
                var yGrid = y / SIZE
                var yNext = if (yGrid <= 1) 0 else yGrid - 1

                // 炮弹前方有障碍物，准备碰撞检测和处理；只需要处理砖块和钢铁的碰撞
                if (doCollision) {
                    handleCollision(DIRECTION_NORTH)
                } else {
                    //前进方向有没有障碍物
                    // 1.没有跨网格
                    println("bullet x grd:$xGrid, y grd:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
                    println("bullet x:$x, y:$y")
                    if (mapArray[yNext][xGrid].toInt() == CP.TILE_BRICK) {//发现砖块障碍物
                        println("发现砖块障碍物，当前:$yGrid, 前方:$yNext")
                        brick = tileArray[yNext][xGrid] as Brick
                        doCollision = true
                        tile = CP.TILE_BRICK
                    } else if (mapArray[yNext][xGrid].toInt() == CP.TILE_IRON) {//发现钢铁障碍物
                        println("发现钢铁障碍物，当前:$yGrid, 前方:$yNext")
                        iron = tileArray[yNext][xGrid] as Iron
                        doCollision = true
                        tile = CP.TILE_IRON
                    } else {
                        if (y <= ground.t) {
                            println("bullet up 333 炮弹已到达顶部")
                            y = ground.t
                            transfer(0, 0)
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            //println("bullet up 444")
                            var yOffset = (-times * speed).toInt()
                            transfer(0, yOffset)
                        }
                    }
                }
            }

            DIRECTION_SOUTH -> {
                var xGrid = x / SIZE
                var yGrid = y / SIZE
                var yNext = if (yGrid >= CP.R - 2) CP.R - 1 else yGrid + 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    handleCollision(DIRECTION_SOUTH)
                } else {
                    //前进方向有没有障碍物
                    // 1.没有跨网格
                    println("bullet x grd:$xGrid, y grd:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
                    println("bullet x:$x, y:$y")
                    if (mapArray[yNext][xGrid].toInt() == CP.TILE_BRICK) {//发现砖块障碍物
                        println("发现砖块障碍物，当前:$yGrid, 前方:$yNext")
                        brick = tileArray[yNext][xGrid] as Brick
                        doCollision = true
                        tile = CP.TILE_BRICK
                    } else if (mapArray[yNext][xGrid].toInt() == CP.TILE_IRON) {//发现钢铁障碍物
                        println("发现钢铁障碍物，当前:$yGrid, 前方:$yNext")
                        iron = tileArray[yNext][xGrid] as Iron
                        doCollision = true
                        tile = CP.TILE_IRON
                    }  else {
                        println(ground.b)
                        if (y >= ground.b) {
                            println("bullet down 333")
                            y = ground.b
                            transfer(0, 0)
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            println("bullet down 444")
                            var yOffset = (times * speed).toInt()
                            transfer(0, yOffset)
                        }
                    }
                }
            }

            DIRECTION_WEST -> {
                var xGrid = x / SIZE
                var yGrid = y / SIZE
                var xNext = if (xGrid - 1 <= 0) 0 else xGrid - 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    handleCollision(DIRECTION_WEST)
                } else {
                    //前进方向有没有障碍物
                    // 1.没有跨网格
                    println("bullet x grd:$xGrid, y grd:$yGrid, next xGrid:${xNext} :${mapArray[yGrid][xNext]}")
                    println("y:$y, maxY:$maxY")
                    if (mapArray[yGrid][xNext].toInt() == CP.TILE_BRICK) {
                        println("发现砖块障碍物，当前:$xGrid, 前方:$xNext")
                        brick = tileArray[yGrid][xNext] as Brick
                        doCollision = true
                        tile = CP.TILE_BRICK
                    } else if (mapArray[yGrid][xNext].toInt() == CP.TILE_IRON) {
                        println("发现钢铁障碍物，当前:$xGrid, 前方:$xNext")
                        iron = tileArray[yGrid][xNext] as Iron
                        doCollision = true
                        tile = CP.TILE_IRON
                    } else {
                        if (x <= ground.l) {
                            println("bullet left 333")
                            x = ground.l
                            transfer(0, 0)
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            println("bullet left 444")
                            var xOffset = (-times * speed).toInt()
                            transfer(xOffset, 0)
                        }
                    }
                }
            }

            DIRECTION_EAST -> {
                var xGrid = x / SIZE
                var yGrid = y / SIZE
                var xNext = if (xGrid >= CP.C - 2) CP.C - 1 else xGrid + 1

                // 炮弹前方有障碍物，准备碰撞检测和处理
                if (doCollision) {
                    handleCollision(DIRECTION_EAST)
                } else {
                    //前进方向有没有障碍物
                    // 1.没有跨网格
                    println("bullet x grd:$xGrid, y grd:$yGrid, next xGrid:${xNext} :${mapArray[yGrid][xNext]}")
                    println("y:$y, maxY:$maxY")
                    if (mapArray[yGrid][xNext].toInt() == CP.TILE_BRICK) {
                        println("发现砖块障碍物，当前:$xGrid, 前方:$xNext")
                        brick = tileArray[yGrid][xNext] as Brick
                        doCollision = true
                        tile = CP.TILE_BRICK
                    } else if (mapArray[yGrid][xNext].toInt() == CP.TILE_IRON) {
                        println("发现砖块障碍物，当前:$xGrid, 前方:$xNext")
                        iron = tileArray[yGrid][xNext] as Iron
                        doCollision = true
                        tile = CP.TILE_IRON
                    } else {
                        if (x >= ground.r) {
                            println("bullet right 333")
                            x = ground.r
                            transfer(0, 0)
                            isDestroyed = true
                            observer?.die(this)
                        } else {
                            println("bullet right 444")
                            var xOffset = (times * speed).toInt()
                            transfer(xOffset, 0)
                        }
                    }
                }
            }

            else -> transfer(0, 0)
        }
    }

    /**
     * 处理与砖块和钢铁的碰撞
     */
    fun handleCollision(direction: Int) {
        var go: GameObject? = null
        when (tile) {
            CP.TILE_BRICK -> {
                go = brick
            }

            CP.TILE_IRON -> {
                go = iron
            }
        }
        var bcx = go!!.cx
        var bcy = go!!.cy
        var wOf2 = (w + go!!.w) / 2
        var hOf2 = (h + go!!.h) / 2
        println("炮弹x:$x, y:$y, $w, $h")
        println("go x:${go!!.x}, y:${go!!.y}, w:${go!!.w}, h:${go!!.h}brickCX:$bcx, brickCY:$bcy")
        if (abs(cx - bcx) <= wOf2 && abs(cy - bcy) <= hOf2) {
            //增加击中爆破效果
            hit.reset()
            hit.observer = observer
            hit.x = x - w / 2
            hit.y = y - h
            observer?.born(hit)

            println("--碰撞--炮弹击中了砖块/钢铁 炮弹x:$x, y:$y, brickCX:$bcx, brickCY:$bcy")
            //通知砖块碰撞消息
            brick?.shells = this
            iron?.shells = this

            //炮弹消失，数据相关数据重置
            doCollision = false
            isDestroyed = true
            observer?.die(this)
            brick = null
            iron = null
        } else {
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
        }
    }

}