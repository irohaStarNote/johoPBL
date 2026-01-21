package model;

import java.io.Serializable;

public class ExpenseItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int amount;
    private boolean checked;

    public ExpenseItem(String name, int amount, boolean checked) {
        this.name = name;
        this.amount = amount;
        this.checked = checked;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}