package game

import java.applet.Applet
import java.applet.AudioClip
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * 坦克，根据给定的坐标绘制一个坦克的俯视图
 * 矩形车身+圆形炮台+矩形炮筒
 */
class Tank(input: Input, ground: Ground) : AbstractTank() {
    private var image: Image = Toolkit.getDefaultToolkit().createImage("image/Snow.png")

    private var input: Input

    var observer: GOObserver? = null
    var fireAC: AudioClip? = null
    val mapArray = CP.mapArray
    var img: BufferedImage
    var imgX = 0
    var imgY = 0
    var transform = AffineTransform()
    var angle = 0.0
    companion object {
        val ANGLE_0 = Math.toRadians(0.0)
        val ANGLE_90 = Math.toRadians(90.0)
        val ANGLE_180 = Math.toRadians(180.0)
        val ANGLE_270 = Math.toRadians(270.0)
    }

    //炮弹缓存
    val shells = Shells()

    init {
        this.ground = ground
        println("w:${ground.width}")
        x = ground.width / 2 - (SIZE * 2.5f).toInt()
        y = ground.height - SIZE
        w = SIZE
        h = SIZE
        shellsX = cx - shells.w / 2
        shellsY = cy
        direction = Shells.DIRECTION_NORTH
        //println("tank x:$x, y:$y, cx:$cx, cy:$cy, shells x:$shellsX, y:$shellsY")
        this.input = input
        times = 2
//        println(javaClass.toString())
//        var resource = javaClass.getResource("")
//        println(resource)
        var tankPath = javaClass.getResource("image/tank.png")
        println(tankPath)
        img = ImageIO.read(tankPath)
        imgX = (w - img.width) / 2
        imgY = (h - img.height) / 2
//        println(resource2)
        fireAC = Applet.newAudioClip(javaClass.getResource("./../Gunfire.wav"))
    }

    override fun draw(g: Graphics?) {
        g?.color = Color.YELLOW
        drawTank(g)
    }

