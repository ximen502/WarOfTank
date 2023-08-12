package game

import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.util.concurrent.CopyOnWriteArrayList
import javax.swing.JFrame

class GameWindow(width: Int, height: Int, windowTitle: String) : JFrame(), GOObserver {
    private var w: Int = 0
    private var h: Int = 0
    private var t: String = ""

    private var fps: Int = 0

    private var tempGraphics: Graphics? = null
    private var tempImage: Image? = null

    private var renderThread: RenderThread? = null

    private var darkAI: DarkAI? = null

    private var ground: Ground//? = null

    //    var list = mutableListOf<GameObject>()
    //为解决ConcurrentModificationException，使用了如下的线程安全的容器类
    var list = CopyOnWriteArrayList<GameObject>()

    private var input: Input

    init {
        this.w = width
        this.h = height
        this.t = windowTitle
        createWindow()

        ground = Ground(w, h)

        input = Input()
        addKeyListener(input)

        var sp = Tank(input)
        sp.ground = ground
        sp.observer = this
        list.add(sp)

        darkAI = DarkAI()

        tempImage = this.createImage(w, h)
        tempGraphics = tempImage?.graphics

        setFps(60)
        renderThread = RenderThread(this)
        renderThread?.start()

        println("Title Height: ${insets.top}")

    }

    private fun createWindow() {
        setSize(w, h)
        title = t
        isVisible = true
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun setFps(fps: Int): Boolean {
        return if (fps <= 0) {
            false
        } else {
            this.fps = fps
            true
        }
    }

    fun getFps() = fps

    fun clear(g: Graphics?) {
        g?.color = Color.BLACK
        g?.fillRect(0, 0, w, h)
    }

    fun exit() {
        println("Good bye")
    }

    override fun paint(g: Graphics?) {

        clear(tempGraphics)

        for (gameObject in list) {
            gameObject.draw(tempGraphics)
            gameObject.onTick()
        }

        g?.drawImage(tempImage, 0, 0, null)

        darkAI?.pushTank(ground, this)
    }

    override fun born(go: GameObject?) {
        println("born")
        list.add(go!!)
    }

    override fun die(go: GameObject?) {
        println("die")
        for (gameObject in list) {
            if (gameObject.id == go?.id) {
                list.remove(gameObject)
                println("founded====")
                break
            }
        }
    }
}