package view;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import model.ExpenseModel;
import model.ExpenseItem;
import controller.AppController;

/*
 * 詳細画面：
 * ・チェック済み支出一覧
 * ・都市別CSV（別ファイル）との比較
 * ・都市選択可能
 * ・CSV列名と一致しない項目は 0
 * ・数値でない値（?? 等）は 0
 */
public class DetailView extends JFrame {

    private ExpenseModel model;
    private JScrollPane scrollPane;

    // 都市名 → CSVファイル
    private static final Map<String, String> CITY_FILES = Map.of(
            "東京", "Tokyo.csv",
            "大阪", "Osaka.csv",
            "福岡", "Fukuoka.csv",
            "札幌", "Sapporo.csv"
    );

    // =====================================================
    // CSV読み込み（安全版）
    // =====================================================
    private Map<String, Integer> loadCityData(String csvPath) {
        Map<String, Integer> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvPath), "UTF-8"))) {

            // ヘッダ
            String headerLine = br.readLine();
            if (headerLine == null) return map;
            headerLine = headerLine.replace("\uFEFF", "");
            String[] headers = headerLine.split(",");

            // 値（1行目のみ想定）
            String valueLine = br.readLine();
            if (valueLine == null) return map;
            String[] values = valueLine.split(",");

            for (int i = 0; i < headers.length && i < values.length; i++) {
                String key = headers[i].trim();
                String raw = values[i].trim();

                int val = 0; // デフォルト 0

                // 完全に数値のときだけ parse
                if (raw.matches("-?\\d+")) {
                    try {
                        val = Integer.parseInt(raw);
                    } catch (NumberFormatException ignored) {
                        val = 0;
                    }
                }

                map.put(key, val);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    // =====================================================
    // 表作成（一致しない項目は 0）
    // =====================================================
    private JTable createTable(String cityName) {
        String csv = CITY_FILES.get(cityName);
        Map<String, Integer> cityData = loadCityData(csv);

        java.util.List<ExpenseItem> list = model.getItems();
        int count = (int) list.stream().filter(e -> e.checked).count();

        String[][] data = new String[count + 1][3];
        String[] columns = {"チェック項目", "今回支出", cityName + "の相場"};

        int idx = 0;
        int totalUser = 0;
        int totalCity = 0;

        for (ExpenseItem e : list) {
            if (!e.checked) continue;

            String itemName = e.name.trim();

            // 一致しない場合は 0
            int cityValue = cityData.containsKey(itemName)
                    ? cityData.get(itemName)
                    : 0;

            data[idx][0] = itemName;
            data[idx][1] = String.valueOf(e.amount);
            data[idx][2] = String.valueOf(cityValue);

            totalUser += e.amount;
            totalCity += cityValue;
            idx++;
        }

        // 合計行
        data[idx][0] = "合計";
        data[idx][1] = String.valueOf(totalUser);
        data[idx][2] = String.valueOf(totalCity);

        JTable table = new JTable(data, columns);
        table.setRowHeight(24);
        return table;
    }

    // =====================================================
    // コンストラクタ
    // =====================================================
    public DetailView(AppController ctrl, ExpenseModel model) {
        this.model = model;

        setTitle("詳細内訳（都市比較）");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // -----------------------------
        // 都市選択
        // -----------------------------
        JComboBox<String> cityCombo =
                new JComboBox<>(CITY_FILES.keySet().toArray(new String[0]));
        cityCombo.setSelectedItem("東京");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("比較都市："));
        top.add(cityCombo);
        add(top, BorderLayout.NORTH);

        // -----------------------------
        // 表
        // -----------------------------
        JTable table = createTable("東京");
        scrollPane = new JScrollPane(table);

        // -----------------------------
        // 左：円グラフ　右：表
        // -----------------------------
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new PieChartPanel(model.getItems()),
                scrollPane
        );
        split.setDividerLocation(500);
        add(split, BorderLayout.CENTER);



        // -----------------------------
        // 都市切替
        // -----------------------------
        cityCombo.addActionListener(e -> {
            String city = (String) cityCombo.getSelectedItem();
            scrollPane.setViewportView(createTable(city));
        });
    }

    // =====================================================
    // 円グラフパネル
    // =====================================================
    private class PieChartPanel extends JPanel {

        private java.util.List<ExpenseItem> items;

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

        public PieChartPanel(java.util.List<ExpenseItem> items) {
            this.items = items;
            setPreferredSize(new Dimension(300, 300));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            java.util.List<ExpenseItem> selected =
                    items.stream().filter(e -> e.checked).toList();

            if (selected.isEmpty()) {
                g2.drawString("選択された項目がありません", 20, 20);
                return;
            }

            int total = selected.stream().mapToInt(e -> e.amount).sum();
            int n = Math.min(10, selected.size());

            int diameter = 180;
            int x = 20;
            int y = 20;
            int startAngle = 0;

            // 円グラフ
            for (int i = 0; i < n; i++) {
                ExpenseItem e = selected.get(i);
                int angle = (int) Math.round((double) e.amount / total * 360);

                g2.setColor(COLORS[i]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);
                startAngle += angle;
            }

            // 凡例
            int lx = x + diameter + 20;
            int ly = y + 20;

            g2.setColor(Color.BLACK);
            g2.drawString("凡例", lx, ly - 10);

            for (int i = 0; i < n; i++) {
                ExpenseItem e = selected.get(i);

                g2.setColor(COLORS[i]);
                g2.fillRect(lx, ly + i * 20, 15, 15);

                g2.setColor(Color.BLACK);
                g2.drawString(e.name + " : " + e.amount + "円",
                        lx + 25, ly + i * 20 + 12);
            }
        }
    }
}
