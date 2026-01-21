package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import controller.AppController;

public class TitleView extends JFrame {
    public TitleView(AppController ctrl) {
        setTitle("新卒生活費シミュレーション");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ヘッダー（タイトル）
        JLabel label = new JLabel("<html><div style='text-align: center;'>新卒生活費<br><span style='font-size:12pt; color:#7F8C8D;'>シミュレーション</span></div></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 28));
        label.setForeground(new Color(44, 62, 80));

        // ボタンエリア
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        buttonPanel.setOpaque(false);

        JButton startBtn = createStyledButton("新しく入力を始める", new Color(52, 152, 219), Color.WHITE);
        JButton loadBtn = createStyledButton("前回のデータから再開", new Color(236, 240, 241), new Color(44, 62, 80));

        startBtn.addActionListener(e -> ctrl.showIncome());
        loadBtn.addActionListener(e -> ctrl.loadPreviousData());

        buttonPanel.add(startBtn);
        buttonPanel.add(loadBtn);

        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Swingのボタン枠線を消してスッキリさせる
        btn.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
        return btn;
    }
}