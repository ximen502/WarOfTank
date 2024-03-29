package game

/**
 * 地图显示和坦克活动的范围
 */
data class Ground(val width: Int, val height: Int) {
    var l = 0
        get() = 0
    var r = 0
        get() = width
    var t = 0
        get() = TITLE_H
    var b = 0
        get() = height

    var rW = 0

    var rH = 0

    companion object {
        // 窗口title的高度
        const val TITLE_H = 0//28
    }
}