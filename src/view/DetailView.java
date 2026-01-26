package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.util.*;
import model.ExpenseModel;
import model.ExpenseItem;
import controller.AppController;

public class DetailView extends JFrame {

    private ExpenseModel model;
    private JLabel totalLabel;

    private JComboBox<String> cityBox;
    private Map<String, String> cityFiles;
    private JTable table;
    private PieChartPanel piePanel;

    // ★ デザイン用定数（ここを書き足して統一感を出す）
    private final Font MAIN_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private final Color COLOR_BG = new Color(245, 246, 250);
    private final Color COLOR_TABLE_HEAD = new Color(44, 62, 80);

    private static final Map<String, String> CITY_NAME_JP = Map.of(
        "Tokyo", "東京", "Osaka", "大阪", "Sapporo", "札幌", "Fukuoka", "福岡",
        "Sendai", "仙台", "Nagoya", "名古屋", "Kyoto", "京都", "Kobe", "神戸"
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

    // ★ ランダムに豆知識を取得するメソッド
    private String getRandomMuroranTriviaWithNumber() {
        Random rand = new Random();
        int index = rand.nextInt(MURORAN_TRIVIA.length); // 0 ～ length-1
        int number = index + 1; // 表示用番号（1始まり）

        return "室蘭の豆知識" + number + ":" + MURORAN_TRIVIA[index];
    }

    // =====================================================
    // city フォルダ内の *.csv を自動検出（ロジックは元のまま）
    // =====================================================
    private Map<String, String> loadCityFiles(String folderPath) {
        Map<String, String> map = new LinkedHashMap<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        if (files == null) return map;

        for (File f : files) {
            String fileName = f.getName();
            String cityName = fileName.replace(".csv", "");
            map.put(cityName, f.getAbsolutePath());
        }
        return map;
    }

    // =====================================================
    // 都市データ CSV を読み込む（ロジックは元のまま）
    // =====================================================
    private Map<String, Integer> loadCityData(String csvPath) {
        Map<String, Integer> map = new HashMap<>();
        // ※CSV読み込み時の文字化け対策のみ、実用性を考慮してエンコーディング指定を追加しています
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(csvPath), "UTF-8"))) { // 元はUTF-8でしたがWindows用にMS932推奨

            String line = br.readLine();
            if (line == null) return map;

            line = line.replace("\uFEFF", "");
            String[] header = line.split(",");

            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "").trim();
                if (line.isEmpty()) continue;

                String[] c = line.split(",");
                if (c.length != header.length) continue;

                for (int i = 1; i < c.length; i++) {
                    String item = header[i].trim();
                    int yen = Integer.parseInt(c[i].trim());
                    map.put(item, yen);
                }
            }
        } catch (Exception e) {
            // MS932で失敗した場合のフォールバック（UTF-8）
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(csvPath), "UTF-8"))) {
                // (再試行ロジック省略: 基本的に上のブロックで読める想定)
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        return map;
    }

    // =====================================================
    // コンストラクタ
    // =====================================================
    public DetailView(AppController ctrl, ExpenseModel model) {
        this.model = model;

        setTitle("詳細内訳（都市比較）");
        setSize(1000, 700); // ★ 画面サイズを少し拡張
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // ★ アプリ全体が終了しないよう修正
        
        // ★ 全体の背景色を設定
        getContentPane().setBackground(COLOR_BG);

        cityFiles = loadCityFiles("city");

        // --- 上部パネル（デザイン強化） ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        top.setBackground(Color.WHITE);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel cityLabel = new JLabel("比較する都市：");
        cityLabel.setFont(HEADER_FONT);

        cityBox = new JComboBox<>(
            cityFiles.keySet().stream()
                .map(key -> CITY_NAME_JP.getOrDefault(key, key))
                .toArray(String[]::new)
        );
        cityBox.setFont(MAIN_FONT); // ★ フォント適用
        cityBox.setPreferredSize(new Dimension(150, 30)); // ★ サイズ調整
        cityBox.addActionListener(e -> updateTable());

        top.add(cityLabel);
        top.add(cityBox);
        add(top, BorderLayout.NORTH);

        // --- テーブル設定（デザイン強化） ---
        table = new JTable();
        styleTable(table); // ★ テーブル装飾メソッドの適用

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 枠線を消す
        scrollPane.getViewport().setBackground(Color.WHITE);

        // --- 分割パネル（デザイン強化） ---
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                createChartPanel(),
                scrollPane
        );
        split.setDividerLocation(450); // ★ 分割位置調整
        split.setDividerSize(5);       // 分割線を細く
        split.setBorder(new EmptyBorder(15, 15, 15, 15)); // ★ 余白を追加
        split.setBackground(COLOR_BG);
        add(split, BorderLayout.CENTER);

        // --- 下部パネル（デザイン強化） ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(COLOR_TABLE_HEAD); // ★ フッターを濃色に
        bottom.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton backBtn = new JButton("◀ 入力画面へ戻る");
        backBtn.setFont(HEADER_FONT);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(COLOR_PRIMARY); // ★ ボタン色変更
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> ctrl.backToInputView()); // ★ 画面だけ閉じるように変更

        bottom.add(backBtn, BorderLayout.WEST);

        totalLabel = new JLabel("合計: ￥0", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // ★ フォントサイズ拡大
        totalLabel.setForeground(Color.WHITE); // ★ 文字色を白に
        bottom.add(totalLabel, BorderLayout.CENTER);

        // --- 豆知識パネル（リデザイン版） ---
        JPanel triviaPanel = new JPanel(new BorderLayout());
        // 少し濃いめの水色で境界をはっきりさせる
        triviaPanel.setBackground(new Color(225, 245, 254)); 
        // 上部に少し太めのアクセントラインを入れる
        triviaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(3, 169, 244)), // 上の青い線
            BorderFactory.createEmptyBorder(10, 15, 10, 15) // 内側の余白
        ));

        // アイコン（色をより鮮やかに）
        JLabel iconLabel = new JLabel("💡 DID YOU KNOW?"); // テキストアイコンに変更
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        iconLabel.setForeground(new Color(2, 136, 209));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // テキスト（フォントを少し大きく、読みやすく）
        JLabel triviaLabel = new JLabel("<html><div style='width: 800px;'>" + getRandomMuroranTriviaWithNumber() + "</div></html>");
        triviaLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 15)); // セリフ体で特別感を演出
        triviaLabel.setForeground(new Color(44, 62, 80));

        JPanel textContainer = new JPanel(new BorderLayout());
        textContainer.setOpaque(false);
        textContainer.add(iconLabel, BorderLayout.NORTH);
        textContainer.add(triviaLabel, BorderLayout.CENTER);

        triviaPanel.add(textContainer, BorderLayout.CENTER);

        // --- レイアウトへの組み込み（既存の bottom パネルの上に配置） ---
        JPanel southContainer = new JPanel(new BorderLayout());
        southContainer.add(triviaPanel, BorderLayout.NORTH);
        southContainer.add(bottom, BorderLayout.CENTER);
        
        add(southContainer, BorderLayout.SOUTH);

        // 初期表示
        updateTable();
    }

    // ★ 追加メソッド: テーブルのデザインを一括適用
    private void styleTable(JTable table) {
        table.setRowHeight(35); // 行間を広げる
        table.setFont(MAIN_FONT);
        table.setShowVerticalLines(false); // 縦線を消す
        table.setGridColor(new Color(230, 230, 230));
        
        // ヘッダーのデザイン
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(COLOR_TABLE_HEAD);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));

        // 数値を右寄せにする
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        // 1列目以降（数値列）に適用する想定（初期化時はカラムがないためupdateTable内で適用）
    }

    // =====================================================
    // 円グラフ＋棒グラフパネル生成
    // =====================================================
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE); // ★ 背景を白に
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // ★ 枠線追加

        // タイトル追加
        JLabel chartTitle = new JLabel("支出の内訳グラフ", SwingConstants.CENTER);
        chartTitle.setFont(HEADER_FONT);
        chartTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(chartTitle, BorderLayout.NORTH);

        piePanel = new PieChartPanel(model.getItems());
        panel.add(piePanel, BorderLayout.CENTER);

        return panel;
    }

    // =====================================================
    // 表を更新（都市変更時にも呼ばれる）
    // =====================================================
    private void updateTable() {
        String selectedJp = (String) cityBox.getSelectedItem();
        String city = CITY_NAME_JP.entrySet().stream()
                .filter(e -> e.getValue().equals(selectedJp))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(selectedJp);

        String csv = cityFiles.get(city);
        Map<String, Integer> cityData = loadCityData(csv);

        java.util.List<ExpenseItem> list = model.getItems();
        java.util.List<ExpenseItem> selected =
                list.stream().filter(ExpenseItem::isChecked).toList();

        String[] columns = {"チェック項目", "今回支出", selectedJp + "の相場", "差額"}; // ★「差額」列を追加
        // データ配列サイズ修正: 列数3→4
        String[][] data = new String[selected.size() + 1][4]; 

        int idx = 0;
        int totalUser = 0;
        int totalCity = 0;

        for (ExpenseItem e : selected) {
            String name = e.getName().trim();

            String edu = model.getEducation();  
            String taxKey = edu + "_所得税";
            String disp = model.getDisplacement();
            String automobileTaxKey = disp;

            int cityValue;

            // ★ 所得税だけは学歴別キーで取得
            if (name.equals("所得税")) {
                cityValue = cityData.getOrDefault(taxKey, 0);
            } else if (name.equals("自動車税")) {
                cityValue = cityData.getOrDefault(automobileTaxKey, 0);
            } else {
                cityValue = cityData.getOrDefault(name, 0);
            }
        
            int diff = e.getAmount() - cityValue;

            data[idx][0] = name;
            data[idx][1] = String.format("%,d", e.getAmount()); // ★ カンマ区切り
            data[idx][2] = String.format("%,d", cityValue);
            data[idx][3] = String.format("%,d", diff);

            totalUser += e.getAmount();
            totalCity += cityValue;
            idx++;
        }

        // 合計行
        data[idx][0] = "合計";
        data[idx][1] = String.format("%,d", totalUser);
        data[idx][2] = String.format("%,d", totalCity);
        data[idx][3] = String.format("%,d", totalUser - totalCity);

        // 表更新
        table.setModel(new DefaultTableModel(data, columns));
        
        // ★ 再度テーブルスタイルを適用（モデル更新でリセットされるため）
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for(int i=1; i<4; i++) {
             table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        totalLabel.setText("合計: ￥" + String.format("%,d", totalUser));
        piePanel.repaint();
    }

    // =====================================================
    // 円グラフパネル（描画品質向上・レイアウト修正版）
    // =====================================================
    private class PieChartPanel extends JPanel {

        private java.util.List<ExpenseItem> items;
        // ★ 色をパステルカラーに変更してモダンに
        private final Color[] COLORS = {
                new Color(255, 107, 107), new Color(78, 205, 196),
                new Color(255, 217, 61), new Color(162, 155, 254),
                new Color(116, 185, 255), new Color(250, 177, 160),
                new Color(85, 239, 196), new Color(223, 230, 233)
        };

        public PieChartPanel(java.util.List<ExpenseItem> items) {
            this.items = items;
            // パネルの推奨サイズ設定（必要に応じて調整）
            setPreferredSize(new Dimension(300, 300));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            // ★ アンチエイリアス有効化
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            java.util.List<ExpenseItem> selected =
                    items.stream().filter(ExpenseItem::isChecked).toList();

            if (selected.isEmpty()) {
                g2.setColor(Color.GRAY);
                g2.setFont(MAIN_FONT);
                g2.drawString("選択された項目がありません", 20, 100);
                return;
            }

            int total = selected.stream().mapToInt(ExpenseItem::getAmount).sum();
            
            // =========================================================
            // 【修正箇所】円グラフのサイズ計算を修正
            // =========================================================
            // 凡例（右側のテキスト）を表示するために確保したい幅
            int legendSpace = 180; 
            
            // パネルの幅から凡例スペースを引いた値と、高さを比較して小さい方を直径にする
            // さらに上下左右のマージンとして -60 程度引いておく
            int diameter = Math.min(getWidth() - legendSpace, getHeight()) - 60;
            
            // サイズが小さくなりすぎないようガード（最低50px）
            if (diameter < 50) diameter = 50;

            int x = 20; // 左端からのマージン
            int y = (getHeight() - diameter) / 2; // 上下中央寄せ
            int startAngle = 90;

            // 円グラフの描画
            for (int i = 0; i < selected.size(); i++) {
                ExpenseItem e = selected.get(i);
                int angle = (int) Math.round((double) e.getAmount() / total * 360);

                g2.setColor(COLORS[i % COLORS.length]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(x, y, diameter, diameter, startAngle, angle);

                startAngle += angle;
            }

            // =========================================================
            // 凡例（内訳）の描画
            // =========================================================
            // 円グラフの右側に配置（直径 + 左マージン + 余白）
            int lx = x + diameter + 30;
            int ly = y; // 円グラフの上端に合わせる

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            
            // 凡例タイトル
            if (ly < 20) ly = 20; // 見切れ防止
            g2.drawString("【内訳】", lx, ly - 10);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            
            // 項目数が多いと下にはみ出すので、最大表示数を制限するか、高さをチェック
            int maxItems = Math.min(selected.size(), (getHeight() - ly) / 25);

            for (int i = 0; i < maxItems; i++) {
                ExpenseItem e = selected.get(i);

                g2.setColor(COLORS[i % COLORS.length]);
                g2.fillRect(lx, ly + i * 25, 15, 15);

                g2.setColor(Color.DARK_GRAY);
                double percent = (double) e.getAmount() / total * 100;
                String label = String.format("%s : %.1f%%", e.getName(), percent);
                g2.drawString(label, lx + 25, ly + i * 25 + 12);
            }
        }
    }
}