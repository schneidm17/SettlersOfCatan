package edu.up.cs301.catan;

import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * Created by oney18 on 11/10/2015.
 */
public class CatanSmartComputerPlayer extends CatanComputerPlayer{

    /**
     * ctor does nothing extra
     *
     * @param name
     */
    public CatanSmartComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof CatanGameState)
        {
            //TODO ranks moves, chooses what do to
        }
    }//receiveInfo

    protected void placeRobber(CatanGameState gameState)
    {
        //TODO ranks spots to place robber, chooses best spot
    }

}
