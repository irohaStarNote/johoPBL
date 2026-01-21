package view;

import controller.AppController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CarSelectView extends JFrame {
    public CarSelectView(AppController ctrl) {
        setTitle("自動車の確認");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JLabel msg = new JLabel("<html><div style='text-align:center;'>所有する車の排気量を<br>選択してください</div></html>", SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(msg, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(0, 1, 0, 12));
        btnGrid.setOpaque(false);

        String[][] options = {
            {"持っていない", "0"},
            {"軽自動車", "900"},
            {"1.0L以下", "2080"},
            {"1.0L超 1.5L以下", "2540"},
            {"1.5L超 2.0L以下", "3000"}
        };

        for (String[] opt : options) {
            JButton btn = new JButton(opt[0]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(248, 249, 250));
            btn.addActionListener(e -> ctrl.onCarSelected(Integer.parseInt(opt[1])));
            btnGrid.add(btn);
        }

        panel.add(btnGrid, BorderLayout.CENTER);
        add(panel);
    }
}