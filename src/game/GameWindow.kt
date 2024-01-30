package game

import game.lib.SoundManager
import game.map.*
import java.applet.AudioClip
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.util.concurrent.CopyOnWriteArrayList
import javax.imageio.ImageIO
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
    private var lightAI: LightAI? = null

    private var ground: Ground//? = null

    var player: Tank//? = null

    var showLine = true //

    var hitAC: AudioClip? = null

    //    var list = mutableListOf<GameObject>()
    //为解决ConcurrentModificationException，使用了如下的线程安全的容器类
    var list = CopyOnWriteArrayList<GameObject>()

    // 瓦片地图容器
    var tileList = mutableListOf<GameObject>()

    private var input: Input
    private val SIZE = CP.SIZE
    private val SIZE_M = CP.SIZE_M

    private var river = 0
    private var gameOver: GameOver
    var bg = ImageIO.read(javaClass.getResource("/game/image/bg_game.png"))

    init {
        this.w = width
        this.h = height
        this.t = windowTitle
        createWindow()

        ground = Ground(w, h)

        input = Input()
        input.frame = this@GameWindow
        addKeyListener(input)

        AC.soundManager = SoundManager(AC.PLAYBACK_FORMAT, 3)
        AC.bang = AC.soundManager?.getSound("/game/sound/Bang.wav")
        AC.soundManagerPD = SoundManager(AC.PLAYBACK_FORMAT_PD, 2)
        AC.playerdie = AC.soundManagerPD?.getSound("/game/sound/playerdie.wav")

        initMap()

        player = Tank(input, ground)
        player.id = 101
        player.w = CP.TANK_SIZE
        player.h = CP.TANK_SIZE
        player.x = w / 2 - SIZE_M * 5 + (SIZE - CP.TANK_SIZE) / 2
        player.y = h - SIZE_M * 2 + (SIZE - CP.TANK_SIZE) / 2
        player.row = player.x / SIZE_M
        player.col = player.y / SIZE_M
        println("row:${player.row}, col:${player.col}")
        player.observer = this
        //list.add(player)

        input.moveListener = player

        darkAI = DarkAI()
        lightAI = LightAI()

        gameOver = GameOver(ground)

        val tips = Tips()
        tips.x = w / 2
        tips.y = h * 2 / 3
        tips.observer = this
        list.add(tips)

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
        // 尝试从地图文件读取地图
        // test map
        val mapArray = CP.mapArray
//        val mapArray = Map().readBMapFromFile()
        val tileArray = CP.tileArray

        // 4个方格代表的一个瓦片（河流、草地、基地...），每个元素存储二维数组的横纵坐标
        val four: MutableList<Int> = ArrayList()
        for (i in four.indices) {
            four[i] = -1
        }

        //i行j列
        for (i in mapArray.indices) {
            for (j in mapArray[i].indices) {
                val tile = mapArray[i][j].toInt()
                println("tile:$tile")
                if (tile == CP.TILE_BRICK) {
                    var brick = Brick()
                    brick.id = (i shl 8 or j).toLong()
                    brick.x = SIZE_M * j
                    brick.y = SIZE_M * i
                    brick.w = SIZE_M
                    brick.h = SIZE_M
                    brick.ground = ground
                    brick.observer = this
                    list.add(brick)
                    tileList.add(brick)
                    tileArray[i][j] = brick
                } else if (tile == CP.TILE_IRON) {
                    var iron = Iron()
                    iron.id = (i shl 8 or j).toLong()
                    iron.x = SIZE_M * j
                    iron.y = SIZE_M * i
                    iron.w = SIZE_M
                    iron.h = SIZE_M
                    iron.ground = ground
                    iron.observer = this
                    list.add(iron)
                    tileList.add(iron)
                    tileArray[i][j] = iron
                } else if (tile == CP.TILE_RIVER) {
                    var river = River()
                    river.id = (i shl 8 or j).toLong()
                    river.x = SIZE * j
                    river.y = SIZE * i
                    river.w = SIZE
                    river.h = SIZE
                    river.ground = ground
                    list.add(river)
                    tileList.add(river)
                    tileArray[i][j] = river
                } else if (tile == CP.TILE_GRASS) {
                    var rowCol = i shl 8 or j
                    //如果包含就不再处理，防止每个小格绘制4个瓦片
                    if (four.contains(rowCol)) {
                        continue
                    }

                    var grass = Grass()
                    grass.id = (i shl 8 or j).toLong()
                    grass.x = SIZE_M * j
                    grass.y = SIZE_M * i
                    grass.w = SIZE
                    grass.h = SIZE
                    grass.ground = ground
                    list.add(grass)
                    tileList.add(grass)
                    tileArray[i][j] = grass

                    four2One(four, rowCol, mapArray, i, j, CP.TILE_GRASS)
                } else if (tile == CP.TILE_EAGLE) {
                    var rowCol = i shl 8 or j
                    //如果包含就不再处理，防止每个小格绘制4个瓦片
                    if (four.contains(rowCol)) {
                        continue
                    }

                    var eagle = Eagle()
                    eagle.id = (i shl 8 or j).toLong()
                    eagle.x = SIZE_M * j
                    eagle.y = SIZE_M * i
                    eagle.w = SIZE
                    eagle.h = SIZE
                    eagle.ground = ground
                    list.add(eagle)
                    tileList.add(eagle)
                    tileArray[i][j] = eagle

                    four2One(four, rowCol, mapArray, i, j, CP.TILE_EAGLE)
                } /*else if (tile == CP.TILE_P1) {
                    var rowCol = i shl 8 or j
                    //如果包含就不再处理，防止每个小格绘制4个瓦片
                    if (four.contains(rowCol)) {
                        continue
                    }

                    player = Tank(input, ground)
                    player?.let {
                        it.row = i
                        it.col = j
                        it.x = j * CP.SIZE_M
                        it.y = i * CP.SIZE_M
                        it.w = CP.TANK_SIZE
                        it.h = CP.TANK_SIZE
                        //it.ground = ground
                        it.observer = this
                    }
                    list.add(player)

                    input.moveListener = player

                    four2One(four, rowCol, mapArray, i, j, CP.TILE_P1)
                }*/
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
//                        brick.x = SIZE * j
//                        brick.y = SIZE * i
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

    /**
     * 4个方格代表一个完整瓦片或游戏物品的情况
     * 这个每个小方格25*25，共同组成一个50*50的瓦片
     */
    private fun four2One(
        four: MutableList<Int>,
        rowCol: Int,
        mapArray: Array<ByteArray>,
        i: Int,
        j: Int,
        tile:Int
    ) {
        four.add(0, rowCol)
        //判断其他3个小格是不是同样是这个瓦片数值(小心数组下标越界)
        if (mapArray[i][j + 1].toInt() == tile) {
            var rc = i shl 8 or (j + 1)
            four.add(1, rc)
        }
        if (mapArray[i + 1][j].toInt() == tile) {
            var rc = (i + 1) shl 8 or j
            four.add(2, rc)
        }
        if (mapArray[i + 1][j + 1].toInt() == tile) {
            var rc = (i + 1) shl 8 or (j + 1)
            four.add(3, rc)
        }
    }

    private fun boom(go: GameObject) {
        val boom = Boom(go.cx, go.cy)
        boom.observer = this@GameWindow
        born(boom)
    }

    /**
     * 判断是否游戏结束，
     * 如果玩家全被消灭或基地被摧毁，游戏结束，停止背景音乐播放
     * 如果敌军全部被消灭，玩家和基地也都健在，那就开始下一关
     */
    private fun isGameOver() {
        river++
        // 当流逝的时间大于1s再进行判断，等待游戏准备好
        if (river >= CP.BEGIN_FPS) {
            val e = CP.tileArray[28][18]
            var over = false
            //基地被摧毁
            if (e.isDestroyed) {
                //println("GAME OVER")
                over = true
            }

            //玩家生命值为0
            lightAI?.let {
                if (it.life == 0 && it.getActive() == 0) {
                    //println("...GAME OVER")
                    over = true
                }
            }

            if (over) {
                if (!gameOver.showing) {
                    born(gameOver)
                    gameOver.showing = true
                    input.moveListener = null
                    renderThread?.stopBgMusic()
                }
            }
        }
    }

    private fun detectCollision() {
        /***************************************************************************************
         * (1)玩家坦克炮弹拿到敌军坦克发射的炮弹的引用，然后进行碰撞检测，如果发生碰撞则相互抵消
         * (2)玩家坦克炮弹拿到敌军坦克的引用，然后进行碰撞检测，如果发生碰撞则敌军坦克发生爆炸被摧毁，炮弹消失
         * (3)敌军坦克炮弹拿到玩家坦克的引用，然后进行碰撞检测，如果发生碰撞则玩家坦克发生爆炸被摧毁，炮弹消失
         * *************************************************************************************/
        val ps = player.shells
        if (!ps.isDestroyed) {
            darkAI?.let {
                for (enemy in it.list) {
                    if (!enemy.shells.isDestroyed) {
                        //玩家的炮弹击中了敌军坦克发射的炮弹
                        if (ps.pickRect().intersects(enemy.shells.pickRect())) {
                            die(ps)
                            die(enemy.shells)
                        }
                    }
                    if (!enemy.isDestroyed) {
                        //玩家的炮弹击中了敌军坦克
                        if (enemy.pickRect().intersects(ps.pickRect())) {
                            AC.soundManager?.play(AC.bang)
                            die(enemy)
                            die(ps)
                            boom(enemy)
                        }
                    }
                }
            }
        }


        darkAI?.let {
            for (enemy in it.list) {
                if (!enemy.shells.isDestroyed && !player.isDestroyed) {
                    //敌军的炮弹击中了玩家坦克
                    if (player.pickRect().intersects(enemy.shells.pickRect())) {
                        AC.soundManagerPD?.play(AC.playerdie)
                        die(player)
                        die(enemy.shells)
                        boom(player)
                    }
                }
            }
        }
    }

    private fun createWindow() {
        setSize(w, h)
        title = t
        isUndecorated = true
//        contentPane = JLabel(ImageIcon(ImageIO.read(javaClass.getResource("image/bg_game.png"))))
        isVisible = true
        setLocationRelativeTo(null)
        defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
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

        //tempGraphics?.drawImage(bg, 0, 0, null)
        for (gameObject in list) {
//            if (gameObject.isDestroyed) {
//                continue
//            }
            gameObject.draw(tempGraphics)
            gameObject.onTick()
        }

        //////////////////方便调试的网格线
        if (showLine) {
            var color = tempGraphics?.color
            tempGraphics?.color = Color.GRAY
            for (i in 0 until CP.R * 2) {
                tempGraphics?.color = Color.GRAY
                tempGraphics?.drawLine(0, CP.SIZE_M * i, w, CP.SIZE_M * i)
            }
            for (j in 0 until CP.C * 2) {
                tempGraphics?.color = Color.GRAY
                tempGraphics?.drawLine(CP.SIZE_M * j, 0, CP.SIZE_M * j, h)
            }

            //行列指示器
//        tempGraphics?.color = Color.ORANGE
//        for (i in 0 until CP.R) {
//            for (j in 0 until CP.C) {
//                if (j == 0) {
//                    tempGraphics?.drawString("$i", 0, SIZE * i + 35)
//                }
//                if (i == 0) {
//                    tempGraphics?.drawString("$j", SIZE * j,  50)
//                }
//            }
//        }
            tempGraphics?.color = color
        }
        ///////////////////end方便调试的网格线
        g?.drawImage(tempImage, 0, 0, null)

        darkAI?.pushTank(ground, this)
        darkAI?.checkCollision()

        lightAI?.dispatchPlayer(ground, this, player, input)

        detectCollision()

        isGameOver()
    }

    override fun born(go: GameObject?) {
        println("born ${go?.javaClass.toString()}")
        list.add(go!!)
    }

    override fun die(go: GameObject?) {
        println("die ${go?.javaClass.toString()}")
        for (gameObject in list) {
            if (gameObject.id == go?.id) {
                go.isDestroyed = true
                list.remove(gameObject)
                println("founded====")
                // 将砖块和铁块从地图上去掉
                if (go is Brick || go is Iron) {
                    var i = (go.id shr 8).toInt()
                    var j = (go.id and 0x00000000000000ff).toInt()
                    CP.mapArray[i][j] = 0
                    CP.tileArray[i][j] = null
                }

                //darkAI?.removeDeadTank(go)
                break
            }
        }
    }
}