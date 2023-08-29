package game

import java.lang.Thread.sleep

class RenderThread(gameWindow: GameWindow) : Runnable {
    private var thread: Thread? = null
    private var exited = false
    private var interval = 0L
    var _gameWindow: GameWindow? = null

    init {
        _gameWindow = gameWindow
        interval = 1000L / _gameWindow!!.getFps()
        println("[Render] created")
        println("[Render] interval $interval ms")
    }

    override fun run() {
        println("start rendering")
        while (!exited){
            _gameWindow?.repaint()
            sleep(interval)
        }
        println("stop rendering")
        _gameWindow?.exit()
    }

    fun start() {
        if (thread == null) {
            thread = Thread(this, "[RenderThread]")
            thread?.start()
        }

//        var timer = Timer(16) {
//            println("timer start rendering")
//            while (!exited) {
//                _gameWindow?.repaint()
////                sleep(interval)
//            }
//            println("timer stop rendering")
//            _gameWindow?.exit()
//        };
//        timer.start()
    }
}