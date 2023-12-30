package game

import java.awt.Color
import java.awt.Graphics
import java.util.*

/**
 * @Class Boom
 * @Description 炮弹击中坦克的粒子爆炸效果，后续可能会改为逐帧动画
 * @Author 某PT
 * @Date 2023-09-08 15:07
 * @Version 1.0
 */
class Boom(x: Int, y: Int) : GameObject() {
    var observer: GOObserver? = null
    private val particles: MutableList<Particle> = ArrayList()
    var colorArray = arrayOf<Color>(
        Color(0xff, 0xa5, 0x00), Color(0xff, 0xbf, 0x0), Color.BLACK, Color.GRAY
    )

    init {
        this.x = x
        this.y = y
        this.id = System.currentTimeMillis()
        generateParticles()
        //println("Boom init{}")
    }

    override fun onTick() {
        updateParticles()
        //println("粒子：${particles.size}")
    }

    override fun draw(g: Graphics?) {
        for (particle in particles) {
            particle.draw(g!!)
        }
    }

    private fun generateParticles() {
        val random = Random()
        for (i in 0 until NUM_PARTICLES) {
            val angle = random.nextInt(360)
            val speed = random.nextDouble() * 5 + 2
//            val color = Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))
            val color = colorArray[random.nextInt(colorArray.size)]
            particles.add(Particle(x, y, angle, speed, color));
        }
    }

    private fun updateParticles() {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            if (particle.update()) {
                iterator.remove()
            }
        }
        if (particles.size == 0) {
            observer?.die(this)
        }
    }

    internal class Particle(private var x: Int, private var y: Int, angle: Int, speed: Double, color: Color) {
        private val speed: Double
        private val angle: Double
        private val color: Color
        private var size = 5 * 2
        fun update(): Boolean {
            x += (speed * Math.cos(angle)).toInt()
            y += (speed * Math.sin(angle)).toInt()
            size--
            //System.out.printf("x:%d, y:%d, size:%d\n", x, y, size)
            return size <= 0
        }

        fun draw(g: Graphics) {
            g.color = color
            g.fillOval(x, y, size, size)
        }

        init {
            this.angle = Math.toRadians(angle.toDouble())
            this.speed = speed
            this.color = color
        }
    }

    companion object {
        private const val NUM_PARTICLES = 100
    }
}