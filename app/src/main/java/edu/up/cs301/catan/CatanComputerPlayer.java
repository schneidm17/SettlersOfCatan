package edu.up.cs301.catan;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanBuildSettlementAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanMoveRobberAction;
import edu.up.cs301.catan.actions.CatanRemoveResAction;
import edu.up.cs301.catan.actions.CatanRollAction;
import edu.up.cs301.catan.actions.CatanUpgradeSettlementAction;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * The easy AI for Catan
 *
 * @author Oney, Goldey, Schneider
 * @version November 2015
 */
public class CatanComputerPlayer extends GameComputerPlayer {

    protected Random RNG; //Used for choosing what to do
    /**
     * ctor does nothing extra
     */
    public CatanComputerPlayer(String name) {
        super(name);
        RNG = new Random();
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
            CatanGameState gameState = (CatanGameState) info;

            if(playerNum == gameState.getPlayersID()) {

                if(gameState.getRound1Placing())
                {

                }

                if (gameState.getNeedToRoll()) //A roll call is needed at beginnign of turn
                {
                    game.sendAction(new CatanRollAction(this));
                }

                if (gameState.getRobberWasRolledPlayer()) //Player must discard resources if total is over 7
                {
                    Hand myHand = gameState.getHand(this.playerNum);
                    this.removeResources(myHand);
                }

                if (gameState.isRolled7()) //Player needs to place the robber
                {
                    this.randomizeRobber(gameState);
                }

                ArrayList<GameAction> actions = new ArrayList<GameAction>();

                //Ending the turn is always an option
                actions.add(new CatanEndTurnAction(this));

                //If they can upgrade a settlement, add them to the list
                if (gameState.playerHasCityRes()) {
                    for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                        if (gameState.canUpgradeSettlement(i)) {
                            actions.add(new CatanUpgradeSettlementAction(this, i));
                        }
                    }
                }

                if(actions.size() > 1)
                {
                    game.sendAction(actions.get(RNG.nextInt(actions.size())));
                }


                //If they can build settlements, add them to the list
                if (gameState.playerHasSettlementRes()) {
                    for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                        if (gameState.canBuildSettlement(i)) {
                            actions.add(new CatanBuildSettlementAction(this, i));
                        }
                    }
                }

                if(actions.size() > 1)
                {
                    game.sendAction(actions.get(RNG.nextInt(actions.size())));
                }

                //If they can build roads, add them to the list
                if (gameState.playerHasRoadRes()) {
                    for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
                        if (gameState.canBuildRoad(i)) {
                            actions.add(new CatanBuildRoadAction(this, i));
                        }
                    }
                }

                game.sendAction(actions.get(RNG.nextInt(actions.size())));
            }

        }
    }//receiveInfo

    protected void randomizeRobber(CatanGameState gameState)
    {
        Building[] buildings = gameState.getBuildings();
        ArrayList<CatanMoveRobberAction> actions = new ArrayList<CatanMoveRobberAction>();

        for(int i = 0; i < Tile.TOTAL_NUMBER_OF_TILE_SPOTS; i++) {
            if(i == gameState.getRobber()) {
                continue;
            }

            boolean adjToPlayer = false;
            byte[] adjList = CatanGameState.tileToBuildingAdjList[i];

            for(int j = 0; j < adjList.length; j++)
            {
                if(buildings[adjList[j]].getPlayer() == this.playerNum)
                {
                    adjToPlayer = true;
                }
            }

            if(!adjToPlayer) //Tile i is not adjacent to player, can place there
            {
                actions.add(new CatanMoveRobberAction(this, i));
            }
        }

        game.sendAction(actions.get(RNG.nextInt(actions.size())));
    }

    protected void removeResources(Hand myHand)
    {
        if(myHand.getTotal() <= 7)
        {
            game.sendAction(new CatanRemoveResAction(this, 0 , 0, 0, 0, 0));
        }
        else
        {
            int woodToLose = 0;
            int sheepToLose = 0;
            int wheatToLose = 0;
            int brickToLose = 0;
            int rockToLose = 0;
            int totalToLose = (int) Math.ceil(myHand.getTotal()*0.5);

            while(woodToLose + sheepToLose + wheatToLose + brickToLose + rockToLose < totalToLose)
            switch(RNG.nextInt(5))
            {
                case 0:
                    if(myHand.getLumber() - woodToLose > 0) {
                        woodToLose++;
                    }
                    break;

                case 1:
                    if(myHand.getWool() - sheepToLose > 0) {
                        sheepToLose++;
                    }
                    break;

                case 2:
                    if(myHand.getWheat() - wheatToLose > 0) {
                        wheatToLose++;
                    }
                    break;

                case 3:
                    if(myHand.getBrick() - brickToLose > 0) {
                        brickToLose++;
                    }
                    break;

                case 4:
                    if(myHand.getOre() - rockToLose > 0) {
                        rockToLose++;
                    }
                    break;

            }
            game.sendAction(new CatanRemoveResAction(this, woodToLose, sheepToLose, wheatToLose,
                                                        brickToLose, rockToLose));
        }
    }

}
