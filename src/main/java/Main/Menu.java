package Main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Menu implements MouseListener, MouseMotionListener {

    private Game game;

    private boolean isMouseInsideQuitButton = false;
    private boolean isMouseInsidePlayButton = false;
    private boolean isMouseInsideSettingsButton = false;

    private Rectangle playButton = new Rectangle(100, Game.HEIGHT - 450, 1, 1);
    private Rectangle settingsButton = new Rectangle(100, Game.HEIGHT - 300, 1, 1);
    private Rectangle quitButton = new Rectangle(100, Game.HEIGHT - 150, 1, 1);
    private Rectangle levelEditorButton = new Rectangle((Game.WIDTH / 2) - 700, Game.HEIGHT - 300, 1, 1);

    private boolean drawSettings = false;

    public Menu(Game game) {
        this.game = game;

    }
    public void render(Graphics g) {

        g.drawImage(DataHandler.menuBackground, 0, 0, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 14));
        Game.drawCenteredString(Game.TITLE, Game.WIDTH / 2, 200, g);

        if (isMouseInsideQuitButton) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 26));

        }
        else {
            g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 32));
            g.setColor(Color.BLUE);

        }
        Game.drawStringInRectangle("- Quit", quitButton, true, g);

        if (isMouseInsideSettingsButton) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 26));

        }
        else {
            g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 32));
            g.setColor(Color.BLUE);

        }
        Game.drawStringInRectangle("- Settings", settingsButton, true, g);

        if (isMouseInsidePlayButton) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 26));

        }
        else {
            g.setFont(new Font("Arial", Font.BOLD, Game.WIDTH / 32));
            g.setColor(Color.BLUE);
        }
        Game.drawStringInRectangle("- Play", playButton, true, g);

        g.setColor(Color.ORANGE);
        Game.drawStringInRectangle("Level Editor", levelEditorButton, true, g);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (Game.gameState == Game.GAME_STATE.MAIN_MENU) {
            final int x = e.getX();
            final int y = e.getY();
            final Point click = new Point(x, y);

            if (quitButton.contains(click)) {
                System.exit(0);
            }
            else if (playButton.contains(click)) {
                Game.gameState = Game.GAME_STATE.MAIN_GAME;
                if (Game.GAME_OVER) {
                    DataHandler.loadMap(Game.LEVEL);
                }
            }
            else if (settingsButton.contains(click)) {
                game.settingsTextArea.setVisible(!game.settingsTextArea.isShowing());
                drawSettings = !drawSettings;
            }
            else if (levelEditorButton.contains(click)) {
                Game.gameState = Game.GAME_STATE.LEVEL_EDITOR;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (Game.gameState == Game.GAME_STATE.MAIN_MENU) {
            final int x = e.getX();
            final int y = e.getY();
            final Point mouse = new Point(x, y);

            if (quitButton.contains(mouse)) {
                isMouseInsideQuitButton = true;
                isMouseInsidePlayButton = false;
                isMouseInsideSettingsButton = false;
            }
            else if (playButton.contains(mouse)) {
                isMouseInsideQuitButton = false;
                isMouseInsidePlayButton = true;
                isMouseInsideSettingsButton = false;
            }
            else if (settingsButton.contains(mouse)) {
                isMouseInsideSettingsButton = true;
                isMouseInsideQuitButton = false;
                isMouseInsidePlayButton = false;
            }
            else {
                isMouseInsideSettingsButton = false;
                isMouseInsideQuitButton = false;
                isMouseInsidePlayButton = false;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }



    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

}
