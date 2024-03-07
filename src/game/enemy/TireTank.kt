package game.enemy

import game.CP
import game.Ground
import game.Shells
import game.lib.Log
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.imageio.ImageIO

/**
 * @Class TireTank
 * @Description 轮胎式坦克
 * @Author xsc
 * @Date 2023-12-13 上午09:55
 */
class TireTank(ground: Ground, position: Int) : BaseEnemyTank() {


    init {
        this.position = position
        when (position) {
            CP.BORN_1 -> {
                x = (SIZE - FAST_S) / 2
                y = (SIZE - FAST_B) / 2

                Log.println("tire cx:$cx, cy:$cy")
            }
            CP.BORN_2 -> {
                x = ground.width / 2 - FAST_S / 2
                y = (SIZE - FAST_B) / 2
            }
            CP.BORN_3 -> {
                x = ground.width - SIZE + (SIZE - FAST_S) / 2
                y = (SIZE - FAST_B) / 2
            }
            else -> {}
        }
        w = FAST_S
        h = FAST_B
        shellsX = cx - shells.w / 2
        shellsY = cy - shells.h / 2
        this.ground = ground
        direction = Shells.DIRECTION_SOUTH
        times = 2

        imgN = ImageIO.read(javaClass.getResource("/game/image/fastn.png"))
        imgS = ImageIO.read(javaClass.getResource("/game/image/fasts.png"))
        imgW = ImageIO.read(javaClass.getResource("/game/image/fastw.png"))
        imgE = ImageIO.read(javaClass.getResource("/game/image/faste.png"))
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
        //g2.drawString("$id", x, cy)
        //drawFrame(g2)
    }

    /**
     * 画一个外框，查看坦克的宽高是否正确
     */
    private fun drawFrame(g2: Graphics2D) {
        var tmp: Color? = null
        tmp = g2.color
        g2.color = Color.RED
        g2.drawRect(x, y, w, h)
        g2.color = tmp
    }

    override fun born() {

    }

    override fun fire() {
        // 简化炮弹是否可以发射的判断逻辑
        if (shells.isDestroyed) {
            val sh = shells
            sh.id = id + 20//(CP.ENEMY shl 8 or id.toInt()).toLong()
            sh.times = 3
            sh.observer = observer
            sh.ground = ground
            sh.setPosition(shellsX, shellsY)
            sh.direction = direction
            sh.isDestroyed = false
            observer?.born(sh)
            //fireAC?.play()
        }
    }

    override fun draw(g: Graphics?) {
        drawTank(g)
    }

    override fun onTick() {
        walk()
        fire()
    }

    override fun walk() {
//        if (direction==Shells.DIRECTION_EAST || direction==Shells.DIRECTION_WEST && y >=(2.5 * 48) ) {
//            //super.walk()
//        } else {
//        }
        super.walk()
        //修改坦克的宽和高，坦克是长宽不等的矩形
        if (direction == Shells.DIRECTION_WEST || direction == Shells.DIRECTION_EAST) {
            this.w = FAST_B
            this.h = FAST_S
        } else if (direction == Shells.DIRECTION_NORTH || direction == Shells.DIRECTION_SOUTH) {
            this.w = FAST_S
            this.h = FAST_B
        }
    }

}