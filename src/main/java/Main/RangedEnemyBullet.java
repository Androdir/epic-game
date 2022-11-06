package Main;

import Main.entities.EnemyBulletObject;

import java.awt.*;

public class RangedEnemyBullet extends EnemyBulletObject {

    public RangedEnemyBullet(Controller controller, float x, float y, boolean direction) {
        super(controller, x, y);

        setWidth(5);
        setHeight(2);

        startingX = x;
        maxDisplacement = Game.WIDTH / 5;
        damage = Game.RANGED_ENEMY_BULLET_DAMAGE;

        if (direction) {
            setVelX(-6);
        }
        else {
            setVelX(6);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (startingX - getX() > maxDisplacement) {
            delete();
        }
        else if (getX() - startingX > maxDisplacement) {
            delete();
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(Color.ORANGE);
        g.drawRect((int) getX(), (int) getY(), 5, 2);
    }
}

