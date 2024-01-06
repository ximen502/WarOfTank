package game

import java.awt.Color
import java.awt.Font
import java.awt.Graphics

/**
 * 游戏相关说明
 * @Author xsc
 */
class Tips : GameObject() {
    private var ttl = 0
    private var alpha = 0x0
    private var textColor: Color
    private var font: Font = Font("Arial", Font.PLAIN, 26)
    private var text: String
    var observer: GOObserver? = null

    init {
        textColor = Color(0xff, 0xff, 0xff, alpha)
        text = "按方向键移动，CTRL键发射炮弹，Esc键退出游戏"
    }

    override fun draw(g: Graphics?) {
        g!!.font = font
        val fontMetrics = g.fontMetrics
        val stringWidth = fontMetrics.stringWidth(text)
        textColor = Color(0xff, 0xff, 0xff, alpha)
        g.color = textColor
        g.drawString(text, x - stringWidth / 2, y)
    }

    override fun onTick() {
        ttl++
        if (ttl <= 20) {
            alpha += 13
            if (alpha > 255) {
                alpha = 255
            }
        } else if (ttl in 151..180) { // 渐渐淡出
            alpha -= 9
            if (alpha <= 0) { // 防止异常发生
                alpha = 0
            }
        } else if (ttl > 180) { // 彻底消失
            observer?.die(this@Tips)
        }
    }
}