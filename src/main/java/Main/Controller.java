package Main;

import Main.entities.*;

import java.awt.*;
import java.util.ArrayList;

public class Controller {

    public ArrayList<FriendlyBodyObject> friendlyEntities;
    public ArrayList<FriendlyBulletObject> friendlyBullets;
    public ArrayList<Tile> tiles;
    public ArrayList<EnemyBodyObject> enemyEntities;
    public ArrayList<EnemyBulletObject> enemyBullets;
    private Game game;

    public Controller(Game game, ArrayList<FriendlyBodyObject> friendlyEntities, ArrayList<FriendlyBulletObject> friendlyBullets, ArrayList<Tile> tiles, ArrayList<EnemyBodyObject> enemyEntities, ArrayList<EnemyBulletObject> enemyBullets) {
        this.game = game;
        this.friendlyEntities = friendlyEntities;
        this.friendlyBullets = friendlyBullets;
        this.tiles = tiles;
        this.enemyEntities = enemyEntities;
        this.enemyBullets = enemyBullets;
    }

    public void tick() {

        for (int i = 0; i < friendlyEntities.size(); i++) {
            try {
                friendlyEntities.get(i).tick();
            } catch (Exception e) {
                System.err.println("Friendly entity tick error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < friendlyBullets.size(); i++) {
            try {
                friendlyBullets.get(i).tick();
            } catch (Exception e) {
                System.err.println("Friendly bullet tick error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < enemyEntities.size(); i++) {
            try {
                enemyEntities.get(i).tick();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Enemy entity tick error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < enemyBullets.size(); i++) {
            try {
                enemyBullets.get(i).tick();
            }
            catch (Exception e) {
                System.err.println("Enemy bullet tick error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < tiles.size(); i++) {
            try {
                tiles.get(i).tick();
            }
            catch (Exception e) {
                System.err.println("Tile tick error (" + e.getMessage() + ")");
            }
        }

    }

    public void render(Graphics g) {

        for (int i = 0; i < tiles.size(); i++) {
            try {
                tiles.get(i).render(g);
            } catch (Exception e) {
                System.err.println("Tile rendering error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < friendlyBullets.size(); i++) {
            try {
                friendlyBullets.get(i).render(g);
            } catch (Exception e) {
                System.err.println("Friendly bullet rendering error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < enemyBullets.size(); i++) {
            try {
                enemyBullets.get(i).render(g);
            }
            catch (Exception e) {
                System.err.println("Enemy bullet rendering error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < friendlyEntities.size(); i++) {
            try {
                friendlyEntities.get(i).render(g);
            } catch (Exception e) {
                System.err.println("Friendly entity rendering error (" + e.getMessage() + ")");
            }
        }

        for (int i = 0; i < enemyEntities.size(); i++) {
            try {
                enemyEntities.get(i).render(g);
            } catch (Exception e) {
                System.err.println("Enemy entity rendering error (" + e.getMessage() + ")");
            }
        }
    }

    public void removeEntity(FriendlyBodyObject friendlyBodyObject) {
        friendlyEntities.remove(friendlyBodyObject);
    }

    public void addEntity(FriendlyBodyObject friendlyBodyObject) {
        friendlyEntities.add(friendlyBodyObject);
    }

    public void removeEntity(FriendlyBulletObject friendlyBulletObject) {
        friendlyBullets.remove(friendlyBulletObject);
    }

    public void addEntity(FriendlyBulletObject friendlyBulletObject) {
        friendlyBullets.add(friendlyBulletObject);
    }

    public void addEntity(Tile block) {
        tiles.add(block);
    }

    public void removeEntity(Tile block) {
        tiles.remove(block);
    }

    public void addEntity(EnemyBodyObject enemy) {
        enemyEntities.add(enemy);
    }

    public void removeEntity(EnemyBodyObject enemy) {
        enemyEntities.remove(enemy);
    }

    public void addEntity(EnemyBulletObject enemyBullet) {
        enemyBullets.add(enemyBullet);
    }

    public void removeEntity(EnemyBulletObject enemyBullet) {
        enemyBullets.remove(enemyBullet);
    }

    public void createBlock(float x, float y, float width, float height, Game.BLOCK_ID blockID) {
        addEntity(new Block(this, game, x, y, width, height, blockID));
    }

    public void createExplosion(float x, float y) {
        addEntity(new Explosion(this, x - 50, y - 50, 250, 250));
    }

    public Player getPlayer() {
        return (Player) friendlyEntities.get(0);
    }

    public ArrayList<EnemyBodyObject> getEnemies() {
        return enemyEntities;
    }

}
