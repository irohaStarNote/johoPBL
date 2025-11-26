package controller;

import model.ExpenseModel;
import view.TitleView;
import view.InputView;
import view.DetailView;

/* 画面遷移をまとめて管理するクラス (C: Controller) */
public class AppController {

    private ExpenseModel model;       // 計算ロジックを保持
    private TitleView titleView;
    private InputView inputView;
    private DetailView detailView;

    public AppController() {
        model = new ExpenseModel(this);   // モデルにコントローラ参照を渡す
    }

    // ===== 画面表示メソッド =====
    public void showTitle() {
        titleView = new TitleView(this);
        titleView.setVisible(true);
    }

    public void showInput() {
        titleView.dispose();              // Window を閉じる
        inputView = new InputView(this, model);
        inputView.setVisible(true);
    }
    public void reset() {
        inputView.dispose();              // Window を閉じる
        inputView = new InputView(this, model);
        inputView.setVisible(true);
    }

    public void showDetail() {
        inputView.dispose();
        detailView = new DetailView(this, model);
        detailView.setVisible(true);
    }

    // ==== クリックイベントから呼ばれるメソッド ====
    public void onAddItem() {
        inputView.addItemRow();           // 入力行を 1 行増やす
    }

    public void onCalculate() {
        model.calculateTotal();           // 合計を計算
        inputView.updateTotal();          // 画面に反映
    }

    public void onShowDetail() {
        showDetail();                     // 詳細画面へ
    }
}