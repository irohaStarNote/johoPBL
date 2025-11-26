package view;

import javax.swing.*;
import java.awt.*;
import controller.AppController;

/* タイトル画面 (V: View) */
public class TitleView extends JFrame {

    public TitleView(AppController ctrl) {
        setTitle("新卒生活費シミュレーション");
        setSize(400, 200);
        setLocationRelativeTo(null);     // 画面中央に配置
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== 画面パーツ =====
        JLabel label = new JLabel("ようこそ！", SwingConstants.CENTER);
        JButton startBtn = new JButton("入力開始！");

        // ボタン押下で入力画面へ
        startBtn.addActionListener(e -> ctrl.showInput());

        // ===== レイアウト =====
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        add(startBtn, BorderLayout.SOUTH);
    }
}