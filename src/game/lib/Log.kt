package game.lib

/**
 * 打印日志管理类
 * Date 2024-03-07 09:52
 */
object Log {
    private var debug = false
    fun println(message: Any?) {
        if (debug)
            System.out.println(message)
    }
}