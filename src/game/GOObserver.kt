package game

interface GOObserver {
    fun born(go: GameObject?)
    fun die(go: GameObject?)
}