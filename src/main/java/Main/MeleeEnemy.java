package Main;

import Main.entities.EnemyBodyObject;
import Main.entities.FriendlyBodyObject;

import java.awt.*;

public class MeleeEnemy extends EnemyBodyObject {

    public MeleeEnemy(Controller controller, float x, float y) {
        super(controller, Game.ENEMY_ID.MELEE, x, y);

        setHealth(Game.MELEE_ENEMY_HEALTH);

        if (isFacingLeft) {
            setVelX(-Game.ENEMY_MOVEMENT_SPEED);
        }
        else {
            setVelX(Game.ENEMY_MOVEMENT_SPEED);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (getVelX() > 0) {
            isFacingLeft = false;
        }
        else if (getVelX() < 0) {
            isFacingLeft = true;
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

        for (FriendlyBodyObject player : controller.friendlyEntities) {
            if (player.getBounds().intersects(getBounds())) {
                if (player.canTakeMeleeDamage) {
                    player.takeDamage(Game.MELEE_ENEMY_DAMAGE);
                    player.canTakeMeleeDamage = false;
                }
                setTexture(DataHandler.enemyMeleeAttack);
                break;
            }
            else {
                setTexture(DataHandler.enemyRun);
            }
        }
    }
}
