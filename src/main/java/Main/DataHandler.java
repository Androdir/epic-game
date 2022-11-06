package Main;

import Main.entities.EnemyBodyObject;
import Main.entities.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static Main.Game.percentageHeight;
import static Main.Game.percentageWidth;

public class DataHandler {

    private static Controller controller;

    // images for upgrade menu
    public static Image
            healAfterLevel,
            damageReduction,
            attackSpeed,
            attackDamage,
            jumpBoost;

    // player textures
    public static Image
            playerJumpLeft,
            playerJumpRight,
            playerWalkLeft,
            playerWalkRight,
            playerIdleLeft,
            playerIdleRight,
            playerBullet,
            playerRocket;


    // explosion
    public static Image explosion;

    // enemy textures
    public static Image
            enemyIdle,
            enemyRun,
            enemyMeleeAttack,
            enemyRangedAttack,
            enemyProne,
            boss,
            bossRocket;

    // blocks + hud + baackground textures
    public static BufferedImage
            fullTopBlock,
            fullBlock,
            halfTopBlock,
            halfBlock,
            acidFull,
            acidTop,
            spike,
            background,
            healthBar,
            playerHealth,
            fireBarrel,
            box,
            doorOpen,
            doorLocked,
            menuBackground,
            upgradeTerminal,
            movingPlatform;

    public DataHandler(Controller controller) {

        DataHandler.controller = controller;

        playerJumpLeft = resizeImage(loadImage("player jump left.gif"), 125, 125);
        playerJumpRight = resizeImage(loadImage("player jump right.gif"), 125, 125);
        playerWalkLeft = resizeImage(loadImage("player walk left.gif"), 125, 125);
        playerWalkRight = resizeImage(loadImage("player walk right.gif"), 125, 125);
        playerIdleLeft = resizeImage(loadImage("player idle left.gif"), 125, 125);
        playerIdleRight = resizeImage(loadImage("player idle right.gif"), 125, 125);
        playerBullet = resizeImage(loadBufferedImage("bullet.png"), 18, 12);
        explosion = resizeImage(loadImage("explosion.gif"), 250, 250);

        fullTopBlock = loadBufferedImage("fullTopBlock.png");
        fullBlock = loadBufferedImage("fullBlock.png");
        halfTopBlock = loadBufferedImage("halfTopBlock.png");
        halfBlock = loadBufferedImage("halfBlock.png");
        acidFull = loadBufferedImage("acidFull.png");
        acidTop = loadBufferedImage("acidTop.png");
        spike = loadBufferedImage("spike.png");
        background = loadBufferedImage("background.png");
        healthBar = loadBufferedImage("health.png");
        playerHealth = loadBufferedImage("health bar.png");
        fireBarrel = loadBufferedImage("fire barrel.png");
        box = loadBufferedImage("box.png");
        doorOpen = loadBufferedImage("door open.png");
        doorLocked = loadBufferedImage("door locked.png");
        menuBackground = loadBufferedImage("menu background.png");
        playerRocket = loadBufferedImage("right rocket.png");
        boss = resizeImage(loadBufferedImage("boss.png"), 125, 125);
        bossRocket = loadBufferedImage("boss rocket.png");
        upgradeTerminal = loadBufferedImage("upgrade terminal.png");
        movingPlatform = loadBufferedImage("moving platform.png");

        enemyIdle = loadImage("enemy idle.gif");
        enemyRun = loadImage("enemy run.gif");
        enemyMeleeAttack = loadImage("enemy melee attack.gif");
        enemyRangedAttack = loadImage("enemy ranged attack.gif");
        enemyProne = loadImage("enemy prone.gif");

        healAfterLevel = loadImage("heal after level.png");

        // Extend background
        BufferedImage newBackgroundImage = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics backgroundGraphics = newBackgroundImage.createGraphics();
        for (int i = 0; i < newBackgroundImage.getWidth(); i += background.getWidth()) {
            backgroundGraphics.drawImage(background, i, 0, null);
        }
        backgroundGraphics.dispose();

        background = newBackgroundImage;

        // Extend menu background
        BufferedImage newMenuBackgroundImage = new BufferedImage(Game.WIDTH / Game.SCALE, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);

        Graphics menuBackgroundGraphics = newMenuBackgroundImage.createGraphics();
        for (int i = 0; i < newMenuBackgroundImage.getWidth(); i += menuBackground.getWidth()) {
            menuBackgroundGraphics.drawImage(menuBackground, i, 0, null);
        }
        menuBackgroundGraphics.dispose();

        menuBackground = newMenuBackgroundImage;
    }

    public static void generateMap() {
        // maybe add random generation later??
        for (int i = -38; i < Game.WIDTH; i += Game.WIDTH / 25) {
            controller.createBlock(i, Game.HEIGHT - (Game.WIDTH / 25), Game.WIDTH / 25, Game.WIDTH / 25, Game.BLOCK_ID.FULL_BLOCK);
            controller.createBlock(i, Game.HEIGHT - (2 * (Game.WIDTH / 25)), Game.WIDTH / 25, Game.WIDTH / 25, Game.BLOCK_ID.FULL_TOP_BLOCK);
        }
    }

