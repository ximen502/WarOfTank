package game.prop

/**
 * @Class PropObject
 * @Description 道具类的父类，拥有道具的公共属性和行为
 * @Author xsc
 * @Date 2024/4/14 下午2:43
 * @Version 1.0
 */
open class PropObject : BaseGameObject() {
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

    fun getShowCounter() : Int {
        return showCounter
    }
}