package model;

import java.io.Serializable;

public class ExpenseCombo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String zei;
    private String[] name;
    private int amount;
    private boolean checked;

    public ExpenseCombo(String zei, String[] name, int amount, boolean checked) {
        this.zei = zei;
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }

    public String getZei() { return zei; }
    public String[] getName() { return name; }
    public int getAmount() { return amount; }
    public boolean isChecked() { return checked; }

    public void setZei(String zei) { this.zei = zei; }
    public void setName(String[] name) { this.name = name; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setChecked(boolean checked) { this.checked = checked; }
}