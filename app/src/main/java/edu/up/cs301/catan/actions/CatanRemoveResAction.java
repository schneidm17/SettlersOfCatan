package edu.up.cs301.catan.actions;

import java.io.Serializable;

import edu.up.cs301.game.Game;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Oney, Goldey, Schneider
 * @version Nov 2015
 *
 * Purpose:
 * Represents a RemoveResources action sent to the localGame.
 */
public class CatanRemoveResAction extends GameAction implements Serializable {

    //The amounts of resources to discard
    public int woodToLose;
    public int sheepToLose;
    public int wheatToLose;
    public int brickToLose;
    public int rockToLose;

    /**
     * Constructor for the GameAction
     *
     * @param player The player who instantiated the action
     * @param woodToLose Amount of lumber to discard
     * @param sheepToLose Amount of wool to discard
     * @param wheatToLose Amount of wheat to discard
     * @param brickToLose Amount of bricks to discard
     * @param rockToLose Amount of ore to discard
     */
    public CatanRemoveResAction(GamePlayer player, int woodToLose, int sheepToLose, int wheatToLose, int brickToLose,
                                int rockToLose)
    {
        super(player);
        this.woodToLose = woodToLose;
        this.sheepToLose = sheepToLose;
        this.wheatToLose = wheatToLose;
        this.brickToLose = brickToLose;
        this.rockToLose = rockToLose;
    }

}
