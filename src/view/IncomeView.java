
package view;

import controller.AppController;
import model.ExpenseModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * 収入入力画面
 */
public class IncomeView extends JFrame {

    private final JComboBox<String> eduBox;   // 最終学歴
    private final JTextField annualField;     // 年収（基本給）
    private final JTextField bonusField;      // ボーナス1回あたり
    private final JTextField bonusCountField; // ボーナス回数
    private final JTextField monthlyField;    // 月収（自動計算）

    public IncomeView(AppController ctrl, ExpenseModel model) {

        setTitle("収入入力 - 生活費シミュレーション");
        setSize(520, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // 背景グレー
        Color COLOR_BG = new Color(245, 246, 250);
        getContentPane().setBackground(COLOR_BG);

        // メインパネル（余白の設定）
        JPanel contentPanel = new JPanel(new BorderLayout(0, 25));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(35, 45, 35, 45));

        // --- ヘッダー ---
        JLabel titleLabel = new JLabel("収入情報の入力");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        // 濃紺
        Color COLOR_DARK = new Color(44, 62, 80);
        titleLabel.setForeground(COLOR_DARK);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // --- フォーム ---
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        formPanel.setOpaque(false);

        // コンポーネントの初期化とスタイリング
        eduBox = new JComboBox<>(new String[]{"高卒", "短大・専門学校卒", "大卒", "大学院卒"});
        eduBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eduBox.addActionListener(e -> applyPreset());

        annualField = createStyledTextField();
        bonusField = createStyledTextField();
        bonusCountField = createStyledTextField();
        
        // 月収フィールドは強調表示（読み取り専用）
        monthlyField = createStyledTextField();
        monthlyField.setEditable(false);
        monthlyField.setBackground(new Color(232, 244, 253));
        monthlyField.setForeground(COLOR_DARK);
        monthlyField.setFont(new Font("SansSerif", Font.BOLD, 16));
        // カラーパレット（InputViewと統一）
        // ブルー
        Color COLOR_PRIMARY = new Color(52, 152, 219);
        monthlyField.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 1));

        // 各行をフォームに追加
        formPanel.add(createLabeledRow("最終学歴（プリセット）", eduBox));
        formPanel.add(createLabeledRow("年収 / 基本給（円）", annualField));
        formPanel.add(createLabeledRow("ボーナス額 / 1回（円）", bonusField));
        formPanel.add(createLabeledRow("ボーナス回数 / 年", bonusCountField));
        formPanel.add(createLabeledRow("算出された月収（目安）", monthlyField));

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // --- フッターボタン ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton backBtn = new JButton("◀ 戻る");
        styleNavButton(backBtn);
        backBtn.addActionListener(e -> ctrl.backToTitleView());

        JButton nextBtn = createStyledButton(COLOR_PRIMARY);
        nextBtn.addActionListener(e -> {
            try {
                int monthly = Integer.parseInt(monthlyField.getText());
            
                // 学歴をモデルに保存
                model.setEducation((String) eduBox.getSelectedItem());
            
                model.setIncome(monthly);
                ctrl.showCarSelect();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "有効な数値を入力してください。");
            }
        });

        footer.add(backBtn, BorderLayout.WEST);
        footer.add(nextBtn, BorderLayout.EAST);
        contentPanel.add(footer, BorderLayout.SOUTH);

        // リアルタイム計算用のリスナー登録
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculate(); }
            public void removeUpdate(DocumentEvent e) { calculate(); }
            public void changedUpdate(DocumentEvent e) { calculate(); }
        };
        annualField.getDocument().addDocumentListener(dl);
        bonusField.getDocument().addDocumentListener(dl);
        bonusCountField.getDocument().addDocumentListener(dl);

        add(contentPanel);

        // 初期表示でプリセットを適用
        applyPreset();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Monospaced", Font.PLAIN, 15));
        tf.setMargin(new Insets(5, 10, 5, 10));
        return tf;
    }

    private JPanel createLabeledRow(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(127, 140, 141));
        label.setPreferredSize(new Dimension(180, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void styleNavButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Color.GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JButton createStyledButton(Color bg) {
        JButton btn = new JButton("次へ（車情報の確認）");
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(10, 20, 10, 20));
        return btn;
    }

    private void applyPreset() {
        String edu = (String) eduBox.getSelectedItem();
        if ("高卒".equals(edu)) {
            annualField.setText("2298000");
            bonusField.setText("150000");
            bonusCountField.setText("2");
        } else if ("短大・専門学校卒".equals(edu)) {
            annualField.setText("2672000");
            bonusField.setText("180000");
            bonusCountField.setText("2");
        } else if ("大卒".equals(edu)) {
            annualField.setText("2876400");
            bonusField.setText("200000");
            bonusCountField.setText("2");
        } else if ("大学院卒".equals(edu)) {
            annualField.setText("3421200");
            bonusField.setText("230000");
            bonusCountField.setText("2");
        }
        calculate();
    }

    private void calculate() {
        try {
            int nen = Integer.parseInt(annualField.getText());
            int bon = Integer.parseInt(bonusField.getText());
            int cnt = Integer.parseInt(bonusCountField.getText());

            // 月収 = (年収 - (ボーナス × 回数)) / 12
            int monthly = (nen - (bon * cnt)) / 12;
            monthlyField.setText(String.valueOf(Math.max(0, monthly)));
        } catch (NumberFormatException e) {
            monthlyField.setText("0");
        }
    }
}