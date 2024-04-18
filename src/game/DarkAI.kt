package game

import game.enemy.BaseEnemyTank
import game.enemy.TireTank
import game.tank.Producing
import java.awt.event.KeyEvent
import kotlin.math.abs
import kotlin.math.ceil

/**
 * 负责敌军坦克的生成和移动、发射炮弹、判断当前关卡是否结束
 * （玩家被消灭、基地被摧毁）
 */
class DarkAI(private var gw: GameWindow) {
    private var diedList = mutableListOf<BaseEnemyTank>()
    // 被派遣到地图上活跃状态的坦克容器
    var list = mutableListOf<BaseEnemyTank>()
    var total = TOTAL
    // 已经创建的坦克数量
    var nums = 0

    // 用于生成坦克的计数变量
    private var fps2Tank = 0
    // 动画生存的帧数
    private var ttlPro = 0
    private var produce: Producing? = null

    // 游戏启动后经过的帧数，60f/s, 90f后开始创建坦克
    private var gameStarted = 0

    // 计时120fps
    private var countDown = WAITING
    private var baseId = ID.ID_ENEMY
    private var baseIdProduce = ID.ID_ENEMY_PRODUCE

    var preciousIndex = 0;

    companion object {
        const val WAITING = 120
        const val TOTAL = 20
        const val BEGIN_FPS = 90
        const val ENEMIES = 4
    }

    fun pushTank(ground: Ground, go: GOObserver) {
//        println("push")
        gameStarted++

        calcLiveEnemies()

        /*************************************************
         * 首先生成一个动画对象放到画面上，计时约600ms，动画消失。
         * 然后创建坦克，坦克出现在画面上。
         * 2s后重复上述动作。
         * 首次创建的时候，先等待游戏创建一定的帧数再开始
         *
         * 在4辆坦克存在的情况，判断是否有坦克被消灭，如果有，库存
         * 还有的话，继续创建新坦克。
         *
         * 此方法逻辑异常复杂，待优化。
         * ***********************************************/

        if (nums < ENEMIES && total > 0 && gameStarted >= BEGIN_FPS) {// 创建坦克生成动画
            // 计时结束，重新开始生成动画和坦克的流程
            if (countDown <= 0) {
                ttlPro = 0
                countDown = 120
            }
            if (ttlPro == 0) {// 创建坦克动画
                var pos = total % 3 + 1
                produce = Producing(ground, pos)
                produce?.id = baseIdProduce++
                produce?.observer = go
                go.born(produce)
            } else if (ttlPro >= Producing.GONE) { // 开始创建坦克
                if (fps2Tank % WAITING == 0) {
    //            var enemyTank = EnemyTank(ground)
                    //CP.BORN_1,2,3
                    var pos = total % 3 + 1
                    var enemyTank = TireTank(ground, pos)
    //            var enemyTank = TripleTank(ground, pos)
    //            var enemyTank = DoubleTank(ground, pos)
                    enemyTank.times = 1
                    enemyTank.id = baseId++
                    enemyTank.observer = go
                    //每四个一组，第三个为携带道具的坦克
                    preciousIndex++
                    if (preciousIndex == 3) {
                        enemyTank.precious = true
                    }
                    if (preciousIndex == 4) {
                        preciousIndex = 0
                    }
                    enemyTank.freeze = gw.freezeFps > 0
                    // 敌军坦克出现
                    go.born(enemyTank)
                    list.add(enemyTank)
                    total--
                    nums++
                }
                countDown--
                fps2Tank ++
            }
            ttlPro++
        } else {
            fps2Tank = 0
            dispatchMoreTank()
        }

        saveTank()
    }

    /**
     * 临时保存敌军坦克，计算剩余坦克数量
     */
    private fun calcLiveEnemies() {
        // 轮询add被消灭的敌军坦克
        var i = 0
        while (i < list.size) {
            if (list[i].isDestroyed) {
                if (!diedList.contains(list[i])) {
                    diedList.add(list[i])
                }
            }
            i++
        }
    }

    /**
     * 检查被挤到窗口外面的敌方坦克，如果有则将坦克重新就近安排到窗口内，
     * 为了解决目前敌方坦克的碰撞处理不够完善，造成的坦克跑到窗口外，而不能被发现的bug。
     */
    private fun saveTank() {
        var msg = ""
        var i = 0
        while (i < list.size) {
            val e = list[i]
            //左上角x方向出去的坦克
            if (e.x < 0) {
                e.x = (CP.SIZE - CP.FAST_S) / 2
                //msg= "enemy left out, id:${e.id}, x=${e.x}, y=${e.y}"
                //println(msg)
            }
            //顶部y方向出去的坦克
            if (e.y < 0) {
                e.y = (CP.SIZE - CP.FAST_B) / 2
                //msg= "enemy top out, id:${e.id}, x=${e.x}, y=${e.y}"
                //println(msg)
            }
            //右上角x方向出去的坦克
            if (e.x >= e.ground.width) {
                e.x = e.ground.width - CP.SIZE + (CP.SIZE - CP.FAST_S) / 2
                //msg= "enemy right out, id:${e.id}, x=${e.x}, y=${e.y}"
                //println(msg)
            }
            i++
        }
    }

