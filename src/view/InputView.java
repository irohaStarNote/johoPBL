package view;

import controller.AppController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.ExpenseCombo;
import model.ExpenseItem;
import model.ExpenseModel;

/* * デザインとロジックを統合した入力画面
 */
public class InputView extends JFrame {

    private ExpenseModel model;
    private AppController ctrl;

    // デザイン用カラーパレット
    private final Color COLOR_PRIMARY = new Color(52, 152, 219);  // ブルー
    private final Color COLOR_DARK = new Color(44, 62, 80);     // 濃紺
    private final Color COLOR_SUCCESS = new Color(46, 204, 113);  // 緑（残額用）
    private final Color COLOR_BG = new Color(245, 246, 250);     // 薄いグレーの背景

    private class Row {
        JCheckBox check;
        JLabel nameField;
        JTextField amountField;
    }

    private class RowCombo {
        JCheckBox check;
        JLabel name;
        JComboBox<String> list;
        JTextField amountField;
    }

    private ArrayList<Row> rows;
    private ArrayList<RowCombo> rows2;

    private JLabel totalLabel;
    private JLabel incomeLabel;
    private JLabel remainLabel;
    private JPanel mainPanel; // 行を追加するためにフィールドに保持

    public InputView(AppController ctrl, ExpenseModel model) {
        this.ctrl = ctrl;
        this.model = model;

        setTitle("生活費シミュレーション - 入力");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_BG);

        rows = new ArrayList<>();
        rows2 = new ArrayList<>();

