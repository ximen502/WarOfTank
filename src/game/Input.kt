package game

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class Input : KeyListener {
    private var keyMap: HashMap<Int, Boolean>
    private val KEY_COUNT = 300
    //private var keyTyped: Int = 0

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
        println("-----------------------")
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