    fun getLiveEnemies(): Int {
        return TOTAL - diedList.size
    }

    fun checkCollision() {
        /*****************************************************
        * 检测敌军坦克之间有没有发生相互碰撞，有的话，处理碰撞逻辑，
        * 让坦克拐弯去其他方向。没有的话，不需要处理。
         *
         * 方法：1矩形碰撞检测；2分离轴定理(暂时没有使用)
        * ****************************************************/
        val size: Int = list.size
        //双重循环实现敌军坦克之间的碰撞检测
        //这里的循环就好比足球比赛的循环赛，每2支队伍都进行一场比赛
        for (i in 0 until size - 1) {
            val bet = list[i]
            for (j in i + 1 until size) {
                val inter = list[j].pickRect().intersects(bet.pickRect())
                if (inter) {
                    //println("敌军坦克撞车了, bet:${bet.pickRect().toString()}, id:${bet.id},key:${bet.key},
                    // bet2:${list[j].pickRect().toString()}, id2:${list[j].id}, key2:${list[j].key}")
                    //有严重的arrayIndexOutOfBounds问题，必须尽快解决
                    // 如果是垂直方向相撞，双方各后撤到互相撞不到的位置，然后随机选择一个新方向继续行走，有bug坦克后撤位移略大
                    // 对角碰撞垂直和水平方向分别处理，效果很生硬，而且有bug，坦克会跑到瓦片上，或超出边缘
                    // 坦克相互碰撞的改进方案：
                    /***********************************************************************************
                     * 坦克后撤要判断能不能后撤，比如后面是墙或不可通行瓦片、坦克，就不可后撤；如果是可以后撤的情况，
                     * 后撤后就暂时不再向与前进方向垂直的方向拐弯。
                     * 如果要支持拐弯，需要遵守后撤后必须对齐到网格线上的前提，否则还会出现下标越界的bug。
                     * *********************************************************************************/
                    intersectsArea(bet, list[j])
                }
            }
        }
    }

    /**
     * 垂直方向互相后撤
     */
    private fun leaveVert(bet: BaseEnemyTank, bet2: BaseEnemyTank) {
        val ok = ceil((CP.FAST_B - abs(bet.y - bet2.y)) / 2.0).toInt()
        if (bet.y < bet2.y) {
            bet.y = bet.y - ok
            bet2.y = bet2.y + ok
        } else {
            bet2.y = bet2.y - ok
            bet.y = bet.y + ok
        }
    }

    /**
     * 水平方向互相后撤
     */
    private fun leaveHori(bet: BaseEnemyTank, bet2: BaseEnemyTank) {
        val ok = ceil((CP.FAST_B - abs(bet.x - bet2.x)) / 2.0).toInt()
        if (bet.x < bet2.x) {
            bet.x = bet.x - ok
            bet2.x = bet2.x + ok
        } else {
            bet2.x = bet2.x - ok
            bet.x = bet.x + ok
        }
    }

    /**
     * 第一个参数代表的坦克后撤
     */
    private fun leave1st(bet: BaseEnemyTank, bet2: BaseEnemyTank) {
        when (bet.key) {
            KeyEvent.VK_UP -> {
                val ok = CP.FAST_B - abs(bet.y - bet2.y);
                bet.y = bet.y + ok
            }
            KeyEvent.VK_DOWN -> {
                val ok = CP.FAST_B - abs(bet.y - bet2.y);
                bet.y = bet.y - ok
            }
            KeyEvent.VK_LEFT -> {
                val ok = CP.FAST_B - abs(bet.x - bet2.x)
                bet.x = bet.x + ok
            }
            KeyEvent.VK_RIGHT -> {
                val ok = CP.FAST_B - abs(bet.x - bet2.x)
                bet.x = bet.x - ok
            }
            else -> {}
        }
    }

    /**
     * 派出更多坦克
     */
    private fun dispatchMoreTank() {
        //是否有坦克被消灭，如果有，库存还有的话，继续创建新坦克
        var minus = false
        // 轮询移除被消灭的敌军坦克
        var i = 0
        while (i < list.size) {
            if (list[i].isDestroyed) {
                list.remove(list[i])
                nums--
                i--
                minus = true
            }
            i++
        }
        if (minus) {
            countDown = 0
        }
    }

