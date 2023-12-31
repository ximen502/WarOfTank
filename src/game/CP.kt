package game

object CP {
    const val R = 15 * 2// + 2
    const val C = 19 * 2// + 2

    const val SIZE = 50

    const val SIZE_M = 25

    const val TANK_SIZE = 40
    const val TANK_W = 40
    const val TANK_H = 44

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
    const val BORN_4 = 4
    const val BORN_5 = 5

    const val PLAYER = 0x60

    const val ENEMY = 0x50

    const val BEGIN_FPS = 60

    val mapArray = Map.map

    var tileArray = Map.tile
}