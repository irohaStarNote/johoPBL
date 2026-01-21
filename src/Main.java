public class Main {
    public static void main(String[] args) {

        // Swing アプリは EDT（イベントディスパッチスレッド）で起動する
        javax.swing.SwingUtilities.invokeLater(() -> {
            controller.AppController controller = new controller.AppController();
            controller.showTitle();  // タイトル画面へ
        });
    }
}