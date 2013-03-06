package com.ericguerra.algoviz;

import com.ericguerra.algoviz.ui.GraphPanel;
import com.ericguerra.algoviz.ui.SortPanel;

import javax.swing.*;

public final class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException ignored) {
                // fall back to the default look and feel
            }
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Sorting", new SortPanel());
            tabs.addTab("Graph search", new GraphPanel());

            JFrame frame = new JFrame("AlgoViz");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(tabs);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
