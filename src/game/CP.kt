package game

object CP {
    const val R = 15 * 2// + 2
    const val C = 19 * 2// + 2

    const val SIZE = 48

    const val SIZE_M = 24

    const val TANK_SIZE = 40
    const val TANK_W = 40
    const val TANK_H = 44
    const val FAST_B = 45// big
    const val FAST_S = 40// small

    const val TILE_BRICK = 1
    const val TILE_IRON = 2
    const val TILE_RIVER = 3
    const val TILE_GRASS = 4
    const val TILE_SNOW = 5
    const val TILE_EAGLE = 6
    const val TILE_P1 = 50
    const val TILE_P2 = 51

    const val BORN_1 = 1
    const val BORN_2 = 2
    const val BORN_3 = 3
    const val BORN_P1 = 4
    const val BORN_P2 = 5

    const val GAME_DATA = 200

    const val PLAYER = 0x60

    const val ENEMY = 0x50

    const val BEGIN_FPS = 60

    const val WAIT_FPS = 150

    var mapArray = Map.map

    var tileArray = Map.tile
}