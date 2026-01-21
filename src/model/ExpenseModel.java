package model;

import java.io.Serializable;
import java.util.ArrayList;

public class ExpenseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<ExpenseItem> items;
    private ArrayList<ExpenseCombo> items2;

    private int total;      // 支出合計
    private int income;     // 月収
    private int shotoku;    // 所得税（月額）

    public ExpenseModel(controller.AppController ctrl) {
        items = new ArrayList<>();
        items2 = new ArrayList<>();

        // -------------------------
        // デフォルト支出項目
        // -------------------------
        items.add(new ExpenseItem("食費", 0, true));
        items.add(new ExpenseItem("光熱費", 0, true));
        items.add(new ExpenseItem("家賃", 40000, true));
        items.add(new ExpenseItem("社会保険", 40938, true));
        items.add(new ExpenseItem("公的年金", 24888, true));
        items.add(new ExpenseItem("健康保険", 13158, true));
        items.add(new ExpenseItem("介護保険", 1509, true));
        items.add(new ExpenseItem("火災保険", 5199, true));
        items.add(new ExpenseItem("自動車損害賠償責任保険", 832, false));
        items.add(new ExpenseItem("任意自動車保険", 11615, false));
        items.add(new ExpenseItem("所得税", 0, true));
        items.add(new ExpenseItem("その他",0 , false));

        // コンボ項目
        // items2.add(new ExpenseCombo("自動車税",
        //         new String[]{"自動車なし", "軽自動車", "普通車"},
        //         0, false));

        calculateTotal();
    }

    // -------------------------
    // 収入（月収）
    // -------------------------
    public void setIncome(int income) {
        this.income = income;
    }

    public int getIncome() {
        return income;
    }

    // -------------------------
    // 所得税（月額）を計算してセット
    // -------------------------
    public void addItem() {
        items.add(new ExpenseItem("新規項目", 0, false));
    }

    public void calculateShotoku() {
        int annual = income * 12;

        int tax;
        if (annual <= 1950000) tax = (int)(annual * 0.05);
        else if (annual <= 3300000) tax = (int)(annual * 0.10 - 97500);
        else if (annual <= 6950000) tax = (int)(annual * 0.20 - 427500);
        else if (annual <= 9000000) tax = (int)(annual * 0.23 - 636000);
        else if (annual <= 18000000) tax = (int)(annual * 0.33 - 1536000);
        else if (annual <= 40000000) tax = (int)(annual * 0.40 - 2796000);
        else tax = (int)(annual * 0.45 - 4796000);

        this.shotoku = tax / 12; // 月額に変換

        // ★ ExpenseCombo の「所得税」にも反映する
        for (ExpenseItem item : items) {
            if (item.getName().equals("所得税")) {
                item.setAmount(this.shotoku);
            }
        }
    }

    public int getShotoku() {
        return shotoku;
    }

    // -------------------------
    // 支出項目
    // -------------------------
    public ArrayList<ExpenseItem> getItems() { return items; }
    public ArrayList<ExpenseCombo> getItems2() { return items2; }
    public int getTotal() { return total; }

    // -------------------------
    // 支出合計を計算
    // -------------------------
    public void calculateTotal() {
        int sum = 0;

        for (ExpenseItem item : items)
            if (item.isChecked()) sum += item.getAmount();

        for (ExpenseCombo item : items2)
            if (item.isChecked()) sum += item.getAmount();

        sum += shotoku; // ← 所得税を支出に含める

        total = sum;
    }

    // 自動車税を項目として設定する
    public void setCarTax(int amount) {
        // 既存のリストに「自動車税」があれば更新、なければ追加
        for (ExpenseItem item : items) {
            if (item.getName().equals("自動車税")) {
                item.setAmount(amount);
                item.setChecked((amount > 0)); // 0円でなければチェックを入れる
                return;
            }
        }
        // 新規追加（初期リストにない場合）
        items.add(new ExpenseItem("自動車税", amount, amount > 0));
    }
}