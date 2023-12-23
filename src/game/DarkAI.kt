package game

import game.enemy.TireTank
import game.tank.Producing

/**
 * 负责敌军坦克的生成和移动、发射炮弹、判断当前关卡是否结束
 * （玩家被消灭、基地被摧毁）
 */
class DarkAI {
    var list = mutableListOf<AbstractTank>()
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
}