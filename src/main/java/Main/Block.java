package Main;

import Main.entities.Tile;

public class Block extends Tile {
    public Block(Controller controller, Game game, float x, float y, float width, float height, Game.BLOCK_ID blockID) {
        super(controller, game, blockID, x, y, width, height);

        if (blockID == Game.BLOCK_ID.SPIKE
                || blockID == Game.BLOCK_ID.EXPLOSION
                || blockID == Game.BLOCK_ID.ACID_FULL
                || blockID == Game.BLOCK_ID.ACID_TOP
                || blockID == Game.BLOCK_ID.DOOR_LOCKED
                || blockID == Game.BLOCK_ID.DOOR_OPEN
                || blockID == Game.BLOCK_ID.UPGRADE_TERMINAL) {
            isTangible = false;
        }
    }
}
