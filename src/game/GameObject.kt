package game

import java.awt.Graphics

abstract class GameObject {
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

    abstract fun draw(g: Graphics?)

    abstract fun onTick()

    open fun isOut(): Boolean {
        if (x < 0 || x > ground.width){
            return true
        }

        if (y < 0 || y > ground.height){
            return true
        }

        return false
    }

}