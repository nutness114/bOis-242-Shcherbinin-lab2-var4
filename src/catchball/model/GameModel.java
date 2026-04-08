package catchball.model;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameModel {

    private static final double BALL_RADIUS = 30.0;
    private static final double DEFAULT_FIELD_WIDTH = 800.0;
    private static final double DEFAULT_FIELD_HEIGHT = 520.0;
    private static final double BASE_STEP = 4.0;
    private static final double MAX_STEP = 9.0;
    private static final double FLEE_DISTANCE = 140.0;
    private static final long TICK_DELAY_MS = 40L;

    private final Random random = new Random();

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final BooleanProperty gameActive = new SimpleBooleanProperty(true);
    private final IntegerProperty ballSpeed = new SimpleIntegerProperty(40);
    private final DoubleProperty ballX = new SimpleDoubleProperty();
    private final DoubleProperty ballY = new SimpleDoubleProperty();
    private final IntegerProperty comboStreak = new SimpleIntegerProperty(0);
    private final IntegerProperty lastAward = new SimpleIntegerProperty(0);
    private final DoubleProperty fieldWidth = new SimpleDoubleProperty(DEFAULT_FIELD_WIDTH);
    private final DoubleProperty fieldHeight = new SimpleDoubleProperty(DEFAULT_FIELD_HEIGHT);

    private Timer timer;
    private double velocityX;
    private double velocityY;

    public GameModel() {
        centerBall();
        randomizeVelocity();
    }

    public void start() {
        stop();
        timer = new Timer("CatchBallTimer", true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (gameActive.get()) {
                        updateBallPosition();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, TICK_DELAY_MS, TICK_DELAY_MS);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void newGame() {
        score.set(0);
        comboStreak.set(0);
        lastAward.set(0);
        gameActive.set(true);
        centerBall();
        randomizeVelocity();
    }

    public void togglePause() {
        gameActive.set(!gameActive.get());
    }

    public void registerHit() {
        if (!gameActive.get()) {
            return;
        }

        int streak = comboStreak.get() + 1;
        comboStreak.set(streak);

        int points = 1 + ((streak - 1) / 3);
        lastAward.set(points);
        score.set(score.get() + points);

        moveBallToRandomPosition();
        randomizeVelocity();
    }

    public void registerMiss() {
        if (!gameActive.get()) {
            return;
        }

        comboStreak.set(0);
        lastAward.set(0);
    }

    public void reactToCursor(double mouseX, double mouseY) {
        if (!gameActive.get()) {
            return;
        }

        double dx = ballX.get() - mouseX;
        double dy = ballY.get() - mouseY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= 0.001 || distance > FLEE_DISTANCE) {
            return;
        }

        double escapeForce = (FLEE_DISTANCE - distance) / FLEE_DISTANCE;
        double safeDistance = Math.max(distance, 1.0);
        double escapeX = dx / safeDistance;
        double escapeY = dy / safeDistance;
        double newStep = Math.min(MAX_STEP, BASE_STEP + escapeForce * 5.0);

        velocityX = escapeX * newStep;
        velocityY = escapeY * newStep;

        double nextX = ballX.get() + velocityX * 2.0;
        double nextY = ballY.get() + velocityY * 2.0;
        ballX.set(clampX(nextX));
        ballY.set(clampY(nextY));
    }

    public void setFieldSize(double width, double height) {
        if (width <= 2 * BALL_RADIUS || height <= 2 * BALL_RADIUS) {
            return;
        }

        fieldWidth.set(width);
        fieldHeight.set(height);
        ballX.set(clampX(ballX.get()));
        ballY.set(clampY(ballY.get()));
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public BooleanProperty gameActiveProperty() {
        return gameActive;
    }

    public IntegerProperty ballSpeedProperty() {
        return ballSpeed;
    }

    public DoubleProperty ballXProperty() {
        return ballX;
    }

    public DoubleProperty ballYProperty() {
        return ballY;
    }

    public IntegerProperty comboStreakProperty() {
        return comboStreak;
    }

    public IntegerProperty lastAwardProperty() {
        return lastAward;
    }

    public double getBallRadius() {
        return BALL_RADIUS;
    }

    private void updateBallPosition() {
        double nextX = ballX.get() + velocityX;
        double nextY = ballY.get() + velocityY;

        if (nextX <= BALL_RADIUS || nextX >= fieldWidth.get() - BALL_RADIUS) {
            velocityX = -velocityX;
            nextX = ballX.get() + velocityX;
        }

        if (nextY <= BALL_RADIUS || nextY >= fieldHeight.get() - BALL_RADIUS) {
            velocityY = -velocityY;
            nextY = ballY.get() + velocityY;
        }

        if (random.nextDouble() < 0.08) {
            velocityX += random.nextDouble() * 1.4 - 0.7;
            velocityY += random.nextDouble() * 1.4 - 0.7;
            normalizeVelocity();
        }

        ballX.set(clampX(nextX));
        ballY.set(clampY(nextY));
    }

    private void moveBallToRandomPosition() {
        ballX.set(BALL_RADIUS + random.nextDouble() * (fieldWidth.get() - 2 * BALL_RADIUS));
        ballY.set(BALL_RADIUS + random.nextDouble() * (fieldHeight.get() - 2 * BALL_RADIUS));
    }

    private void centerBall() {
        ballX.set(fieldWidth.get() / 2.0);
        ballY.set(fieldHeight.get() / 2.0);
    }

    private void randomizeVelocity() {
        double angle = random.nextDouble() * Math.PI * 2.0;
        double step = BASE_STEP + random.nextDouble() * 2.0;
        velocityX = Math.cos(angle) * step;
        velocityY = Math.sin(angle) * step;
        normalizeVelocity();
    }

    private void normalizeVelocity() {
        double length = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (length < 0.001) {
            velocityX = BASE_STEP;
            velocityY = BASE_STEP;
            length = Math.sqrt(2.0 * BASE_STEP * BASE_STEP);
        }

        double limitedLength = Math.max(BASE_STEP, Math.min(MAX_STEP, length));
        velocityX = velocityX / length * limitedLength;
        velocityY = velocityY / length * limitedLength;
        ballSpeed.set((int) Math.round(limitedLength * 10.0));
    }

    private double clampX(double x) {
        return Math.max(BALL_RADIUS, Math.min(fieldWidth.get() - BALL_RADIUS, x));
    }

    private double clampY(double y) {
        return Math.max(BALL_RADIUS, Math.min(fieldHeight.get() - BALL_RADIUS, y));
    }
}