        // ===== メインコンテンツエリア（スクロール内） =====
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // モデルの項目を反映
        for (ExpenseItem item : model.getItems()) {
            addRowUI(mainPanel, item);
        }
        for (ExpenseCombo item : model.getItems2()) {
            addRowComboUI(mainPanel, item);
        }

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null); // 枠線を消してフラットに
        scroll.getVerticalScrollBar().setUnitIncrement(16); // スクロールを滑らかに

        // ===== 下部ステータス・ボタンエリア =====
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(COLOR_DARK);
        footerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // 左：操作ボタン
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actionBtns.setOpaque(false);
        
        JButton addBtn = createStyledButton("＋ 項目を追加", Color.WHITE, COLOR_DARK);
        JButton detailBtn = createStyledButton("詳細・分析グラフを表示", COLOR_PRIMARY, Color.WHITE);
        
        addBtn.addActionListener(e -> ctrl.onAddItem());
        detailBtn.addActionListener(e -> ctrl.onShowDetail());
        
        actionBtns.add(addBtn);
        actionBtns.add(detailBtn);

        // 中央右：金額情報
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        infoPanel.setOpaque(false);

        incomeLabel = createStatusLabel("月収: ￥0");
        totalLabel = createStatusLabel("支出合計: ￥0");
        remainLabel = createStatusLabel("残額: ￥0");
        remainLabel.setForeground(COLOR_SUCCESS);

        infoPanel.add(incomeLabel);
        infoPanel.add(totalLabel);
        infoPanel.add(remainLabel);

        footerPanel.add(actionBtns, BorderLayout.WEST);
        footerPanel.add(infoPanel, BorderLayout.EAST);

        // ===== 上部：ナビゲーションバー =====
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        navBar.setBackground(Color.WHITE);
        
        JButton backTitleBtn = new JButton("◀ タイトルへ");
        JButton backIncomeBtn = new JButton("◀ 収入入力へ");
        styleNavButton(backTitleBtn);
        styleNavButton(backIncomeBtn);

        backTitleBtn.addActionListener(e -> ctrl.backToTitleView());
        backIncomeBtn.addActionListener(e -> ctrl.backToIncomeView());

        navBar.add(backTitleBtn);
        navBar.add(backIncomeBtn);

        // 全体のレイアウト設定
        setLayout(new BorderLayout());
        add(navBar, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        updateTotal();
    }

    /* テキスト項目の UI 行追加 */
    private void addRowUI(JPanel parent, ExpenseItem item) {
        Row row = new Row();
        JPanel line = createRowContainer();

        row.check = new JCheckBox();
        row.check.setSelected(item.isChecked());
        row.check.setOpaque(false);

        row.nameField = new JLabel(item.getName());
        row.nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        row.nameField.setPreferredSize(new Dimension(150, 30));

        row.amountField = new JTextField(String.valueOf(item.getAmount()), 10);
        styleTextField(row.amountField);

        // ロジック（チェック・入力変更）
        row.check.addActionListener(e -> {
            item.setChecked(row.check.isSelected());
            updateTotal();
            ctrl.saveData();
        });

        row.amountField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { rt(); }
            public void removeUpdate(DocumentEvent e) { rt(); }
            public void changedUpdate(DocumentEvent e) { rt(); }
            private void rt() {
                try {
                    item.setAmount(Integer.parseInt(row.amountField.getText()));
                } catch (NumberFormatException ex) {
                    item.setAmount(0);
                }
                item.setChecked(row.check.isSelected());
                updateTotal();
                ctrl.saveData();
            }
        });

        line.add(row.check);
        line.add(row.nameField);
        line.add(new JLabel("￥"));
        line.add(row.amountField);

        parent.add(line);
        rows.add(row);
    }

    /* コンボ項目の UI 行追加 */
    private void addRowComboUI(JPanel parent, ExpenseCombo item) {
        RowCombo row = new RowCombo();
        JPanel line = createRowContainer();

        row.check = new JCheckBox();
        row.check.setSelected(item.isChecked());
        row.check.setOpaque(false);

        row.name = new JLabel(item.getZei());
        row.name.setFont(new Font("SansSerif", Font.BOLD, 14));
        row.name.setPreferredSize(new Dimension(100, 30));

        row.list = new JComboBox<>(item.getName());
        row.amountField = new JTextField(String.valueOf(item.getAmount()), 10);
        styleTextField(row.amountField);

        row.check.addActionListener(e -> {
            item.setChecked(row.check.isSelected());
            updateTotal();
            ctrl.saveData();
        });

        row.list.addActionListener(e -> {
            String selected = (String) row.list.getSelectedItem();
            int newAmount = 0;
            if (item.getZei().equals("自動車税")) {
                if ("自動車なし".equals(selected)) newAmount = 0;
                else if ("軽自動車".equals(selected)) newAmount = 900; // 元データに合わせる
                else if ("普通車".equals(selected)) newAmount = 2500;
            }
            row.amountField.setText(String.valueOf(newAmount));
            item.setAmount(newAmount);
            updateTotal();
            ctrl.saveData();
        });

        line.add(row.check);
        line.add(row.name);
        line.add(row.list);
        line.add(new JLabel("￥"));
        line.add(row.amountField);

        parent.add(line);
        rows2.add(row);
    }

    // ===== ヘルパーメソッド（デザイン用） =====

    private JPanel createRowContainer() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(1000, 50));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(236, 240, 241)));
        return panel;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("Monospaced", Font.PLAIN, 14));
        tf.setMargin(new Insets(2, 5, 2, 5));
    }

    private void styleNavButton(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(Color.GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    private JLabel createStatusLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 16));
        l.setForeground(Color.WHITE);
        return l;
    }

    // ===== ロジックメソッド（元の機能を維持） =====

    public void addItemRow() {
        model.addItem();
        ExpenseItem last = model.getItems().get(model.getItems().size() - 1);
        addRowUI(mainPanel, last);
        revalidate();
        repaint();
    }

    public void updateTotal() {
        // ExpenseItemの同期
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            ExpenseItem it = model.getItems().get(i);
            it.setChecked(r.check.isSelected());
            try {
                it.setAmount(Integer.parseInt(r.amountField.getText()));
            } catch (NumberFormatException e) { it.setAmount(0); }
        }

        // ExpenseComboの同期
        for (int i = 0; i < rows2.size(); i++) {
            RowCombo r = rows2.get(i);
            ExpenseCombo it = model.getItems2().get(i);
            it.setChecked(r.check.isSelected());
            try {
                it.setAmount(Integer.parseInt(r.amountField.getText()));
            } catch (NumberFormatException e) { it.setAmount(0); }
        }

        model.calculateTotal();

        // ラベル更新
        incomeLabel.setText("月収: ￥" + String.format("%,d", model.getIncome()));
        totalLabel.setText("支出合計: ￥" + String.format("%,d", model.getTotal()));
        int remain = model.getIncome() - model.getTotal();
        remainLabel.setText("残額: ￥" + String.format("%,d", remain));
        
        // 残額がマイナスなら赤字にする
        remainLabel.setForeground(remain < 0 ? new Color(231, 76, 60) : COLOR_SUCCESS);
    }
}