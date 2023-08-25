package game.map

import game.GameObject
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import javax.imageio.ImageIO

/**
 * @Class Grass
 * @Description 草坪方块，坦克可以穿过，草坪会覆盖在坦克上面，局部能看见草地下面的坦克
 * @Author xsc
 * @Date 2023/8/19 上午10:56
 * @Version 1.0
 */
class Grass : GameObject() {
    //1.实现草地绘制
    //2.确定全局地图的样貌
    //3.根据全局样貌进行地图初始化
    var grass: Image? = null

    init {
        val path = javaClass.getResource("../image/grass.png")
        println(path)
        grass = ImageIO.read(path)
    }

    override fun draw(g: Graphics?) {
        val g2 = g as Graphics2D
        g2.drawImage(grass, x, y, null)
    }

    override fun onTick() {

    }
}