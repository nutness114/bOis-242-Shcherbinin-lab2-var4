package catchball.view;

import catchball.model.GameModel;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class GamePanel extends JPanel {

    private final GameModel gameModel;

    public GamePanel(GameModel gameModel) {
        this.gameModel = gameModel;
        setOpaque(false);
        setPreferredSize(new Dimension(800, 560));
    }

    public boolean isBallHit(int x, int y) {
        double dx = x - gameModel.getBallX();
        double dy = y - gameModel.getBallY();
        double radius = gameModel.getBallRadius();
        return dx * dx + dy * dy <= radius * radius;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint backgroundPaint = new GradientPaint(
                0,
                0,
                new Color(255, 255, 255, 235),
                0,
                getHeight(),
                new Color(255, 232, 217, 235)
        );
        g2.setPaint(backgroundPaint);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1.0, getHeight() - 1.0, 18, 18));

        g2.setColor(new Color(241, 180, 140));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3.0, getHeight() - 3.0, 18, 18));

        float radius = (float) gameModel.getBallRadius();
        float centerX = (float) gameModel.getBallX();
        float centerY = (float) gameModel.getBallY();

        RadialGradientPaint ballPaint = new RadialGradientPaint(
                centerX - radius * 0.2f,
                centerY - radius * 0.2f,
                radius,
                new float[]{0.0f, 0.45f, 1.0f},
                new Color[]{
                        new Color(255, 208, 181),
                        new Color(255, 122, 89),
                        new Color(217, 54, 62)
                }
        );

        Ellipse2D ball = new Ellipse2D.Double(centerX - radius, centerY - radius, radius * 2.0, radius * 2.0);
        g2.setPaint(ballPaint);
        g2.fill(ball);
        g2.setColor(new Color(143, 29, 44));
        g2.setStroke(new BasicStroke(3f));
        g2.draw(ball);

        g2.dispose();
    }
}
