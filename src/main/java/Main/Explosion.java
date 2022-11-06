package Main;

import Main.entities.EnemyBodyObject;
import Main.entities.FriendlyBodyObject;
import Main.entities.Tile;

import java.util.Timer;
import java.util.TimerTask;

public class Explosion extends Tile {

    public Explosion(Controller controller, float x, float y, int width, int height) {
        super(controller, x, y, width, height);

        setID(Game.BLOCK_ID.EXPLOSION);

        isTangible = false;
        explode();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                delete(false);
            }
        }, (150 * 4));
    }

    private void explode() {

        for (FriendlyBodyObject player : controller.friendlyEntities) {
            if (player.getBounds().intersects(getBounds())) {
                player.takeDamage(20);
            }
        }

        for (EnemyBodyObject enemy : controller.enemyEntities) {
            if (enemy.getBounds().intersects(getBounds())) {
                if (enemy.getHealth() - Game.FIRE_BARREL_DAMAGE > 0) {
                    enemy.setHealth(enemy.getHealth() - Game.FIRE_BARREL_DAMAGE);
                }
                else {
                    enemy.setHealth(0);
                }
            }
        }

    }
}
