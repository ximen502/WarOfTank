package game

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JFrame
import javax.swing.JOptionPane

class Input : KeyListener {
    private var keyMap: HashMap<Int, Boolean>
    private val KEY_COUNT = 300
    //private var keyTyped: Int = 0
    var frame: JFrame? = null
    init {
        keyMap = HashMap(KEY_COUNT)
        for (i in 0..KEY_COUNT) {
            keyMap[i] = false
        }
    }

    override fun keyTyped(e: KeyEvent?) {
//        println(e?.keyCode)
//        println(e?.keyChar)
//        keyTyped = e?.keyChar?.code!!
    }

    override fun keyPressed(e: KeyEvent?) {
        e?.keyCode?.let { keyMap.put(it, true) }
//        println("pre")
    }

    override fun keyReleased(e: KeyEvent?) {
        e?.keyCode?.let { keyMap.put(it, false) }
//        println("rel")
        if (e?.keyCode == 27) { // Esc
            var option =
                JOptionPane.showConfirmDialog(frame, "确认要退出吗", "游戏提示", JOptionPane.YES_NO_OPTION)
            println("op:$option")
            if (option == 0) { // yes
                System.exit(1)
            } else if (option == 1) { // no
                // do nothing
            }
        }
        println("-----------------------")//${e?.keyCode}
    }

    fun getKeyDown(keyCode: Int): Boolean? {
        return keyMap?.get(keyCode)
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