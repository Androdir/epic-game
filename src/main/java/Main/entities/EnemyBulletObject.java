package Main.entities;

import Main.Controller;
import Main.Game;

import java.awt.*;

public abstract class EnemyBulletObject {

    public float startingX;
    public float maxDisplacement;
    public float damage;

    private float x, y;
    private float width = 10, height = 10;
    private float velX = 0, velY = 0;

    public Controller controller;

    public EnemyBulletObject(Controller controller, float x, float y) {
        this.controller = controller;
        this.x = x;
        this.y = y;
    }

    public void tick() {

        setX(getX() + getVelX());
        setY(getY() + getVelY());

        if (x < 0) {
            controller.removeEntity(this);
        }

        if (x > Game.WIDTH) {
            controller.removeEntity(this);
        }

        for (Tile block : controller.tiles) {
            if (getBounds().intersects(block.getBounds()) && block.isTangible) {
                delete();
                if (block.getID() == Game.BLOCK_ID.FIRE_BARREL) {
                    block.delete(true);
                }
            }
        }

        for (FriendlyBodyObject player : controller.friendlyEntities) {
            if (getBounds().intersects(player.getBounds())) {
                dealDamageToPlayer(damage);
            }
        }
    }

    public void render(Graphics g) {
        g.drawRect((int) x, (int) y, 5, 2);
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public Shape getBounds() {
        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    public void delete() {
        controller.removeEntity(this);
    }

    public void dealDamageToPlayer(float damage) {
        controller.getPlayer().takeDamage(damage);
        delete();
    }
}
