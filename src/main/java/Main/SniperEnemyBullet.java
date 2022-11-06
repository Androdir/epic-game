package Main;

import Main.entities.EnemyBulletObject;

import java.awt.*;

public class SniperEnemyBullet extends EnemyBulletObject {
    public SniperEnemyBullet(Controller controller, float x, float y, boolean isFacingLeft) {
        super(controller, x, y);

        setWidth(20);
        setHeight(8);

        startingX = x;
        maxDisplacement = Game.WIDTH / 3;
        damage = Game.RANGED_ENEMY_BULLET_DAMAGE * 5;

        if (isFacingLeft) {
            setVelX(-9);
        }
        else {
            setVelX(9);
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

        g.setColor(Color.CYAN);
        g.fillRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }
}
