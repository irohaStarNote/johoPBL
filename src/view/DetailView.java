package view;

import javax.swing.*;
import java.awt.*;
import model.ExpenseModel;
import model.ExpenseItem;
import controller.AppController;

/* 詳細画面：チェック済み項目を一覧表示 */
public class DetailView extends JFrame {

    private ExpenseModel model;
    private JLabel totalLabel;

    public DetailView(AppController ctrl, ExpenseModel model) {
        this.model = model;

        setTitle("詳細内訳");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel totalLabel = new JLabel("合計: ￥" + model.getTotal(), SwingConstants.RIGHT);

        // ===== レイアウト =====
        add(createPieChartPanel(), BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);
    }

    // ----------------------------
    // ここから円グラフ機能の追加コード
    // ----------------------------

    /**
     * DetailView 内に追加する円グラフパネル
     */
    private JPanel createPieChartPanel() {
        return new PieChartPanel(model.getItems());
    }

    /**
     * 円グラフ描画用の内部クラス
     */
    private class PieChartPanel extends JPanel {

        private java.util.List<model.ExpenseItem> items;

        // 10種類の固定カラー
        private final Color[] COLORS = {
                new Color(244, 67, 54),
                new Color(33, 150, 243),
                new Color(76, 175, 80),
                new Color(255, 193, 7),
                new Color(156, 39, 176),
                new Color(255, 87, 34),
                new Color(63, 81, 181),
                new Color(0, 150, 136),
                new Color(205, 220, 57),
                new Color(121, 85, 72)
        };

        public PieChartPanel(java.util.List<model.ExpenseItem> items) {
            this.items = items;
            setPreferredSize(new Dimension(500, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Font legendFont = new Font("SansSerif", Font.PLAIN, 20); // フォントサイズ設定
            g2.setFont(legendFont);

            // checked == true の項目のみ使用
            java.util.List<model.ExpenseItem> selected = items.stream().filter(e -> e.checked).toList();

            if (selected.isEmpty()) {
                g2.drawString("選択された項目がありません", 30, 30);
                return;
            }

            int n = Math.min(10, selected.size());
            int total = selected.stream().mapToInt(e -> e.amount).sum();

            int diameter = 300; // 円グラフの直系
            int x = 20;
            int y = 20;
            int startAngle = 0;

            // 円グラフ本体
            for (int i = 0; i < n; i++) {
                model.ExpenseItem e = selected.get(i);
                int value = e.amount;

                int angle = (int) Math.round((double) value / total * 360);

                g2.setColor(COLORS[i]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);

                startAngle += angle;
            }

            // 凡例
            int lx = x + diameter + 30;
            int ly = y + 20;

            g2.setColor(Color.BLACK);
            g2.drawString("凡例", lx, ly - 10);

            for (int i = 0; i < n; i++) {
                model.ExpenseItem e = selected.get(i);

                g2.setColor(COLORS[i]);
                g2.fillRect(lx, ly + i * 20, 15, 15);

                g2.setColor(Color.BLACK);
                g2.drawString(e.name + " : " + e.amount + "円", lx + 25, ly + i * 20 + 12);
            }
        }
    }
}