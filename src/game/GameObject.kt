package game

import java.awt.Graphics

open class GameObject {
    var id = 0L
    var x: Int = 0
    var y: Int = 0
    open var ground: Ground = Ground(0, 0)

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun transfer(xOffset: Int, yOffset: Int) {
        this.x += xOffset
        this.y += yOffset
    }

    open fun draw(g: Graphics?) {}

    open fun onTick() {}
}