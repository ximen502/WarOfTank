package game.prop

import game.GameObject
import java.awt.Graphics

/**
 * @Class PropObject
 * @Description 道具类的父类，拥有道具的公共属性和行为
 * @Author xsc
 * @Date 2024/4/14 下午2:43
 * @Version 1.0
 */
open class PropObject : GameObject() {
    private var showCounter = 0
    var disappear = false

    init {
        showCounter = 30 * 60
    }

    override fun onTick() {
        if (showCounter > 0) {
            showCounter--
            if (showCounter <= 0) {
                disappear = true
            }
        }
    }

    override fun draw(g: Graphics?) {

    }

    fun getShowCounter() : Int {
        return showCounter
    }
}