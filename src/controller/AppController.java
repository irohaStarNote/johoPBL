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
        model = new ExpenseModel();
    }

    private void closeAll() {
        if (titleView != null) titleView.dispose();
        if (incomeView != null) incomeView.dispose();
        if (inputView != null) inputView.dispose();
        if (detailView != null) detailView.dispose();
        if (carSelectView != null) carSelectView.dispose();

        titleView = null;
        incomeView = null;
        inputView = null;
        detailView = null;
        carSelectView = null;
    }

    public void showTitle() {
        closeAll();
        titleView = new TitleView(this);
        titleView.setVisible(true);
    }

    public void showInput() {
        closeAll();
        inputView = new InputView(this, model);
        inputView.refreshUIFromModel();   // ★ Model → UI 同期
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

    public void showIncome() {
        closeAll();
        incomeView = new IncomeView(this, model);
        incomeView.setVisible(true);
    }

    public void showCarSelect() {
        closeAll();
        carSelectView = new CarSelectView(this, model);
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
        inputView.refreshUIFromModel();   // ★ 所得税など最新値を反映
        inputView.setVisible(true);
    }

    public void backToTitleView() {
        closeAll();
        showTitle();
    }

    public void backToIncomeView() {
        closeAll();
        incomeView = new IncomeView(this, model);
        incomeView.setVisible(true);
    }
    public void onShowDetail() {
        showDetail();
    }

    // public void onCarSelected(String name,int taxAmount) {
    //     model.setCarTax(name, taxAmount);
    //     if (carSelectView != null) carSelectView.dispose();
    //     showInput();
    // }
}