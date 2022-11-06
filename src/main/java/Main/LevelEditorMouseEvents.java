package Main;

import Main.entities.Tile;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class LevelEditorMouseEvents implements MouseListener, MouseMotionListener, MouseWheelListener {

    private Controller controller;
    private boolean selected = false;
    private Tile blockToMove = null;

    public LevelEditorMouseEvents(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
            selected = false;
            blockToMove = null;
        }
    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseExited(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
            float mx = e.getX();
            float my = e.getY();

            for (Tile block : controller.tiles) {
                if (block.getBounds().contains(mx, my) && Game.SCREEN == 1) {
                    if (!selected || blockToMove == block) {
                        block.setX((float) (mx - block.getBounds().getWidth() / 2));
                        block.setY((float) (my - block.getBounds().getHeight() / 2));
                        block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                    }
                    else {
                        blockToMove = block;
                        selected = true;
                    }
                    return;
                }
                else if (block.getBounds().contains(mx + (Game.WIDTH / 2), my) && Game.SCREEN == 2) {
                    if (!selected || blockToMove == block) {
                        block.setX((float) (mx + (Game.WIDTH / 2) - block.getBounds().getWidth() / 2));
                        block.setY((float) (my - block.getBounds().getHeight() / 2));
                        block.setStartingPoint(new Point((int) block.getX(), (int) block.getY()));
                    }
                    else {
                        blockToMove = block;
                        selected = true;
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseMoved(java.awt.event.MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (Game.gameState == Game.GAME_STATE.LEVEL_EDITOR) {
            int mx = e.getX();
            int my = e.getY();

            for (Tile block : controller.tiles) {
                if (block.getBounds().contains(mx, my) && Game.SCREEN == 1) {
                    if (!selected || blockToMove == block) {
                        if (e.getWheelRotation() == -1) {
                            if (block.getID() == Game.BLOCK_ID.HALF_BLOCK || block.getID() == Game.BLOCK_ID.HALF_TOP_BLOCK || block.getID() == Game.BLOCK_ID.SPIKE || block.getID() == Game.BLOCK_ID.MOVING_PLATFORM) {
                                block.setHeight((float) (block.getBounds().getHeight() * 2 + 2));
                            }
                            else {
                                block.setHeight((float) (block.getBounds().getHeight() + 1));
                            }
                            block.setWidth((float) (block.getBounds().getWidth() + 1));
                        }
                        else {
                            if (block.getID() == Game.BLOCK_ID.HALF_BLOCK || block.getID() == Game.BLOCK_ID.HALF_TOP_BLOCK || block.getID() == Game.BLOCK_ID.SPIKE || block.getID() == Game.BLOCK_ID.MOVING_PLATFORM) {
                                block.setHeight((float) (block.getBounds().getHeight() * 2 - 1));
                            }
                            else {
                                block.setHeight((float) (block.getBounds().getHeight() - 1));
                            }
                            block.setWidth((float) (block.getBounds().getWidth() - 1));
                        }
                    }
                    else {
                        blockToMove = block;
                        selected = true;
                    }
                    break;
                }
                else if (block.getBounds().contains(mx + (Game.WIDTH / 2), my) && Game.SCREEN == 2) {
                    if (!selected || blockToMove == block) {
                        if (e.getWheelRotation() == -1) {
                            if (block.getID() == Game.BLOCK_ID.HALF_BLOCK || block.getID() == Game.BLOCK_ID.HALF_TOP_BLOCK || block.getID() == Game.BLOCK_ID.SPIKE) {
                                block.setHeight((float) (block.getBounds().getHeight() * 2 + 2));
                            }
                            else {
                                block.setHeight((float) (block.getBounds().getHeight() + 1));
                            }
                            block.setWidth((float) (block.getBounds().getWidth() + 1));
                        }
                        else {
                            if (block.getID() == Game.BLOCK_ID.HALF_BLOCK || block.getID() == Game.BLOCK_ID.HALF_TOP_BLOCK || block.getID() == Game.BLOCK_ID.SPIKE) {
                                block.setHeight((float) (block.getBounds().getHeight() * 2 - 1));
                            }
                            else {
                                block.setHeight((float) (block.getBounds().getHeight() - 1));
                            }
                            block.setWidth((float) (block.getBounds().getWidth() - 1));
                        }
                    }
                    else {
                        blockToMove = block;
                        selected = true;
                    }
                    break;
                }
            }
        }
    }
}
