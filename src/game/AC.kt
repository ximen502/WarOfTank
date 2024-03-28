package game

import com.brackeen.sound.Sound
import com.brackeen.sound.SoundManager
import game.lib.MidiPlayer
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

    // Gunfire.wav音频格式
    val PLAYBACK_FORMAT_GF = AudioFormat(8363f, 16, 1, true, false)
    var soundManagerGF: SoundManager? = null
    var gunfire: Sound? = null

    // Peow.wav音频格式
    val PLAYBACK_FORMAT_PEOW = AudioFormat(8402f, 16, 1, true, false)
    var soundManagerPeow: SoundManager? = null
    var peow: Sound? = null
}