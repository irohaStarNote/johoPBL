package model;

import java.util.ArrayList;

/* 支出リストの保持と合計計算を行うクラス */
public class ExpenseModel {

    private ArrayList<ExpenseItem> items;
    private int total;                       // 合計金額
    private controller.AppController ctrl;   // コントローラ参照

    public ExpenseModel(controller.AppController ctrl) {
        this.ctrl = ctrl;
        items = new ArrayList<>();
        // 初期行を 3 つ用意
        items.add(new ExpenseItem("家賃", 0, true));
        items.add(new ExpenseItem("食費", 0, true));
        items.add(new ExpenseItem("光熱費", 0, true));
    }

    // 新規行を追加
    public void addItem() {
        items.add(new ExpenseItem("新規項目", 0, false));
    }

    // ゲッター
    public ArrayList<ExpenseItem> getItems() {
        return items;
    }

    public int getTotal() {
        return total;
    }

    /* 合計金額を再計算する */
    public void calculateTotal() {
        int sum = 0;
        for (ExpenseItem item : items) {
            if (item.checked) {
                sum += item.amount;
            }
        }
        total = sum;
    }
}