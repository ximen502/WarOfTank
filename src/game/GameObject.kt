package game

import java.awt.Graphics

/**
 * 游戏基类
 * 在游戏窗口的对象中id是唯一的
 * @Author xsc
 */
abstract class GameObject {
    var id = 0L
    var x: Int = 0
    var y: Int = 0

    var w: Int = 0
    var h: Int = 0

    var cx = 0
        get() = x + w / 2
    var cy = 0
        get() = y + h / 2

    val cxf
        get() = x + w / 2.0f
    val cyf
        get() = y + h / 2.0f

    var maxX = 0
        get() = x + w
    var maxY = 0
        get() = y + h

    var isDestroyed = false

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

    override fun equals(other: Any?): Boolean {
        return if (other is GameObject) {
            other.id == id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + x
        result = 31 * result + y
        result += 78
        result += 73
        result += 63
        return result
    }

}