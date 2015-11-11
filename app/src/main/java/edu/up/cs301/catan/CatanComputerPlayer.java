package edu.up.cs301.catan;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * An AI for Catan
 *
 * @author Jarrett Oney
 * @version November 2015
 */
public class CatanComputerPlayer extends GameComputerPlayer {

    /**
     * ctor does nothing extra
     */
    public CatanComputerPlayer(String name) {
        super(name);
    }

    /**
     * callback method--game's state has changed
     *
     * @param info
     * 		the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof CatanGameState)
        {
            //TODO ai stuff
        }
    }//receiveInfo

    protected void randomizeRobber(CatanGameState gameState)
    {
        //TODO randomize robber placement
    }

}
