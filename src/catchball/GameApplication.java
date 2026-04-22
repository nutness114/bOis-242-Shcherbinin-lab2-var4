package catchball;

import catchball.controller.GameController;

import javax.swing.SwingUtilities;

public final class GameApplication {

    private GameApplication() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            controller.show();
        });
    }
}
