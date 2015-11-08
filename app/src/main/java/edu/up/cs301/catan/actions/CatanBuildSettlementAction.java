package edu.up.cs301.catan.actions;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by oney18 on 11/7/2015.
 */
public class CatanBuildSettlementAction extends GameAction {

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
