package com.ericguerra.algoviz.ui;

import com.ericguerra.algoviz.graph.Graph;
import com.ericguerra.algoviz.graph.GraphSearch;
import com.ericguerra.algoviz.graph.GraphStep;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/** Click a node to set the source, shift-click to set the target. */
public final class GraphPanel extends JPanel {
    private final Canvas canvas = new Canvas();
    private final JLabel status = new JLabel(" ");
    private final JComboBox<GraphSearch.Kind> kind = new JComboBox<>(GraphSearch.Kind.values());
    private final Player<GraphStep> player = new Player<>(canvas::show);
    private Graph graph = Graph.random(8, 5, System.nanoTime());
    private int source = 0;
    private int target;

    public GraphPanel() {
        super(new BorderLayout());
        target = graph.size() - 1;

        JButton regenerate = new JButton("New graph");
        regenerate.addActionListener(e -> {
            graph = Graph.random(8, 5, System.nanoTime());
            source = 0;
            target = graph.size() - 1;
            prepare();
        });
        kind.addActionListener(e -> prepare());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Search:"));
        top.add(kind);
        top.add(regenerate);
        top.add(new JLabel("  click = source, shift+click = target"));

        add(top, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new Controls(player), BorderLayout.WEST);
        bottom.add(status, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        player.setOnStateChange(() -> status.setText("  step " + (player.index() + 1) + "/" + player.size()
                + "  |  " + (canvas.step == null ? "" : canvas.step.message())));
        prepare();
    }

    private void prepare() {
        var result = GraphSearch.run((GraphSearch.Kind) kind.getSelectedItem(), graph, source, target);
        player.load(result.steps());
    }

    private final class Canvas extends JComponent {
        private static final int R = 14;
        private GraphStep step;

        Canvas() {
            setPreferredSize(new Dimension(900, 500));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int hit = nodeAt(e.getX(), e.getY());
                    if (hit < 0) return;
                    if (e.isShiftDown()) target = hit; else source = hit;
                    prepare();
                }
            });
        }

        void show(GraphStep s) {
            step = s;
            repaint();
        }

        private Point screen(int node) {
            Point2D.Double p = graph.position(node);
            return new Point((int) (p.x * (getWidth() - 2 * R) + R), (int) (p.y * (getHeight() - 2 * R) + R));
        }

        private int nodeAt(int x, int y) {
            for (int i = 0; i < graph.size(); i++) {
                if (screen(i).distance(x, y) <= R) return i;
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(g.getFont().deriveFont(11f));

            for (Graph.Edge e : graph.edges()) {
                Point a = screen(e.from()), b = screen(e.to());
                boolean onPath = step != null && onPath(e);
                boolean active = step != null && e.equals(step.activeEdge());
                g.setColor(onPath ? new Color(0x1E88E5) : active ? new Color(0xFFB300) : new Color(0xCFD8DC));
                g.setStroke(new BasicStroke(onPath || active ? 4 : 2));
                g.drawLine(a.x, a.y, b.x, b.y);
                g.setColor(Color.DARK_GRAY);
                g.drawString(String.valueOf(e.weight()), (a.x + b.x) / 2 + 3, (a.y + b.y) / 2 - 3);
            }

            for (int i = 0; i < graph.size(); i++) {
                Point p = screen(i);
                g.setColor(fill(i));
                g.fillOval(p.x - R, p.y - R, 2 * R, 2 * R);
                g.setColor(i == source || i == target ? Color.BLACK : Color.GRAY);
                g.setStroke(new BasicStroke(i == source || i == target ? 3 : 1));
                g.drawOval(p.x - R, p.y - R, 2 * R, 2 * R);
                g.setColor(Color.BLACK);
                String label = step != null && step.distances().containsKey(i) ? String.valueOf(step.distances().get(i)) : String.valueOf(i);
                g.drawString(label, p.x - g.getFontMetrics().stringWidth(label) / 2, p.y + 4);
            }
        }

        private boolean onPath(Graph.Edge e) {
            var path = step.path();
            for (int i = 0; i + 1 < path.size(); i++) {
                int a = path.get(i), b = path.get(i + 1);
                if ((e.from() == a && e.to() == b) || (e.from() == b && e.to() == a)) return true;
            }
            return false;
        }

        private Color fill(int node) {
            if (step == null) return Color.WHITE;
            if (step.path().contains(node)) return new Color(0x90CAF9);
            if (step.visited().contains(node)) return new Color(0xA5D6A7);
            if (step.frontier().contains(node)) return new Color(0xFFE082);
            return Color.WHITE;
        }
    }
}
