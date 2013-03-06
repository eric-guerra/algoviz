package com.ericguerra.algoviz.ui;

import javax.swing.*;
import java.awt.*;

/** Shared playback controls: step back, play/pause, step forward, speed. */
final class Controls extends JPanel {
    Controls(Player<?> player) {
        super(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("◀");
        JButton play = new JButton("Play");
        JButton forward = new JButton("▶");
        JSlider speed = new JSlider(1, 200, 50);
        speed.setInverted(true);
        speed.setToolTipText("Delay per step (ms)");

        back.addActionListener(e -> player.stepBack());
        forward.addActionListener(e -> player.stepForward());
        play.addActionListener(e -> {
            if (player.isPlaying()) player.pause(); else player.play();
        });
        speed.addChangeListener(e -> player.setDelayMs(speed.getValue()));

        Timer refresh = new Timer(100, e -> play.setText(player.isPlaying() ? "Pause" : "Play"));
        refresh.start();

        add(back);
        add(play);
        add(forward);
        add(new JLabel("Speed"));
        add(speed);
    }
}
