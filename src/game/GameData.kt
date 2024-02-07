package game

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


/**
 * @Class GameData
 * @Description 统计游戏中玩家生命、分数、关卡、敌军坦克等数据，并渲染到页面上
 * @Author xsc
 * @Date 2024/2/3 下午4:17
 * @Version 1.0
 */
class GameData(ground: Ground) : GameObject() {
    // 敌军坦克剩余数量
    // 玩家生命、分数
    // 关卡数据

    private var img: BufferedImage
    var enemies = ""
    var p1 = ""
    var stage = ""
    private var font = Font("simsun", Font.PLAIN, 18)

    init {
        this.ground = ground
        img = ImageIO.read(javaClass.getResource("/game/image/bg_data.png"))
        w = img.width
        h = img.height
        x = ground.r
        y = ground.t
    }

    override fun draw(g: Graphics?) {
        val g2d = g as Graphics2D
        g2d.drawImage(img, x, y, null)
        var temp = g2d.color
        g2d.color = Color.YELLOW
        var fontTemp = g2d.font
        g2d.font = font
        g2d.drawString(enemies, ground.r + 13, 110)
        g2d.drawString(p1, ground.r + 20, 325)
        g2d.drawString(stage, ground.r + 15, 590)
        g2d.color = temp
        g2d.font = fontTemp
    }

    override fun onTick() {

    }
}