package game

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
        println("[Render] created")
        println("[Render] interval $interval ms")
    }

    override fun run() {
        println("start rendering")
        bgMusic()
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

    /**
     * 音乐如果放在主线程，初始化时间会变长，导致窗口白屏时间太久，
     * 在异步线程初始化时间也很长，这也是个问题。
     */
    private fun bgMusic() {
        Thread() {
            run(){
                val resource = this@RenderThread.javaClass.getResource("sound/midifile0.mid")
                val audioClip = Applet.newAudioClip(resource)
                //        audioClip.play();//从头播放
                audioClip.loop() //循环播放
                //        audioClip.stop();//停止播放
            }
        }.start()
    }
}