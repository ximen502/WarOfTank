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
class Tank(input: Input) : GameObject() {
    private var image: Image = Toolkit.getDefaultToolkit().createImage("image/Snow.png")
    private var speed = 4 * 2
    private var input: Input
    private var key = 0
    var observer: GOObserver? = null
    var shellsList: ArrayList<Shells> = ArrayList()

    //炮弹初始位置坐标
    var shellsX = 0
    var shellsY = 0

    companion object {
        const val SIZE = 50
    }

    init {
        x = 100
        y = 100
        this.input = input
    }

    override fun draw(g: Graphics?) {
        super.draw(g)
        when {
            y > ground.height -> y = 0
            y < 0 -> y = ground.height
            x > ground.width -> x = 0
            x < 0 -> x = ground.width
        }
        g?.color = Color.YELLOW
        drawTank(g)
    }

    private fun drawTank(g: Graphics?) {
        var g2 = g as Graphics2D

        val offset = SIZE / 2
        //车身
        g2?.drawRect(x - offset, y - offset, SIZE, SIZE)
        //炮台
        var offsetOval = offset / 2
        var sizeOval = SIZE / 2
        g2?.drawOval(x - offsetOval, y - offsetOval, sizeOval, sizeOval)

        //炮筒(需要根据行走方向调整指向)
        var ptRadius = 6
        var ptLength = 36
        var arc = 2

        if (key == KeyEvent.VK_LEFT) {
            shellsX = x - ptLength
            shellsY = y - ptRadius / 2
            g2?.fillRoundRect(x - ptLength, y - ptRadius / 2, ptLength, ptRadius, arc, arc)
        } else if (key == KeyEvent.VK_RIGHT) {
            shellsX = x + ptLength
            shellsY = y - ptRadius / 2
            g2?.fillRoundRect(x, y - ptRadius / 2, ptLength, ptRadius, arc, arc)
        } else if (key == KeyEvent.VK_UP) {
            shellsX = x - ptRadius / 2
            shellsY = y - ptLength
            g2?.fillRoundRect(x - ptRadius / 2, y - ptLength, ptRadius, ptLength, arc, arc)
        } else if (key == KeyEvent.VK_DOWN) {
            shellsX = x - ptRadius / 2
            shellsY = y + ptLength
            g2?.fillRoundRect(x - ptRadius / 2, y, ptRadius, ptLength, arc, arc)
        }

    }

    override fun onTick() {
        super.onTick()
        if (input.getKeyDown(KeyEvent.VK_LEFT) == true) {
            transfer(-1 * speed, 0)
            key = KeyEvent.VK_LEFT
        }
        if (input.getKeyDown(KeyEvent.VK_RIGHT) == true) {
            transfer(1 * speed, 0)
            key = KeyEvent.VK_RIGHT
        }
        if (input.getKeyDown(KeyEvent.VK_UP) == true) {
            transfer(0, -1 * speed)
            key = KeyEvent.VK_UP
        }
        if (input.getKeyDown(KeyEvent.VK_DOWN) == true) {
            transfer(0, 1 * speed)
            key = KeyEvent.VK_DOWN
        }

        if (input.getKeyDown(KeyEvent.VK_CONTROL) == true) {
            println("control is press")
            if (shellsList.isEmpty()) {
                var sh = Shells()
                sh.id = System.currentTimeMillis()
                sh.setPosition(shellsX, shellsY)
                sh.direction = when (key) {
                    KeyEvent.VK_UP -> Shells.DIRECTION_NORTH
                    KeyEvent.VK_DOWN -> Shells.DIRECTION_SOUTH
                    KeyEvent.VK_LEFT -> Shells.DIRECTION_WEST
                    KeyEvent.VK_RIGHT -> Shells.DIRECTION_EAST
                    else -> 0
                }
                shellsList.add(sh)
                observer?.born(sh)
            }
        }

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

//    private fun fire() {
//        //取消上一次的防抖任务
//        executor?.shutdownNow()
//
//        executor = Executors.newSingleThreadScheduledExecutor()
//        executor?.schedule(Runnable {
//            println("fire in the hole")
//        }, DELAY, TimeUnit.MILLISECONDS)
//    }
}