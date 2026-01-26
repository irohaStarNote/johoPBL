package view;

import controller.AppController;
import model.ExpenseModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CarSelectView extends JFrame {
    private AppController ctrl;
    private ExpenseModel model;

    public CarSelectView(AppController ctrl, ExpenseModel model) {
        this.ctrl = ctrl;
        this.model = model;
        
        setTitle("自動車の確認");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JLabel msg = new JLabel("<html><div style='text-align:center;'>所有する車の排気量を<br>選択してください</div></html>", SwingConstants.CENTER);
        msg.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(msg, BorderLayout.NORTH);

        JPanel btnGrid = new JPanel(new GridLayout(0, 1, 0, 12));
        btnGrid.setOpaque(false);

        // String[][] options = {
        //     {"持っていない", "0"},
        //     {"軽自動車", "900"},
        //     {"1.0L以下", "2080"},
        //     {"1.0L超 1.5L以下", "2540"},
        //     {"1.5L超 2.0L以下", "3000"}
        // };

        String[][] options = {
            {"持っていない", "0"},
            {"軽自動車", "900"},
            {"1.0L以下", "2000"},
            {"1.0L超 1.5L以下", "2500"},
            {"1.5L超 2.0L以下", "3000"},
            {"2.0L超 2.5L以下", "3600"},
            {"2.5L超 3.0L以下", "4100"},
            {"3.0L超 3.5L以下", "4700"},
            {"3.5L超 4.0L以下", "5400"},
            {"4.0L超 4.5L以下", "6200"},
            {"4.5L超 6.0L以下", "7200"},
            {"6.0L超", "9100"},
        };

        for (String[] opt : options) {
            JButton btn = new JButton(opt[0]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(248, 249, 250));
            btn.addActionListener(e -> {
                model.setDisplacement(opt[0]);
                model.setAutomobileTax(Integer.parseInt(opt[1]));
                ctrl.showInput();
            });
            btnGrid.add(btn);
        }

        // スクロールできるようにしておく（ボタン数が多いので安全のため）
        JScrollPane scrollPane = new JScrollPane(btnGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }
}