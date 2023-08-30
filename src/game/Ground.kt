package game

data class Ground(val width: Int, val height: Int) {
    var l = 0
    var r = 0
    var t = 0
    var b = 0

    init {
        l = 0
        r = width
        t = TITLE_H
        b = height
    }

    companion object {
        // 窗口title的高度
        const val TITLE_H = 0//28
    }
}