package model;

public class ExpenseCombo {
    public String zei;
    public String[] name;
    public int amount;
    public boolean checked;
    
    public ExpenseCombo(String zei,String[] name, int amount, boolean checked) {
        this.zei=zei;
        this.name = name;
        this.amount = amount;  
        this.checked = checked;
    }
}
