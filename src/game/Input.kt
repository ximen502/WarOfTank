package game

import game.lib.Log
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JOptionPane

class Input(private var gw: GameWindow) : KeyListener {
    private var keyMap: HashMap<Int, Boolean>
    private val KEY_COUNT = 300
    //private var keyTyped: Int = 0
    var moveListener: MoveListener? = null
    var debug = false

    init {
        keyMap = HashMap(KEY_COUNT)
        for (i in 0..KEY_COUNT) {
            keyMap[i] = false
        }
    }

    override fun keyTyped(e: KeyEvent?) { }

    override fun keyPressed(e: KeyEvent?) {
        e?.keyCode?.let { keyMap.put(it, true) }
        Log.println("key pressed:${e?.keyCode}")

        if (e?.keyCode == KeyEvent.VK_UP) {
            moveListener?.begin(KeyEvent.VK_UP)
        } else if (e?.keyCode == KeyEvent.VK_DOWN) {
            moveListener?.begin(KeyEvent.VK_DOWN)
        } else if (e?.keyCode == KeyEvent.VK_LEFT) {
            moveListener?.begin(KeyEvent.VK_LEFT)
        } else if (e?.keyCode == KeyEvent.VK_RIGHT) {
            moveListener?.begin(KeyEvent.VK_RIGHT)
        } else if (e?.keyCode == KeyEvent.VK_T) {
            debug = false//!debug
        }
    }

    override fun keyReleased(e: KeyEvent?) {
        e?.keyCode?.let { keyMap.put(it, false) }
        //this.e = System.currentTimeMillis()

        if (e?.keyCode == KeyEvent.VK_UP) {
            moveListener?.end(KeyEvent.VK_UP)
        } else if (e?.keyCode == KeyEvent.VK_DOWN) {
            moveListener?.end(KeyEvent.VK_DOWN)
        } else if (e?.keyCode == KeyEvent.VK_LEFT) {
            moveListener?.end(KeyEvent.VK_LEFT)
        } else if (e?.keyCode == KeyEvent.VK_RIGHT) {
            moveListener?.end(KeyEvent.VK_RIGHT)
        } else if (e?.keyCode == 27) { // Esc
            val option =
                JOptionPane.showConfirmDialog(gw, "确认要退出吗", "游戏提示", JOptionPane.YES_NO_OPTION)
            //println("op:$option")
            if (option == 0) { // yes
                //System.exit(1)
                gw.dispose()
                //AC.bgMusicAC?.stop()
                AC.midiPlayer?.stop()
            } else if (option == 1) { // no
                // do nothing
            }
        } else if (e?.keyCode == KeyEvent.VK_CONTROL) {
            gw.lightAI.player?.fireCounter = 0
        }
        Log.println("key release:${e?.keyCode}-----------------------")//${e?.keyCode}
    }

    fun getKeyDown(keyCode: Int): Boolean? {
        return keyMap[keyCode]
    }

//    fun getKeyTyped(keyCode: Int): Boolean {
//        println("kc:$keyCode")
//        println("kt:$keyTyped")
//        var valueOf = Integer.valueOf(keyCode.toString(), 16)
//        println("vo:$valueOf")
//        //j's unicode code
//        return keyTyped == 74 || keyTyped == 106
//    }

}