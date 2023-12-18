package game.enemy

import game.AbstractTank
import game.CP
import game.Ground
import game.Shells
import java.awt.Graphics
import java.util.*

/**
 * @Class BaseEnemyTank
 * @Description 敌军坦克基类
 * @Author xsc
 * @Date 2023/12/18 下午3:16
 * @Version 1.0
 */
open class BaseEnemyTank :AbstractTank() {

    //炮弹缓存
    val shells = Shells()

    protected var r = Random()

    private var directionList = ArrayList<Int>()

    val DIR_LIST = arrayListOf(
        Shells.DIRECTION_NORTH,
        Shells.DIRECTION_SOUTH,
        Shells.DIRECTION_WEST,
        Shells.DIRECTION_EAST
    )

    /**
     * 如果一个方向撞墙，直接在剩余的方向中随机选择一个方向，简单的进行处理
     */
    fun adjustDirection(direction: Int): Unit {
        directionList.clear()
        directionList.addAll(DIR_LIST)
        directionList.remove(direction)
        var index = r.nextInt(directionList.size)
        this.direction = directionList.get(index)
        /*return if (isOut()) {
            var str = logstr(direction)
            //println("^-^ is out, dir is $direction, $str x is $x, y is $y")
            var directionList = arrayListOf(
                Shells.DIRECTION_NORTH,
                Shells.DIRECTION_SOUTH,
                Shells.DIRECTION_WEST,
                Shells.DIRECTION_EAST
            )
            if (x <= 0) {
                directionList.remove(Shells.DIRECTION_WEST)
            }
            if (x + SIZE >= ground.width) {
                directionList.remove(Shells.DIRECTION_EAST)
            }
            if (y <= Ground.TITLE_H) {
                directionList.remove(Shells.DIRECTION_NORTH)
            }
            if (y + SIZE > ground.height) {
                directionList.remove(Shells.DIRECTION_SOUTH)
            }

            var index = r.nextInt(directionList.size)

            logstr(directionList[index])
            directionList[index]
        } else {
            //println("in the ground, dir is $direction, x is $x, y is $y")
            direction
        }*/
    }

    fun logstr(d: Int) {
        var str1 = ""
        when (d) {
            Shells.DIRECTION_NORTH -> {
                str1 = "north"
            }

            Shells.DIRECTION_SOUTH -> {
                str1 = "south"
            }

            Shells.DIRECTION_WEST -> {
                str1 = "west"
            }

            Shells.DIRECTION_EAST -> {
                str1 = "east"
            }

            else -> {
            }
        }
        //println("new dir is ${str1}")
    }
    override fun drawTank(g: Graphics?) {
        
    }

    override fun born() {
        
    }

    override fun walk() {
        
    }

    override fun fire() {
        
    }

    override fun draw(g: Graphics?) {
        
    }

    override fun onTick() {
        
    }
}