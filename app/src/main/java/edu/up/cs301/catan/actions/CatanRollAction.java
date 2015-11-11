package edu.up.cs301.catan.actions;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents a Roll action sent to the localGame.
 */
public class CatanRollAction extends GameAction {
    /**
     * Constructor for GameAction
     *
     * @param player the player who made the action
     */
    public CatanRollAction(GamePlayer player)
    {
        super(player);
    }
}
