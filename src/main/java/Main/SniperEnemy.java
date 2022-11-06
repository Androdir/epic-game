package Main;

import Main.entities.EnemyBodyObject;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class SniperEnemy extends EnemyBodyObject {

    private final Timer shootTimer = new Timer();

    public SniperEnemy(Controller controller, float x, float y) {
        super(controller, Game.ENEMY_ID.SNIPER, x ,y);

        setTexture(DataHandler.enemyIdle);
        setWidth(DataHandler.enemyIdle.getWidth(null));
        setHeight(DataHandler.enemyIdle.getHeight(null));
        setHealth(Game.RANGED_ENEMY_HEALTH);
        setVelX(0);

        long difference = ThreadLocalRandom.current().nextLong(0, 150 + 1);

        shootTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (controller.enemyEntities.contains(SniperEnemy.this)) {
                    if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                        if (controller.getPlayer().getX() - getX() >= Game.WIDTH / 3 || getX() - controller.getPlayer().getX() <= Game.WIDTH / 3) {
                            setTexture(DataHandler.enemyProne);
                            setWidth(DataHandler.enemyProne.getWidth(null));
                            setHeight(DataHandler.enemyProne.getHeight(null));  controller.addEntity(new SniperEnemyBullet(controller, getX() + getWidth() / 2, getY() + getHeight() / 2, isFacingLeft));
                        }
                        else {
                            setTexture(DataHandler.enemyIdle);
                            setWidth(DataHandler.enemyIdle.getWidth(null));
                            setHeight(DataHandler.enemyIdle.getHeight(null));
                        }
                    }
                }
                else {
                    shootTimer.cancel();
                    shootTimer.purge();
                }
            }
        }, difference, (long) (Game.RANGED_ENEMY_COOLDOWN * 2.5 - difference));
    }

    @Override
    public void tick() {
        super.tick();

        if (getTexture() == DataHandler.enemyIdle) {
            setWidth(DataHandler.enemyIdle.getWidth(null));
            setHeight(DataHandler.enemyIdle.getHeight(null));
        }
        else {
            setWidth(DataHandler.enemyProne.getWidth(null));
            setHeight(DataHandler.enemyProne.getHeight(null));
        }

        setVelX(0);
        isFacingLeft = !(controller.getPlayer().getX() > getX());
    }

    @Override
    public void delete() {
        controller.removeEntity(this);
        shootTimer.cancel();
        shootTimer.purge();
    }
}
