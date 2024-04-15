package game

import game.lib.Log
import game.lib.MidiPlayer
import java.applet.Applet
import java.lang.Thread.sleep

class RenderThread(gameWindow: GameWindow) : Runnable {
    private var thread: Thread? = null
    private var exited = false
    private var interval = 0L
    var _gameWindow: GameWindow? = null

    init {
        _gameWindow = gameWindow
        interval = 1000L / _gameWindow!!.getFps()
        Log.println("[Render] created")
        Log.println("[Render] interval $interval ms")
    }

    override fun run() {
        Log.println("start rendering")
        bgMusic()
        while (!exited){
            _gameWindow?.repaint()
            sleep(interval)
        }
        Log.println("stop rendering")
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

    /**
     * 音乐如果放在主线程，初始化时间会变长，导致窗口白屏时间太久，
     * 在异步线程初始化时间也很长，这也是个问题。
     *
     * 这个问题，通过MidiPlayer类已经解决了。
     */
    private fun bgMusic() {
        val player = MidiPlayer()
        AC.midiPlayer = player
        player.play(player.getSequence("/game/sound/midifile0.mid"), true)
    }

    fun stopBgMusic() {
        //AC.bgMusicAC?.stop()
        AC.midiPlayer?.stop()
    }

}