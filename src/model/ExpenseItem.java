package model;

/* 1 つの支出項目を表すクラス (M: Model) */
public class ExpenseItem {
    public String name;   // 項目名
    public int amount;    // 金額
    public boolean checked; // チェックボックス状態

    public ExpenseItem(String name, int amount, boolean checked) {
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }
}