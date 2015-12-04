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
 * Represents a AddResources action sent to the localGame.
 */
public class CatanAddResAction extends GameAction implements Serializable{

    //The amounts of resources to discard
    public int woodToGain;
    public int sheepToGain;
    public int wheatToGain;
    public int brickToGain;
    public int rockToGain;

    /**
     * Constructor for the GameAction
     *
     * @param player The player who made this action
     * @param woodToGain Amount of lumber to discard
     * @param sheepToGain Amount of wool to discard
     * @param wheatToGain Amount of wheat to discard
     * @param brickToGain Amount of bricks to discard
     * @param rockToGain Amount of ore to discard
     */
    public CatanAddResAction(GamePlayer player, int woodToGain, int sheepToGain, int wheatToGain, int brickToGain,
                                int rockToGain)
    {
        super(player);
        this.woodToGain = woodToGain;
        this.sheepToGain = sheepToGain;
        this.wheatToGain = wheatToGain;
        this.brickToGain = brickToGain;
        this.rockToGain = rockToGain;
    }

}
