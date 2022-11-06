package Main;

import Main.entities.FriendlyBodyObject;
import Main.entities.Tile;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Player extends FriendlyBodyObject {

    public int regenUpgrades = 0;
    public int attackSpeedUpgrades = 0;
    public int damageUpgrades = 0;
    public int jumpBoostUpgrades = 0;

    private Controller controller;
    private UpgradeMenu upgradeMenu;
    private int rocketCount = 5;
    private boolean canShootBullet = true;
    private boolean canShootRocket = true;
    public boolean isShootingBullets = true;

    public Player(Controller controller, UpgradeMenu upgradeMenu) {
        super(controller);
        this.controller = controller;
        this.upgradeMenu = upgradeMenu;

        setX(0);
        setY(Game.HEIGHT - 2 * (Game.WIDTH / 25));
    }

    @Override
    public void render(Graphics g) {
        super.render(g);

        // Use this to draw the hitbox
/*
        g.setColor(Color.WHITE);
        g.fillRect(((int) getX()), ((int) getY()), getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.RED);
        g2d.fill(getBoundsUp());
        g2d.setColor(Color.GREEN);
        g2d.fill(getBoundsDown());

        g2d.setColor(Color.BLUE);
        g2d.fill(getBoundsLeft());
        g2d.setColor(Color.ORANGE);
        g2d.fill(getBoundsRight());
*/
    }


    @Override
    public void tick() {
        super.tick();

        if (isShooting) {
            shoot();
        }

        if (getHealth() <= 0) {
            die();
        }

        if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {
            if (isMovingRight) {
                setX(getX() + Game.PLAYER_MOVEMENT_SPEED);
            }

            else if (isMovingLeft) {
                setX(getX() - Game.PLAYER_MOVEMENT_SPEED);
            }
        }
        else if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
            setX(getX() + getVelX());
        }

        setY(getY() + getVelY());

        if (getX() > Game.WIDTH / 2) {
            Game.SCREEN = 2;
        }
        else {
            Game.SCREEN = 1;
        }

        if (getX() <= 0) {
            setX(0);
        }
        if (getX() >= Game.WIDTH - 160) {
            setX(Game.WIDTH - 160);
        }
        if (getY() <= 0) {
            setY(0);
            setVelY(0);
        }
        if (isOnGround()) {
            setVelY(0);
            canDoubleJump = false;
            jumps = 0;
        }
    }

    @Override
    public void checkBlockCollision() {

        for (Tile tile : controller.tiles) {

            if (getBoundsDown().intersects(tile.getBounds()) && tile.isTangible) {
                setVelY(0);
                setY((float) (tile.getBounds().getY() - getHeight()));
            }

            if (getBoundsUp().intersects(tile.getBounds()) && tile.isTangible) {
                setVelY(0);
                setY((float) (tile.getBounds().getY() + tile.getBounds().getHeight()));
            }

            if (getBoundsRight().intersects(tile.getBounds()) && tile.isTangible) {
                setVelX(0);
                setX((float) (tile.getBounds().getX() - getWidth() - 5));
            }

            if (getBoundsLeft().intersects(tile.getBounds()) && tile.isTangible) {
                setVelX(0);
                setX((float) (tile.getBounds().getX() + tile.getBounds().getWidth()));
            }

            if (getBounds().intersects(tile.getBounds()) && tile.getID() == Game.BLOCK_ID.SPIKE) {
                if (canTakeBlockDamage) {
                    takeDamage(10);
                    canTakeBlockDamage = false;
                }
            }

            if (getBounds().intersects(tile.getBounds()) && tile.getID() == Game.BLOCK_ID.DOOR_OPEN && controller.getEnemies().size() <= 0) {
                Game.LEVEL++;
                DataHandler.loadMap(Game.LEVEL);
                upgradeMenu.loadUpgrades(this);

                if (rocketCount + 2 <= 5) {
                    rocketCount += 2;
                }
                else {
                    rocketCount = 5;
                }

                final float heal;

                if (regenUpgrades > 0) {
                    heal = Game.HEAL_AFTER_LEVEL + (5 * regenUpgrades);
                }
                else {
                    heal = Game.HEAL_AFTER_LEVEL;
                }

                setHealth((int) Math.min(getHealth() + heal, 100));
            }

            if (getBounds().intersects(tile.getBounds()) && (tile.getID() == Game.BLOCK_ID.ACID_FULL || tile.getID() == Game.BLOCK_ID.ACID_TOP)) {
                if (canTakeBlockDamage) {
                    takeDamage(20);
                    canTakeBlockDamage = false;
                }
            }
        }
    }

    public void die() {
        rocketCount = 5;

        setHealth(100);
        if (Game.gameState == Game.GAME_STATE.MAIN_GAME) {

            dmgReductionUpgrades = 0;
            damageUpgrades = 0;
            regenUpgrades = 0;
            jumpBoostUpgrades = 0;
            attackSpeedUpgrades = 0;

            Game.gameState = Game.GAME_STATE.MAIN_MENU;
            Game.LEVEL = 1;
            DataHandler.loadMap(Game.LEVEL);
            Game.GAME_OVER = true;
            isFacingRight = true;
            isMovingRight = false;
            isMovingLeft = false;
        }
    }

    public void jump() {

        final float jumpPower;

        if (jumpBoostUpgrades > 0) {
            jumpPower = Game.JUMP_POWER + (0.8f * jumpBoostUpgrades);
        }
        else {
            jumpPower = Game.JUMP_POWER;
        }

        if (jumps == 1 && !isOnGround() && canDoubleJump) {
            setVelY(-jumpPower);
            jumps++;
            canDoubleJump = false;
        }

        if (jumps == 0 && isOnGround()) {
            setVelY(-jumpPower);
            jumps++;
        }

    }

    public void moveLeft() {
        isMovingLeft = true;
        isMovingRight = false;
        isFacingRight = false;
    }

    public void moveRight() {
        isMovingLeft = false;
        isMovingRight = true;
        isFacingRight = true;
    }

    public void shoot() {

        if (isShootingBullets) {
            if (canShootBullet) {
                if (isFacingRight) {
                    controller.addEntity(new PlayerBullet(this, controller, getX() + getWidth() - 10, getY() + (getHeight() / 2) + 5, isFacingRight, isShootingBullets ? DataHandler.playerBullet : DataHandler.playerRocket));
                }
                else {
                    controller.addEntity(new PlayerBullet(this, controller, getX() - 10, getY() + (getHeight() / 2) + 5, isFacingRight, isShootingBullets ? DataHandler.playerBullet : DataHandler.playerRocket));;
                }

                canShootBullet = false;

                final Timer cooldown = new Timer();
                cooldown.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canShootBullet = true;
                    }
                }, (long) (Game.PLAYER_SHOOT_COOLDOWN * (1 - attackSpeedUpgrades * 0.12)));
            }
        }
        else {
            if (canShootRocket && rocketCount > 0) {
                if (isFacingRight) {
                    controller.addEntity(new PlayerBullet(this, controller, getX() + getWidth() - 10, getY() + (getHeight() / 2) + 5, isFacingRight, isShootingBullets ? DataHandler.playerBullet : DataHandler.playerRocket));
                }
                else {
                    controller.addEntity(new PlayerBullet(this, controller, getX() - 10, getY() + (getHeight() / 2) + 5, isFacingRight, isShootingBullets ? DataHandler.playerBullet : DataHandler.playerRocket));;
                }

                canShootRocket = false;
                rocketCount--;

                final Timer cooldown = new Timer();
                cooldown.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canShootRocket = true;
                    }
                }, (long) (6 * (Game.PLAYER_SHOOT_COOLDOWN * (1 - attackSpeedUpgrades * 0.12))));
            }
        }
    }

    public void addUpgrade(Game.UPGRADE upgrade) {
        if (upgrade == Game.UPGRADE.DMG_REDUCTION) {
            dmgReductionUpgrades++;
        }
        else if (upgrade == Game.UPGRADE.ATTACK_SPEED) {
            attackSpeedUpgrades++;
        }
        else if (upgrade == Game.UPGRADE.JUMP) {
            jumpBoostUpgrades++;
        }
        else if (upgrade == Game.UPGRADE.DAMAGE) {
            damageUpgrades++;
        }
        else if (upgrade == Game.UPGRADE.HEAL_AFTER_LEVEL) {
            regenUpgrades++;
        }
    }

    @Override
    public void delete() {
        super.delete();
    }
}

