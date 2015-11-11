package edu.up.cs301.catan.actions;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by oney18 on 11/7/2015.
 */
public class CatanBuildRoadAction extends GameAction {

    public int spot; //spot to build road

    /**
     * Constructor for GameAction
     *
     * @param player The player who instantiated the action
     * @param spot The spot to build the road
     */
    public CatanBuildRoadAction(GamePlayer player, int spot)
    {
        super(player);
        this.spot = spot;

    }
}
