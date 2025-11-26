package view;

import javax.swing.*;
import java.awt.*;
import model.ExpenseModel;
import model.ExpenseItem;
import controller.AppController;

/* 詳細画面：チェック済み項目を一覧表示 */
public class DetailView extends JFrame {

    public DetailView(AppController ctrl, ExpenseModel model) {
        setTitle("詳細内訳");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ===== 表形式で内訳表示 =====
        String[] columns = {"項目", "金額(￥)"};
        java.util.List<ExpenseItem> list = model.getItems();
        int rowCount = 0;
        for (ExpenseItem e : list) if (e.checked) rowCount++;

        String[][] data = new String[rowCount][2];
        int idx = 0;
        for (ExpenseItem e : list) {
            if (e.checked) {
                data[idx][0] = e.name;
                data[idx][1] = String.valueOf(e.amount);
                idx++;
            }
        }

        JTable table = new JTable(data, columns);
        JScrollPane scroll = new JScrollPane(table);

        JLabel totalLabel = new JLabel("合計: ￥" + model.getTotal(), SwingConstants.RIGHT);

        // ===== レイアウト =====
        add(scroll, BorderLayout.CENTER);
        add(totalLabel, BorderLayout.SOUTH);

        // === ボタン類 ===
        JButton restartBtn = new JButton("もう一度始める");
        restartBtn.addActionListener(e -> ctrl.showInput());

        // ===== 下部パネル =====
        JPanel bottom = new JPanel();
        bottom.add(restartBtn);
    }
}