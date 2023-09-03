package game

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import javax.imageio.ImageIO

/**
 * @Class Hit
 * @Description 炮弹命中目标的爆炸效果
 * @Author xsc
 * @Date 2023/9/3 下午3:46
 * @Version 1.0
 */
class Hit : GameObject() {
    //1.实现砖块绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var hit: Image? = null

    //显示固定的帧数后消失
    val MAX = 10

    var ttl = 0

    var observer: GOObserver? = null

    init {
        val path = javaClass.getResource("image/hit.png")
        println(path)
        hit = ImageIO.read(path)
        id = System.currentTimeMillis()
    }

    override fun draw(g: Graphics?) {
        var g2 = g as Graphics2D
        if (ttl <= MAX) {
            g2.drawImage(hit, x, y, null)
        } else {
            observer?.die(this)
        }
    }

    override fun onTick() {
        ttl++
    }

    fun reset() {
        ttl = 0
    }
}