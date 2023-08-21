package game

import game.map.Brick
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.util.*
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
        input.frame = this@GameWindow
        addKeyListener(input)

        initMap()

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
        // 延迟500ms再启动渲染线程避免一些对象没有初始化完成导致的Exception, 如NPE...
        // 估计还是有问题，list绘制对象容器，添加内容的时机应该也要晚一些才行。
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                renderThread?.start()
//            }
//        }, 500L)

        println("Title Height: ${insets.top}")

    }

    /**
     * 使用byte二维数组实现地图布局
     */
    private fun initMap() {
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1]
//        [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1]
//        [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1]
//        [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1]
//        [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1]
//        [1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
//        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        val mapArray = Array(15) { ByteArray(19) }
        for (i in mapArray.indices) {
            if (i in 6..8) {
                continue
            }
            if (i == 14){
                continue
            }
            for (j in mapArray[i].indices) {
                if (i == 0) {
                    mapArray[i][j] = 0
                } else {
                    if (j % 2 == 0) {
                        mapArray[i][j] = 1

                        var brick = Brick()
                        brick.x = 50 * j
                        brick.y = 50 * i
                        brick.ground = ground
                        list.add(brick)
                    }
                }
            }

        }
        for (i in mapArray.indices) {
            println(Arrays.toString(mapArray[i]))
        }
    }

    private fun createWindow() {
        setSize(w, h)
        title = t
        isUndecorated = true
        isVisible = true
        setLocationRelativeTo(null)
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