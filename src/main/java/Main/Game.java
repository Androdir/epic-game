package Main;

import Main.entities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Game {

    private static final GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    // Scale makes the map bigger easily
    public static final int SCALE = 2;
    public static final int WIDTH = graphicsDevice.getDisplayMode().getWidth() * SCALE;
    public static final int HEIGHT = graphicsDevice.getDisplayMode().getHeight();

    public static final String TITLE = "epic game";
    public static int SCREEN = 1;
    public static int LEVEL = 1;
    public static final int MAX_LEVEL = 10;
    public static boolean GAME_OVER = false;

    // Game constants
    public static final float GRAVITY_VEL = 0.50f;
    public static final float ENEMY_MOVEMENT_SPEED = 2.5f;
    public static final long RANGED_ENEMY_COOLDOWN = 1500;
    public static final float RANGED_ENEMY_BULLET_DAMAGE = 6;
    public static final float MELEE_ENEMY_DAMAGE = 10;
    public static final float PLAYER_BULLET_SPEED = 8.5f;
    public static final float MELEE_ENEMY_HEALTH = 30;
    public static final float RANGED_ENEMY_HEALTH = 15;
    public static final float FIRE_BARREL_DAMAGE = 45;
    public static final float BOSS_BULLET_DAMAGE = 35;
    public static final float BOSS_BULLET_RANGE = (float) WIDTH / 3;
    public static final long BOSS_BULLET_COOLDOWN = 3300;
    public static final float MOVING_PLATFORM_SPEED = 1.5f;

    public static final float JUMP_POWER = 10.5f;
    public static final float PLAYER_BULLET_DAMAGE = 4;
    public static final float PLAYER_MOVEMENT_SPEED = 5f;
    public static final float HEAL_AFTER_LEVEL = 25;
    public static final long PLAYER_SHOOT_COOLDOWN = 150;


    private static final float defaultHeight = 1080;
    private static final float defaultWidth = 3840;
    public static final float percentageHeight = Game.HEIGHT / defaultHeight;
    public static final float percentageWidth = Game.WIDTH / defaultWidth;

    public JTextArea settingsTextArea = new JTextArea();

    public enum BLOCK_ID {
        FULL_BLOCK,
        FULL_TOP_BLOCK,
        HALF_BLOCK,
        HALF_TOP_BLOCK,
        ACID_FULL,
        ACID_TOP,
        SPIKE,
        FIRE_BARREL,
        BOX,
        DOOR_LOCKED,
        DOOR_OPEN,
        EXPLOSION,
        MOVING_PLATFORM,
        UPGRADE_TERMINAL
    }

    public enum ENEMY_ID {
        MELEE,
        RANGED,
        SNIPER,
        BOSS
    }

    public enum GAME_STATE {
        LEVEL_EDITOR,
        MAIN_MENU,
        MAIN_GAME,
    }

    public enum UPGRADE {
        DMG_REDUCTION,
        JUMP,
        HEAL_AFTER_LEVEL,
        ATTACK_SPEED,
        DAMAGE
    }

    public static GAME_STATE gameState = GAME_STATE.MAIN_MENU;

    public final GamePanel gamePanel;
    private final JFrame window;
    public final UpgradeMenu upgradeMenu;

    private static final int TICKS = 60;

    private final ArrayList<FriendlyBodyObject> friendlyEntities = new ArrayList<>();
    private final ArrayList<FriendlyBulletObject> friendlyBullets = new ArrayList<>();
    private final ArrayList<Tile> tiles = new ArrayList<>();
    private final ArrayList<EnemyBodyObject> enemyEntities = new ArrayList<>();
    private final ArrayList<EnemyBulletObject> enemyBullets = new ArrayList<>();

    private final Controller controller;
    private final Menu menu;
    public Player player;

    private void tick() {
        if (gameState == GAME_STATE.MAIN_GAME || gameState == GAME_STATE.LEVEL_EDITOR) {
            controller.tick();
        }
    }

    private void render() {
        gamePanel.render();
    }

    public static boolean yes = true;


    public Game() {

        controller = new Controller(this, friendlyEntities, friendlyBullets, tiles, enemyEntities, enemyBullets);
        new DataHandler(controller);
        upgradeMenu = new UpgradeMenu();
        player = new Player(controller, upgradeMenu);
        controller.addEntity(player);

        DataHandler.loadMap(LEVEL);

        window = new JFrame();
        window.setUndecorated(true);

        LevelEditorMouseEvents mouseEvent = new LevelEditorMouseEvents(controller);

        window.addMouseListener(upgradeMenu);
        window.addMouseListener(mouseEvent);
        window.addMouseMotionListener(mouseEvent);
        window.addMouseWheelListener(mouseEvent);

        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setTitle(TITLE);
        window.setLocation(0, 0);

        window.setBackground(Color.WHITE);
        window.setIconImage(new ImageIcon("D:\\Users\\dru\\OneDrive\\Downloads\\icon.png").getImage());

        final Dimension dimension = new Dimension(WIDTH / 2, HEIGHT);
        window.setMinimumSize(dimension);
        window.setMaximumSize(dimension);
        window.setPreferredSize(dimension);

        menu = new Menu(this);
        gamePanel = new GamePanel(controller, this, menu, upgradeMenu);

        window.addMouseListener(menu);
        window.addMouseMotionListener(menu);

        window.setLayout(null);

        settingsTextArea.setText("ok");
        settingsTextArea.setBackground(Color.ORANGE);
        settingsTextArea.setLayout(null);
        settingsTextArea.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        settingsTextArea.setLineWrap(true);
        settingsTextArea.setWrapStyleWord(true);
        settingsTextArea.setHighlighter(null);
        settingsTextArea.setEditable(false);
        settingsTextArea.setLocation(Game.WIDTH / 4, 500);
        settingsTextArea.setSize(500, 500);

        window.add(settingsTextArea);
        window.add(gamePanel);

        window.pack();
        window.setVisible(true);
        settingsTextArea.setVisible(false);

        final Timer gameLoop = new Timer();
        gameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                render();
                tick();
            }
        }, 0, 1000 / TICKS);
    }

    public static void main(String[] args) {
        new Game();
    }

    public Graphics getGraphics() {
        return window.getBufferStrategy().getDrawGraphics();
    }

    public static void drawCenteredString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        int y = (fm.getAscent() + (h - fm.getAscent() + (fm.getDescent())) / 2);
        g.drawString(s, x, y);
    }

    public static void drawStringInRectangle(String string, Rectangle rectangle, boolean resizeRectangle, Graphics g) {
        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        int textWidth = (int) g.getFont().getStringBounds(string, frc).getWidth();
        int textHeight = fm.getHeight() + fm.getDescent();
        int stringX, stringY;
        if (resizeRectangle) {
            rectangle.setBounds(new Rectangle((int) rectangle.getX(), (int) rectangle.getY(), textWidth + 50, textHeight / 2));
            int rectWidth = (int) rectangle.getWidth();
            int rectHeight = (int) rectangle.getHeight();
            stringX = (int) (rectangle.getX() - 25 + (rectWidth - textWidth));
            stringY = (int) (rectangle.getY() + (rectHeight - 75 + textHeight) / 2 - fm.getDescent());
        }
        else {
            int rectWidth = (int) rectangle.getWidth();
            int rectHeight = (int) rectangle.getHeight();
            stringX = ((int) rectangle.getX() + rectWidth) - textWidth - ((rectWidth - textWidth)/ 2);
            stringY = (int) rectangle.getY() + ((rectHeight - fm.getHeight()) / 2) + fm.getAscent();
        }

        g.drawString(string, stringX, stringY);
    }
}
