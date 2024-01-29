package game.lib;

import javax.sound.midi.*;
import java.io.IOException;

/**
 * 添加日期：2024-01-29 11:06
 * 播放midi格式音乐的类
 * 代码来源brackeen
 */
public class MidiPlayer implements MetaEventListener {

    public static final int END_OF_TRACK_MESSAGE = 47;
    private Sequencer sequencer;
    private boolean loop;
    private boolean paused;

    public MidiPlayer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.addMetaEventListener(this);
        } catch (MidiUnavailableException e) {
            sequencer = null;
            //throw new RuntimeException(e);
        }
    }

    public Sequence getSequence(String filename) {
        try {
            return MidiSystem.getSequence(getClass().getResourceAsStream(filename));
            //return MidiSystem.getSequence(new FileInputStream(filename));
        } catch (InvalidMidiDataException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 播放序列，可选循环。这个方法立即返回。无效序列不播放
     *
     * @param sequence
     * @param loop
     */
    public void play(Sequence sequence, boolean loop) {
        if (sequencer != null && sequence != null && sequencer.isOpen()) {
            try {
                sequencer.setSequence(sequence);
                sequencer.start();
                this.loop = loop;
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发生元事件时，声音系统调用这个方法，这时，收到磁道结尾元事件时，如果循环打开，
     * 则序列重新启动
     *
     * @param event the meta-message that the sequencer encountered
     */
    @Override
    public void meta(MetaMessage event) {
        //System.out.println("type:"+event.getType());
        if (event.getType() == END_OF_TRACK_MESSAGE) {
            //System.out.println("sequencer.isOpen():" + sequencer.isOpen() + " loop:" + loop);
            if (sequencer != null && sequencer.isOpen() && loop) {
                stop();
                sequencer.start();
            }
        }
    }

    public void stop() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.stop();
            sequencer.setMicrosecondPosition(0);
        }
    }

    public void close() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.close();
        }
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    /**
     * 设置暂停状态，音乐不一定立即暂停
     *
     * @param paused
     */
    public void setPaused(boolean paused) {
        if (this.paused != paused && sequencer != null && sequencer.isOpen()) {
            this.paused = paused;
            if (paused) {
                sequencer.stop();
            } else {
                sequencer.start();
            }
        }
    }

    public boolean isPaused() {
        return paused;
    }
}
