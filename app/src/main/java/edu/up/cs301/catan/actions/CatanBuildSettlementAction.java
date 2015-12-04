package edu.up.cs301.catan.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents a BuildSettlement action sent to the localGame.
 */
public class CatanBuildSettlementAction extends GameAction implements Serializable {

    public int spot; //The spot to build the settlement
    /**
     * Constructor for GameAction
     *
     * @param player The player who instanciated the action
     * @param spot The spot to build the settlement on
     */
    public CatanBuildSettlementAction(GamePlayer player, int spot)
    {
        super(player);
        this.spot = spot;
    }
}
