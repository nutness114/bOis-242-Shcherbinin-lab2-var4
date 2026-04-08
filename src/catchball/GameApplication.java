package catchball;

import catchball.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/catchball/view/GameView.fxml"));
        Parent root = loader.load();

        GameController controller = loader.getController();

        Scene scene = new Scene(root, 960, 720);
        primaryStage.setTitle("Лови шарик");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(820);
        primaryStage.setMinHeight(640);
        primaryStage.setOnCloseRequest(event -> controller.shutdown());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
