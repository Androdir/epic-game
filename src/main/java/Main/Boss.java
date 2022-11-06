package Main;

import Main.entities.EnemyBodyObject;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Boss extends EnemyBodyObject {

    public Boss(Controller controller, float x, float y) {
        super(controller, Game.ENEMY_ID.BOSS, x, y);
        setWidth(125);
        setHeight(125);
        setHealth(200);

        final long difference = ThreadLocalRandom.current().nextLong(0, 150 + 1);

        Timer shootTimer = new Timer();
        shootTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                    if (controller.enemyEntities.contains(Boss.this)) {
                        if (controller.getPlayer().getX() - getX() <= Game.BOSS_BULLET_RANGE || getX() - controller.getPlayer().getX() <= Game.BOSS_BULLET_RANGE) {
                            controller.addEntity(new BossBullet(controller, getX(), getY()));
                        }
                    }
                    else {
                        shootTimer.cancel();
                        shootTimer.purge();
                    }
                }
            }
        }, difference, Game.BOSS_BULLET_COOLDOWN - difference);
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(DataHandler.boss, (int) getX(), (int) getY(), null);
    }

    @Override
    public void tick() {
        super.tick();

        if (isFacingLeft) {
            setVelX(-Game.ENEMY_MOVEMENT_SPEED);
        }
        else {
            setVelX(Game.ENEMY_MOVEMENT_SPEED);
        }

        if (isFacingLeft && isGoingToFall() && isOnGround()) {
            isFacingLeft = false;
            setVelX(Game.ENEMY_MOVEMENT_SPEED);
        }
        else if (!isFacingLeft && isGoingToFall() && isOnGround()) {
            isFacingLeft = true;
            endPoint = new Point((int) getX() + getWidth(), (int) getY() + getHeight() / 2);
            setVelX(-Game.ENEMY_MOVEMENT_SPEED);
        }

        if (getVelX() > 0) {
            isFacingLeft = false;
        }
        else if (getVelX() < 0) {
            isFacingLeft = true;
        }
    }
}
