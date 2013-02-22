package com.ericguerra.algoviz.ui;

import javax.swing.Timer;
import java.util.List;
import java.util.function.Consumer;

/** Steps through a list of frames on a Swing timer with play/pause/step/seek. */
public final class Player<T> {
    private final Timer timer;
    private List<T> frames = List.of();
    private int index;
    private final Consumer<T> onFrame;
    private Runnable onStateChange = () -> {};

    public Player(Consumer<T> onFrame) {
        this.onFrame = onFrame;
        this.timer = new Timer(50, e -> {
            if (!stepForward()) pause();
        });
    }

    public void load(List<T> frames) {
        pause();
        this.frames = frames;
        this.index = 0;
        if (!frames.isEmpty()) onFrame.accept(frames.get(0));
        onStateChange.run();
    }

    public void play() {
        if (index >= frames.size() - 1) index = 0;
        timer.start();
        onStateChange.run();
    }

    public void pause() {
        timer.stop();
        onStateChange.run();
    }

    public boolean isPlaying() {
        return timer.isRunning();
    }

    public boolean stepForward() {
        if (index + 1 >= frames.size()) return false;
        onFrame.accept(frames.get(++index));
        onStateChange.run();
        return true;
    }

    public void stepBack() {
        if (index == 0) return;
        onFrame.accept(frames.get(--index));
        onStateChange.run();
    }

    public void setDelayMs(int ms) {
        timer.setDelay(ms);
    }

    public int index() {
        return index;
    }

    public int size() {
        return frames.size();
    }

    public void setOnStateChange(Runnable r) {
        this.onStateChange = r;
    }
}
