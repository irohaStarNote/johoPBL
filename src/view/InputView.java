package view;

import controller.AppController;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import model.ExpenseCombo;
import model.ExpenseItem;
import model.ExpenseModel;

/* 入力画面(チェックボックス付き) */
public class InputView extends JFrame {
    private ExpenseModel model;
    private AppController ctrl;
    
    // 入力行を扱う内部クラス(1 行 = チェック + テキスト + 入力欄)
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
    private ArrayList<Row> rows; // 画面上の行リスト
    private ArrayList<RowCombo> rows2;
    
    private JLabel totalLabel;
    
    public InputView(AppController ctrl, ExpenseModel model) {
        this.ctrl = ctrl;
        this.model = model;
        setTitle("生活費入力");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        rows = new ArrayList<>();
        rows2= new ArrayList<>();
    
        // ===== メインパネル =====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    
        // 既存モデル項目を表示
        for (ExpenseItem item : model.getItems()) {
            addRowUI(mainPanel, item);
        }
        for (ExpenseCombo item : model.getItems2()) {
            addRowComboUI(mainPanel, item);
        }
        // ===  ボタン類 ===
        JButton addBtn = new JButton("新規項目を追加");
        addBtn.addActionListener(e -> ctrl.onAddItem());
        JButton calcBtn = new JButton("計算");
        calcBtn.addActionListener(e -> ctrl.onCalculate());
        JButton detailBtn = new JButton("詳細を表示する");
        detailBtn.addActionListener(e -> ctrl.onShowDetail());
        totalLabel = new JLabel("計算結果: ￥0");

        // ===== 下部パネル =====
        JPanel bottom = new JPanel();
        bottom.add(addBtn);
        bottom.add(calcBtn);
        bottom.add(detailBtn);
        bottom.add(totalLabel);

        // ===== スクロール対応 =====
        JScrollPane scroll = new JScrollPane(mainPanel);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
    
    /* モデルから 1 行分の UI を生成しメインパネルへ追加 */
    private void addRowUI(JPanel parent, ExpenseItem item) {
        Row row = new Row();
        row.check = new JCheckBox();
        row.nameField = new JLabel(item.name, 10);
        row.amountField = new JTextField(String.valueOf(item.amount), 7);
        JPanel line = new JPanel();
        line.add(row.check);
        line.add(row.nameField);
        line.add(new JLabel("￥"));
        line.add(row.amountField);
        parent.add(line);
        rows.add(row);
    }
    private void addRowComboUI(JPanel parent,ExpenseCombo item){
        RowCombo row = new RowCombo();
        row.check = new JCheckBox();
        row.name =new JLabel(item.zei,10);
        row.list = new JComboBox<>(item.name);
        row.amountField = new JTextField(String.valueOf(item.amount), 7);

// ComboBox にイベントリスナーを追加
        row.list.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>)e.getSource();
            String selected = (String)cb.getSelectedItem();
            
            int newAmount = 0; // 新しい金額
            
            // 選択された項目名に応じて金額を設定
            if (item.zei.equals("所得税")) { // 学歴選択の場合
                if ("未選択".equals(selected)) newAmount = 0;
                else if ("高卒".equals(selected)) newAmount = model.addShotoku(191500);
                else if ("短大・専門学校卒".equals(selected)) newAmount = model.addShotoku(223000);
                else if ("大卒".equals(selected)) newAmount = model.addShotoku(239700);
                else if ("大学院卒".equals(selected)) newAmount = model.addShotoku(285100);
                

            } else if (item.zei.equals("自動車税")) { // 車選択の場合
                if ("自動車なし".equals(selected)) newAmount = 0;
                else if ("軽自動車".equals(selected)) newAmount = 10800; // 仮の月額費用
                else if ("普通車".equals(selected)) newAmount = 29500;  // 仮の月額費用
                
                
            }
            
            // 選択に基づいて TextField の値を更新
            row.amountField.setText(String.valueOf(newAmount));
            
            // モデル側にも値を反映させる（calculateTotal() 実行時にまとめて反映させる設計だが、即時反映が必要な場合はここで実行）
            item.amount = newAmount;
        });

        JPanel line = new JPanel();
        line.add(row.check);
        line.add(row.name);
        line.add(row.list);
        line.add(new JLabel("￥"));
        line.add(row.amountField);
        parent.add(line);
        rows2.add(row);

    }
 
    /* コントローラ経由で呼ばれる：行を 1 行追加 */
    public void addItemRow() {
        // モデル側にも追加
        model.addItem();
        // 画面にも追加
        ExpenseItem last = model.getItems().get(model.getItems().size() - 1);
        addRowUI((JPanel) ((JScrollPane)getContentPane().getComponent(0)).getViewport().getView(), last);
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
            it.amount = 0;  // 数字以外なら 0
            }
        }
//  修正点: ExpenseCombo の値をモデルに反映 
        ArrayList<ExpenseCombo> items2 = model.getItems2();
        for (int i = 0; i < rows2.size(); i++) {
            RowCombo r = rows2.get(i);
            ExpenseCombo it = items2.get(i);
            
            it.checked = r.check.isSelected();
            // it.zei は JLabel なので更新不要
            // it.name (選択肢の配列) は更新不要
            
            try {
            it.amount = Integer.parseInt(r.amountField.getText());
            } catch (NumberFormatException e) {
            it.amount = 0; // 数字以外なら 0
            }
        }
        // モデルで計算 (コントローラ側で呼び出し済だが安全のため)
        model.calculateTotal();
        totalLabel.setText("計算結果: ￥" + model.getTotal());
    }
}