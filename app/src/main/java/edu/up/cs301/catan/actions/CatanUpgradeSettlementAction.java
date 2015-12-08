package edu.up.cs301.catan.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents n UpgradeSettlement action sent to the localGame.
 */
public class CatanUpgradeSettlementAction extends GameAction implements Serializable {

    public int spot; //Where to upgrade the settlement
    /**
     * Constructor for GameAction
     *
     * @param player The player who instantiated the action
     * @param spot The spot which to upgrade
     */
    public CatanUpgradeSettlementAction(GamePlayer player, int spot)
    {
        super(player);
        this.spot = spot;
    }
}
