package catchball.model;

import java.util.Random;

public class GameModel {

    private static final double BALL_RADIUS = 30.0;
    private static final double DEFAULT_FIELD_WIDTH = 800.0;
    private static final double DEFAULT_FIELD_HEIGHT = 520.0;
    private static final double BASE_STEP = 4.0;
    private static final double MAX_STEP = 9.0;
    private static final double FLEE_DISTANCE = 140.0;

    private final Random random = new Random();

    private int score;
    private boolean gameActive = true;
    private int ballSpeed = 40;
    private double ballX;
    private double ballY;
    private int comboStreak;
    private int lastAward;
    private double fieldWidth = DEFAULT_FIELD_WIDTH;
    private double fieldHeight = DEFAULT_FIELD_HEIGHT;
    private double velocityX;
    private double velocityY;

    public GameModel() {
        centerBall();
        randomizeVelocity();
    }

    public void newGame() {
        score = 0;
        comboStreak = 0;
        lastAward = 0;
        gameActive = true;
        centerBall();
        randomizeVelocity();
    }

    public void togglePause() {
        gameActive = !gameActive;
    }

    public void registerHit() {
        if (!gameActive) {
            return;
        }

        comboStreak++;
        int points = 1 + ((comboStreak - 1) / 3);
        lastAward = points;
        score += points;

        moveBallToRandomPosition();
        randomizeVelocity();
    }

    public void registerMiss() {
        if (!gameActive) {
            return;
        }

        comboStreak = 0;
        lastAward = 0;
    }

    public void reactToCursor(double mouseX, double mouseY) {
        if (!gameActive) {
            return;
        }

        double dx = ballX - mouseX;
        double dy = ballY - mouseY;
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

        ballX = clampX(ballX + velocityX * 2.0);
        ballY = clampY(ballY + velocityY * 2.0);
        normalizeVelocity();
    }

    public void setFieldSize(double width, double height) {
        if (width <= 2 * BALL_RADIUS || height <= 2 * BALL_RADIUS) {
            return;
        }

        fieldWidth = width;
        fieldHeight = height;
        ballX = clampX(ballX);
        ballY = clampY(ballY);
    }

    public void updateBallPosition() {
        if (!gameActive) {
            return;
        }

        double nextX = ballX + velocityX;
        double nextY = ballY + velocityY;

        if (nextX <= BALL_RADIUS || nextX >= fieldWidth - BALL_RADIUS) {
            velocityX = -velocityX;
            nextX = ballX + velocityX;
        }

        if (nextY <= BALL_RADIUS || nextY >= fieldHeight - BALL_RADIUS) {
            velocityY = -velocityY;
            nextY = ballY + velocityY;
        }

        if (random.nextDouble() < 0.08) {
            velocityX += random.nextDouble() * 1.4 - 0.7;
            velocityY += random.nextDouble() * 1.4 - 0.7;
            normalizeVelocity();
        }

        ballX = clampX(nextX);
        ballY = clampY(nextY);
    }

    public int getScore() {
        return score;
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public int getBallSpeed() {
        return ballSpeed;
    }

    public double getBallX() {
        return ballX;
    }

    public double getBallY() {
        return ballY;
    }

    public int getComboStreak() {
        return comboStreak;
    }

    public int getLastAward() {
        return lastAward;
    }

    public double getBallRadius() {
        return BALL_RADIUS;
    }

    private void moveBallToRandomPosition() {
        ballX = BALL_RADIUS + random.nextDouble() * (fieldWidth - 2 * BALL_RADIUS);
        ballY = BALL_RADIUS + random.nextDouble() * (fieldHeight - 2 * BALL_RADIUS);
    }

    private void centerBall() {
        ballX = fieldWidth / 2.0;
        ballY = fieldHeight / 2.0;
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
        ballSpeed = (int) Math.round(limitedLength * 10.0);
    }

    private double clampX(double x) {
        return Math.max(BALL_RADIUS, Math.min(fieldWidth - BALL_RADIUS, x));
    }

    private double clampY(double y) {
        return Math.max(BALL_RADIUS, Math.min(fieldHeight - BALL_RADIUS, y));
    }
}
