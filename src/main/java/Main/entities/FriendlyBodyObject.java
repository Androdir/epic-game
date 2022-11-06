package Main.entities;

import Main.Controller;
import Main.DataHandler;
import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

public abstract class FriendlyBodyObject {

    private Controller controller;

    public int dmgReductionUpgrades = 0;

    private float health;
    private float x, y;
    public boolean isShooting = false;
    private static int WIDTH = 125, HEIGHT = 125;
    private float velX, velY;

    public boolean isFacingRight = true;
    public boolean isMovingRight = false;
    public boolean isMovingLeft = false;

    public boolean canDoubleJump = false;
    public int jumps = 0;

    public boolean canTakeBlockDamage = true;
    public boolean canTakeMeleeDamage = true;

    private final Timer spikeDamageTimer = new Timer();
    private final Timer enemyDamageTimer = new Timer();

    public FriendlyBodyObject(Controller controller) {
        this.controller = controller;
        setHealth(100);

        spikeDamageTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                canTakeBlockDamage = true;
            }
        }, 500, 500);

        enemyDamageTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                canTakeMeleeDamage = true;
            }
        }, 300, 300);

    }

    public void render(Graphics g) {
        if (isMovingLeft) {
            g.drawImage(DataHandler.playerWalkLeft, (int) x, (int) y, null);
        }
        else if (isMovingRight) {
            g.drawImage(DataHandler.playerWalkRight, (int) x, (int) y, null);
        }
        else if (isFacingRight) {
            g.drawImage(DataHandler.playerIdleRight, (int) x, (int) y, null);
        }
        else if (!isFacingRight) {
            g.drawImage(DataHandler.playerIdleLeft, (int) x, (int) y, null);
        }
    }

    public void tick() {
        if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
            if (!isOnGround()) {
                setVelY(getVelY() + Game.GRAVITY_VEL);
            }
        }

        checkBlockCollision();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public boolean isOnGround() {
        if (y >= Game.HEIGHT - 40) {
            return true;
        }

        for (Tile block : controller.tiles) {
            Rectangle2D tempRect = block.getBounds();
            tempRect.setRect(tempRect.getX(), tempRect.getY() - getHeight(), tempRect.getWidth(), tempRect.getHeight());

            if (getBoundsUp().intersects(tempRect) && block.isTangible) {
                return true;
            }
        }

        return false;
    }

    public Shape getBoundsUp() { // top part of hitbox
        float rectX = x + 10;
        float rectY = y + velY;
        float rectW = WIDTH - 10;
        float rectH = 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Shape getBoundsLeft() { // left part of hitbox
        float rectX = x + velX * 2;
        float rectY = y + 10;
        float rectW = 5;
        float rectH = HEIGHT - 10;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Shape getBoundsDown() { // bottom part of hitbox
        float rectX = x + 10;
        float rectY = y + HEIGHT + velY;
        float rectW = WIDTH - 10;
        float rectH = 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Shape getBoundsRight() { // right part of hitbox
        float rectX = x + WIDTH + velX;
        float rectY = y + 10;
        float rectW = 5;
        float rectH = HEIGHT - 10;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Rectangle2D getBounds() {
        return new Rectangle((int) x, (int) y, WIDTH, HEIGHT);
    }

    public int getHeight() {
        return HEIGHT;
    }

    public int getWidth() {
        return WIDTH;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void checkBlockCollision() {

        for (Tile tile : controller.tiles) {

            if (getBoundsDown().intersects(tile.getBounds()) && tile.isTangible) {
                setVelY(0);
                setY((float) (tile.getBounds().getY() - getHeight()));
            }

            if (getBoundsUp().intersects(tile.getBounds()) && tile.isTangible) {
                setVelY(0);
                setY((float) (tile.getBounds().getY() + tile.getBounds().getHeight()));
            }

            if (getBoundsRight().intersects(tile.getBounds()) && tile.isTangible) {
                setVelX(0);
                setX((float) (tile.getBounds().getX() - getWidth() - 5));
            }

            if (getBoundsLeft().intersects(tile.getBounds()) && tile.isTangible) {
                setVelX(0);
                setX((float) (tile.getBounds().getX() + tile.getBounds().getWidth()));
            }

            if (getBounds().intersects(tile.getBounds()) && tile.getID() == Game.BLOCK_ID.SPIKE) {
                if (canTakeBlockDamage) {
                    takeDamage(10);
                    canTakeBlockDamage = false;
                }
            }

            if (getBounds().intersects(tile.getBounds()) && tile.getID() == Game.BLOCK_ID.DOOR_OPEN && controller.getEnemies().size() <= 0) {
                Game.LEVEL++;
                DataHandler.loadMap(Game.LEVEL);

                if (getHealth() + Game.HEAL_AFTER_LEVEL > 100) {
                    setHealth(100);
                }
                else {
                    setHealth(getHealth() + Game.HEAL_AFTER_LEVEL);
                }
            }

            if (getBounds().intersects(tile.getBounds()) && (tile.getID() == Game.BLOCK_ID.ACID_FULL || tile.getID() == Game.BLOCK_ID.ACID_TOP)) {
                if (canTakeBlockDamage) {
                    takeDamage(25);
                    canTakeBlockDamage = false;
                }
            }
        }
    }

    public void delete() {
        spikeDamageTimer.cancel();
        enemyDamageTimer.cancel();
        controller.removeEntity(this);
    }

    public void takeDamage(float damage) {
        damage = damage * (1 - (0.1f * dmgReductionUpgrades));

        if (getHealth() - damage > 0) {
            setHealth(getHealth() - damage);
        }
        else {
            setHealth(0);
        }
    }
}
