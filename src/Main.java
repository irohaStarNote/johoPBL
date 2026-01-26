public class Main {
    public static void main(String[] args) {

        // Swing 起動
        javax.swing.SwingUtilities.invokeLater(() -> {
            controller.AppController controller = new controller.AppController();
            controller.showTitle();  // タイトル画面へ
        });
    }
}