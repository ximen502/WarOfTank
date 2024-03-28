package game.prop

import game.GameObject
import java.awt.Graphics
import java.awt.Rectangle

open class BaseGameObject : GameObject() {
    private var rect: Rectangle = Rectangle(x, y, w, h)

    override fun draw(g: Graphics?) {}
    override fun onTick() {}

    /**
     * 替代get方法
     */
    open fun pickRect(): Rectangle {
        rect.x = x
        rect.y = y
        rect.width = w
        rect.height = h
        return rect
    }
}