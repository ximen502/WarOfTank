package game

import game.lib.MidiPlayer
import game.lib.Sound
import game.lib.SoundManager
import java.applet.AudioClip
import javax.sound.sampled.AudioFormat

/**
 * @Class AC
 * @Description
 * @Author xsc
 * @Date 2024/01/02 下午03:01
 * @Version 1.0
 */
object AC {
    // 击中钢铁或多命坦克的音效
    var hitAC: AudioClip? = null
    //var enemyDieAC: AudioClip? = null
    //var bgMusicAC: AudioClip? = null
    var midiPlayer: MidiPlayer? = null

    // Bang.wav音频格式
    val PLAYBACK_FORMAT = AudioFormat(14914f, 16, 1, true, false)
    var soundManager: SoundManager? = null
    var bang: Sound? = null

    // playerdie.wav音频格式
    val PLAYBACK_FORMAT_PD = AudioFormat(12000f, 16, 1, true, false)
    var soundManagerPD: SoundManager? = null
    var playerdie: Sound? = null
}