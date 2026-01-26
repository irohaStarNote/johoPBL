package model;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支出項目 1つ分のデータ
 * - name: 項目名（例: 食費）
 * - amount: 金額（円/月）
 * - checked: 合計に含めるかどうか
 */
public class ExpenseItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String name;
    private int amount;
    private boolean checked;

    public ExpenseItem(String name, int amount, boolean checked) {
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }

    public String getName() { return name; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