    /*************************************************************************************
     * 升级版解决方案：
     * 当tank1,tank2水平方向相撞
     * (1)1，2互相撞击，1向右，2向左
     * (2)1撞击2，1向右，2向上下右
     * (3)2撞击1，1向上下左，2向左
     *
     * 当tank1,tank2垂直方向相撞
     * (1)1，2互相撞击，1向下，2向上
     * (2)1撞击2，1向下，2向左右下
     * (3)2撞击1，1向左右上，2向上
     *
     * 判断2个tank水平还是垂直分布，计算重叠区域的宽度和高度，如果宽小，那就是水平分布，如果高小，
     * 那就是垂直分布。
     *
     * 当后撤的时候，坦克应该在后撤到对齐网格线的情况下，随机选择新的方向，增加坦克移动的随机性。
     *
     ************************************************************************************/
    private fun intersectsArea(t1: BaseEnemyTank, t2: BaseEnemyTank) {
        var w = (t1.w + t2.w) / 2 - abs(t1.cx - t2.cx)
        var h = (t1.h + t2.h) / 2 - abs(t1.cy - t2.cy)
        if (w < h) {
            //horizontal
            //0)面对面相撞
            //1)1 hit 2
            //2)2 hit 1
            if (t1.key == KeyEvent.VK_RIGHT) {
                if (t2.key == KeyEvent.VK_LEFT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t1, t2)
                    t1.adjustDirection2(t1.key)
                }
            } else if (t1.key == KeyEvent.VK_LEFT) {
                if (t2.key == KeyEvent.VK_RIGHT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {// t1 hit t2
                    leave1st(t1, t2)
                    t1.adjustDirection2(t1.key)
                }
            } else if (t2.key == KeyEvent.VK_RIGHT) {
                if (t1.key == KeyEvent.VK_LEFT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t2, t1)
                    t2.adjustDirection2(t2.key)
                }
            } else if (t2.key == KeyEvent.VK_LEFT) {
                if (t1.key == KeyEvent.VK_RIGHT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {// t1 hit t2
                    leave1st(t2, t1)
                    t2.adjustDirection2(t1.key)
                }
            }
        } else if (h < w) {
            //vertical
            //面对面相撞
            if (t1.key == KeyEvent.VK_DOWN) {
                if (t2.key == KeyEvent.VK_UP) {
                    leaveVert(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t1, t2)
                    t1.adjustDirection2(t1.key)
                }
            } else if (t1.key == KeyEvent.VK_UP) {
                if (t2.key == KeyEvent.VK_DOWN) {
                    leaveVert(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t1, t2)
                    t1.adjustDirection2(t1.key)
                }
            } else if (t2.key == KeyEvent.VK_DOWN) {
                if (t1.key == KeyEvent.VK_UP) {
                    leaveVert(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t2, t1)
                    t2.adjustDirection2(t2.key)
                }
            } else if (t2.key == KeyEvent.VK_UP) {
                if (t1.key == KeyEvent.VK_DOWN) {
                    leaveVert(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t2, t1)
                    t2.adjustDirection2(t2.key)
                }
            }
        } else if (w == h) {//正方形
            //either is ok
            //for example choose horizontal
            //0)面对面相撞
            //1)1 hit 2
            //2)2 hit 1
            if (t1.key == KeyEvent.VK_RIGHT) {
                if (t2.key == KeyEvent.VK_LEFT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t1, t2)
                    t1.adjustDirection2(t1.key)
                }
            } else if (t1.key == KeyEvent.VK_LEFT) {
                if (t2.key == KeyEvent.VK_RIGHT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {// t1 hit t2
                    leave1st(t1, t2)
                    t1.adjustDirection2(t1.key)
                }
            } else if (t2.key == KeyEvent.VK_RIGHT) {
                if (t1.key == KeyEvent.VK_LEFT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {
                    leave1st(t2, t1)
                    t2.adjustDirection2(t2.key)
                }
            } else if (t2.key == KeyEvent.VK_LEFT) {
                if (t1.key == KeyEvent.VK_RIGHT) {
                    leaveHori(t1, t2)
                    t1.adjustDirection2(t1.key)
                    t2.adjustDirection2(t2.key)
                } else {// t1 hit t2
                    leave1st(t2, t1)
                    t2.adjustDirection2(t1.key)
                }
            }
        }
    }

    fun reset() {
        nums = 0
        total = TOTAL
        gameStarted = 0
        fps2Tank = 0
        ttlPro = 0
        countDown = 0
        baseId = ID.ID_ENEMY
        baseIdProduce = ID.ID_ENEMY_PRODUCE
        list.clear()
        diedList.clear()
    }
}