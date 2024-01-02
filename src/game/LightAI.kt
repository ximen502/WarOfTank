package game

import game.tank.Producing

/**
 * @Class LightAI
 * @Description 负责派遣玩家坦克到游戏地图上，与DarkAI相对
 * @Author xsc
 * @Date 2023/12/31 下午2:25
 * @Version 1.0
 */
class LightAI {
    /******************************************************
     * 玩家的生命数如果大于0，就继续创建玩家坦克，否则就不再创建
     * ****************************************************/
    // 初始生命数量
    var life = INIT_LIFE
    // 游戏启动后经过的帧数，60f/s, 90f后开始创建坦克
    private var gameStarted = 0
    // 动画生存的帧数
    private var ttlPro = 0
    // 活动的坦克数量
    private var active = 0


    companion object {
        const val INIT_LIFE = 5
        const val ACTIVE_PLAYER = 1
    }

    fun dispatchPlayer(ground: Ground, go: GOObserver, playerInit: Tank, input: Input) {
        gameStarted++

        //准备创建玩家坦克
        if (life > 0 && active < ACTIVE_PLAYER && gameStarted >= CP.BEGIN_FPS) {
            if (ttlPro == 0) {
                //创建玩家坦克生成动画
                val pos = CP.BORN_4
                val produce = Producing(ground, pos)
                /* 这里的动画id，111，112为玩家准备 */
                produce.id = 111
                produce.observer = go
                go.born(produce)
            } else if (ttlPro >= Producing.GONE) {
                //创建玩家坦克
                if (playerInit.isDestroyed) {
                    val w = ground.width
                    val h = ground.height
                    val player = Tank(input, ground)
                    player.id = 101
                    player.w = CP.TANK_SIZE
                    player.h = CP.TANK_SIZE
                    player.x = w / 2 - CP.SIZE_M * 5 + (CP.SIZE - CP.TANK_SIZE) / 2
                    player.y = h - CP.SIZE_M * 2 + (CP.SIZE - CP.TANK_SIZE) / 2
                    player.row = player.x / CP.SIZE_M
                    player.col = player.y / CP.SIZE_M
                    player.observer = go
                    input.moveListener = player
                    (go as GameWindow).player = player
                    go.born(player)
                } else {
                    go.born(playerInit)
                }
                life--
                active++
            }
            ttlPro++
        } else {
            //玩家被消灭
            if (playerInit.isDestroyed) {
                ttlPro = 0
                active = 0
            }
        }
    }

    fun getActive(): Int = active

}