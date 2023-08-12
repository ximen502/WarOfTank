package game

import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Toolkit
import java.awt.event.KeyEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 坦克，根据给定的坐标绘制一个坦克的俯视图
 * 矩形车身+圆形炮台+矩形炮筒
 */
class Tank(input: Input) : AbstractTank() {
    private var image: Image = Toolkit.getDefaultToolkit().createImage("image/Snow.png")

    private var input: Input

    var observer: GOObserver? = null

    companion object {
        const val SIZE = 50
    }

    init {
        println("w:${ground.width}")
        x = 100
        y = 100
        this.input = input
    }

    override fun draw(g: Graphics?) {
        super.draw(g)
        g?.color = Color.YELLOW
        drawTank(g)
    }

    override fun onTick() {
        super.onTick()
        if (input.getKeyDown(KeyEvent.VK_LEFT) == true) {
            direction = Shells.DIRECTION_WEST
            if (x <= 0) {
                x = 0
                transfer(0, 0)
            } else {
                var xOffset = 0
                //当前坐标与边界的距离不足一步(取决于速度)的距离
                xOffset = if (x < times * speed) {
                    -x
                } else {
                    -times * speed
                }
                transfer(xOffset, 0)
            }
        } else if (input.getKeyDown(KeyEvent.VK_RIGHT) == true) {
            direction = Shells.DIRECTION_EAST
//            println("x:$x")
            if (x >= ground.width) {
                x = ground.width
                transfer(0, 0)
            } else {
                var xOffset = 0
                //注意车身的尺寸不能忽略
                xOffset = if ((ground.width - SIZE - x) < (times * speed)) {
                    ground.width - SIZE - x
                } else {
                    times * speed
                }
                transfer(xOffset, 0)
            }
//            println("x=:$x")
        } else if (input.getKeyDown(KeyEvent.VK_UP) == true) {
            direction = Shells.DIRECTION_NORTH
            if (y <= Ground.TITLE_H) {
                y = Ground.TITLE_H
                transfer(0, 0)
            } else {
                var yOffset = 0
                yOffset = if (y - Ground.TITLE_H < times * speed) {
                    y - Ground.TITLE_H
                } else {
                    -times * speed
                }
                transfer(0, yOffset)
            }
        } else if (input.getKeyDown(KeyEvent.VK_DOWN) == true) {
            direction = Shells.DIRECTION_SOUTH
            if (y >= ground.height) {
                y = ground.height
                transfer(0, 0)
            } else {
                var yOffset = 0
                yOffset = if (ground.height - SIZE - y < times * speed) {
                    ground.height - SIZE - y
                } else {
                    times * speed
                }
                transfer(0, yOffset)
            }
        }

//        when (direction) {
//            Shells.DIRECTION_WEST -> {
//            }
//
//            Shells.DIRECTION_EAST -> {
//
//            }
//
//            Shells.DIRECTION_NORTH -> {
//
//            }
//
//            Shells.DIRECTION_SOUTH -> {
//
//            }
//
//            else -> {}
//        }

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