package edu.up.cs301.catan.actions;

import edu.up.cs301.game.Game;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents a MoveRobber action sent to the localGame.
 */
public class CatanMoveRobberAction extends GameAction{

    public int spot; //where to put the robber
    /**
     * Constructor for GameAction
     *
     * @param player The player who instanciated the action
     * @param spot Where to move the robber
     */
    public CatanMoveRobberAction(GamePlayer player, int spot)
    {
        super(player);
        this.spot = spot;
    }
}
