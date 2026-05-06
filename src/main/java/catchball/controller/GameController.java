package catchball.controller;

import catchball.model.GameModel;
import catchball.view.GamePanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameController {

    private final GameModel gameModel = new GameModel();
    private final JFrame frame = new JFrame("Лови шарик");
    private final JLabel scoreLabel = new JLabel();
    private final JLabel comboLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JLabel speedLabel = new JLabel();
    private final GamePanel gamePanel = new GamePanel(gameModel);
    private final Timer hitDelayTimer;
    private final Timer gameLoopTimer;

    public GameController() {
        hitDelayTimer = new Timer(220, event -> {
            gameModel.registerHit();
            refreshView();
        });
        hitDelayTimer.setRepeats(false);

        gameLoopTimer = new Timer(40, event -> {
            gameModel.updateBallPosition();
            refreshView();
        });

        configureFrame();
        bindEvents();
        gameModel.newGame();
        refreshView();
    }

    public void show() {
        gameModel.setFieldSize(gamePanel.getWidth(), gamePanel.getHeight());
        gameLoopTimer.start();
        frame.setVisible(true);
    }

    private void configureFrame() {
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(820, 640));
        frame.setSize(960, 720);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(createHeaderPanel(), BorderLayout.NORTH);
        frame.add(createCenterPanel(), BorderLayout.CENTER);
        frame.add(createFooterLabel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        headerPanel.setBackground(new Color(255, 250, 245));

        JLabel titleLabel = new JLabel("Лови шарик");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(122, 31, 36));

        JLabel hintLabel = new JLabel("Один клик по шарику даёт очки, двойной клик ставит игру на паузу. Промах сбрасывает серию.");
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hintLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hintLabel.setForeground(new Color(109, 76, 65));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setOpaque(false);

        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(154, 31, 41));

        comboLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        comboLabel.setForeground(new Color(168, 77, 0));

        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        statusLabel.setForeground(new Color(55, 71, 79));

        speedLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        speedLabel.setForeground(new Color(69, 90, 100));

        JButton newGameButton = new JButton("Новая игра");
        newGameButton.setFocusPainted(false);
        newGameButton.setBackground(new Color(217, 54, 62));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.addActionListener(event -> {
            hitDelayTimer.stop();
            gameModel.newGame();
            refreshView();
        });

        infoPanel.add(scoreLabel);
        infoPanel.add(comboLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createHorizontalStrut(24));
        infoPanel.add(speedLabel);
        infoPanel.add(newGameButton);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(hintLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(infoPanel);
        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(255, 245, 238));
        container.setBorder(BorderFactory.createEmptyBorder(8, 16, 16, 16));
        container.add(gamePanel, BorderLayout.CENTER);
        return container;
    }

    private JLabel createFooterLabel() {
        JLabel footerLabel = new JLabel("Бонусный вариант: каждые 3 точных попадания подряд увеличивают награду на 1 очко.");
        footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        footerLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        footerLabel.setForeground(new Color(109, 76, 65));
        footerLabel.setOpaque(true);
        footerLabel.setBackground(new Color(255, 250, 245));
        return footerLabel;
    }

    private void bindEvents() {
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent event) {
                hitDelayTimer.stop();
                gameLoopTimer.stop();
            }
        });

        gamePanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                gameModel.setFieldSize(gamePanel.getWidth(), gamePanel.getHeight());
                refreshView();
            }
        });

        gamePanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                gameModel.reactToCursor(event.getX(), event.getY());
                refreshView();
            }
        });

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (gamePanel.isBallHit(event.getX(), event.getY())) {
                    handleBallClick(event);
                } else if (event.getClickCount() == 1) {
                    hitDelayTimer.stop();
                    gameModel.registerMiss();
                    refreshView();
                }
            }
        });
    }

    private void handleBallClick(MouseEvent event) {
        if (event.getClickCount() >= 2) {
            hitDelayTimer.stop();
            gameModel.togglePause();
            refreshView();
            return;
        }

        hitDelayTimer.restart();
    }

    private void refreshView() {
        scoreLabel.setText("Счёт: " + gameModel.getScore());
        statusLabel.setText(gameModel.isGameActive() ? "Статус: игра активна" : "Статус: пауза");
        speedLabel.setText("Скорость шарика: " + gameModel.getBallSpeed());

        int streak = gameModel.getComboStreak();
        int award = gameModel.getLastAward();
        if (streak == 0) {
            comboLabel.setText("Серия: нет");
        } else if (award > 1) {
            comboLabel.setText("Серия: " + streak + " подряд, +" + (award - 1) + " бонус");
        } else {
            comboLabel.setText("Серия: " + streak + " подряд");
        }

        gamePanel.repaint();
    }
}
