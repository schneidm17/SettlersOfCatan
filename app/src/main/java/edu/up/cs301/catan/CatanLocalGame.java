package edu.up.cs301.catan;

import edu.up.cs301.catan.actions.CatanAddResAction;
import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanBuildSettlementAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanMoveRobberAction;
import edu.up.cs301.catan.actions.CatanRemoveResAction;
import edu.up.cs301.catan.actions.CatanRollAction;
import edu.up.cs301.catan.actions.CatanUpgradeSettlementAction;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * class CatanLocalGame controls the play of the game
 *
 * @author Oney, Goldey, Schneider
 * @version November 2015
 */
public class CatanLocalGame extends LocalGame {

    private CatanGameState gameState;

    /**
     * This ctor creates a new game state
     */
    public CatanLocalGame() {
        gameState = new CatanGameState();
    }

    /**
     * can the player with the given id take an action right now?
     */
    @Override
    public boolean canMove(int playerIdx) {
        return playerIdx == gameState.getPlayersID();
    }

    /**
     * This method is called when a new action arrives from a player
     *
     * @return true if the action was taken or false if the action was invalid/illegal.
     */
    @Override
    public boolean makeMove(GameAction action) {

        if(this.canMove(this.getPlayerIdx(action.getPlayer())))
        {
            if(action instanceof CatanBuildRoadAction)
            {
                gameState.buildRoad(((CatanBuildRoadAction) action).spot);
                return true;
            }
            else if(action instanceof CatanBuildSettlementAction)
            {
                gameState.buildSettlement(((CatanBuildSettlementAction) action).spot);
                return true;
            }
            else if(action instanceof CatanEndTurnAction)
            {
                gameState.endTurn();
                return true;
            }
            else if(action instanceof CatanMoveRobberAction)
            {
                gameState.moveRobber(((CatanMoveRobberAction) action).spot);
                return true;
            }
            else if(action instanceof CatanRemoveResAction)
            {
                gameState.removeResources(((CatanRemoveResAction) action).woodToLose,((CatanRemoveResAction) action).sheepToLose,
                        ((CatanRemoveResAction) action).wheatToLose, ((CatanRemoveResAction) action).brickToLose,
                        ((CatanRemoveResAction) action).rockToLose);
                return true;
            }else if(action instanceof CatanAddResAction)
            {
                gameState.addResources(((CatanAddResAction) action).woodToGain, ((CatanAddResAction) action).sheepToGain,
                        ((CatanAddResAction) action).wheatToGain, ((CatanAddResAction) action).brickToGain,
                        ((CatanAddResAction) action).rockToGain);
                return true;
            }else if(action instanceof CatanRollAction)
            {
                gameState.roll();
                return true;
            }
            else if(action instanceof CatanUpgradeSettlementAction)
            {
                gameState.upgradeSettlement(((CatanUpgradeSettlementAction) action).spot);
                return true;
            }
        }
        return false;

    }//makeMove

    /**
     * send the updated state to a given player
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        CatanGameState copy = new CatanGameState(gameState);
        p.sendInfo(copy);
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
        int[] scores = gameState.getScores();
        if(scores[gameState.getPlayersID()] >= CatanGameState.VICTORY_POINTS_TO_WIN)
        {
            return "Player " + gameState.getPlayersID() + ", " + playerNames[gameState.getPlayersID()] +", wins!";
        }
        return null;
    }

    @Override
    public void start(GamePlayer[] players)
    {
        gameState.setNumPlayers(players.length);
        super.start(players);
    }

}// class CatanLocalGame
