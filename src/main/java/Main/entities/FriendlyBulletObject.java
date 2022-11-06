package Main.entities;

import Main.Controller;
import Main.DataHandler;
import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class FriendlyBulletObject {

    private float x, y;
    private final int WIDTH = 18, HEIGHT = 12;
    public float maxDistance;
    private Image texture;
    private float startingX;
    private boolean direction;
    private float velX;
    public Controller controller;

    public FriendlyBulletObject(Controller controller, float x, float y, boolean direction) {
        this.controller = controller;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.startingX = x;
        setTexture(DataHandler.playerBullet);

        if (direction) {
            setVelX(Game.PLAYER_BULLET_SPEED);
        }
        else {
            setVelX(-Game.PLAYER_BULLET_SPEED);
        }
    }

    public void tick() {
        x += getVelX();

        for (Tile tile : controller.tiles) {
            if (tile.getBounds().intersects(getBounds()) && tile.isTangible) {
                delete();
            }
        }

        if (getX() < 0 || getX() > Game.WIDTH) {
            delete();
        }

        if (getVelX() > 0) {
            if (getX() - startingX > maxDistance) {
                delete();
            }
        }

        else if (getVelX() < 0) {
            if (startingX - getX() > maxDistance) {
                delete();
            }
        }

        for (Tile tile : controller.tiles) {
            if (tile.getID() == Game.BLOCK_ID.FIRE_BARREL) {
                if (tile.getBounds().intersects(getBounds())) {
                    delete();
                    tile.delete(true);
                }
            }
        }
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        if (direction) {
            g2d.drawImage(getTexture(), (int) (getX() + getTexture().getWidth(null)), (int) getY(), -getTexture().getWidth(null), getTexture().getHeight(null), null);
        }
        else {
            g2d.drawImage(getTexture(), (int) getX(), (int) getY(), getTexture().getWidth(null), getTexture().getHeight(null), null);
        }
    }

    public Image getTexture() {
        return texture;
    }

    public void setTexture(Image texture) {
        this.texture = texture;
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public Rectangle2D getBounds() {
        return new Rectangle((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }

    public void delete() {
        controller.removeEntity(this);
    }
}
