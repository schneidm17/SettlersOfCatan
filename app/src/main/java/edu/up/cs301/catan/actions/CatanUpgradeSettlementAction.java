package edu.up.cs301.catan.actions;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by oney18 on 11/7/2015.
 */
public class CatanUpgradeSettlementAction extends GameAction {

    public int spot; //Where to upgrade the settlement
    /**
     * Constructor for GameAction
     *
     * @param player The player who instanciated the action
     * @param spot The spot which ot upgrade
     */
    public CatanUpgradeSettlementAction(GamePlayer player, int spot)
    {
        super(player);
        this.spot = spot;
    }
}
