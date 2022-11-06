package Main;

import Main.entities.EnemyBodyObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class RangedEnemy extends EnemyBodyObject {

    private final Timer shootTimer = new Timer();

    private int timesShot = 0;
    private boolean direction;

    public RangedEnemy(Controller controller, float x, float y) {
        super(controller, Game.ENEMY_ID.RANGED, x, y);

        setTexture(DataHandler.enemyIdle);
        setHealth(Game.RANGED_ENEMY_HEALTH);
        setVelX(0);

        long difference = ThreadLocalRandom.current().nextLong(0, 150 + 1);

        shootTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                if (controller.enemyEntities.contains(RangedEnemy.this)) {
                    if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
                        if (controller.getPlayer().getX() - getX() >= ((Game.WIDTH / 25) * 5) || getX() - controller.getPlayer().getX() <= ((Game.WIDTH / 25) * 5)) {
                            setTexture(DataHandler.enemyRangedAttack);
                            Timer shootMultipleTimes = new Timer();
                            shootMultipleTimes.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {

                                    if (timesShot == 0) {
                                        direction = isFacingLeft;
                                    }

                                    controller.addEntity(new RangedEnemyBullet(controller, getX() + getWidth() / 2, getY() + getHeight() / 2, direction));
                                    timesShot++;

                                    if (timesShot == 3) {
                                        shootMultipleTimes.cancel();
                                        shootMultipleTimes.purge();
                                        timesShot = 0;
                                    }
                                }
                            }, 0, 100);
                        }
                        else {
                            setTexture(DataHandler.enemyIdle);
                        }
                    }
                }
                else {
                    shootTimer.cancel();
                    shootTimer.purge();
                }
            }
        }, difference, Game.RANGED_ENEMY_COOLDOWN - difference);
    }

    @Override
    public void tick() {
        super.tick();

        isFacingLeft = !(controller.getPlayer().getX() > getX());

    }

    @Override
    public void delete() {
        controller.removeEntity(this);
        shootTimer.cancel();
        shootTimer.purge();
    }
}
