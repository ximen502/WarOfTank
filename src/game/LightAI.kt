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
    // 创建好的玩家坦克
    var player: Tank? = null


    companion object {
        const val INIT_LIFE = 5
        const val ACTIVE_PLAYER = 1
    }

    fun dispatchPlayer(ground: Ground, go: GOObserver, input: Input) {
        gameStarted++

        //准备创建玩家坦克
        if (life > 0 && active < ACTIVE_PLAYER && gameStarted >= CP.BEGIN_FPS) {
            if (ttlPro == 0) {
                //创建玩家坦克生成动画
                val pos = CP.BORN_P1
                val produce = Producing(ground, pos)
                /* 这里的动画id，111，112为玩家准备 */
                produce.id = ID.ID_P1_PRODUCE
                produce.observer = go
                go.born(produce)
            } else if (ttlPro >= Producing.GONE) {
                var construct = false
                //创建玩家坦克
                if (player == null) {
                    construct = true
                } else {
                    if (player?.isDestroyed == true) {
                        construct = true
                    }
                }
                if (construct) {
                    val w = ground.width
                    val h = ground.height
                    val tank = Tank(input, ground)
                    tank.id = ID.ID_P1
                    tank.w = CP.TANK_SIZE
                    tank.h = CP.TANK_SIZE
                    tank.x = w / 2 - CP.SIZE_M * 5 + (CP.SIZE - CP.TANK_SIZE) / 2
                    tank.y = h - CP.SIZE_M * 2 + (CP.SIZE - CP.TANK_SIZE) / 2
                    tank.row = tank.x / CP.SIZE_M
                    tank.col = tank.y / CP.SIZE_M
                    tank.observer = go
                    tank.invincible = true
                    tank.invincibleCounter = 60 * 5//5 seconds, 60fps
                    input.moveListener = tank
                    //(go as GameWindow).player = tank
                    player = tank
                    go.born(tank)
                }
                life--
                active++
            }
            ttlPro++
        } else {
            //玩家被消灭
            if (player?.isDestroyed == true) {
                ttlPro = 0
                active = 0
            }
        }
    }

    fun getActive(): Int = active

    fun reset() {
        life += active
        active = 0
        gameStarted = 0
        ttlPro = 0
        player?.reset()
    }

}