package Main.entities;

import Main.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Tile {

    public float x, y;
    public boolean isTangible = true;
    public float width, height;
    public Game.BLOCK_ID blockID;
    private Point startingPoint;
    private boolean isMovingLeft = false;

    public Controller controller;
    public Game game;

    public Tile(Controller controller, Game game, Game.BLOCK_ID blockID, float x, float y, float width, float height) {
        this.controller = controller;
        this.blockID = blockID;
        this.game = game;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        startingPoint = new Point((int) x, (int) y);
    }

    public Tile(Controller controller, float x, float y, int width, int height) {
        this.controller = controller;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        startingPoint = new Point((int) x, (int) y);
    }

    public void render(Graphics g) {
        if (blockID == Game.BLOCK_ID.FULL_TOP_BLOCK){
            g.drawImage(DataHandler.fullTopBlock, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.FULL_BLOCK) {
            g.drawImage(DataHandler.fullBlock, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.HALF_BLOCK) {
            g.drawImage(DataHandler.halfBlock, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.HALF_TOP_BLOCK) {
            g.drawImage(DataHandler.halfTopBlock, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.ACID_FULL) {
            g.drawImage(DataHandler.acidFull, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.ACID_TOP) {
            g.drawImage(DataHandler.acidTop, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.SPIKE) {
            g.drawImage(DataHandler.spike, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.FIRE_BARREL) {
            g.drawImage(DataHandler.fireBarrel, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.BOX) {
            g.drawImage(DataHandler.box, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.DOOR_LOCKED) {
            g.drawImage(DataHandler.doorLocked, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.DOOR_OPEN) {
            g.drawImage(DataHandler.doorOpen, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.EXPLOSION) {
            g.drawImage(DataHandler.explosion, ((int) x), ((int) y), 250, 250, null);
        }
        else if (blockID == Game.BLOCK_ID.MOVING_PLATFORM) {
            g.drawImage(DataHandler.movingPlatform, (int) x, (int) y, (int) width, (int) height, null);
        }
        else if (blockID == Game.BLOCK_ID.UPGRADE_TERMINAL) {
            g.drawImage(DataHandler.upgradeTerminal, (int) x, (int) y, (int) width, (int) height, null);
        }
    }

    public void tick() {

        if (Game.gameState == Game.GAME_STATE.MAIN_GAME && controller.getEnemies().size() <= 0 && blockID == Game.BLOCK_ID.DOOR_LOCKED) {
            blockID = Game.BLOCK_ID.DOOR_OPEN;
        }

        else if (controller.getEnemies().size() > 0 && blockID == Game.BLOCK_ID.DOOR_OPEN) {
            blockID = Game.BLOCK_ID.DOOR_LOCKED;
        }

        else if (blockID == Game.BLOCK_ID.MOVING_PLATFORM && Game.gameState == Game.GAME_STATE.MAIN_GAME) {
            float changeInX;

            if (isMovingLeft) {
                changeInX = -Game.MOVING_PLATFORM_SPEED;
            }
            else {
                changeInX = Game.MOVING_PLATFORM_SPEED;
            }

            x += changeInX;

            if (controller.getPlayer().getBoundsDown().intersects(getBounds())) {
                controller.getPlayer().setX(controller.getPlayer().getX() + changeInX);
            }

            for (EnemyBodyObject enemy : controller.enemyEntities) {
                if (enemy.getBoundsDown().intersects(getBounds())) {
                    enemy.setX(enemy.getX() + changeInX);
                }
            }

            for (Tile block : controller.tiles) {
                if (block != this) {
                    if (getBoundsRight().intersects(block.getBounds())) {
                        isMovingLeft = true;
                        break;
                    }
                    else if (getBoundsLeft().intersects(block.getBounds()) || getBounds().contains(startingPoint)) {
                        startingPoint = new Point((int) x, (int) y);
                        isMovingLeft = false;
                        break;
                    }
                }
            }
        }

        else if (blockID == Game.BLOCK_ID.UPGRADE_TERMINAL) {
            if (controller.getPlayer().getBounds().intersects(getBounds()) && Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                game.upgradeMenu.show();
            }
            else {
                game.upgradeMenu.hide();
            }
        }
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

    public Rectangle2D getBounds() {
        if (getID() == Game.BLOCK_ID.HALF_TOP_BLOCK || getID() == Game.BLOCK_ID.HALF_BLOCK || getID() == Game.BLOCK_ID.MOVING_PLATFORM) {
            return new Rectangle((int) x, (int) y, (int) width, (int) height / 2);
        }

        if (getID() == Game.BLOCK_ID.SPIKE) {
            return new Rectangle((int) x, (int) (y + height / 2), (int) width, (int) height / 2);
        }

        return new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    public Game.BLOCK_ID getID() {
        return blockID;
    }

    public void setID(Game.BLOCK_ID blockID) {
        if (blockID != Game.BLOCK_ID.EXPLOSION) {
            controller.addEntity(new Block(controller, game, x, y, width, height, blockID));
            delete(false);
        }
        else {
            this.blockID = blockID;
        }
    }

    public void delete(boolean doExplosion) {
        controller.removeEntity(this);

        if (getID() == Game.BLOCK_ID.FIRE_BARREL && doExplosion) {
            controller.createExplosion(((int) getX()), ((int) getY()));
        }
    }

    public Point getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Point startingPoint) {
        this.startingPoint = startingPoint;
    }

    public Rectangle2D getBoundsRight() { // right part of hitbox
        float rectX = x + getWidth() + Game.MOVING_PLATFORM_SPEED;
        float rectY = y + 5;
        float rectW = 5;
        float rectH = getHeight() - 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }

    public Rectangle2D getBoundsLeft() { // left part of hitbox
        float rectX = x + Game.MOVING_PLATFORM_SPEED * 2;
        float rectY = y + 5;
        float rectW = 5;
        float rectH = getHeight() - 5;

        return new Rectangle((int) rectX, (int) rectY, (int) rectW, (int) rectH);
    }
}
