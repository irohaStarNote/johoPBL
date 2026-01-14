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
 * ・画面下部に室蘭の豆知識をランダム表示
 */
public class DetailView extends JFrame {

    private ExpenseModel model;
    private JScrollPane scrollPane;

    // =====================================================
    // 都市名 → CSVファイル
    // =====================================================
    private static final Map<String, String> CITY_FILES = Map.of(
            "東京", "Tokyo.csv",
            "大阪", "Osaka.csv",
            "福岡", "Fukuoka.csv",
            "札幌", "Sapporo.csv"
    );

    // =====================================================
    // 室蘭の豆知識
    // =====================================================
    private static final String[] MURORAN_TRIVIA = {
            "室蘭は「工場夜景」が有名で、日本夜景遺産にも選ばれています。",
            "白鳥大橋は東日本最大級の吊り橋です。",
            "地球岬は「地球が丸く見える」絶景スポットです。",
            "地球岬は北海道自然景勝地に指定されている。",
            "室蘭は北海道の中でも雪が少ない地域です。",
            "室蘭焼き鳥は鶏肉ではなく豚肉を使うのが特徴です。",
            "イタンキ浜は鳴き砂の浜として知られている。",
            "イタンキ浜はアイヌの人たちがつけた「ハワノタ(声のある砂浜)」と呼ばれる地名があったことが由来です。",
            "イタンキ浜は平成8年には「日本の渚百選」に選定されました。",
            "室蘭港は天然の良港として発展してきました。",
            "室蘭は「鉄の街」と呼ばれることがある。",
            "チキウ岬灯台は1920年点灯。",
            "測量山ライトアップは1988年開始。夜の街景を彩る恒例の光。",
            "イルカ・クジラに出会える海。ウォッチングのフェリーもあります。",
            "港湾取扱貨物量は道内3位（H24）。物流の街はモノの流れが良く買い物にも恩恵。",
            "港の岸壁が109バースと道内最多。釣り・散策のシーンも多彩。",
            "室蘭やきとりは“豚＋玉ねぎ＋洋がらし”が定番。",
            "スポーツ都市宣言のまち。学校・地域でスポーツ機会が多い。",
            "B&G海洋センターで海のスポーツ体験。海育が身近。",
            "室蘭の夜景は海面反射が美しい。",
            "工業遺産ツアーが企画されることがある。",
            "港の文学館・市民美術館など、子供の感性を育む文化施設が近い。",
            "製造品出荷額等は道内2位（市町村別順位）。",
            "港は国際拠点港湾で海運・物流の仕事が多彩。",
            "PCB廃棄物処理施設など環境事業の拠点",
            "むろらん港まつりは1947年開始。花火が打ちあがる。",
            "姉妹都市は静岡市・上越市・ノックスビル、交流都市は宮古島市。",
            "旧室蘭駅舎の文化財登録など、近代化遺産を身近に感じる暮らし。",
            "「室蘭」の読みは昔「もろらん」とも呼ばれていた時期がある。",
            "崎守埠頭は、劇場版名探偵コナン銀翼の奇術師の舞台ともなった。",
            "室蘭まちづくり放送（FMびゅー）が運用されている。",
            "白鳥大橋の主塔に登れるクルーズがある。",
            "室蘭周辺の海は霧が発生しやすい。",
            "室蘭カレーラーメンは北海道三大ご当地ラーメンの一つである。",
            "地球岬は北海道自然景勝地に指定されている。",
            "環境科学館×図書館が複合で、学びと科学体験を融合。",
            "港の開港は1872年。海に開かれた街の原点。",
            "「鉄のまち」として発展しながら、海鳥・野鳥・海獣と共生する個性派都市。",
            "商工会議所が「室蘭やきとりの会」を設立している。",
            "室蘭は鉄鋼業を中心に発展した工業都市です。"
    };

    private String getRandomMuroranTriviaWithNumber() {
        Random rand = new Random();
        int index = rand.nextInt(MURORAN_TRIVIA.length); // 0 ～ length-1
        int number = index + 1; // 表示用番号（1始まり）

        return "室蘭の豆知識" + number + ":" + MURORAN_TRIVIA[index];
    }


    // =====================================================
    // CSV読み込み
    // =====================================================
    private Map<String, Integer> loadCityData(String csvPath) {
        Map<String, Integer> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvPath), "UTF-8"))) {

            String headerLine = br.readLine();
            if (headerLine == null) return map;
            headerLine = headerLine.replace("\uFEFF", "");
            String[] headers = headerLine.split(",");

            String valueLine = br.readLine();
            if (valueLine == null) return map;
            String[] values = valueLine.split(",");

            for (int i = 0; i < headers.length && i < values.length; i++) {
                String key = headers[i].trim();
                String raw = values[i].trim();

                int val = 0;
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
    // 表作成
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
            int cityValue = cityData.getOrDefault(itemName, 0);

            data[idx][0] = itemName;
            data[idx][1] = String.valueOf(e.amount);
            data[idx][2] = String.valueOf(cityValue);

            totalUser += e.amount;
            totalCity += cityValue;
            idx++;
        }

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
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -----------------------------
        // 上部：都市選択
        // -----------------------------
        JComboBox<String> cityCombo =
                new JComboBox<>(CITY_FILES.keySet().toArray(new String[0]));
        cityCombo.setSelectedItem("東京");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("比較都市："));
        top.add(cityCombo);
        add(top, BorderLayout.NORTH);

        // -----------------------------
        // 中央：円グラフ + 表
        // -----------------------------
        JTable table = createTable("東京");
        scrollPane = new JScrollPane(table);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new PieChartPanel(model.getItems()),
                scrollPane
        );
        split.setDividerLocation(500);
        add(split, BorderLayout.CENTER);

        // -----------------------------
        // 下部：背景色・アイコン付き豆知識バー
        // -----------------------------
        JPanel triviaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        triviaPanel.setBackground(new Color(230, 245, 255)); // 薄い水色
        triviaPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        triviaPanel.setOpaque(true);

        // アイコン（Unicode絵文字を使用）
        JLabel iconLabel = new JLabel("ℹ");
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        iconLabel.setForeground(new Color(30, 136, 229));

        // テキスト
        JLabel triviaLabel = new JLabel(getRandomMuroranTriviaWithNumber());

        triviaLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        triviaLabel.setForeground(Color.DARK_GRAY);

        // 追加
        triviaPanel.add(iconLabel);
        triviaPanel.add(Box.createHorizontalStrut(8)); // 余白
        triviaPanel.add(triviaLabel);

        add(triviaPanel, BorderLayout.SOUTH);


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

            for (int i = 0; i < n; i++) {
                ExpenseItem e = selected.get(i);
                int angle = (int) Math.round((double) e.amount / total * 360);

                g2.setColor(COLORS[i]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);
                startAngle += angle;
            }

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
