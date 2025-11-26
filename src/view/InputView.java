package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import model.ExpenseItem;
import model.ExpenseModel;
import controller.AppController;

/* 入力画面 (チェックボックス付き) */
public class InputView extends JFrame {

    private ExpenseModel model;
    private AppController ctrl;

    // 入力行を扱う内部クラス (1 行 = チェック + テキスト + 入力欄)
    private class Row {
        JCheckBox check;
        JTextField nameField;
        JTextField amountField;
    }
    private ArrayList<Row> rows;  // 画面上の行リスト

    private JLabel totalLabel;

    public InputView(AppController ctrl, ExpenseModel model) {
        this.ctrl = ctrl;
        this.model = model;

        setTitle("生活費入力");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        rows = new ArrayList<>();

        // ===== メインパネル =====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // 既存モデル項目を表示
        for (ExpenseItem item : model.getItems()) {
            addRowUI(mainPanel, item);
        }

        // === ボタン類 ===
        JButton addBtn = new JButton("新規項目を追加");
        addBtn.addActionListener(e -> ctrl.onAddItem());

        JButton calcBtn = new JButton("計算");
        calcBtn.addActionListener(e -> ctrl.onCalculate());

        JButton detailBtn = new JButton("詳細を表示する");
        detailBtn.addActionListener(e -> ctrl.onShowDetail());

        totalLabel = new JLabel("計算結果: ￥0");

        JButton reset = new JButton("やり直す");
        reset.addActionListener(e -> ctrl.reset());

        // ===== 下部パネル =====
        JPanel bottom = new JPanel();
        bottom.add(addBtn);
        bottom.add(calcBtn);
        bottom.add(detailBtn);
        bottom.add(totalLabel);
        bottom.add(reset);

        // ===== スクロール対応 =====
        JScrollPane scroll = new JScrollPane(mainPanel);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    /* モデルから 1 行分の UI を生成しメインパネルへ追加 */
    private void addRowUI(JPanel parent, ExpenseItem item) {
        Row row = new Row();
        row.check = new JCheckBox();
        row.nameField = new JTextField(item.name, 10);
        row.amountField = new JTextField(String.valueOf(item.amount), 7);

        JPanel line = new JPanel();
        line.add(row.check);
        line.add(row.nameField);
        line.add(new JLabel("￥"));
        line.add(row.amountField);

        parent.add(line);
        rows.add(row);
    }

    /* コントローラ経由で呼ばれる：行を 1 行追加 */
    public void addItemRow() {
        // モデル側にも追加
        model.addItem();
        // 画面にも追加
        ExpenseItem last = model.getItems().get(model.getItems().size() - 1);
        addRowUI((JPanel) ((JScrollPane) getContentPane().getComponent(0)).getViewport().getView(), last);
        // 再描画
        revalidate();
        repaint();
    }

    /* 合計を再計算しラベル更新 */
    public void updateTotal() {
        // まず UI → モデルへ値を保存
        ArrayList<ExpenseItem> items = model.getItems();
        for (int i = 0; i < rows.size(); i++) {
            Row r = rows.get(i);
            ExpenseItem it = items.get(i);

            it.checked = r.check.isSelected();
            it.name = r.nameField.getText();

            try {
                it.amount = Integer.parseInt(r.amountField.getText());
            } catch (NumberFormatException e) {
                it.amount = 0;     // 数字以外なら 0
            }
        }
        // モデルで計算 (コントローラ側で呼び出し済だが安全のため)
        model.calculateTotal();
        totalLabel.setText("計算結果: ￥" + model.getTotal());
    }
}