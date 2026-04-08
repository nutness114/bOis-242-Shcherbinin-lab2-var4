package catchball.controller;

import catchball.model.GameModel;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {

    @FXML
    private Pane gamePane;

    @FXML
    private Circle ball;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label comboLabel;

    @FXML
    private Label speedLabel;

    @FXML
    private Button newGameButton;

    private final GameModel gameModel = new GameModel();
    private final PauseTransition hitDelay = new PauseTransition(Duration.millis(220));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ball.setRadius(gameModel.getBallRadius());
        ball.setFill(new RadialGradient(
                0,
                0.2,
                0.35,
                0.35,
                0.8,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#ffd0b5")),
                new Stop(0.45, Color.web("#ff7a59")),
                new Stop(1.0, Color.web("#d9363e"))
        ));
        ball.setStroke(Color.web("#8f1d2c"));
        ball.setStrokeWidth(3.0);

        ball.centerXProperty().bind(gameModel.ballXProperty());
        ball.centerYProperty().bind(gameModel.ballYProperty());

        scoreLabel.textProperty().bind(Bindings.concat("Счёт: ", gameModel.scoreProperty()));
        statusLabel.textProperty().bind(Bindings.when(gameModel.gameActiveProperty())
                .then("Статус: игра активна")
                .otherwise("Статус: пауза"));
        comboLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    int streak = gameModel.comboStreakProperty().get();
                    int award = gameModel.lastAwardProperty().get();
                    if (streak == 0) {
                        return "Серия: нет";
                    }
                    if (award > 1) {
                        return "Серия: " + streak + " подряд, +" + (award - 1) + " бонус";
                    }
                    return "Серия: " + streak + " подряд";
                },
                gameModel.comboStreakProperty(),
                gameModel.lastAwardProperty()
        ));
        speedLabel.textProperty().bind(Bindings.concat("Скорость шарика: ", gameModel.ballSpeedProperty()));

        hitDelay.setOnFinished(event -> gameModel.registerHit());

        gamePane.widthProperty().addListener((obs, oldValue, newValue) ->
                gameModel.setFieldSize(newValue.doubleValue(), gamePane.getHeight()));
        gamePane.heightProperty().addListener((obs, oldValue, newValue) ->
                gameModel.setFieldSize(gamePane.getWidth(), newValue.doubleValue()));

        Platform.runLater(() -> {
            gameModel.setFieldSize(gamePane.getWidth(), gamePane.getHeight());
            gameModel.newGame();
            gameModel.start();
        });
    }

    @FXML
    private void handleBallClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            hitDelay.stop();
            gameModel.togglePause();
            event.consume();
            return;
        }

        if (event.getClickCount() == 1) {
            hitDelay.playFromStart();
            event.consume();
        }
    }

    @FXML
    private void handlePaneClick(MouseEvent event) {
        if (event.getTarget() == ball || event.getClickCount() != 1) {
            return;
        }

        hitDelay.stop();
        gameModel.registerMiss();
    }

    @FXML
    private void handleMouseMoved(MouseEvent event) {
        gameModel.reactToCursor(event.getX(), event.getY());
    }

    @FXML
    private void handleNewGame() {
        hitDelay.stop();
        gameModel.newGame();
    }

    public void shutdown() {
        hitDelay.stop();
        gameModel.stop();
    }
}
