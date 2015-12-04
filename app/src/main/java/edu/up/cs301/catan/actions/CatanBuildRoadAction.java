package edu.up.cs301.catan.actions;

import java.io.Serializable;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents a BuildRoad action sent to the localGame.
 */
public class CatanBuildRoadAction extends GameAction implements Serializable{

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
