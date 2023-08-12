package game

/**
 * 负责敌军坦克的生成和移动、发射炮弹、判断当前关卡是否结束
 * （玩家被消灭、基地被摧毁）
 */
class DarkAI {
    var list = mutableListOf<EnemyTank>()
    fun pushTank(ground: Ground, go: GOObserver) {
//        println("push")
        if (list.isEmpty()) {
            var enemyTank = EnemyTank(ground)
            enemyTank.observer = go
            // 敌军坦克出现
            go.born(enemyTank)
            list.add(enemyTank)
        }
    }
}