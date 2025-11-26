/* アプリのエントリポイント */
public class Main {
    public static void main(String[] args) {
        // まずコントローラを作成 → 画面を順に表示させる
        controller.AppController controller = new controller.AppController();
        controller.showTitle();   // タイトル画面へ
    }
}