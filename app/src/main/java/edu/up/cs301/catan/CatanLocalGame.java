package edu.up.cs301.catan;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * class CatanLocalGame controls the play of the game
 *
 * @author Andrew M. Nuxoll
 * @version August 2015
 */
public class CatanLocalGame extends LocalGame {

    /**
     * This ctor creates a new game state
     */
    public CatanLocalGame() {
        //TODO  You will implement this constructor
    }

    /**
     * can the player with the given id take an action right now?
     */
    @Override
    public boolean canMove(int playerIdx) {
        //TODO  You will implement this method
        return false;
    }

    /**
     * This method is called when a new action arrives from a player
     *
     * @return true if the action was taken or false if the action was invalid/illegal.
     */
    @Override
    public boolean makeMove(GameAction action) {
        //TODO  You will implement this method
        return false;

    }//makeMove

    /**
     * send the updated state to a given player
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        //TODO  You will implement this method
    }//sendUpdatedSate

    /**
     * Check if the game is over
     *
     * @return
     * 		a message that tells who has won the game, or null if the
     * 		game is not over
     */
    @Override
    public String checkIfGameOver() {
        //TODO  You will implement this method
        return null;
    }

}// class CatanLocalGame