    override fun onTick() {
        if (input.getKeyDown(KeyEvent.VK_LEFT) == true) {
            direction = Shells.DIRECTION_WEST
            angle = ANGLE_270
            var xGrid = x / SIZE
            var yGrid = y / SIZE
            var xNext = if (xGrid - 1 <= 0) 0 else xGrid - 1

            //前进方向有没有障碍物
            // 分2种情况，判断y有没有跨网格
            // 1.没有跨网格
            // 2.有跨网格
            println("y:$y, maxY:$maxY")
            var mod = y % SIZE
            println("mod:$mod")
            if (mod == 0) {//1
                if (mapArray[yGrid][xNext].toInt() in 1..3) {
                    if (x <= xGrid * SIZE) {
                        x = xGrid * SIZE
                        transfer(0, 0)
                    } else {
                        var xOffset = (-times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                } else {
                    if (x <= 0) {
                        x = 0
                        transfer(0, 0)
                    } else {
                        var xOffset = (-times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                }
            } else {//2
                val yCur = mapArray[yGrid][xNext].toInt()
                val yNext = mapArray[yGrid + 1][xNext].toInt()
                println("yCur:$yCur, yNext:$yNext")
                if (yCur in 1..3 || yNext in 1..3) {
                    if (x <= xGrid * SIZE) {
                        x = xGrid * SIZE
                        transfer(0, 0)
                    } else {
                        var xOffset = (-times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                } else {
                    if (x <= 0) {
                        x = 0
                        transfer(0, 0)
                    } else {
                        var xOffset = (-times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                }
            }
            //炮弹初始位置
            shellsX = cx
            shellsY = cy - shells.h / 2
        } else if (input.getKeyDown(KeyEvent.VK_RIGHT) == true) {
            direction = Shells.DIRECTION_EAST
            angle = ANGLE_90
            var xGrid = x / SIZE
            var yGrid = y / SIZE
            var xNext = if (xGrid + 1 >= CP.C - 1) CP.C - 1 else xGrid + 1

            //前进方向有没有障碍物
            // 分2种情况，判断y有没有跨网格
            // 1.没有跨网格
            // 2.有跨网格
            println("y:$y, maxY:$maxY")
            var mod = y % SIZE
            println("mod:$mod")
            if (mod == 0) {//1
                //println("x:$xGrid, y:$yGrid, next xGrid:${xNext} :${mapArray[yGrid][xNext]}")
                if (mapArray[yGrid][xNext].toInt() in 1..3) {
                    if (x >= xGrid * SIZE) {
                        //println("r111")
                        x = xGrid * SIZE
                        transfer(0, 0)
                    } else {//测试发现没执行，然而程序逻辑正常
                        //println("r222")
                        var xOffset = (times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                } else {
                    if (x + SIZE >= ground.width) {
                        //println("r333")
                        x = ground.width - SIZE
                        transfer(0, 0)
                    } else {
                        //println("r444")
                        var xOffset = (times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                }
            } else {//2
                val yCur = mapArray[yGrid][xNext].toInt()
                val yNext = mapArray[yGrid + 1][xNext].toInt()
                println("yCur:$yCur, yNext:$yNext")
                if (yCur in 1..3 || yNext in 1..3) {
                    if (x >= xGrid * SIZE) {
                        //println("r111")
                        x = xGrid * SIZE
                        transfer(0, 0)
                    } else {//测试发现没执行，然而程序逻辑正常
                        //println("r222")
                        var xOffset = (times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                } else {
                    if (x + SIZE >= ground.width) {
                        //println("r333")
                        x = ground.width - SIZE
                        transfer(0, 0)
                    } else {
                        //println("r444")
                        var xOffset = (times * speed).toInt()
                        transfer(xOffset, 0)
                    }
                }
            }
            //炮弹初始位置
            shellsX = cx
            shellsY = cy - shells.h / 2
        } else if (input.getKeyDown(KeyEvent.VK_UP) == true) {
            direction = Shells.DIRECTION_NORTH
            angle = ANGLE_0
            var xGrid = x / SIZE
            var yGrid = y / SIZE
            var yNext = if (yGrid - 1 <= 0) 0 else yGrid - 1

            //前进方向有没有障碍物
            // 分2种情况，判断x有没有跨网格
            // 1.没有跨网格
            // 2.有跨网格
            println(mapArray[yGrid].contentToString())
            println("x:$xGrid, y:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
            var mod = x % SIZE
            if (mod == 0) {//1
                if (mapArray[yNext][xGrid].toInt() in 1..3) {
                    if (y <= yGrid * SIZE) {
                        //println("up111")
                        y = yGrid * SIZE
                        transfer(0, 0)
                    } else {
                        //println("up222")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                } else {
                    if (y <= Ground.TITLE_H) {
                        //println("up333")
                        y = Ground.TITLE_H
                        transfer(0, 0)
                    } else {
                        //println("up444")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }
            } else {//2
                if (mapArray[yNext][xGrid].toInt() in 1..3 || mapArray[yNext][xGrid + 1].toInt() in 1..3) {
                    if (y <= yGrid * SIZE) {
                        //println("up111")
                        y = yGrid * SIZE
                        transfer(0, 0)
                    } else {
                        //println("up222")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                } else {
                    if (y <= Ground.TITLE_H) {
                        //println("up333")
                        y = Ground.TITLE_H
                        transfer(0, 0)
                    } else {
                        //println("up444")
                        var yOffset = (-times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }
            }
            //炮弹初始位置
            shellsX = cx - shells.w / 2
            shellsY = cy
        } else if (input.getKeyDown(KeyEvent.VK_DOWN) == true) {
            direction = Shells.DIRECTION_SOUTH
            angle = ANGLE_180
            var xGrid = x / SIZE
            var yGrid = y / SIZE
            var yNext = if (yGrid + 1 >= CP.R - 1) CP.R - 1 else yGrid + 1

            //前进方向有没有障碍物
            // 分2种情况，判断x有没有跨网格
            // 1.没有跨网格
            // 2.有跨网格
            println(mapArray[yGrid].contentToString())
            println("x:$xGrid, y:$yGrid, next yGrid:${yNext} :${mapArray[yNext][xGrid]}")
            var mod = x % SIZE
            if (mod == 0) {//1
                if (mapArray[yNext][xGrid].toInt() in 1..3) {
                    if (y + SIZE >= yGrid * SIZE) {
                        //println("down111")
                        y = yGrid * SIZE
                        transfer(0, 0)
                    } else {// 可以去掉，不影响逻辑
                        //println("down222")
                        var yOffset = (times * speed).toInt()
                        transfer(0, yOffset)
                    }
                } else {
                    if (y + SIZE >= ground.height) {
                        //println("down333")
                        y = ground.height - SIZE
                        transfer(0, 0)
                    } else {
                        //println("down444")
                        var yOffset = (times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }
            } else {//2
                if (mapArray[yNext][xGrid].toInt() in 1..3 || mapArray[yNext][xGrid + 1].toInt() in 1..3) {
                    if (y + SIZE >= yGrid * SIZE) {
                        //println("down111")
                        y = yGrid * SIZE
                        transfer(0, 0)
                    } else {// 可以去掉，不影响逻辑
                        //println("down222")
                        var yOffset = (times * speed).toInt()
                        transfer(0, yOffset)
                    }
                } else {
                    if (y + SIZE >= ground.height) {
                        //println("down333")
                        y = ground.height - SIZE
                        transfer(0, 0)
                    } else {
                        //println("down444")
                        var yOffset = (times * speed).toInt()
                        transfer(0, yOffset)
                    }
                }
            }
            //炮弹初始位置
            shellsX = cx - shells.w / 2
            shellsY = cy
        }

        //根据方向决定坦克的朝向
        //val transform = AffineTransform()
        // 重置状态，避免每次新建对象
        transform.setToIdentity()
        transform.translate((x + imgX + img.width / 2).toDouble(), (y + imgY + img.height / 2).toDouble())
        transform.rotate(angle)
        transform.translate((-img.width / 2).toDouble(), (-img.height / 2).toDouble())

        //shells born
        if (input.getKeyDown(KeyEvent.VK_CONTROL) == true) {
            println("control is pressed, fire in the hole. shellsList size:${shellsList.size}")
            if (shellsList.isEmpty()) {
                var sh = shells
                sh.id = System.currentTimeMillis()
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

        //shells die
        var iterator = shellsList.iterator()
        while (iterator.hasNext()) {
            var next = iterator.next()
            if (next.isDestroyed) {
                iterator.remove()
            }
        }
    }

    override fun drawTank(g: Graphics?) {
        var g2 = g as Graphics2D
//        g2.drawImage(img, transform, null)

        //车身
        g2?.drawRect(x, y, SIZE, SIZE)
        //炮台
        g2?.drawOval(x + ptOffset, y + ptOffset, halfSize, halfSize)

        //炮筒
        if (direction == Shells.DIRECTION_WEST) {
            g2?.fillRoundRect(cx - ptLength, cy - ptRadius / 2, ptLength, ptRadius, arc, arc)
        } else if (direction == Shells.DIRECTION_EAST) {
            g2?.fillRoundRect(cx, cy - ptRadius / 2, ptLength, ptRadius, arc, arc)
        } else if (direction == Shells.DIRECTION_NORTH) {
            g2?.fillRoundRect(cx - ptRadius / 2, cy - ptLength, ptRadius, ptLength, arc, arc)
        } else if (direction == Shells.DIRECTION_SOUTH) {
            g2?.fillRoundRect(cx - ptRadius / 2, cy, ptRadius, ptLength, arc, arc)
        }

    }
}