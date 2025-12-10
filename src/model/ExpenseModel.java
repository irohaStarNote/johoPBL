package model;

import java.util.ArrayList;

/* 支出リストの保持と合計計算を行うクラス */
public class ExpenseModel {
    private ArrayList<ExpenseItem> items;
    private ArrayList<ExpenseCombo> items2;
    private int total;  // 合計金額
    private controller.AppController ctrl;  // コントローラ参照

    public ExpenseModel(controller.AppController ctrl) {
        this.ctrl = ctrl;
        items = new ArrayList<>();
        items2=  new ArrayList<>();
        // 初期行を3つ用意
        items.add(new ExpenseItem("食費", 0, false));
        items.add(new ExpenseItem("光熱費", 0, false));
        items.add(new ExpenseItem("家賃",40000 , false));
        items.add(new ExpenseItem("社会保険", 40938, false)); 
        items.add(new ExpenseItem("公的年金", 24888, false));
        items.add(new ExpenseItem("健康保険", 13158, false));
        items.add(new ExpenseItem("介護保険", 1509, false)); 
        items.add(new ExpenseItem("火災保険",5199 , false)); 
        items.add(new ExpenseItem("自動車損害賠償責任保険", 832, false));
        items.add(new ExpenseItem("任意自動車保険", 11615, false)); 
        items2.add(new ExpenseCombo("所得税",new String[]{"未選択","高卒","専門卒","大卒","大学院卒"}, 0, false));
        items2.add(new ExpenseCombo("自動車税",new String[]{"自動車なし","軽自動車","普通車"}, 0, false));
        
    }

    // 新規行を追加
    public void addItem() {
        items.add(new ExpenseItem("新規項目", 0, false));  
    }

    // 所得税を追加
    public int addShotoku(int gesshuu) {
        int nennshuu=gesshuu*16;
        int shotokuzei=0;
        if(nennshuu<3300000){
            shotokuzei+=(nennshuu*0.1-97500)/12;
        }
        else{
            shotokuzei+=(nennshuu*0.2-427500)/12;
        }
        return shotokuzei;   
    }


    // ゲッター
    public ArrayList<ExpenseItem> getItems() {
        return items;
    }
    public ArrayList<ExpenseCombo> getItems2(){
        return items2;
    }
    public int getTotal() {
        return total;
    }
 
    /* 合計金額を再計算する     */
    public void calculateTotal() {
        int sum = 0;
        for (ExpenseItem item : items) {
            if (item.checked) {
                sum += item.amount;
            }
        }
// 修正点: ExpenseCombo (コンボボックスの項目) の合計を追加
        for (ExpenseCombo item : items2) {
            if (item.checked) {
                sum += item.amount;
            }
        }
        total = sum;
    }
}