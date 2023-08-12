package game

import java.util.*

class SharedDrawList {
    companion object {
        var gameObjects = LinkedList<GameObject>()
        var diedGameObjects = LinkedList<GameObject>()
    }
}