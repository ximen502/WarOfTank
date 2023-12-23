package game.enemy

import game.CP
import game.Ground
import game.Shells
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
                x = (SIZE - TANK_SIZE) / 2
                y = (SIZE - TANK_SIZE) / 2

                println("tire cx:$cx, cy:$cy")
            }
            CP.BORN_2 -> {
                x = ground.width / 2 - TANK_SIZE / 2
                y = (SIZE - TANK_SIZE) / 2
            }
            CP.BORN_3 -> {
                x = ground.width - SIZE + (SIZE - TANK_SIZE) / 2
                y = (SIZE - TANK_SIZE) / 2
            }
            else -> {}
        }
        w = TANK_W
        h = TANK_H
        shellsX = cx - shells.w / 2
        shellsY = cy - shells.h / 2
        this.ground = ground
        direction = Shells.DIRECTION_SOUTH
        times = 2

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

    override fun fire() {
        // 简化炮弹是否可以发射的判断逻辑
        if (shells.isDestroyed) {
            val sh = shells
            sh.id = (CP.ENEMY shl 8 or id.toInt()).toLong()
            sh.times = 3
            sh.observer = observer
            sh.ground = ground
            sh.setPosition(shellsX, shellsY)
            sh.direction = direction
            sh.isDestroyed = false
            shellsList.add(sh)
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

}