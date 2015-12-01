package edu.up.cs301.catan;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanBuildSettlementAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanMoveRobberAction;
import edu.up.cs301.catan.actions.CatanRemoveResAction;
import edu.up.cs301.catan.actions.CatanRollAction;
import edu.up.cs301.catan.actions.CatanUpgradeSettlementAction;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * The hard AI for Catan
 *
 * @author Oney, Goldey, Schneider
 * @version November 2015
 */
public class CatanSmartComputerPlayer extends CatanComputerPlayer{

    private boolean[] resourcesHave;
    /**
     * ctor does nothing extra
     *
     * @param name
     */
    public CatanSmartComputerPlayer(String name) {
        super(name);
        resourcesHave = new boolean[5];
        for(int i = 0; i < resourcesHave.length; i++)
        {
            resourcesHave[i] = false;
        }
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
                Hand myHand = gameState.getHand(playerNum);

                if (gameState.getNeedToRoll()) //A roll call is needed at beginning of turn
                {
                    game.sendAction(new CatanRollAction(this));
                    return;
                }

                if (gameState.getRobberWasRolledPlayer()) //Player must discard resources if total is over 7
                {
                    this.removeResources(myHand);
                    return;
                }

                if (gameState.isRolled7()) //Player needs to place the robber
                {
                    this.placeRobber(gameState);
                    return;
                }

                Building[] buildings = gameState.getBuildings();
                Tile[] tiles = gameState.getTiles();
                ArrayList<GameAction> actions = new ArrayList<GameAction>(30);

                //TODO only update this on new buildings generated
                for (int i = 0; i < buildings.length; i++) {
                    if (buildings[i].getPlayer() == this.playerNum) {
                        byte[] adjList = CatanGameState.buildingToTileAdjList[i];
                        for (int j = 0; j < adjList.length; j++) {
                            resourcesHave[tiles[j].getResource() - 1] = true;
                        }
                    }
                }

                //If they can upgrade a settlement, add them to the list
                if (gameState.playerHasCityRes()) {
                    for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                        if (gameState.canUpgradeSettlement(i)) {
                            actions.add(new CatanUpgradeSettlementAction(this, i));
                        }
                    }
                }

                if(actions.size() > 0)
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

                if(actions.size() > 0)
                {
                    game.sendAction(actions.get(RNG.nextInt(actions.size())));
                }

                boolean placeToBuildSettlement = false;
                //If they can build roads, add them to the list
                if (gameState.playerHasRoadRes() && myHand.getLumber() > 1 && myHand.getBrick() > 1) {
                    for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
                        if (gameState.canBuildRoad(i)) {
                            actions.add(new CatanBuildRoadAction(this, i));
                        }
                    }
                }

                if(actions.size() > 0)
                {
                    game.sendAction(actions.get(RNG.nextInt(actions.size())));
                }

                game.sendAction(new CatanEndTurnAction(this));
            }

        }
    }//receiveInfo

    protected void placeRobber(CatanGameState gameState)
    {
        Building[] buildings = gameState.getBuildings();
        int[] scores = gameState.getScores();

        int maxRank = -1;
        int maxIndex = -1;
        for(int i = 0; i < 19; i++) {
            if(i == gameState.getRobber()) {
                continue;
            }
            boolean adjToPlayer = false;
            boolean adjToOthers = false;
            byte[] adjList = CatanGameState.tileToBuildingAdjList[i];
            int ranking = 0;

            for(int j = 0; j < adjList.length; j++)
            {
                if(buildings[adjList[j]].getPlayer() == this.playerNum)
                {
                    adjToPlayer = true;
                    break;
                }
                else if(!buildings[adjList[j]].isEmpty())
                {
                    adjToOthers = true;
                    ranking += 1;

                    //add ranking dependent on the players current points
                    ranking += scores[buildings[adjList[j]].getPlayer()];

                    if(buildings[adjList[j]].getTypeOfBuilding() == Building.CITY)
                    {
                        //add 1 ranking if the building is a city
                        ranking += 3;
                    }
                }
            }

            if(!adjToPlayer && adjToOthers && ranking > maxRank) //Tile i is not adjacent to player, can place there
            {
                maxRank = ranking;
                maxIndex = i;
            }
        }

        if(0 <= maxIndex && maxIndex <19) {
            game.sendAction(new CatanMoveRobberAction(this, maxIndex));
        }
        else //No possible choices, happens if player is adjacent to everything, revert to dumb
        {
            Log.d("DUMB ALGORITHM USED: ", "player " + playerNum + " used dumb algorithm");
            super.randomizeRobber(gameState);
        }
    }

    protected void removeResources(Hand myHand)
    {
        if(myHand.getTotal() <= 7)
        {
            game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 0, 0));
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
                while(true) //The breaks in the first if statements break this loop
                {
                    if(myHand.getWool() - sheepToLose > 1) {
                        sheepToLose++;
                        break;
                    }

                    if(myHand.getOre() - rockToLose > 3)
                    {
                        rockToLose++;
                        break;
                    }

                    if(myHand.getWheat() - wheatToLose > 2)
                    {
                        wheatToLose++;
                        break;
                    }

                    if(myHand.getLumber() - woodToLose > 2)
                    {
                        woodToLose++;
                        break;
                    }

                    if(myHand.getBrick() - brickToLose > 2)
                    {
                        brickToLose++;
                        break;
                    }


                    //Player has too much stuff, will randomly discard now
                    switch(RNG.nextInt(5)) {
                        case 0:
                         if (myHand.getLumber() - woodToLose > 0) {
                              woodToLose++;
                        }
                        break;

                    case 1:
                        if (myHand.getWool() - sheepToLose > 0) {
                            sheepToLose++;
                        }
                        break;

                    case 2:
                        if (myHand.getWheat() - wheatToLose > 0) {
                            wheatToLose++;
                        }
                        break;

                    case 3:
                        if (myHand.getBrick() - brickToLose > 0) {
                            brickToLose++;
                        }
                        break;

                    case 4:
                        if (myHand.getOre() - rockToLose > 0) {
                            sheepToLose++;
                        }
                        break;
                    }
                    break; //breaks out of infinite loop to check the total to discard
                }
            game.sendAction(new CatanRemoveResAction(this, woodToLose, sheepToLose, wheatToLose,
                    brickToLose, rockToLose));
        }
    }

}
