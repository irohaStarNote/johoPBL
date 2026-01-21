package controller;

import java.io.*;
import javax.swing.JOptionPane;

import model.ExpenseModel;
import view.TitleView;
import view.CarSelectView;
import view.InputView;
import view.DetailView;
import view.IncomeView;

public class AppController {

    private ExpenseModel model;
    private TitleView titleView;
    private IncomeView incomeView;
    private CarSelectView carSelectView;
    private InputView inputView;
    private DetailView detailView;

    public AppController() {
        model = new ExpenseModel(this);
    }

    private void closeAll() {
        if (titleView != null) titleView.dispose();
        if (incomeView != null) incomeView.dispose();   // ★ 追加
        if (inputView != null) inputView.dispose();
        if (detailView != null) detailView.dispose();

        titleView = null;
        incomeView = null;
        inputView = null;
        detailView = null;
    }


    public void showTitle() {
        closeAll();
        titleView = new TitleView(this);
        titleView.setVisible(true);
    }

    public void showInput() {
        closeAll();
        inputView = new InputView(this, model);
        inputView.setVisible(true);
    }

    public void loadPreviousData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("lastdata.dat"))) {
            model = (ExpenseModel) in.readObject();
            showInput();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "前回データが見つかりません", "エラー", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("lastdata.dat"))) {
            out.writeObject(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        saveData();
        model = new ExpenseModel(this);
        showInput();
    }

    public void showIncome() {
        closeAll();
        if (incomeView == null) {
            incomeView = new IncomeView(this, model);
        }
        incomeView.setVisible(true);
    }

    // 収入入力画面から自動車選択へ
    public void showCarSelect() {
        if (titleView != null) titleView.dispose();
        carSelectView = new CarSelectView(this);
        carSelectView.setVisible(true);
    }

    public void showDetail() {
        inputView.updateTotal();
        saveData();
        closeAll();
        detailView = new DetailView(this, model);
        detailView.setVisible(true);
    }

    public void backToInputView() {
        closeAll();
        inputView = new InputView(this, model);
        inputView.setVisible(true);
    }

    public void backToTitleView() {
        closeAll();
        showTitle();
    }

    public void backToIncomeView() {
        closeAll();
        if (incomeView == null) {
            incomeView = new IncomeView(this, model);
        }
        incomeView.setVisible(true);
    }


    public void onAddItem() {
        inputView.addItemRow();
    }

    public void onCalculate() {
        inputView.updateTotal();
        saveData();
    }

    public void onShowDetail() {
        showDetail();
    }

    // 自動車税が決定した時の処理
    public void onCarSelected(int taxAmount) {
        model.setCarTax(taxAmount); // モデルに保存
        carSelectView.dispose();
        showInput(); // 入力画面へ
    }
}