    public static void loadMap(int level) {
        controller.getPlayer().setX(0);
        controller.getPlayer().setY(Game.HEIGHT - (2 * (Game.WIDTH / 25)));
        try {

            for (int i = 0; i < controller.enemyEntities.size(); i++) {
                while (i != controller.enemyEntities.size() && controller.enemyEntities.contains(controller.enemyEntities.get(i))) {
                    controller.enemyEntities.get(i).delete();
                }
            }

            for (int i = 0; i < controller.enemyBullets.size(); i++) {
                while (i != controller.enemyBullets.size() && controller.enemyBullets.contains(controller.enemyBullets.get(i))) {
                    controller.enemyBullets.get(i).delete();
                }
            }

            for (int i = 0; i < controller.friendlyBullets.size(); i++) {
                while (i != controller.friendlyBullets.size() && controller.friendlyBullets.contains(controller.friendlyBullets.get(i))) {
                    controller.removeEntity(controller.friendlyBullets.get(i));
                }
            }

            for (int i = 0; i < controller.tiles.size(); i++) {
                while (i != controller.tiles.size() && controller.tiles.contains(controller.tiles.get(i))) {
                    controller.removeEntity(controller.tiles.get(i));
                }
            }

            File blockData = new File("res/map data/level " + level + " blocks.txt");
            File enemyData = new File("res/map data/level " + level + " enemies.txt");

            if (!blockData.exists() || !enemyData.exists()) {
                boolean enemyFileCreated = blockData.createNewFile();
                boolean blockFileCreated = enemyData.createNewFile();

                if (enemyFileCreated && blockFileCreated) {
                    System.out.println("New files for level " + level + " have been created.");
                }

                generateMap();
                saveMap();
            }

            final BufferedReader blockDataReader = new BufferedReader(new FileReader("res/map data/level " + level + " blocks.txt"));
            final BufferedReader enemyDataReader = new BufferedReader(new FileReader("res/map data/level " + level + " enemies.txt"));

            String line;

            while ((line = blockDataReader.readLine()) != null) {
                final String[] splitLine = line.split(" ");
                final float x = Float.parseFloat(splitLine[0]) * percentageWidth;
                final float y = Float.parseFloat(splitLine[1]) * percentageHeight;
                final float width = Float.parseFloat(splitLine[2]) * percentageWidth;
                final float height = Float.parseFloat(splitLine[3]) * percentageHeight;
                final Game.BLOCK_ID blockID = Game.BLOCK_ID.valueOf(splitLine[4]);
                DataHandler.controller.createBlock(x, y, width, height, blockID);
            }

            while ((line = enemyDataReader.readLine()) != null) {
                final String[] splitLine = line.split(" ");
                final float x = Float.parseFloat(splitLine[0]) * percentageWidth;
                final float y = Float.parseFloat(splitLine[1]) * percentageWidth;
                final Game.ENEMY_ID enemyID = Game.ENEMY_ID.valueOf(splitLine[2]);
                if (enemyID == Game.ENEMY_ID.BOSS) {
                    DataHandler.controller.addEntity(new Boss(controller, x, y));
                }
                else if (enemyID == Game.ENEMY_ID.RANGED) {
                    DataHandler.controller.addEntity(new RangedEnemy(controller, x, y));
                }
                else if (enemyID == Game.ENEMY_ID.MELEE) {
                    DataHandler.controller.addEntity(new MeleeEnemy(controller, x, y));
                }
                else if (enemyID == Game.ENEMY_ID.SNIPER) {
                    DataHandler.controller.addEntity(new SniperEnemy(controller, x, y));
                }
            }

            blockDataReader.close();
            enemyDataReader.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void saveMap() throws IOException {
        final File blocksFile = new File("res/map data/level " + Game.LEVEL + " blocks.txt");
        final File enemiesFile = new File("res/map data/level " + Game.LEVEL + " enemies.txt");
        boolean deletedBlocks = blocksFile.delete();
        boolean createdBlocks = enemiesFile.createNewFile();
        boolean deletedEnemies = enemiesFile.delete();
        boolean createdEnemies = enemiesFile.createNewFile();

        final PrintWriter blocksDataWriter = new PrintWriter(new FileWriter("res/map data/level " + Game.LEVEL + " blocks.txt"));
        final PrintWriter enemiesDataWriter = new PrintWriter(new FileWriter("res/map data/level " + Game.LEVEL + " enemies.txt"));

        for (Tile block : controller.tiles) {
            if (block.getID() == Game.BLOCK_ID.HALF_BLOCK || block.getID() == Game.BLOCK_ID.HALF_TOP_BLOCK || block.getID() == Game.BLOCK_ID.SPIKE || block.getID() == Game.BLOCK_ID.MOVING_PLATFORM) {
                blocksDataWriter.write(block.getStartingPoint().getX() + " " + block.getStartingPoint().getY() + " " + block.getBounds().getWidth() + " " + block.getBounds().getHeight() * 2 + " " + block.getID() + "\n");
            }
            else {
                blocksDataWriter.write(block.getStartingPoint().getX() + " " + block.getStartingPoint().getY() + " " + block.getBounds().getWidth() + " " + block.getBounds().getHeight() + " " + block.getID() + "\n");
            }
        }

        for (EnemyBodyObject enemy : controller.enemyEntities) {
            enemiesDataWriter.write(enemy.getStartingX() + " " + enemy.getY() + " " + enemy.getID() + "\n");
        }

        blocksDataWriter.flush();
        blocksDataWriter.close();
        enemiesDataWriter.flush();
        enemiesDataWriter.close();
    }


    public Image loadImage(String path) {
        try {
            return new ImageIcon("res/textures/" + path).getImage();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public BufferedImage loadBufferedImage(String path) {
        try {
            return ImageIO.read(new File("res/textures/" + path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        Image tempImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage newBufferedImage = new BufferedImage(width, height, image.getType());

        Graphics2D g = newBufferedImage.createGraphics();
        g.drawImage(tempImage, 0, 0, null);
        g.dispose();

        return newBufferedImage;
    }

    private Image resizeImage(Image image, int width, int height) {
        return image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }
}
