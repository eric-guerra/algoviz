package com.ericguerra.algoviz.ui;

import com.ericguerra.algoviz.sort.SortAlgorithm;
import com.ericguerra.algoviz.sort.SortRecorder;
import com.ericguerra.algoviz.sort.SortStep;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public final class SortPanel extends JPanel {
    private static final Color BAR = new Color(0x90A4AE);
    private static final Color COMPARE = new Color(0xFFB300);
    private static final Color SWAP = new Color(0xE53935);
    private static final Color SORTED = new Color(0x43A047);

    private final Canvas canvas = new Canvas();
    private final JLabel status = new JLabel(" ");
    private final JComboBox<SortAlgorithm> algorithms = new JComboBox<>(SortAlgorithm.all().toArray(SortAlgorithm[]::new));
    private final JSpinner size = new JSpinner(new SpinnerNumberModel(40, 5, 200, 5));
    private final Player<SortStep> player = new Player<>(canvas::show);
    private int[] input;
    private SortRecorder recorder;

    public SortPanel() {
        super(new BorderLayout());
        algorithms.setRenderer((list, value, index, selected, focus) ->
                new DefaultListCellRenderer().getListCellRendererComponent(list, value.name(), index, selected, focus));

        JButton shuffle = new JButton("Shuffle");
        shuffle.addActionListener(e -> { input = randomArray((Integer) size.getValue()); prepare(); });
        algorithms.addActionListener(e -> prepare());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Algorithm:"));
        top.add(algorithms);
        top.add(new JLabel("Size:"));
        top.add(size);
        top.add(shuffle);

        add(top, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new Controls(player), BorderLayout.WEST);
        bottom.add(status, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        player.setOnStateChange(this::updateStatus);
        input = randomArray(40);
        prepare();
    }

    private void prepare() {
        SortAlgorithm algo = (SortAlgorithm) algorithms.getSelectedItem();
        recorder = algo.run(input);
        player.load(recorder.steps());
    }

    private void updateStatus() {
        SortAlgorithm algo = (SortAlgorithm) algorithms.getSelectedItem();
        SortStep step = canvas.step;
        status.setText(String.format("  %s — %s  |  step %d/%d  |  %d comparisons, %d writes  |  %s",
                algo.name(), algo.complexity(), player.index() + 1, player.size(),
                recorder.comparisons(), recorder.writes(), step == null ? "" : step.describe()));
    }

    private static int[] randomArray(int n) {
        Random rnd = new Random();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = 1 + rnd.nextInt(100);
        return a;
    }

    private static final class Canvas extends JComponent {
        private SortStep step;

        Canvas() {
            setPreferredSize(new Dimension(900, 500));
        }

        void show(SortStep s) {
            this.step = s;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            if (step == null) return;
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int[] v = step.values();
            int max = 1;
            for (int x : v) max = Math.max(max, x);
            double w = (double) getWidth() / v.length;
            for (int i = 0; i < v.length; i++) {
                int h = (int) ((getHeight() - 10) * (v[i] / (double) max));
                g.setColor(colorFor(i));
                g.fillRect((int) (i * w) + 1, getHeight() - h, Math.max(1, (int) w - 2), h);
            }
        }

        private Color colorFor(int i) {
            boolean active = i == step.i() || i == step.j();
            if (active && step.type() == SortStep.Type.COMPARE) return COMPARE;
            if (active && (step.type() == SortStep.Type.SWAP || step.type() == SortStep.Type.WRITE)) return SWAP;
            if (step.sorted().contains(i)) return SORTED;
            return BAR;
        }
    }
}
