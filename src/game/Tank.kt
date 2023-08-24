package game

import java.applet.Applet
import java.applet.AudioClip
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.Toolkit
import java.awt.event.KeyEvent

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

    init {
        this.ground = ground
        println("w:${ground.width}")
        x = ground.width / 2 - (SIZE * 2.5f).toInt()
        y = ground.height - SIZE
        this.input = input
        times = 2
//        println(javaClass.toString())
//        var resource = javaClass.getResource("")
//        println(resource)
//        var resource2 = javaClass.getResource("./../Gunfire.wav")
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
                if (mapArray[yGrid][xNext].toInt() == 1) {
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
                if (yCur == 1 || yNext == 1) {
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
        } else if (input.getKeyDown(KeyEvent.VK_RIGHT) == true) {
            direction = Shells.DIRECTION_EAST
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
                if (mapArray[yGrid][xNext].toInt() == 1) {
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
                if (yCur == 1 || yNext == 1) {
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
        } else if (input.getKeyDown(KeyEvent.VK_UP) == true) {
            direction = Shells.DIRECTION_NORTH
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
                if (mapArray[yNext][xGrid].toInt() == 1) {
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
                if (mapArray[yNext][xGrid].toInt() == 1 || mapArray[yNext][xGrid + 1].toInt() == 1) {
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
        } else if (input.getKeyDown(KeyEvent.VK_DOWN) == true) {
            direction = Shells.DIRECTION_SOUTH
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
                if (mapArray[yNext][xGrid].toInt() == 1) {
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
                if (mapArray[yNext][xGrid].toInt() == 1 || mapArray[yNext][xGrid + 1].toInt() == 1) {
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
        }

        //shells born
        if (input.getKeyDown(KeyEvent.VK_CONTROL) == true) {
            //println("control is press")
            if (shellsList.isEmpty()) {
                var sh = Shells()
                sh.id = System.currentTimeMillis()
                sh.setPosition(shellsX, shellsY)
                sh.direction = direction
                shellsList.add(sh)
                observer?.born(sh)
                fireAC?.play()
            }
        }

        //shells die
        var iterator = shellsList.iterator()
        while (iterator.hasNext()) {
            var next = iterator.next()
            if (next.x > ground.width || next.x < 0) {
                observer?.die(next)
                println("out xx")
                iterator.remove()
            }

            if (next.y > ground.height || next.y < 0) {
                observer?.die(next)
                println("out yy")
                iterator.remove()
            }
        }
    }
}