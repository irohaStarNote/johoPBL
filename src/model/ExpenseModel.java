package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExpenseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 支出項目リスト（チェックされたものだけ合計に入る） */
    private final ArrayList<ExpenseItem> items = new ArrayList<>();

    private int total;              // 支出合計(円/月)
    private int income;             // 月収
    private String education;       // 学歴
    private String displacement;    // 車の排気量区分
    private int automobileTax;      // 自動車税(円/月)

    public ExpenseModel() {
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
        items.add(new ExpenseItem("自動車損害賠償責任保険", 832, true));
        items.add(new ExpenseItem("任意自動車保険", 11615, true));
        items.add(new ExpenseItem("所得税", 0, true));
        items.add(new ExpenseItem("自動車税", 0, true));

        recalculateAll();
    }

    // =========================================================
    // 入力値（収入）
    // =========================================================
    public void setIncome(int income) {
        this.income = Math.max(income, 0);
        recalculateAll();
    }

    public int getIncome() {
        return income;
    }

    /**
     * 収入から所得税（月額）を再計算します。
     * ※本アプリでは簡易的な税率テーブルを使用しています。
     */
    private void calculateIncomeTax() {
        int annual = income * 12;

        int tax;
        if (annual <= 1_950_000) tax = (int) (annual * 0.05);
        else if (annual <= 3_300_000) tax = (int) (annual * 0.10 - 97_500);
        else if (annual <= 6_950_000) tax = (int) (annual * 0.20 - 427_500);
        else if (annual <= 9_000_000) tax = (int) (annual * 0.23 - 636_000);
        else if (annual <= 18_000_000) tax = (int) (annual * 0.33 - 1_536_000);
        else if (annual <= 40_000_000) tax = (int) (annual * 0.40 - 2_796_000);
        else tax = (int) (annual * 0.45 - 4_796_000);

        // 所得税（円/月）
        int incomeTax = Math.max(tax / 12, 0);

        // 支出項目「所得税」に反映（1ヶ所で持つ）
        setAmountByName("所得税", incomeTax);
    }

    // =========================================================
    // 支出項目
    // =========================================================

    public List<ExpenseItem> getItems() {
        return items;
    }

    /** 自由入力項目を1つ増やす */
    public void addCustomItem() {
        items.add(new ExpenseItem("新規項目", 0, false));
        recalculateTotal();
    }

    public int getTotal() {
        return total;
    }

    /** チェック済みの項目だけを合計 */
    public void recalculateTotal() {
        int sum = 0;
        for (ExpenseItem item : items) {
            if (item.isChecked()) sum += Math.max(item.getAmount(), 0);
        }
        total = sum;
    }

    // =========================================================
    // 学歴・車（都市比較のキーとして利用）
    // =========================================================
    public void setEducation(String education) {
        this.education = education;
    }

    public String getEducation() {
        return education;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    public String getDisplacement() {
        return displacement;
    }

    public void setAutomobileTax(int automobileTax) {
        this.automobileTax = Math.max(automobileTax, 0);
        setAmountByName("自動車税", this.automobileTax);
        recalculateTotal();
    }

    // =========================================================
    // 再計算
    // =========================================================
    /** 収入変更など「派生値」が変わるときはここを呼ぶ */
    public void recalculateAll() {
        calculateIncomeTax();
        setAmountByName("自動車税", automobileTax);
        recalculateTotal();
    }

    private void setAmountByName(String name, int amount) {
        for (ExpenseItem item : items) {
            if (item.getName().equals(name)) {
                item.setAmount(amount);
                return;
            }
        }
    }
}
