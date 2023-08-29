package game

import game.map.Brick
import game.map.Grass
import game.map.Iron
import game.map.River
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

    private var player: Tank

    //    var list = mutableListOf<GameObject>()
    //为解决ConcurrentModificationException，使用了如下的线程安全的容器类
    var list = CopyOnWriteArrayList<GameObject>()
    // 瓦片地图容器
    var tileList = mutableListOf<GameObject>()

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

        player = Tank(input, ground)
        player.w = 50
        player.h = 50
        //player.ground = ground
        player.observer = this
        list.add(player)

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
        // test map
        val mapArray = CP.mapArray
        val tileArray = CP.tileArray

        for (i in mapArray.indices) {
            for (j in mapArray[i].indices) {
                val tile = mapArray[i][j].toInt()
                if (tile == CP.TILE_BRICK) {
                    var brick = Brick()
                    brick.x = 50 * j
                    brick.y = 50 * i
                    brick.w = 50
                    brick.h = 50
                    brick.ground = ground
                    list.add(brick)
                    tileList.add(brick)
                    tileArray[i][j] = brick
                } else if (tile == CP.TILE_IRON) {
                    var iron = Iron()
                    iron.x = 50 * j
                    iron.y = 50 * i
                    iron.w = 50
                    iron.h = 50
                    iron.ground = ground
                    list.add(iron)
                    tileList.add(iron)
                    tileArray[i][j] = iron
                } else if (tile == CP.TILE_RIVER) {
                    var river = River()
                    river.x = 50 * j
                    river.y = 50 * i
                    river.w = 50
                    river.h = 50
                    river.ground = ground
                    list.add(river)
                    tileList.add(river)
                    tileArray[i][j] = river
                } else if (tile == CP.TILE_GRASS) {
                    var grass = Grass()
                    grass.x = 50 * j
                    grass.y = 50 * i
                    grass.w = 50
                    grass.h = 50
                    grass.ground = ground
                    list.add(grass)
                    tileList.add(grass)
                    tileArray[i][j] = grass
                }
            }
        }

        // good map
//        val mapArray = Array(CP.R) { ByteArray(CP.C) }
//        for (i in mapArray.indices) {
//            if (i in 6..8) {
//                break
//                //continue
//            }
//            if (i == 14){
//                continue
//            }
//            for (j in mapArray[i].indices) {
//                if (i == 0) {
//                    mapArray[i][j] = 0
//                } else {
//                    if (j % 2 == 0) {
//                        mapArray[i][j] = 1
//
//                        var brick = Brick()
//                        brick.x = 50 * j
//                        brick.y = 50 * i
//                        brick.ground = ground
//                        list.add(brick)
//                    }
//                }
//            }
//
//        }
//        for (i in mapArray.indices) {
//            println(Arrays.toString(mapArray[i]))
//        }
    }

    private fun detectCollision() {
//        tileList.forEachIndexed { index, tile ->
//            val tcx = tile.x + tile.w / 2
//            val pcx = player.x + player.w / 2
//            val tpwhalf = (tile.w + player.w) / 2
//
//            val tcy = tile.y + tile.h / 2
//            val pcy = player.y + player.h / 2
//            val tphhalf = (tile.h + player.h) / 2

//            if (Math.abs(tcx - pcx) <= tpwhalf && Math.abs(tcy - pcy) <= tphhalf) {
//                println("--碰撞--${index}")
//                when (player.direction) {
//                    Shells.DIRECTION_NORTH -> {
//                        println("上面撞墙了，边界坐标y:${tile.y + tile.h}")
//                    }
//                    Shells.DIRECTION_SOUTH -> {
//                        println("下面撞墙了，边界坐标y:${tile.y}")
//                    }
//                    Shells.DIRECTION_WEST -> {
//                        println("左面撞墙了，边界坐标x:${tile.x + tile.w}")
//                    }
//                    Shells.DIRECTION_EAST -> {
//                        println("右面撞墙了，边界坐标x:${tile.x}")
//                    }
//                    else -> {}
//                }
//            }
//        }
//        for (tile in tileList) {
//            val tcx = tile.x + tile.w / 2
//            val pcx = player.x + player.w / 2
//            val tpwhalf = (tile.w + player.w) / 2
//
//            val tcy = tile.y + tile.h / 2
//            val pcy = player.y + player.h / 2
//            val tphhalf = (tile.h + player.h) / 2
//
//            if (Math.abs(tcx - pcx) <= tpwhalf && Math.abs(tcy - pcy) <= tphhalf) {
//                println("--碰撞${tile}")
//            }
//        }
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

        //////////////////方便调试的网格线
        var color = tempGraphics?.color
        tempGraphics?.color = Color.GRAY
        for (i in 0 until CP.C) {
            tempGraphics?.drawLine(50 * i, 0, 50 * i, h)
            for (j in 0 until CP.R) {
                tempGraphics?.drawLine(0, 50 * j, w, 50 * j)
            }
        }
        tempGraphics?.color = color
        ///////////////////end方便调试的网格线
        g?.drawImage(tempImage, 0, 0, null)

        darkAI?.pushTank(ground, this)

        //detectCollision()
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