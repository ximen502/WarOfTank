package game

import com.brackeen.sound.SoundManager
import game.lib.findStr
import game.map.*
import java.applet.AudioClip
import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.*
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

    var showLine = true //

    var hitAC: AudioClip? = null

    //    var list = mutableListOf<GameObject>()
    //为解决ConcurrentModificationException，使用了如下的线程安全的容器类
    var list = CopyOnWriteArrayList<GameObject>()

    // 瓦片地图容器
    var tileList = mutableListOf<GameObject>()

    // 敌军坦克已经被摧毁，发射的炮弹还在前进
    private var loneShell: Array<Shells?>? = arrayOfNulls(2)

    private var input: Input
    private val SIZE = CP.SIZE
    private val SIZE_M = CP.SIZE_M

    private var river = 0
    private var gameOver: GameOver
    private val bg: Image
    private var gameData: GameData? = null
    private var stack: Deque<String>? = null
    private var wait2Next = CP.WAIT_FPS //下一关的等待时间
    private var nowStage = 0 // 关卡编号

    init {
        this.w = width
        this.h = height
        this.t = windowTitle
        createWindow()

        listenClose()
        bg = ImageIO.read(javaClass.getResource("/game/image/bg_game.png"))

        ground = Ground(w - CP.SIZE, h)
        ground.rW = w
        ground.rH = h

        initStage()

        input = Input()
        input.frame = this@GameWindow
        addKeyListener(input)

        AC.soundManager = SoundManager(AC.PLAYBACK_FORMAT, 3)
        AC.bang = AC.soundManager?.getSound("/game/sound/Bang.wav")
        AC.soundManagerPD = SoundManager(AC.PLAYBACK_FORMAT_PD, 2)
        AC.playerdie = AC.soundManagerPD?.getSound("/game/sound/playerdie.wav")
        AC.soundManagerGF = SoundManager(AC.PLAYBACK_FORMAT_GF, 3)
        AC.gunfire = AC.soundManagerGF?.getSound("/game/sound/Gunfire.wav")

        initMap("lv01.map")

        darkAI = DarkAI()
        lightAI = LightAI()

        gameOver = GameOver(ground)

        val tips = Tips()
        tips.x = w / 2
        tips.y = h * 2 / 3
        tips.observer = this
        list.add(tips)

        initGameData()

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

    private fun initStage() {
        // 创建一个双端队列（Deque）实现栈
        stack = LinkedList()
        stack?.let {
            // 入栈
            it.push("lv18.map")
            it.push("lv05.map")
            it.push("lv02.map")
            //it.push("lv01.map")
        }
    }

    /**
     * 开始下一关
     */
    private fun nextStage() {
        wait2Next--
        //等待2s再开始下一关
        if (wait2Next > 0) {
            return
        }
        // river重置
        river = 0
        wait2Next = CP.WAIT_FPS
        //1.准备好关卡数据
        stack?.let {
            while (it.isNotEmpty()) {
                val stage = it.pop()
                //1.1清空地图
                clearMap()
                //1.2重置AI
                darkAI?.reset()
                lightAI?.reset()
                //2.地图重新load
                initMap(stage)
                break//选择下一关后，跳出循环
            }
        }
        //2.1初始化游戏数据
        initGameData()
        //3.从头播放bg music
        AC.midiPlayer?.play(AC.midiPlayer?.getSequence("/game/sound/midifile0.mid"), true)
        //4.玩家和敌军坦克
        //相关状态、数据重置或清除
        //(敌军坦克数量、关卡编号)
    }

    private fun clearMap() {
        val mapArray = CP.mapArray
        val tileArray = CP.tileArray
        for (i in mapArray.indices) {
            for (j in mapArray[i].indices) {
                mapArray[i][j] = 0
                tileArray[i][j] = null
            }
        }
        list.clear()
        tileList.clear()
    }

    private fun initGameData() {
        gameData = GameData(ground)
        gameData?.id = CP.GAME_DATA.toLong()
        list.add(gameData)
    }

    /**
     * 使用byte二维数组实现地图布局
     */
    private fun initMap(filename: String) {
        var stageStr = filename.findStr(filename, "[0-9]{2}")
        nowStage = try {
            Integer.parseInt(stageStr)
        } catch (e: NumberFormatException) {
            0
        }
        // 从地图文件读取地图
        val map = Map()
        val mapArray = map.readBMapFromFile(filename)
        CP.mapArray = mapArray
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
                    val rowCol = i shl 8 or j
                    if (four.contains(rowCol)) {
                        continue
                    }

                    val river = River()
                    river.id = (i shl 8 or j).toLong()
                    river.x = SIZE_M * j
                    river.y = SIZE_M * i
                    river.w = SIZE_M
                    river.h = SIZE_M
                    river.ground = ground
                    list.add(river)
                    tileList.add(river)
                    tileArray[i][j] = river

                    four2One(four, rowCol, mapArray, i, j, CP.TILE_RIVER)
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
     * 判断是否游戏结束，或者过关
     * 如果玩家全被消灭或基地被摧毁，游戏结束，停止背景音乐播放
     * 如果敌军全部被消灭，玩家和基地也都健在，那就开始下一关
     * 如果过关，则开始下一个关卡
     * 敌军坦克全部被消灭，则过关
     */
    private fun isGameOverOrCompleteLevel() {
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
                    //input.moveListener = null
                    lightAI?.player?.gameOver = true
                    renderThread?.stopBgMusic()
                }
            } else {
                // 判断是否过关
                darkAI?.let {
                    //println("enemies:${it.nums + it.total}")
                    if (it.nums + it.total == 0) {//敌军全部被消灭
                        renderThread?.stopBgMusic()
                        nextStage()
                    }
                }
            }
        }
    }

    private fun statistics() {
        darkAI?.let {
            val num = it.getLiveEnemies()
            gameData?.enemies = num.toString()
        }
        lightAI?.let {
            gameData?.p1 = (it.life + it.getActive()).toString()
        }
        gameData?.stage = nowStage.toString()
    }

    private fun detectCollision() {
        /***************************************************************************************
         * (1)玩家坦克炮弹拿到敌军坦克发射的炮弹的引用，然后进行碰撞检测，如果发生碰撞则相互抵消
         * (2)玩家坦克炮弹拿到敌军坦克的引用，然后进行碰撞检测，如果发生碰撞则敌军坦克发生爆炸被摧毁，炮弹消失
         * (3)敌军坦克炮弹拿到玩家坦克的引用，然后进行碰撞检测，如果发生碰撞则玩家坦克发生爆炸被摧毁，炮弹消失
         * *************************************************************************************/
        val ps = lightAI?.player?.shells
        if (ps?.isDestroyed == false) {
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
                //println("enemy:$enemy, enemy shells destroy:${enemy.shells.isDestroyed}")
                //保存敌军被消灭前最后一发炮弹
                if (enemy.isDestroyed) {
                    loneShell?.set(0, enemy.shells)
                }
                val player = lightAI?.player ?: break
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

            // 临时解决玩家被敌军最后一发炮弹击中没有产生碰撞的bug，后续2 players要改为循环遍历数组的判断方式
            if (loneShell?.get(0) != null) {
                val shell = loneShell?.get(0)!!
//                println("****************************************************************")
//                println("shell id:"+Integer.toHexString(shell?.id!!.toInt())+" isDestroy:${shell?.isDestroyed}")
//                println("****************************************************************")
                val player = lightAI?.player!!
                if (!shell.isDestroyed && !player.isDestroyed) {
                    val ps = player.shells
                    //敌军最后一发炮弹击中了玩家坦克的炮弹
                    if (ps.pickRect().intersects(shell.pickRect())) {
                        die(ps)
                        die(shell)
                        loneShell?.set(0, null)
                    }
                    //敌军最后一发炮弹击中了玩家坦克
                    if (player.pickRect().intersects(shell.pickRect())) {
                        AC.soundManagerPD?.play(AC.playerdie)
                        die(player)
                        die(shell)
                        boom(player)
                        loneShell?.set(0, null)
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

        tempGraphics?.drawImage(bg, 0, 0, null)
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

        lightAI?.dispatchPlayer(ground, this, input)

        detectCollision()

        isGameOverOrCompleteLevel()

        statistics()
    }

    override fun born(go: GameObject?) {
        println("born ${go?.javaClass.toString()}")
        list.add(go!!)
    }

    override fun die(go: GameObject?) {
        println("die ${go?.javaClass.toString()}")
//        var count = 0
//        for (gameObject in list) {
//            if (gameObject.id == 20L) {
//                println("${gameObject?.id},------ ${gameObject?.isDestroyed}")
//            }
//        }
//        if (count > 1) {
//            println("*********************************************")
//            println("${go?.id}founded more than once")
//            println("*********************************************")
//        }
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
                //break
            }
        }
    }

    private fun listenClose() {
        val windowListener: WindowListener = object : WindowListener {
            override fun windowOpened(e: WindowEvent) {}
            override fun windowClosing(e: WindowEvent) {
                println("窗口关闭中")
            }
            override fun windowClosed(e: WindowEvent) {
                println("窗口已关闭")
                //1.1清空地图
                clearMap()
                //1.2重置AI
                darkAI?.reset()
                lightAI?.reset()
            }
            override fun windowIconified(e: WindowEvent) {}
            override fun windowDeiconified(e: WindowEvent) {}
            override fun windowActivated(e: WindowEvent) {}
            override fun windowDeactivated(e: WindowEvent) {}
        }

        addWindowListener(windowListener)

    }
}