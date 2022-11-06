package Main;

import Main.entities.EnemyBodyObject;
import Main.entities.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GamePanel extends JPanel {

    public Graphics g = null;
    private Controller controller;
    private Menu menu;
    private UpgradeMenu upgradeMenu;
    private Game game;

    public GamePanel(Controller controller, Game game, Menu menu, UpgradeMenu upgradeMenu) {
        this.controller = controller;
        this.game = game;
        this.menu = menu;
        this.upgradeMenu = upgradeMenu;

        setOpaque(false);
        setBorder(null);
        setLayout(null);
        setDoubleBuffered(true);
        setBackground(Color.BLACK);
        setBounds(0, 0, Game.WIDTH / 2, Game.HEIGHT);

        registerLevelEditorKeyBindings();
        registerMainKeyBindings();
        registerPlayerKeyBindings();
    }

    public void render() {
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (Game.gameState == Game.GAME_STATE.MAIN_GAME || Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
            BufferedImage canvas = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_ARGB);

            if (this.g == null) {
                this.g = g;
            }

            final Graphics2D graphics = canvas.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.drawImage(DataHandler.background, 0, 0, null);

            if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                graphics.setColor(Color.WHITE);
                graphics.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
                graphics.drawString("Controls:", 50, 80);
                graphics.drawString("R - load map", 50, 140);
                graphics.drawString("Y - save map", 50, 200);
                graphics.drawString("I - change block type", 50, 260);
                graphics.drawString("Arrow keys - move block", 50, 320);
                graphics.drawString("Del - delete block/enemy", 50, 380);
                graphics.drawString("M - add melee enemy", 50, 440);
                graphics.drawString("N - add ranged enemy", 50, 500);
                graphics.drawString("C - create block", 50, 560);
                graphics.drawString("L - next level", 50, 620);
                graphics.drawString("K - previousi level", 50, 680);
                graphics.drawString("B - add boss", 50, 740);
                graphics.drawString("LEVEL: " + Game.LEVEL, 1000, 200);
            }

            controller.render(graphics);

            if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                if (Game.SCREEN == 1) {
                    g.drawImage(canvas.getSubimage(0, 0, Game.WIDTH / 2, Game.HEIGHT), 0, 0, null);
                }
                else {
                    g.drawImage(canvas.getSubimage(Game.WIDTH / 2, 0, Game.WIDTH / 2, Game.HEIGHT), 0 , 0, null);
                }
            }

            else if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {

                int x = (int) controller.getPlayer().getX() - Game.WIDTH / (2 * Game.SCALE);
                int y = 0;
                int width = (int) (Game.WIDTH / (Game.SCALE) + controller.getPlayer().getX());
                int height = Game.HEIGHT;

                if (x < 0) {
                    x = 0;
                }

                if (x > Game.WIDTH / Game.SCALE) {
                    x = Game.WIDTH / Game.SCALE;
                }

                if (x + width > Game.WIDTH / Game.SCALE) {
                    width = Game.WIDTH / Game.SCALE;
                }

                if (x + width < 0) {
                    width = 0;
                }

                g.drawImage(canvas.getSubimage(x, y, width, height), 0, 0, null);
            }

            g.drawImage(DataHandler.playerHealth, 50, 50, null);
            try {
                g.drawImage(DataHandler.healthBar.getSubimage(184, 0, Math.round((float) ((DataHandler.healthBar.getWidth() - 184) * controller.getPlayer().getHealth()) / 100), DataHandler.healthBar.getHeight()), 187, 50, null);
            } catch (Exception e) {

            }

            graphics.dispose();
        }
        else if (Game.gameState == Game.GAME_STATE.MAIN_MENU) {
            menu.render(g);
        }

        upgradeMenu.render(g);

        g.dispose();
    }

    @Override
    public Graphics getGraphics() {
        return this.g;
    }


    public void addKeyBinding(JPanel panel, String key, int keyCode, AbstractAction action) {
        panel.getInputMap().put(KeyStroke.getKeyStroke(keyCode, 0), key);
        panel.getActionMap().put(key, action);
    }

    public void addKeyBinding(JPanel panel, String key, KeyStroke keyStroke, AbstractAction action) {
        panel.getInputMap().put(keyStroke, key);
        panel.getActionMap().put(key, action);
    }

    private void registerLevelEditorKeyBindings() {

        // Save map
        addKeyBinding(this, "saveMap", KeyEvent.VK_Y, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    try {
                        DataHandler.saveMap();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Load map (temporary)
        addKeyBinding(this, "loadMap", KeyEvent.VK_R, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    DataHandler.loadMap(Game.LEVEL);
                }
            }
        });

        // Change block type
        addKeyBinding(this, "cycleBlockType", KeyEvent.VK_I, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            switch (block.getID()) {
                                case FULL_BLOCK -> block.setID(Game.BLOCK_ID.FULL_TOP_BLOCK);
                                case FULL_TOP_BLOCK -> block.setID(Game.BLOCK_ID.HALF_BLOCK);
                                case HALF_BLOCK -> block.setID(Game.BLOCK_ID.HALF_TOP_BLOCK);
                                case HALF_TOP_BLOCK -> block.setID(Game.BLOCK_ID.ACID_FULL);
                                case ACID_FULL -> block.setID(Game.BLOCK_ID.ACID_TOP);
                                case ACID_TOP -> block.setID(Game.BLOCK_ID.SPIKE);
                                case SPIKE -> block.setID(Game.BLOCK_ID.FIRE_BARREL);
                                case FIRE_BARREL -> block.setID(Game.BLOCK_ID.BOX);
                                case BOX -> block.setID(Game.BLOCK_ID.DOOR_LOCKED);
                                case DOOR_LOCKED -> block.setID(Game.BLOCK_ID.MOVING_PLATFORM);
                                case MOVING_PLATFORM -> block.setID(Game.BLOCK_ID.UPGRADE_TERMINAL);
                                case UPGRADE_TERMINAL -> block.setID(Game.BLOCK_ID.FULL_BLOCK);
                            }
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            switch (block.getID()) {
                                case FULL_BLOCK -> block.setID(Game.BLOCK_ID.FULL_TOP_BLOCK);
                                case FULL_TOP_BLOCK -> block.setID(Game.BLOCK_ID.HALF_BLOCK);
                                case HALF_BLOCK -> block.setID(Game.BLOCK_ID.HALF_TOP_BLOCK);
                                case HALF_TOP_BLOCK -> block.setID(Game.BLOCK_ID.ACID_FULL);
                                case ACID_FULL -> block.setID(Game.BLOCK_ID.ACID_TOP);
                                case ACID_TOP -> block.setID(Game.BLOCK_ID.SPIKE);
                                case SPIKE -> block.setID(Game.BLOCK_ID.FIRE_BARREL);
                                case FIRE_BARREL -> block.setID(Game.BLOCK_ID.BOX);
                                case BOX -> block.setID(Game.BLOCK_ID.DOOR_LOCKED);
                                case DOOR_LOCKED -> block.setID(Game.BLOCK_ID.MOVING_PLATFORM);
                                case MOVING_PLATFORM -> block.setID(Game.BLOCK_ID.UPGRADE_TERMINAL);
                                case UPGRADE_TERMINAL -> block.setID(Game.BLOCK_ID.FULL_BLOCK);
                            }
                            break;
                        }
                    }
                }
            }
        });

        // Controls to make map editing easier
        addKeyBinding(this, "moveBlockLeft", KeyEvent.VK_LEFT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setX(block.getX() - 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setX(block.getX() - 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockRight", KeyEvent.VK_RIGHT, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setX(block.getX() + 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setX(block.getX() + 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockUp", KeyEvent.VK_UP, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setY(block.getY() - 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setY(block.getY() - 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockDown", KeyEvent.VK_DOWN, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setY(block.getY() + 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setY(block.getY() + 1);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockLeftBig", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setX(block.getX() - 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setX(block.getX() - 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockRightBig", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setX(block.getX() + 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setX(block.getX() + 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockUpBig", KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setY(block.getY() - 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setY(block.getY() - 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "moveBlockDownBig", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.setY(block.getY() + 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.setY(block.getY() + 5);
                            block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "deleteBlock", KeyEvent.VK_DELETE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    int x = (int) mousePos.getX();
                    int y = (int) mousePos.getY();

                    for (Tile block : controller.tiles) {
                        if (block.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            block.delete(false);
                            break;
                        }
                        else if (block.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            block.delete(false);
                            break;
                        }
                    }

                    for (EnemyBodyObject enemy : controller.enemyEntities) {
                        if (enemy.getBounds().contains(x, y) && Game.SCREEN == 1) {
                            enemy.delete();
                            break;
                        }
                        else if (enemy.getBounds().contains(x + (Game.WIDTH / 2), y) && Game.SCREEN == 2) {
                            enemy.delete();
                            break;
                        }
                    }
                }
            }
        });

        addKeyBinding(this, "addMeleeEnemy", KeyEvent.VK_M, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    float x;
                    float y = (int) mousePos.getY();

                    if (Game.SCREEN == 1) {
                        x = (int) mousePos.getX();
                    }
                    else {
                        x = (int) mousePos.getX() + Game.WIDTH / 2;
                    }

                    controller.addEntity(new MeleeEnemy(controller, x, y));
                }
            }
        });

        addKeyBinding(this, "addRangedEnemy", KeyEvent.VK_N, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    float x;
                    float y = (int) mousePos.getY();

                    if (Game.SCREEN == 1) {
                        x = (int) mousePos.getX();
                    }
                    else {
                        x = (int) mousePos.getX() + Game.WIDTH / 2;
                    }

                    controller.addEntity(new RangedEnemy(controller, x, y));
                }
            }
        });

        addKeyBinding(this, "addSniperEnemy", KeyEvent.VK_J, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    float x;
                    float y = (int) mousePos.getY();

                    if (Game.SCREEN == 1) {
                        x = (int) mousePos.getX();
                    }
                    else {
                        x = (int) mousePos.getX() + Game.WIDTH / 2;
                    }

                    controller.addEntity(new SniperEnemy(controller, x, y));
                }
            }
        });

        addKeyBinding(this, "addBoss", KeyEvent.VK_B, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    float x;
                    float y = (int) mousePos.getY();

                    if (Game.SCREEN == 1) {
                        x = (int) mousePos.getX();
                    }
                    else {
                        x = (int) mousePos.getX() + Game.WIDTH / 2;
                    }

                    controller.addEntity(new Boss(controller, x, y));
                }
            }
        });

        addKeyBinding(this, "addBlock", KeyEvent.VK_C, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    Point mousePos = GamePanel.this.getMousePosition();
                    float x;
                    float y = (int) mousePos.getY();

                    if (Game.SCREEN == 1) {
                        x = (int) mousePos.getX();
                    }
                    else {
                        x = (int) mousePos.getX() + Game.WIDTH / 2;
                    }

                    controller.addEntity(new Block(controller, game, x, y, Game.WIDTH / 25, Game.WIDTH / 25, Game.BLOCK_ID.FULL_BLOCK));
                }
            }
        });

        addKeyBinding(this, "nextlevel", KeyEvent.VK_L, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.LEVEL >= Game.MAX_LEVEL) {
                    Game.LEVEL = 1;
                }
                else {
                    Game.LEVEL++;
                }
                DataHandler.loadMap(Game.LEVEL);
            }
        });

        addKeyBinding(this, "previousLevel", KeyEvent.VK_K, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.LEVEL <= 1) {
                    Game.LEVEL = 10;
                }
                else {
                    Game.LEVEL--;
                }
                DataHandler.loadMap(Game.LEVEL);
            }
        });
    }

    private void registerPlayerKeyBindings() {

        addKeyBinding(this, "moveDownPress", KeyEvent.VK_S, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    controller.getPlayer().setVelY(Game.PLAYER_MOVEMENT_SPEED * 2);
                }
            }
        });

        addKeyBinding(this, "moveDownRelease", KeyStroke.getKeyStroke("released S"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
                    controller.getPlayer().setVelY(0);
                }
            }
        });
        addKeyBinding(this, "moveLeftPress", KeyEvent.VK_A, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                    controller.getPlayer().moveLeft();
                }
                else {
                    controller.getPlayer().setVelX(-Game.PLAYER_MOVEMENT_SPEED * 2);
                }
            }
        });

        addKeyBinding(this, "moveLeftRelease", KeyStroke.getKeyStroke("released A"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getPlayer().isMovingLeft = false;
                controller.getPlayer().setVelX(0);
            }
        });

        addKeyBinding(this, "moveRightPress", KeyEvent.VK_D, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                    controller.getPlayer().moveRight();
                }
                else {
                    controller.getPlayer().setVelX(Game.PLAYER_MOVEMENT_SPEED * 2);
                }
            }
        });

        addKeyBinding(this, "moveRightRelease", KeyStroke.getKeyStroke("released D"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getPlayer().isMovingRight = false;
                controller.getPlayer().setVelX(0);
            }
        });

        addKeyBinding(this, "jumpPress", KeyEvent.VK_W, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                    controller.getPlayer().jump();
                }
                else {
                    controller.getPlayer().setVelY(-Game.PLAYER_MOVEMENT_SPEED * 2);
                }
            }
        });

        addKeyBinding(this, "jumpRelease", KeyStroke.getKeyStroke("released W"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                    controller.getPlayer().canDoubleJump = true;
                }
                else {
                    controller.getPlayer().setVelY(0);
                }
            }
        });

        addKeyBinding(this, "shoot", KeyEvent.VK_SPACE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getPlayer().isShooting = true;
            }
        });

        addKeyBinding(this, "toggleAttack", KeyEvent.VK_G, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player player = controller.getPlayer();
                player.isShootingBullets = !player.isShootingBullets;
            }
        });

        addKeyBinding(this, "shootRelease", KeyStroke.getKeyStroke("released SPACE"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getPlayer().isShooting = false;
            }
        });

        addKeyBinding(this, "heal", KeyEvent.VK_H, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.getPlayer().setHealth(100);
            }
        });
    }

    private void registerMainKeyBindings() {
        addKeyBinding(this, "menu", KeyEvent.VK_ESCAPE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Game.gameState = Game.GAME_STATE.MAIN_MENU;
            }
        });
    }

}
