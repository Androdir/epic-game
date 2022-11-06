package Main;

import Main.entities.EnemyBodyObject;
import Main.entities.FriendlyBulletObject;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class PlayerBullet extends FriendlyBulletObject {

    private final float damage;

    public PlayerBullet(Player player, Controller controller, float x, float y, boolean direction, Image texture) {
        super(controller, x, y, direction);

        final Thread thread = new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("D:\\Users\\dru\\OneDrive\\Downloads\\bullet sfx.wav").getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        setTexture(texture);

        final float damageMultiplier = 1.1f;
        if (texture == DataHandler.playerBullet) {
            damage = Game.PLAYER_BULLET_DAMAGE + (damageMultiplier * player.damageUpgrades);
            maxDistance = ((Game.WIDTH / 25) * 5);
        }
        else {
            damage = (Game.PLAYER_BULLET_DAMAGE + (damageMultiplier * player.damageUpgrades)) * 6;
            maxDistance = ((Game.WIDTH / 25) * 8);
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
    }

    @Override
    public void tick() {
        super.tick();

        for (EnemyBodyObject enemy : controller.enemyEntities) {
            if (enemy.getBounds().intersects(getBounds())) {
                delete();
                enemy.setHealth(enemy.getHealth() - damage);
                break;
            }
        }
    }
}
