package Main;

import Main.entities.EnemyBulletObject;
import Main.entities.FriendlyBodyObject;
import Main.entities.Tile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class BossBullet extends EnemyBulletObject {

    private float angleLookingAt;
    private float angleToPlayer;
    private float distanceTravelled = 0;
    private final float turnSpeed = 0.3f;

    public BossBullet(Controller controller, float x, float y) {
        super(controller, x, y);

        setWidth(DataHandler.bossRocket.getWidth(null));
        setHeight(DataHandler.bossRocket.getHeight(null));
        setVelX(4);
        setVelY(4);

        startingX = x;
        maxDisplacement = Game.BOSS_BULLET_RANGE;
        damage = Game.BOSS_BULLET_DAMAGE;

        angleLookingAt = (float) Math.toDegrees(Math.atan2(controller.getPlayer().getY() - getY(), controller.getPlayer().getX() - getX()));
        angleLookingAt = (float) (angleLookingAt + Math.ceil(-angleLookingAt / 360) * 360);
    }

    @Override
    public void render(Graphics g) {

        AffineTransform at = AffineTransform.getTranslateInstance(getX(), getY());
        at.rotate(Math.toRadians(angleLookingAt + 90), getWidth() / 2, getHeight() / 2);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(DataHandler.bossRocket, at, null);
    }

    @Override
    public void tick() {
        super.tick();
        if (distanceTravelled < maxDisplacement) {
            angleToPlayer = (float) Math.toDegrees(Math.atan2(controller.getPlayer().getY() - getY(), controller.getPlayer().getX() - getX()));
            angleToPlayer = (float) (angleToPlayer + Math.ceil(-angleToPlayer / 360) * 360);

            if (Math.abs(angleLookingAt - angleToPlayer) < 180) {
                if (angleLookingAt < angleToPlayer) {
                    angleLookingAt += turnSpeed;
                }
                else {
                    angleLookingAt -= turnSpeed;
                }
            }
            else {
                if (angleLookingAt < angleToPlayer) {
                    angleLookingAt -= turnSpeed;
                }
                else {
                    angleLookingAt += turnSpeed;
                }
            }

            float velY = (float) ((4 * Math.sin(Math.toRadians(angleLookingAt))));
            float velX = (float) ((4 * Math.cos(Math.toRadians(angleLookingAt))));

            distanceTravelled += Math.abs(velY);
            distanceTravelled += Math.abs(velX);

            setVelX(velX);
            setVelY(velY);

            angleLookingAt = ((angleLookingAt % 360) + 360) % 360;
        }
        else {
            angleLookingAt = 90;
            if (getVelY() % Game.GRAVITY_VEL >= 0 && getVelY() % Game.GRAVITY_VEL < Game.GRAVITY_VEL) {
                setVelY(getVelY() + Game.GRAVITY_VEL / 2);
            }
            else {
                setVelY(Game.GRAVITY_VEL / 2);
            }
            setY(getY() + (getVelY()));

            for (Tile tile : controller.tiles) {
                if (getBounds().intersects(tile.getBounds())) {
                    delete();
                }
            }
        }
    }

    @Override
    public Shape getBounds() {

        Rectangle2D rect = new Rectangle2D.Double((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());

        AffineTransform transform = new AffineTransform();
        double x = rect.getCenterX();
        double y = rect.getCenterY();
        transform.rotate(Math.toRadians(angleLookingAt + 90), x, y);

        return transform.createTransformedShape(rect);
    }
}
