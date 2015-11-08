package edu.up.cs301.catan.actions;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by oney18 on 11/7/2015.
 */
public class CatanEndTurnAction extends GameAction {

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
