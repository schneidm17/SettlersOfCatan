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
    protected boolean[] resourcesHave;
    protected boolean initialPlacement;

    /**
     * Constructor for computer player
     *
     * Creates random used for placement, initializes resourcesHave
     * @param name
     */
    public CatanComputerPlayer(String name) {
        super(name);
        RNG = new Random();
        resourcesHave = new boolean[5];
        for(int i = 0; i < resourcesHave.length; i++)
        {
            resourcesHave[i] = false;
        }

        initialPlacement = true;
    }

    /**
     * callback method--game's state has changed, the player reacts
     *
     * @param info
     * 		the information (presumably containing the game's state)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        if(info instanceof CatanGameState)
        {
            CatanGameState gameState = (CatanGameState) info;

            if(playerNum == gameState.getPlayersID()) { //Player's turn to play

                //Get the hand
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

                                        //If the resource does not have an adjacent settlement, adds score
                                        if (!resourcesHave[tiles[tileAdjList[j]].getResource() - 1]) {
                                            score += 5;
                                        }

                                        //Add score dependent on the roll num
                                        score += (6 - Math.abs(tiles[tileAdjList[j]].getRollNumber() - 7))/2 + 1;

                                        //Add score to 'red' numbers
                                        if(tiles[tileAdjList[j]].getRollNumber() == 6 || tiles[tileAdjList[j]].getRollNumber() == 8)
                                        {
                                            score += 1;
                                        }
                                    }
                                }

                                //Reduces score if adjacent resources are the same
                                if(resList.length > 1) {
                                    if (resList[0] == resList[1]) {
                                        score -= 5;
                                    } else if (resList.length > 2) {
                                        if (resList[0] == resList[2] || resList[1] == resList[2]) {
                                            score -= 5;
                                        }
                                    }
                                }

                                //Reassign maxScore/Index if current spot is better
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

                    if (myHand.getRoadsAvail() == 15) //Place first road randomly
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

                    //Place the second settlement with same process as above,
                    // with more weight for resources
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

                    if(myHand.getRoadsAvail() == 14) //Place second road randomly
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

                if (gameState.getNeedToRoll()) //A roll call is needed at beginnign of turn
                {
                    game.sendAction(new CatanRollAction(this));
                }

                if (gameState.getRobberWasRolledPlayer()) //Player must discard resources if total is over 7
                {
                    this.removeResources(myHand);
                }

                if (gameState.isRolled7()) //Player needs to place the robber
                {
                    this.randomizeRobber(gameState);
                }

                //List of actions to do
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

                if(actions.size() > 1) //Build the city
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

                if(actions.size() > 1) //Build the settlements
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

    /**
     * randomizeRobber
     *
     * Randomly chooses a spot not adjacent to the player and puts
     * the robber there
     *
     * @param gameState a copy of the gamestate sent to player
     */
    protected void randomizeRobber(CatanGameState gameState)
    {
        Building[] buildings = gameState.getBuildings();
        ArrayList<CatanMoveRobberAction> actions = new ArrayList<CatanMoveRobberAction>(19);

        for(int i = 0; i < Tile.TOTAL_NUMBER_OF_TILE_SPOTS; i++) {

            //Robber currently at this spot
            if(i == gameState.getRobber()) {
                continue;
            }

            boolean adjToPlayer = false;
            byte[] adjList = CatanGameState.tileToBuildingAdjList[i];

            for(int j = 0; j < adjList.length; j++)
            {
                //Checks if the player is adjacent to the spot
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

        //Choose a spot to place robber
        game.sendAction(actions.get(RNG.nextInt(actions.size())));
    }//randomizeRobber

    /**
     * removeResources
     *
     * This method decides which cards to remove if a 7 was rolled
     *
     * @param myHand The players hand
     */
    protected void removeResources(Hand myHand)
    {
        if(myHand.getTotal() <= 7) //No need to discard anything
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
            int totalToLose = (int) Math.floor(myHand.getTotal()*0.5); //Needs to discard half, rounded down

            //Randomly add cards to discard until correct amount attained
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
    }//removeResources

}//CatanComputerPlayer
