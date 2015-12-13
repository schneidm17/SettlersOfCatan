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
     * callback method--game's state has changed, the player must react
     *
     * @param info
     * 		the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof CatanGameState)
        {
            CatanGameState gameState = new CatanGameState((CatanGameState) info);

            if(playerNum == gameState.getPlayersID()) { //Is the players turn

                Hand myHand = gameState.getHand(playerNum);

                //INITIAL PLACEMENT BLOCK
                if(initialPlacement) {
                    int maxScore = -1;
                    int maxIndex = -1;
                    Tile[] tiles = gameState.getTiles();

                    //Place first settlement
                    if (myHand.getSettlementsAvail() == 5)
                    {
                        for (int i = 0; i < Building.TOTAL_NUMBER_OF_BUILDING_SPOTS; i++) {
                            if (gameState.canBuildSettlement(i)) {
                                int score = 0;
                                byte[] tileAdjList = gameState.buildingToTileAdjList[i];
                                int[] resList = new int[tileAdjList.length];

                                //Add rank for each roll num and resource if do not have
                                for (int j = 0; j < tileAdjList.length; j++) {
                                    //If the player does not have this resource, add to spot score
                                    resList[j] = tiles[tileAdjList[j]].getResource();
                                    if(tiles[tileAdjList[j]].getResource() != 0) {
                                        if (!resourcesHave[tiles[tileAdjList[j]].getResource() - 1]) {
                                            score += 5;
                                        }

                                        if(tiles[tileAdjList[j]].getResource() == Tile.WHEAT)
                                        {
                                         score += 2;
                                        }

                                        //Adds score for rollnu}
                                        score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7))/2 + 1;

                                        //Add score if it 'red' number
                                        if(tiles[tileAdjList[j]].getRollNumber() == 6 || tiles[tileAdjList[j]].getRollNumber() == 8)
                                        {
                                            score += 1;
                                        }
                                    }
                                }

                                //Detract score if adjacent resources are the same
                                if(resList.length > 1) {
                                    if (resList[0] == resList[1]) {
                                        score -= 5;
                                    } else if (resList.length > 2) {
                                        if (resList[0] == resList[2] || resList[1] == resList[2]) {
                                            score -= 5;
                                        }
                                    }
                                }

                                //Reassign max if current spot is better
                                if (score > maxScore) {
                                    maxScore = score;
                                    maxIndex = i;
                                }
                                //If a second spot with the same score is found, 50/50 chance of usurping
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

                    //Place first road
                    if (myHand.getRoadsAvail() == 15)
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

                    //Place second settlement using same process but more weight on resources
                    if (myHand.getSettlementsAvail() == 4)
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

                                        if(tiles[tileAdjList[j]].getResource() == Tile.WHEAT)
                                        {
                                            score += 2;
                                        }

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

                    //Place second road
                    if(myHand.getRoadsAvail() == 14)
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

                Building[] buildings = gameState.getBuildings();
                Tile[] tiles = gameState.getTiles();
                Road[] roads = gameState.getRoads();
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

                //Ensures that an error does not occur
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

                //error checking again
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

                //Place the best road they can if available
                if (gameState.playerHasRoadRes() && myHand.getLumber() > spotChecker && myHand.getBrick() > spotChecker) {

                    if(gameState.getTurnCount() % 2 == 0)
                    {
                        maxIndex = buildRoadEven(gameState);
                        if(maxIndex > -1)
                        {
                            game.sendAction(new CatanBuildRoadAction(this, maxIndex));
                        }
                    }
                    else
                    {
                        maxIndex = buildRoadOdd(gameState);
                        if(maxIndex > -1)
                        {
                            game.sendAction(new CatanBuildRoadAction(this, maxIndex));
                        }
                    }
                }

                //Randomize the roads if no smart roads can be placed
                //Does not include roads that lead to settlement dead ends
                if (gameState.playerHasRoadRes() && myHand.getLumber() > spotChecker && myHand.getBrick() > spotChecker) {
                    for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
                        if (gameState.canBuildRoad(i)) {
                            byte[] buildAdjList = gameState.roadToBuildingAdjList[i];
                            boolean adjToSettlement = false;

                            for(int j = 0; j < buildAdjList.length; j++)
                            {
                                if(buildings[buildAdjList[j]].getPlayer() != playerNum &&
                                        buildings[buildAdjList[j]].getPlayer() != Building.EMPTY)
                                {
                                    adjToSettlement = true;
                                }
                            }

                            if(!adjToSettlement) {
                                actions.add(new CatanBuildRoadAction(this, i));
                            }
                        }
                    }
                }

                if(actions.size() > 0)
                {
                    game.sendAction(actions.get(RNG.nextInt(actions.size())));
                    return;
                }

                if(checkResources(myHand)) //Checks hand to see if a trade should be done
                {
                    return;
                }

                //No more actions, end the turn
                game.sendAction(new CatanEndTurnAction(this));
            }

        }
    }//receiveInfo

    /**
     * checkResources
     *
     * Checks the hand of resources to see if the player can trade in resources
     *
     * Computers trade at 3:1 ratio instead of 4:1 ratio for difficulty
     *
     * @param hand the player's hand
     * @return if a trade was made
     */
    protected boolean checkResources(Hand hand)
    {
        //Trade resources for settlement res if possible
        if(hand.getSettlementsAvail() > 1) {
            if (hand.getBrick() == 0) {
                if (hand.getWool() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 3, 0, -1, 0));
                    return true;
                } else if (hand.getOre() >= 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, -1, 3));
                    return true;
                } else if (hand.getLumber() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 3, 0, 0, -1, 0));
                    return true;
                } else if (hand.getWheat() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 3, -1, 0));
                    return true;
                }
            }

            if (hand.getLumber() == 0) {
                if (hand.getWool() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 3, 0, 0, 0));
                    return true;
                } else if (hand.getOre() >= 3) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 0, 3));
                    return true;
                } else if (hand.getWheat() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 0, 3, 0, 0));
                    return true;
                } else if (hand.getBrick() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 3, 0));
                    return true;
                }
            }

            if (hand.getWheat() == 0) {
                if (hand.getWool() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 3, -1, 0, 0));
                    return true;
                } else if (hand.getOre() >= 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 0, 3));
                    return true;
                } else if (hand.getLumber() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 3, 0, -1, 0, 0));
                    return true;
                } else if (hand.getBrick() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 3, 0));
                    return true;
                }
            }

            if (hand.getWool() == 0) {
                if (hand.getOre() >= 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 0, 3));
                    return true;
                } else if (hand.getWheat() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, -1, 3, 0, 0));
                    return true;
                } else if (hand.getLumber() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 3, -1, 0, 0, 0));
                    return true;
                } else if (hand.getBrick() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 3, 0));
                    return true;
                }
            }
        }
        else if(hand.getCitiesAvail() > 0) //Trade for city res if only have 1 settlement or less left
        {
            if (hand.getOre() < 3) {
                if (hand.getWool() > 2) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 3, 0, 0, -1));
                    return true;
                } else if (hand.getWheat() > 4) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 3, 0, -1));
                    return true;
                } else if (hand.getLumber() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 3, 0, 0, 0, -1));
                    return true;
                } else if (hand.getBrick() > 3) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 3, -1));
                    return true;
                }
            }

            if (hand.getWheat() < 2) {
                if (hand.getWool() > 2) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 3, -1, 0, 0));
                    return true;
                } else if (hand.getLumber() > 2) {
                    game.sendAction(new CatanRemoveResAction(this, 3, 0, -1, 0, 0));
                    return true;
                } else if (hand.getBrick() > 2) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 3, 0));
                    return true;
                }
            }
        }

        if(hand.getTotal() > 7) //get rid of cards at random to go below 7 in case of robber
        {
            if(hand.getWool() > 3)
            {
                switch(RNG.nextInt(3))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 3, 0, 0, 0));
                        return true;

                    case 1: //Add wheat
                        game.sendAction(new CatanRemoveResAction(this, 0, 3, -1, 0, 0));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 0, 3, 0, -1, 0));
                        return true;

                    case 3:
                        game.sendAction(new CatanRemoveResAction(this, 0, 3, 0, 0, -1));
                        return true;
                }
            }
            else if(hand.getOre() >= 3)
            {
                switch(RNG.nextInt(3))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 0, 3));
                        return true;

                    case 1: //Add wheat
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 0, 3));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, -1, 3));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 0, 3));
                        return true;
                }
            }
            else if(hand.getWheat() > 3)
            {
                switch(RNG.nextInt(3))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 0, 3, 0, 0));
                        return true;

                    case 1: //Add rock
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 3, 0, -1));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 3, -1, 0));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 0, -1, 3, 0, 0));
                        return true;
                }
            }
            else if(hand.getLumber() > 3)
            {
                switch(RNG.nextInt(3))
                {
                    case 0: //Add a wheat to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, 3, 0, -1, 0, 0));
                        return true;

                    case 1: //Add rock
                        game.sendAction(new CatanRemoveResAction(this, 3, 0, 0, 0, -1));
                        return true;

                    case 2: //Add brick
                        game.sendAction(new CatanRemoveResAction(this, 3, 0, 0, -1, 0));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 3, -1, 0, 0, 0));
                        return true;
                }
            }
            else if(hand.getBrick() > 3)
            {
                switch(RNG.nextInt(3))
                {
                    case 0: //Add a wood to reduce amount down
                        game.sendAction(new CatanRemoveResAction(this, -1, 0, 0, 3, 0));
                        return true;

                    case 1: //Add rock
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 3, -1));
                        return true;

                    case 2: //Add wheat
                        game.sendAction(new CatanRemoveResAction(this, 0, 0, -1, 3, 0));
                        return true;

                    case 3: //Add sheep
                        game.sendAction(new CatanRemoveResAction(this, 0, -1, 0, 3, 0));
                        return true;
                }
            }
        }
        return false;
    }//checkResources

    /**
     * placeRobber
     *
     * Finds the best place to place the robber
     *
     * @param gameState the gamestate sent to the player
     */
    protected void placeRobber(CatanGameState gameState)
    {
        Building[] buildings = gameState.getBuildings();
        Tile[] tiles = gameState.getTiles();
        int[] scores = gameState.getScores();

        int maxRank = -1;
        int maxIndex = -1;
        for(int i = 0; i < 19; i++) {
            if(i == gameState.getRobber()) {
                continue;
            }
            else if(tiles[i].getResource() == Tile.DESERT)
            {
                continue;
            }

            boolean adjToPlayer = false;
            boolean adjToOthers = false;
            byte[] adjList = CatanGameState.tileToBuildingAdjList[i];
            int ranking = 0;

            //Add ranking for rollnums
            ranking += (6 - Math.abs(tiles[i].getRollNumber() - 7)) / 2 + 1;

            if(tiles[i].getRollNumber() == 6 || tiles[i].getRollNumber() == 8)
            {
                ranking += 1;
            }


            for(int j = 0; j < adjList.length; j++)
            {
                if(buildings[adjList[j]].getPlayer() == this.playerNum)
                {
                    //This spot has the player's buildings adjacent to it, should not place
                    adjToPlayer = true;
                    break;
                }
                else if(!buildings[adjList[j]].isEmpty())
                {
                    //This spot it adjacent to the enemy
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
            else if(!adjToPlayer && adjToOthers && ranking == maxRank && RNG.nextBoolean())
            {
                maxIndex = i;
            }
        }

        if(-1 < maxIndex) {
            game.sendAction(new CatanMoveRobberAction(this, maxIndex));
        }
        else //No possible choices, happens if player is adjacent to everything, revert to dumb
        {
            //Log.d("DUMB ALGORITHM USED: ", "player " + playerNum + " used dumb algorithm");
            super.randomizeRobber(gameState);
        }
    }//placeRobber

    /**
     * removeResources
     *
     * Chooses which resources to remove from the players hand when a 7 is rolled
     *
     * @param myHand The players hand
     */
    protected void removeResources(Hand myHand)
    {
        if(myHand.getTotal() <= 7) //No need to discard anything
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

            while(woodToLose + sheepToLose + wheatToLose + brickToLose + rockToLose < totalToLose) {
                while (true) //The breaks in the first if statements break this loop
                {
                    if (myHand.getWool() - sheepToLose > 1) {
                        sheepToLose++;
                        break;
                    }

                    if (myHand.getOre() - rockToLose > 3) {
                        rockToLose++;
                        break;
                    }

                    if (myHand.getWheat() - wheatToLose > 2) {
                        wheatToLose++;
                        break;
                    }

                    if (myHand.getLumber() - woodToLose > 2) {
                        woodToLose++;
                        break;
                    }

                    if (myHand.getBrick() - brickToLose > 2) {
                        brickToLose++;
                        break;
                    }


                    //Player has too much stuff, will randomly discard now
                    switch (RNG.nextInt(5)) {
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
            }
            game.sendAction(new CatanRemoveResAction(this, woodToLose, sheepToLose, wheatToLose,
                    brickToLose, rockToLose));
        }
    }//removeResources

    /**
     * buildRoadEven
     *
     * Searches the board for the best spot to build a road, starting at spot 0
     *
     * @return if a road was built or not
     */
    public int buildRoadEven(CatanGameState gameState)
    {
        int maxScore = -1;
        int maxIndex = -1;
        Tile[] tiles = gameState.getTiles();
        Building[] buildings = gameState.getBuildings();
        for (int i = 0; i < Road.TOTAL_NUMBER_OF_ROAD_SPOTS; i++) {
            if (gameState.canBuildRoad(i)) {

                int score = 0;
                byte[] adjList = gameState.roadToBuildingAdjList[i];
                for (int k = 0; k < adjList.length; k++) {
                    boolean ableToBuildSettlement = true;

                    if(!buildings[adjList[k]].isEmpty())
                    {
                        ableToBuildSettlement = false;
                    }

                    //Get list of adjacent buildings and roads to the current spot
                    byte[] buildingAdjList = gameState.buildingToBuildingAdjList[adjList[k]];

                    //Check to see if a building is too close, if so return false
                    for(int j = 0; j < buildingAdjList.length; j++)
                    {
                        if(!buildings[buildingAdjList[j]].isEmpty())
                        {
                            ableToBuildSettlement = false;
                        }
                    }

                    //If the road leads to a settlement spot, adds score to the spot based on rollnums
                    if (ableToBuildSettlement) {
                        byte[] tileAdjList = gameState.buildingToTileAdjList[adjList[k]];
                        for (int j = 0; j < tileAdjList.length; j++) {
                            if (tiles[tileAdjList[j]].getResource() == Tile.DESERT) {
                                score -= 5;
                            } else {
                                score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7)) / 2 + 1;
                            }
                        }
                    }
                }

                //Reassigns max score if current score is larger
                if(score <= 0)
                {
                    //Do not place this road
                }
                else if (score > maxScore) {
                    maxScore = score;
                    maxIndex = i;
                }
                else if (score == maxScore && RNG.nextBoolean()) {
                    maxIndex = i;
                }
            }
        }

        return maxIndex;
    }//buildRoadEven

    /**
     * buildRoadOdd
     *
     * Searches the board for the best spot to build a road, starting at spot 71
     *
     * @return if a road was built or not
     */
    public int buildRoadOdd(CatanGameState gameState)
    {
        int maxScore = -1;
        int maxIndex = -1;
        Tile[] tiles = gameState.getTiles();
        Building[] buildings = gameState.getBuildings();
        for (int i = Road.TOTAL_NUMBER_OF_ROAD_SPOTS - 1; i > -1; i--) {
            if (gameState.canBuildRoad(i)) {

                int score = 0;
                byte[] adjList = gameState.roadToBuildingAdjList[i];
                for (int k = 0; k < adjList.length; k++) {
                    boolean ableToBuildSettlement = true;

                    if(!buildings[adjList[k]].isEmpty())
                    {
                        ableToBuildSettlement = false;
                    }

                    //Get list of adjacent buildings and roads to the current spot
                    byte[] buildingAdjList = gameState.buildingToBuildingAdjList[adjList[k]];

                    //Check to see if a building is too close, if so return false
                    for(int j = 0; j < buildingAdjList.length; j++)
                    {
                        if(!buildings[buildingAdjList[j]].isEmpty())
                        {
                            ableToBuildSettlement = false;
                        }
                    }

                    //If the road leads to a settlement spot, adds score to the spot based on rollnums
                    if (ableToBuildSettlement) {
                        byte[] tileAdjList = gameState.buildingToTileAdjList[adjList[k]];
                        for (int j = 0; j < tileAdjList.length; j++) {
                            if (tiles[tileAdjList[j]].getResource() == Tile.DESERT) {
                                score -= 5;
                            } else {
                                score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7)) / 2 + 1;
                            }
                        }
                    }
                }

                //Reassigns max score if current score is larger
                if(score <= 0)
                {
                   //Do not place this road
                }
                else if (score > maxScore) {
                    maxScore = score;
                    maxIndex = i;
                }
                else if (score == maxScore && RNG.nextBoolean()) {
                    maxIndex = i;
                }
            }
        }
        return maxIndex;
    }//buildRoadOdd
}//CatanSmartComputerPlayer
