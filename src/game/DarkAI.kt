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
class DarkAI {
    var list = mutableListOf<BaseEnemyTank>()
    var total = TOTAL
    // 已经创建的坦克数量
    var nums = 0

    // 用于生成坦克的计数变量
    var fps2Tank = 0
    // 动画生存的帧数
    var ttlPro = 0
    var produce: Producing? = null

    // 游戏启动后经过的帧数，60f/s, 90f后开始创建坦克
    var gameStarted = 0

    // 计时120fps
    var countDown = WAITING

    companion object {
        const val WAITING = 120
        const val TOTAL = 20
        const val BEGIN_FPS = 90
        const val ENEMIES = 4
    }

    fun pushTank(ground: Ground, go: GOObserver) {
//        println("push")
        gameStarted++

        /*************************************************
         * 首先生成一个动画对象放到画面上，计时约600ms，动画消失。
         * 然后创建坦克，坦克出现在画面上。
         * 2s后重复上述动作。
         * 首次创建的时候，先等待游戏创建一定的帧数再开始
         * ***********************************************/

        if (nums < ENEMIES && total > 0 && gameStarted >= BEGIN_FPS) {// 创建坦克生成动画
            // 计时结束，重新开始生成动画和坦克的流程
            if (countDown <= 0) {
                ttlPro = 0
                countDown = 120
            }
            if (ttlPro == 0) {
                var pos = total % 3 + 1
                produce = Producing(ground, pos)
                produce?.id = 110
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
                    enemyTank.id = total.toLong()
                    enemyTank.observer = go
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
        }
    }

    fun checkCollision() {
        /*****************************************************
        * 检测敌军坦克之间有没有发生相互碰撞，有的话，处理碰撞逻辑，
        * 让坦克拐弯去其他方向。没有的话，不需要处理。
         *
         * 方法：1矩形碰撞检测；2分离轴定理(暂时没有使用)
        * ****************************************************/
        var size: Int = list.size
        //双重循环实现敌军坦克之间的碰撞检测
        //这里的循环就好比足球比赛的循环赛，每2支队伍都进行一场比赛
        for (i in 0 until size - 1) {
            val bet = list[i]
            for (j in i + 1 until size) {
                var inter = list[j].pickRect().intersects(bet.pickRect())
                if (inter) {
                    //println("敌军坦克撞车了, bet:${bet.pickRect().toString()}, j:${list[j].pickRect().toString()}")
                    // 如果是垂直方向相撞，双方各后撤到互相撞不到的位置，然后随即选择一个新方向继续行走，有bug坦克后撤位移略大
                    // 对角碰撞垂直和水平方向分别处理，效果很生硬，而且有bug，坦克会跑到瓦片上，或超出边缘
                    if (bet.x == list[j].x) {// 垂直方向碰撞
                        val ok = ceil((CP.TANK_H - abs(bet.y - list[j].y)) / 2.0).toInt()
                        if (bet.y < list[j].y) {
                            bet.y = bet.y - ok
                            list[j].y = list[j].y + ok
                        } else {
                            list[j].y = list[j].y - ok
                            bet.y = bet.y + ok
                        }
                    } else if (bet.y == list[j].y) {// 水平方向碰撞
                        val ok = ceil((CP.TANK_W - abs(bet.x - list[j].x)) / 2.0).toInt()
                        if (bet.x < list[j].x) {
                            bet.x = bet.x - ok
                            list[j].x = list[j].x + ok
                        } else {
                            list[j].x = list[j].x - ok
                            bet.x = bet.x + ok
                        }
                    } else { // 对角方向碰撞
                        when (bet.key) {
                            //敌军坦克撞车了,
                            // bet:java.awt.Rectangle[x=805,y=280,width=40,height=44],
                            //   j:java.awt.Rectangle[x=830,y=305,width=40,height=44]
                            KeyEvent.VK_UP -> {
                                val ok = ceil((CP.TANK_H - abs(bet.y - list[j].y)) / 2.0).toInt()
                                list[j].y = list[j].y - ok
                                bet.y = bet.y + ok
                            }
                            KeyEvent.VK_DOWN -> {
                                val ok = ceil((CP.TANK_H - abs(bet.y - list[j].y)) / 2.0).toInt()
                                bet.y = bet.y - ok
                                list[j].y = list[j].y + ok
                            }
                            KeyEvent.VK_LEFT -> {
                                val ok = ceil((CP.TANK_W - abs(bet.x - list[j].x)) / 2.0).toInt()
                                list[j].x = list[j].x - ok
                                bet.x = bet.x + ok
                            }
                            KeyEvent.VK_RIGHT -> {
                                val ok = ceil((CP.TANK_W - abs(bet.x - list[j].x)) / 2.0).toInt()
                                list[j].x = list[j].x + ok
                                bet.x = bet.x - ok
                            }
                            else -> {}
                        }
                    }
                    bet.adjustDirection(bet.key)
                    list[j].adjustDirection(list[j].key)
                }
            }
        }
    }

    /**
     * 移除已经被摧毁的坦克
     */
//    fun removeDeadTank(go: GameObject) {
//        list.removeIf {
//            it.id == go.id
//        }
//    }
}