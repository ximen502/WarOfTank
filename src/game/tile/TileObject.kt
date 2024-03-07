package game.tile

import game.GameObject
import java.awt.Graphics

/**
 * @Class TileObject
 * @Description tile对象的基类
 * @Author xsc
 * @Date 2024/3/6 上午10:37
 * @Version 1.0
 */
open class TileObject : GameObject() {
    var col = 0
    var row = 0
    override fun draw(g: Graphics?) {

    }

    override fun onTick() {

    }
}