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

    /**
     * ctor does nothing extra
     *
     * @param name
     */
    public CatanSmartComputerPlayer(String name) {
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
            CatanGameState gameState = (CatanGameState) info;

            if(playerNum == gameState.getPlayersID()) {
                Hand myHand = gameState.getHand(playerNum);

                //INITIAL PLACEMENT BLOCK
                if(initialPlacement) {
                    int maxScore = -1;
                    int maxIndex = -1;
                    Tile[] tiles = gameState.getTiles();
                    if (myHand.getSettlementsAvail() == 5) //Place first settlement
                    {
                        for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                            if (gameState.canBuildSettlement(i)) {
                                int score = 0;
                                byte[] tileAdjList = gameState.buildingToTileAdjList[i];
                                int[] resList = new int[tileAdjList.length];

                                //Add rank for each roll num and resrouce if do not have
                                for (int j = 0; j < tileAdjList.length; j++) {
                                    //If the player does not have this resource, add to spot score
                                    resList[j] = tiles[tileAdjList[j]].getResource();
                                    if(tiles[tileAdjList[j]].getResource() != 0) {
                                        if (!resourcesHave[tiles[tileAdjList[j]].getResource() - 1]) {
                                            score += 5;
                                        }

                                        score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7))/2 + 1;

                                        if(tiles[tileAdjList[j]].getRollNumber() == 6 || tiles[tileAdjList[j]].getRollNumber() == 8)
                                        {
                                            score += 1;
                                        }
                                    }
                                }

                                if(resList.length > 1) {
                                    if (resList[0] == resList[1]) {
                                        score -= 5;
                                    } else if (resList.length > 2) {
                                        if (resList[0] == resList[2] || resList[1] == resList[2]) {
                                            score -= 5;
                                        }
                                    }
                                }

                                if (score > maxScore) {
                                    maxScore = score;
                                    maxIndex = i;
                                }
                                else if (score == maxScore && RNG.nextBoolean()) {
                                    maxIndex = i;
                                }
                            }
                        }

                        //Add to the resources obtained list
                        byte[] finalTileAdjList = gameState.buildingToTileAdjList[maxIndex];
                        for (int i = 0; i < finalTileAdjList.length; i++) {
                            if(tiles[finalTileAdjList[i]].getResource() != 0) {
                                resourcesHave[tiles[finalTileAdjList[i]].getResource() - 1] = true;
                            }
                        }

                        //Send the best build spot
                        game.sendAction(new CatanBuildSettlementAction(this, maxIndex));
                        return;
                    }

                    //TODO make this smart
                    if (myHand.getRoadsAvail() == 15) //Place first road
                    {
                        ArrayList<CatanBuildRoadAction> actions = new ArrayList<CatanBuildRoadAction>(3);
                        for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
                            if (gameState.canBuildRoad(i)) {
                                actions.add(new CatanBuildRoadAction(this, i));
                            }
                        }

                        game.sendAction(actions.get(RNG.nextInt(actions.size())));
                        return;
                    }

                    if (myHand.getSettlementsAvail() == 4) //Place second settlement
                    {
                        for (int i = Building.TOTAL_NUMBER_OF_BUILDING_SPOTS - 1; i > -1; i--) {
                            if (gameState.canBuildSettlement(i)) {
                                int score = 0;
                                byte[] tileAdjList = gameState.buildingToTileAdjList[i];
                                int[] resList = new int[tileAdjList.length];

                                //Add rank for each roll num and resrouce if do not have
                                for (int j = 0; j < tileAdjList.length; j++) {
                                    //If the player does not have this resource, add to spot score
                                    resList[j] = tiles[tileAdjList[j]].getResource();
                                    if(tiles[tileAdjList[j]].getResource() != 0) {
                                        if (!resourcesHave[tiles[tileAdjList[j]].getResource() - 1]) {
                                            score += 15;
                                        }

                                        score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7))/2;

                                        if(tiles[tileAdjList[j]].getRollNumber() == 6 || tiles[tileAdjList[j]].getRollNumber() == 8)
                                        {
                                            score += 1;
                                        }
                                    }
                                }

                                if(resList.length > 1) {
                                    if (resList[0] == resList[1]) {
                                        score -= 10;
                                    } else if (resList.length > 2) {
                                        if (resList[0] == resList[2] || resList[1] == resList[2]) {
                                            score -= 10;
                                        }
                                    }
                                }

                                if (score > maxScore) {
                                    maxScore = score;
                                    maxIndex = i;
                                }
                                else if (score == maxScore && RNG.nextBoolean()) {
                                    maxIndex = i;
                                }
                            }
                        }

                        //Add to the resources obtained list
                        byte[] finalTileAdjList = gameState.buildingToTileAdjList[maxIndex];
                        for (int i = 0; i < finalTileAdjList.length; i++) {
                            if(tiles[finalTileAdjList[i]].getResource() != 0) {
                                resourcesHave[tiles[finalTileAdjList[i]].getResource() - 1] = true;
                            }
                        }

                        //Send the best build spot
                        game.sendAction(new CatanBuildSettlementAction(this, maxIndex));
                        return;
                    }

                    //TODO make this smart
                    if(myHand.getRoadsAvail() == 14) //Place second road
                    {
                        ArrayList<CatanBuildRoadAction> actions = new ArrayList<CatanBuildRoadAction>(3);
                        for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {

                            if (gameState.canBuildRoad(i)) {
                                actions.add(new CatanBuildRoadAction(this, i));
                            }
                        }

                        game.sendAction(actions.get(RNG.nextInt(actions.size())));
                        return;
                    }

                    //All initial placements have finished, sets boolean to skip this block
                    initialPlacement = false;
                }
                //END PLACEMENT BLOCK


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

                if(checkResources(myHand))
                {
                    return;
                }


                Building[] buildings = gameState.getBuildings();
                Tile[] tiles = gameState.getTiles();
                ArrayList<GameAction> actions = new ArrayList<GameAction>(30);

                //Used for ranking moves
                int maxScore = -1;
                int maxIndex = -1;

                //If they can upgrade a settlement, find the best to upgrade
                if (gameState.playerHasCityRes()) {
                    for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                        if (gameState.canUpgradeSettlement(i)) {
                            int score = 0;
                            byte[] tileAdjList = gameState.buildingToTileAdjList[i];

                            for(int j = 0; j < tileAdjList.length; j++)
                            {
                                score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7))/2 + 1;
                            }

                            if (score > maxScore) {
                                maxScore = score;
                                maxIndex = i;
                            }
                            else if (score == maxScore && RNG.nextBoolean()) {
                                maxIndex = i;
                            }
                        }
                    }
                }

                if(maxIndex > -1)
                {
                    game.sendAction(new CatanUpgradeSettlementAction(this, maxIndex));
                    return;
                }


                //If they can build settlements, find the best spot to build
                if (gameState.playerHasSettlementRes()) {
                    for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                        if (gameState.canBuildSettlement(i)) {
                            int score = 0;
                            byte[] tileAdjList = gameState.buildingToTileAdjList[i];

                            for(int j = 0; j < tileAdjList.length; j++)
                            {
                                score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7))/2 + 1;
                            }

                            if (score > maxScore) {
                                maxScore = score;
                                maxIndex = i;
                            }
                            else if (score == maxScore && RNG.nextBoolean()) {
                                maxIndex = i;
                            }
                        }
                    }
                }

                if(maxIndex > -1)
                {
                    game.sendAction(new CatanBuildSettlementAction(this, maxIndex));
                    return;
                }

                int spotChecker = 0;
                for(int i = 0; i < buildings.length; i++)
                {
                    if(gameState.canBuildSettlement(i)) //Player has a spot for settlement, should save resources
                    {
                        spotChecker = 1;
                        break;
                    }
                }

                //If they can build good roads, add them to the list
                if (gameState.playerHasRoadRes() && myHand.getLumber() > spotChecker && myHand.getBrick() > spotChecker) {
                    if(gameState.getTurnCount() % 2 == 0) {
                        for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
                            if (gameState.canBuildRoad(i)) {
                                CatanGameState tempState = new CatanGameState(gameState);
                                tempState.buildRoad(i);
                                int score = 0;
                                byte[] adjList = tempState.roadToBuildingAdjList[i];
                                for (int k = 0; k < adjList.length; k++) {
                                    if (tempState.canBuildSettlement(adjList[k])) {
                                        byte[] tileAdjList = tempState.buildingToTileAdjList[adjList[k]];
                                        for (int j = 0; j < tileAdjList.length; j++) {
                                            if (tiles[tileAdjList[j]].getResource() == Tile.DESERT) {
                                                score -= 5;
                                            } else {
                                                score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7)) / 2 + 1;
                                            }
                                        }
                                    }
                                }
                                if (score > maxScore) {
                                    maxScore = score;
                                    maxIndex = i;
                                }
                                else if (score == maxScore && RNG.nextBoolean()) {
                                    maxIndex = i;
                                }
                            }
                        }

                        if (maxIndex > -1) {
                            game.sendAction(new CatanBuildRoadAction(this, maxIndex));
                            return;
                        }
                    }
                    else
                    {
                        for (int i = Road.TOTAL_NUMBER_OF_ROAD_SPOTS - 1; i > -1; i--) {
                            if (gameState.canBuildRoad(i)) {
                                CatanGameState tempState = new CatanGameState(gameState);
                                tempState.buildRoad(i);
                                int score = 0;
                                byte[] adjList = tempState.roadToBuildingAdjList[i];
                                for (int k = 0; k < adjList.length; k++) {
                                    if (tempState.canBuildSettlement(adjList[k])) {
                                        byte[] tileAdjList = tempState.buildingToTileAdjList[adjList[k]];
                                        for (int j = 0; j < tileAdjList.length; j++) {
                                            if (tiles[tileAdjList[j]].getResource() == Tile.DESERT) {
                                                score -= 5;
                                            } else {
                                                score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7)) / 2 + 1;
                                            }
                                        }
                                    }
                                }
                                if (score > maxScore) {
                                    maxScore = score;
                                    maxIndex = i;
                                }
                                else if (score == maxScore && RNG.nextBoolean()) {
                                    maxIndex = i;
                                }
                            }
                        }

                        if (maxIndex > -1) {
                            game.sendAction(new CatanBuildRoadAction(this, maxIndex));
                            return;
                        }
                    }
                }

                //Randomize the roads if no smart roads can be placed
                if (gameState.playerHasRoadRes() && myHand.getLumber() > spotChecker && myHand.getBrick() > spotChecker) {
                    for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
                        if (gameState.canBuildRoad(i)) {
                            actions.add(new CatanBuildRoadAction(this, i));
                        }
                    }
                }

                if(actions.size() > 0)
                {
                    game.sendAction(actions.get(RNG.nextInt(actions.size())));
                    return;
                }

                game.sendAction(new CatanEndTurnAction(this));
            }

        }
    }//receiveInfo

    protected boolean checkResources(Hand hand)
    {
        //wood sheep wheat brick rock
        if(hand.getSettlementsAvail() > 1) {
            if (hand.getBrick() == 0) {
                if (hand.getWool() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 4, 0, -1, 0));
                    return true;
                } else if (hand.getOre() >= 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, -1, 4));
                    return true;
                } else if (hand.getLumber() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 4, 0, 0, -1, 0));
                    return true;
                } else if (hand.getWheat() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 4, -1, 0));
                    return true;
                }
            }

            if (hand.getLumber() == 0) {
                if (hand.getWool() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 4, 0, 0, 0));
                    return true;
                } else if (hand.getOre() >= 4) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 0, 4));
                    return true;
                } else if (hand.getWheat() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 0, 4, 0, 0));
                    return true;
                } else if (hand.getBrick() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 4, 0));
                    return true;
                }
            }

            if (hand.getWheat() == 0) {
                if (hand.getWool() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 4, -1, 0, 0));
                    return true;
                } else if (hand.getOre() >= 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 0, 4));
                    return true;
                } else if (hand.getLumber() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 4, 0, -1, 0, 0));
                    return true;
                } else if (hand.getBrick() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 4, 0));
                    return true;
                }
            }

            if (hand.getWool() == 0) {
                if (hand.getOre() >= 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 0, 4));
                    return true;
                } else if (hand.getWheat() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, -1, 4, 0, 0));
                    return true;
                } else if (hand.getLumber() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 4, -1, 0, 0, 0));
                    return true;
                } else if (hand.getBrick() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 4, 0));
                    return true;
                }
            }
        }
        else if(hand.getCitiesAvail() > 0)
        {
            if (hand.getOre() < 3) {
                if (hand.getWool() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 4, 0, 0, -1));
                    return true;
                } else if (hand.getWheat() > 5) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 4, 0, -1));
                    return true;
                } else if (hand.getLumber() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 4, 0, 0, 0, -1));
                    return true;
                } else if (hand.getBrick() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 4, -1));
                    return true;
                }
            }

            if (hand.getWheat() < 2) {
                if (hand.getWool() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 4, -1, 0, 0));
                    return true;
                } else if (hand.getLumber() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 4, 0, -1, 0, 0));
                    return true;
                } else if (hand.getBrick() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 4, 0));
                    return true;
                }
            }
        }

        if(hand.getTotal() > 7)
        {
            if(hand.getWool() > 4)
            {
                switch(RNG.nextInt(4))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 4, 0, 0, 0));
                        return true;

                    case 1: //Add wheat
                        game.sendAction(new CatanRemoveResAction(this, 0, 4, -1, 0, 0));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 0, 4, 0, -1, 0));
                        return true;

                    case 3:
                        game.sendAction(new CatanRemoveResAction(this, 0, 4, 0, 0, -1));
                        return true;
                }
            }
            else if(hand.getOre() >= 4)
            {
                switch(RNG.nextInt(4))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 0, 4));
                        return true;

                    case 1: //Add wheat
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 0, 4));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, -1, 4));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 0, 4));
                        return true;
                }
            }
            else if(hand.getWheat() > 4)
            {
                switch(RNG.nextInt(4))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 0, 4, 0, 0));
                        return true;

                    case 1: //Add rock
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 4, 0, -1));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 4, -1, 0));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 0, -1, 4, 0, 0));
                        return true;
                }
            }
            else if(hand.getLumber() > 4)
            {
                switch(RNG.nextInt(4))
                {
                    case 0: //Add a wheat to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, 4, 0, -1, 0, 0));
                        return true;

                    case 1: //Add rock
                        game.sendAction(new CatanRemoveResAction(this, 4, 0, 0, 0, -1));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 4, 0, 0, -1, 0));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 4, -1, 0, 0, 0));
                        return true;
                }
            }
            else if(hand.getBrick() > 4)
            {
                switch(RNG.nextInt(4))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 4, 0));
                        return true;

                    case 1: //Add rock
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 4, -1));
                        return true;

                    case 2: //Add wheat
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 4, 0));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 4, 0));
                        return true;
                }
            }
        }
        return false;
    }

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
            int totalToLose = (int) Math.floor(myHand.getTotal()*0.5);

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
                            rockToLose++;
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
