package game

/**
 * 管理游戏对象的id，所有游戏对象的id，在一个时间点必须是全局唯一
 * Date:2024-03-06 21:43
 */
object ID {
    var id = 0L
    fun generateID(): Long {
        if (id >= Long.MAX_VALUE) {
            id = 0L
        } else {
            id++
        }
        return id
    }

    // 30 * 38 = 1140
    // id分配记录
    // tile id从1~1150
    const val ID_TILE = 1L
    // 敌军坦克id从1151~1200
    const val ID_ENEMY = 1151L
    // 敌军坦克produce id从1201~1220
    const val ID_ENEMY_PRODUCE = 1201L
    // player坦克produce id从1221~1222
    const val ID_P1_PRODUCE = 1221L
    const val ID_P2_PRODUCE = 1222L
    // player坦克id从1231~1232
    const val ID_P1 = 1231L
    const val ID_P2 = 1232L
    const val ID_P1_SHELL = 1233L
    const val ID_P2_SHELL = 1234L

    // Boom id 1301~1310
    private const val ID_BOOM_BEGIN = 1301L
    private const val ID_BOOM_END = 1320L
    private var baseIdBoom = ID_BOOM_BEGIN
    fun generateBoomID(): Long {
        if (baseIdBoom >= ID_BOOM_END) {
            baseIdBoom = ID_BOOM_BEGIN
        } else {
            baseIdBoom++
        }
        return baseIdBoom
    }

    // Hit id 1321~1340
    private const val ID_HIT_BEGIN = 1321L
    private const val ID_HIT_END = 1340L
    private var baseIdHit = ID_HIT_BEGIN
    fun generateHitID(): Long {
        if (baseIdHit >= ID_HIT_END) {
            baseIdHit = ID_HIT_BEGIN
        } else {
            baseIdHit++
        }
        return baseIdHit
    }

    // GameData id 1341
    const val ID_GAME_DATA = 1341L
    const val ID_GAME_OVER = 1342L
}