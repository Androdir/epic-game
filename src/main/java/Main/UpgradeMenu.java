package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

public class UpgradeMenu implements MouseListener {

    private ArrayList<Game.UPGRADE> upgrades = new ArrayList<>();
    private ArrayList<Game.UPGRADE> levelsUpgrades = new ArrayList<>();

    private boolean isVisible = false;
    private boolean isUpgradeBought = false;

    private final int x = Game.WIDTH / 8;
    private final int y = Game.HEIGHT / 4;
    private final int w = Game.WIDTH / 4;
    private final int h = Game.HEIGHT / 2;

    private final Rectangle upgrade1 = new Rectangle(x + 50 + 20, y + 50 + 10, w / 4, h - 100 - h / 5);
    private final Rectangle upgrade2 = new Rectangle(x + 50 + 10 + (w / 4) + 60, y + 50 + 10, w / 4, h - 100 - h / 5);
    private final Rectangle upgrade3 = new Rectangle(x + 50 + 10 + (2 * ((w / 4 ) + 60)), y + 50 + 10, w / 4 - 10, h - 100 - h / 5);

    private Player player = null;

    public UpgradeMenu() {
        upgrades.addAll(Arrays.asList(Game.UPGRADE.values()));

        final ArrayList<Game.UPGRADE> upgradesCopy = new ArrayList<>(upgrades);

        for (int i = 0; i < 3; i++) {
            final int index = (int) (Math.random() * upgradesCopy.size());
            levelsUpgrades.add(upgradesCopy.get(index));
            upgradesCopy.remove(index);
        }
    }

    public void render(Graphics g) {

        if (isVisible && !isUpgradeBought) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.fillRect(x, y, w, h);
            g2d.setColor(Color.CYAN);
            g2d.fillRect(x + 50, y + 50, w - 100, h - 100);
            g2d.setColor(Color.BLUE);

            g2d.fill(upgrade1);
            g2d.fill(upgrade2);
            g2d.fill(upgrade3);


            float width1 = DataHandler.healAfterLevel.getWidth(null);
            float height1 = DataHandler.healAfterLevel.getHeight(null);
            float ratio = height1 / width1;

            int imageWidth1 = (w / 4) - 50;

            g2d.drawImage(DataHandler.healAfterLevel, x + 50 + 20 + 25, y + 50 + 10 , imageWidth1, (int) ((imageWidth1) * ratio), null);

            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            g2d.drawString(formatString(levelsUpgrades.get(0).toString()), upgrade1.x, upgrade1.y + upgrade1.height + 20 + fontMetrics.getHeight());
            g2d.drawString(formatString(levelsUpgrades.get(1).toString()), upgrade2.x, upgrade2.y + upgrade2.height + 20 + fontMetrics.getHeight());
            g2d.drawString(formatString(levelsUpgrades.get(2).toString()), upgrade3.x, upgrade3.y + upgrade3.height + 20 + fontMetrics.getHeight());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        final Point click = new Point(e.getX(), e.getY());

        if (isVisible && !isUpgradeBought) {
            if (upgrade1.contains(click)) {
                isUpgradeBought = true;
                player.addUpgrade(levelsUpgrades.get(0));
            }
            else if (upgrade2.contains(click)) {
                isUpgradeBought = true;
                player.addUpgrade(levelsUpgrades.get(1));
            }
            else if (upgrade3.contains(click)) {
                isUpgradeBought = true;
                player.addUpgrade(levelsUpgrades.get(2));
            }
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public boolean getVisiblity() {
        return isVisible;
    }

    private String formatString(String string) {
        string = string.replace("_", " ");
        final StringBuilder newString = new StringBuilder(string);

        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                newString.setCharAt(i, Character.toLowerCase(string.charAt(i)));
            }
        }

        newString.setCharAt(0, Character.toUpperCase(string.charAt(0)));

        return newString.toString();
    }

    public void loadUpgrades(Player player) {
        this.isUpgradeBought = false;

        if (this.player == null) {
            this.player = player;
        }

        levelsUpgrades = new ArrayList<>();

        final ArrayList<Game.UPGRADE> upgradesCopy = new ArrayList<>(upgrades);

        if (player.regenUpgrades >= 3) {
            upgradesCopy.remove(Game.UPGRADE.HEAL_AFTER_LEVEL);
        }
        if (player.jumpBoostUpgrades >= 3) {
            upgradesCopy.remove(Game.UPGRADE.JUMP);
        }
        if (player.damageUpgrades >= 3) {
            upgradesCopy.remove(Game.UPGRADE.DAMAGE);
        }
        if (player.attackSpeedUpgrades >= 3) {
            upgradesCopy.remove(Game.UPGRADE.ATTACK_SPEED);
        }
        if (player.dmgReductionUpgrades >= 3) {
            upgradesCopy.remove(Game.UPGRADE.DMG_REDUCTION);
        }

        for (int i = 0; i < 3; i++) {
            final int index = (int) (Math.random() * upgradesCopy.size());
            levelsUpgrades.add(upgradesCopy.get(index));
            upgradesCopy.remove(index);
        }
    }
}
