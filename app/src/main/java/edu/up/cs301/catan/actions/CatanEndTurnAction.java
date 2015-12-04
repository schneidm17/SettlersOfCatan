package edu.up.cs301.catan.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents an EndTurn action sent to the localGame.
 */
public class CatanEndTurnAction extends GameAction implements Serializable {

    /**
     * Constructor for GameAction
     *
     * @param player The player who instanciated the action
     */
    public CatanEndTurnAction(GamePlayer player)
    {
        super(player);
    }
}
