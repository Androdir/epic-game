package Main.entities;

import Main.Controller;
import Main.DataHandler;
import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class EnemyBodyObject {

    private Image texture;
    private float x = 500, y = 500;
    private float velX = 0, velY = 0;
    private float health;
    private int width = DataHandler.enemyIdle.getWidth(null), height = DataHandler.enemyIdle.getHeight(null);
    public boolean isFacingLeft = false;
    public Controller controller;
    private Game.ENEMY_ID enemyID;

    public Point startPoint;
    public Point endPoint;

    public EnemyBodyObject(Controller controller, Game.ENEMY_ID enemyID, float x, float y) {
        this.controller = controller;
        this.enemyID = enemyID;
        setX(x);
        setY(y);
        setTexture(DataHandler.enemyIdle);
        this.startPoint = new Point((int) x, (int) y + getHeight() / 2);
        this.endPoint = new Point((int) (startPoint.x  + (Game.ENEMY_MOVEMENT_SPEED * 60 * 4)), startPoint.y);
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (isFacingLeft) {
            g2d.drawImage(getTexture(), (int) (getX() + getTexture().getWidth(null)), (int) getY(), -getTexture().getWidth(null), getTexture().getHeight(null), null);
        }
        else {
            g2d.drawImage(getTexture(), (int) getX(), (int) getY(), getTexture().getWidth(null), getTexture().getHeight(null), null);
        }
    }

    public void tick() {

        if (!isOnGround()) {
            setVelY(getVelY() + Game.GRAVITY_VEL);
        }

        if (enemyID != Game.ENEMY_ID.RANGED) {
            x += velX;
        }
        y += velY;

        if (getHealth() <= 0) {
            delete();
        }

        if (y > Game.HEIGHT) {
            delete();
        }

        checkCollision();
    }

    public void checkCollision() {


        if (getID() == Game.ENEMY_ID.MELEE || getID() == Game.ENEMY_ID.BOSS) {
            if (getBounds().contains(endPoint)) {
                isFacingLeft = true;
                setVelX(-Game.ENEMY_MOVEMENT_SPEED);
                return;
            }

            if (getBounds().contains(startPoint)) {
                isFacingLeft = false;
                setVelX(Game.ENEMY_MOVEMENT_SPEED);
                return;
            }
        }

        for (Tile tile : controller.tiles) {

            if (isCollidingDown(tile) && tile.isTangible) {
                setVelY(0);
                setY((float) (tile.getBounds().getY() - getHeight()));
            }

            if (isCollidingUp(tile) && tile.isTangible) {
                setVelY(0);
                setY((float) (tile.getBounds().getY() + tile.getBounds().getHeight()));
            }

            if (isCollidingRight(tile) && tile.isTangible) {
                if (getID() == Game.ENEMY_ID.MELEE) {
                    setVelX(-Game.ENEMY_MOVEMENT_SPEED);
                    isFacingLeft = true;
                }
                endPoint = new Point((int) getX() + getWidth(), (int) getY() + getHeight() / 2);
                setX((float) (tile.getBounds().getX() - getWidth() - 5));
            }

            if (isCollidingLeft(tile) && tile.isTangible) {
                if (getID() == Game.ENEMY_ID.MELEE) {
                    setVelX(Game.ENEMY_MOVEMENT_SPEED);
                    isFacingLeft = false;
                }
                setX((float) (tile.getBounds().getX() + tile.getBounds().getWidth()));
            }
        }
    }

    public boolean isGoingToFall() {
        Rectangle2D rectangle = getBounds();
        if (isFacingLeft) {
            rectangle.setRect(rectangle.getX() - 40, rectangle.getY() + rectangle.getHeight() + 10, rectangle.getWidth() / 2, rectangle.getHeight() / 2);
            for (Tile tile : controller.tiles) {
                if (rectangle.intersects(tile.getBounds())) {
                    return false;
                }
            }
        }
        else {
            rectangle.setRect(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight() + 10, rectangle.getWidth() / 2, rectangle.getHeight() / 2);
            for (Tile tile : controller.tiles) {
                if (rectangle.intersects(tile.getBounds())) {
                    return false;
                }
            }
        }

        return true;
    }

    public Image getTexture() {
        return texture;
    }

    public void setTexture(Image texture) {
        this.texture = texture;
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

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelX() {
        return velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public Rectangle2D getBoundsUp() { // top part of hitbox
        float rectX = x + 5;
        float rectY = y + velY;
        float rectW = width - 5;
        float rectH = 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Rectangle2D getBoundsLeft() { // left part of hitbox
        float rectX = x + velX;
        float rectY = y + 5;
        float rectW = 5;
        float rectH = height - 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Rectangle2D getBoundsDown() { // bottom part of hitbox
        float rectX = x + 5;
        float rectY = y + height + velY;
        float rectW = width - 5;
        float rectH = 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Rectangle2D getBoundsRight() { // right part of hitbox
        float rectX = x + + width + velX;
        float rectY = y + 5;
        float rectW = 5;
        float rectH = height - 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Rectangle2D getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public boolean isOnGround() {
        if (y >= Game.HEIGHT - 40) {
            return true;
        }

        for (Tile block : controller.tiles) {
            Rectangle tempRect = (Rectangle) block.getBounds();
            tempRect.y = (int) (tempRect.y - getBounds().getHeight());

            if (tempRect.intersects(getBoundsUp()) && block.isTangible) {
                return true;
            }
        }

        return false;
    }

    public float getStartingX() {
        return startPoint.x;
    }

    public boolean isCollidingLeft(Tile tile) {
        if (getBoundsLeft().intersects(tile.getBounds())) {
            return true;
        }

        return false;
    }

    public boolean isCollidingRight(Tile tile) {
        if (getBoundsRight().intersects(tile.getBounds())) {
            return true;
        }

        return false;
    }

    public boolean isCollidingUp(Tile tile) {
        if (getBoundsUp().intersects(tile.getBounds())) {
            return true;
        }

        return false;
    }

    public boolean isCollidingDown(Tile tile) {
        if (getBoundsDown().intersects(tile.getBounds())) {
            return true;
        }

        return false;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public Game.ENEMY_ID getID() {
        return enemyID;
    }

    public void setID(Game.ENEMY_ID enemyID) {
        this.enemyID = enemyID;
    }

    public void delete() {
        controller.removeEntity(this);
    }

}
