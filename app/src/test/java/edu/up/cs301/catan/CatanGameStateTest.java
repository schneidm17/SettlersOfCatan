package edu.up.cs301.catan;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test Cases to test GameState for all methods.
 *
 * @author Jarrett Oney
 * @author Jordan Goldey
 * @author Matthew Schneider
 *
 * @version Nov 2015
 */
public class CatanGameStateTest {

    @Test
    public void testCatanGameState() throws Exception {
        CatanGameState soc = new CatanGameState();
        soc.setNumPlayers(4);
        soc.setPlayersID(0);

        //Alter the state via rolls, manipulating resources and spots
        soc.generateBuilding(22, 0, Building.SETTLEMENT); //creates a 0 settlement at 22
        soc.generateBuilding(41, 2, Building.CITY); //creates a 2 city at 41
        soc.generateRoad(19, 3); //creates a 3 road at 19
        soc.givePlayerResources(1); //gives 1 10 of everything
        soc.generateRoll(4, 3); //alters robberWasRolled

        //Test Copy Ctor
        CatanGameState testSoc = new CatanGameState(soc);

        Hand[] testHands = testSoc.getHands();
        Building[] testBuildings = testSoc.getBuildings();
        Road[] testRoads = testSoc.getRoads();
        boolean[] testRob = testSoc.getRobberWasRolled();

        assertEquals(testBuildings[22].isEmpty(), false);
        assertEquals(testBuildings[22].getPlayer(), 0);
        assertEquals(testBuildings[22].getTypeOfBuilding(), Building.SETTLEMENT);

        assertEquals(testBuildings[41].isEmpty(), false);
        assertEquals(testBuildings[41].getPlayer(), 2);
        assertEquals(testBuildings[41].getTypeOfBuilding(), Building.CITY);

        assertEquals(testBuildings[4].isEmpty(), true); //Should be empty

        assertEquals(testRoads[19].isEmpty(), false);
        assertEquals(testRoads[19].getPlayer(), 3);

        assertEquals(testRoads[5].isEmpty(), true); //Should be empty

        assertEquals(testHands[1].getTotal(), 50);
        assertEquals(testHands[2].getOre(), 0);
        assertEquals(testHands[3].getTotal(), 0); //Should be at 0

        assertEquals(testRob[0], true); //Same result for all entries in matrix
    }

    @Test
    public void testRemoveResources() throws Exception {
        CatanGameState soc = new CatanGameState();
        soc.setNumPlayers(4);
        soc.setPlayersID(0);
        Hand[] testHands;
        //Player 0 now has 10 of everything
        soc.givePlayerResources(0);
        testHands = soc.getHands();
        assertEquals(testHands[0].getLumber(), 10);
        assertEquals(testHands[0].getWool(), 10);
        assertEquals(testHands[0].getWheat(), 10);
        assertEquals(testHands[0].getBrick(), 10);
        assertEquals(testHands[0].getOre(), 10);
        assertEquals(testHands[0].getTotal(), 50);

        //Remove resources from 0's hand
        soc.removeResources(3, 4, 5, 6, 7);
        testHands = soc.getHands();

        assertEquals(testHands[0].getLumber(), 7);
        assertEquals(testHands[0].getWool(), 6);
        assertEquals(testHands[0].getWheat(), 5);
        assertEquals(testHands[0].getBrick(), 4);
        assertEquals(testHands[0].getOre(), 3);
        assertEquals(testHands[0].getTotal(), 25);
    }

    @Test
    public void testBuildRoad() throws Exception {
        CatanGameState soc = new CatanGameState();
        soc.setNumPlayers(4);
        soc.setPlayersID(0);
        Road[] testRoads;
        Hand[] testHands;

        //Handles initial placement to ensure that tests run as before
        soc.generateBuilding(0, 0, Building.SETTLEMENT);
        soc.generateRoad(0, 0);
        soc.generateBuilding(1, 0, Building.SETTLEMENT);
        soc.generateRoad(1, 0);
        soc.generateBuilding(2, 1, Building.SETTLEMENT);
        soc.generateRoad(2, 0);
        soc.generateBuilding(3, 1, Building.SETTLEMENT);
        soc.generateRoad(3, 0);
        soc.generateBuilding(4, 2, Building.SETTLEMENT);
        soc.generateRoad(4, 0);
        soc.generateBuilding(5, 2, Building.SETTLEMENT);
        soc.generateRoad(5, 0);
        soc.generateBuilding(6, 3, Building.SETTLEMENT);
        soc.generateRoad(6, 0);
        soc.generateBuilding(7, 3, Building.SETTLEMENT);
        soc.generateRoad(7, 0);

        soc.givePlayerResources(0);
        soc.buildRoad(35); //should be unable to as nothing exists on the board
        testRoads = soc.getRoads();
        testHands = soc.getHands();
        assertEquals(testRoads[51].getPlayer(), Road.EMPTY);
        assertEquals(testRoads[35].isEmpty(), true); //nothing built in spot
        assertEquals(testHands[0].getTotal(), 50); //nothing should have been spent

        soc.generateBuilding(33, 0, Building.SETTLEMENT); //0 has a settlement at spot 33
        soc.generateRoad(44, 0); //0 has a road at spot 44, simulating starting conditions
        soc.buildRoad(51); //adjacent to spot 33, should be built

        testRoads = soc.getRoads();
        testHands = soc.getHands();
        assertEquals(testRoads[51].getPlayer(), 0);
        assertEquals(testRoads[51].isEmpty(), false); //road in spot
        assertEquals(testHands[0].getTotal(), 48); //2 resources spent, 1 wood 1 brick
        assertEquals(testHands[0].getLumber(), 9);
        assertEquals(testHands[0].getBrick(), 9);

        soc.generateRoad(57, 1); //1 has a road at 57, is adjacent to 51
        soc.buildRoad(57); //0 tries to build a road where 1 already has a road, should do nothing

        testRoads = soc.getRoads();
        testHands = soc.getHands();
        assertEquals(testRoads[57].getPlayer(), 1); //0 did not overwrite
        assertEquals(testHands[0].getTotal(), 48); //0 shoudl not have changed
        assertEquals(testHands[0].getLumber(), 9);
        assertEquals(testHands[0].getBrick(), 9);

        soc.removeResources(9, 10, 10, 9, 10); //0 has nothing
        soc.buildRoad(43); //0 tries to build at 43, but lacks resources, nothign should change

        testRoads = soc.getRoads();
        testHands = soc.getHands();
        assertEquals(testRoads[43].getPlayer(), Road.EMPTY); //0 did not overwrite
        assertEquals(testRoads[43].isEmpty(), true);
        assertEquals(testHands[0].getTotal(), 0); //0 should have nothing
        assertEquals(testHands[0].getLumber(), 0);
        assertEquals(testHands[0].getBrick(), 0);


    }

    @Test
    public void testBuildSettlement() throws Exception {
        CatanGameState soc = new CatanGameState();
        soc.setNumPlayers(4);
        soc.setPlayersID(0);
        Building[] testBuildings;
        Hand[] testHands;

        //Handles initial placement to ensure that tests run as before
        soc.generateBuilding(0, 0, Building.SETTLEMENT);
        soc.generateRoad(0, 0);
        soc.generateBuilding(1, 0, Building.SETTLEMENT);
        soc.generateRoad(1, 0);
        soc.generateBuilding(2, 1, Building.SETTLEMENT);
        soc.generateRoad(2, 0);
        soc.generateBuilding(3, 1, Building.SETTLEMENT);
        soc.generateRoad(3, 0);
        soc.generateBuilding(4, 2, Building.SETTLEMENT);
        soc.generateRoad(4, 0);
        soc.generateBuilding(5, 2, Building.SETTLEMENT);
        soc.generateRoad(5, 0);
        soc.generateBuilding(6, 3, Building.SETTLEMENT);
        soc.generateRoad(6, 0);
        soc.generateBuilding(7, 3, Building.SETTLEMENT);
        soc.generateRoad(7, 0);

        soc.givePlayerResources(0);
        soc.buildSettlement(21);
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();
        assertEquals(testBuildings[21].isEmpty(), true); //nothing is built since no adjacent road
        assertEquals(testHands[0].getTotal(), 50); //nothing should have been spent

        soc.generateRoad(28, 0); //force it to make a road

        soc.buildSettlement(21); //build a settlement
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();

        //Test building is made
        assertEquals(testBuildings[21].isEmpty(), false); //building spot is not empty
        assertEquals(testBuildings[21].getTypeOfBuilding(), Building.SETTLEMENT); //building is a settlement
        assertEquals(testBuildings[21].getPlayer(), 0); //building belongs to player 0

        //Test hand is decremented accordingly
        assertEquals(testHands[0].getTotal(), 46); //total resources removed
        assertEquals(testHands[0].getLumber(), 9); //wood resources removed
        assertEquals(testHands[0].getBrick(), 9); //brick resources removed
        assertEquals(testHands[0].getWool(), 9); //sheep resources removed
        assertEquals(testHands[0].getWheat(), 9); //wheat resources removed

        //Test score is increased

        assertEquals(soc.getScores()[0], 1); //Score increased by one

        //Go to next player
        soc.endTurn();

        soc.givePlayerResources(1);

        soc.generateRoad(27, 1); //force it to make a road

        soc.buildSettlement(20); //build a settlement
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();

        assertEquals(testBuildings[20].isEmpty(), true); //nothing is built since building is too close
        assertEquals(testHands[1].getTotal(), 50); //nothing should have been spent

        soc.buildSettlement(21); //build a settlement
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();

        assertEquals(testBuildings[21].isEmpty(), false); //nothing is built since building is player0's
        assertEquals(testBuildings[21].getPlayer(), 0); //building still belongs to player 0
        assertEquals(testHands[1].getTotal(), 50); //nothing should have been spent

        //Go to next player
        soc.endTurn();

        soc.generateRoad(12, 2); //force it to make a road

        soc.buildSettlement(10); //build a settlement
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();

        assertEquals(testBuildings[10].isEmpty(), true); //nothing is built since no resources
        assertEquals(testHands[2].getTotal(), 0); //nothing should have been spent
    }

    @Test
    public void testUpgradeSettlement() throws Exception {
        CatanGameState soc = new CatanGameState();
        soc.setNumPlayers(4);
        soc.setPlayersID(0);
        Building[] testBuildings;
        Hand[] testHands;

        //Build a building not owned by player
        soc.generateBuilding(21, 1, Building.SETTLEMENT); //force it to build a settlement

        //Try to upgrade a building the player doesn't own
        soc.upgradeSettlement(21);
        testBuildings = soc.getBuildings();
        assertEquals(testBuildings[21].isEmpty(), false); //building spot is not empty
        assertEquals(testBuildings[21].getTypeOfBuilding(), Building.SETTLEMENT); //building is still a settlement
        assertEquals(testBuildings[21].getPlayer(), 1); //building belongs to player 1

        //Give the player resources
        soc.givePlayerResources(0);

        //Build a building owned by player but a city
        soc.generateBuilding(21, 0, Building.CITY); //force it to build a settlement

        //Try to upgrade an already upgraded building
        soc.upgradeSettlement(21);
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();
        assertEquals(testBuildings[21].isEmpty(), false); //building spot is not empty
        assertEquals(testBuildings[21].getTypeOfBuilding(), Building.CITY); //building is still a settlement
        assertEquals(testBuildings[21].getPlayer(), 0); //building belongs to player 1
        assertEquals(testHands[0].getTotal(), 50); //nothing should have been spent

        //Build a road and a settlement for the player to upgrade
        soc.generateRoad(12, 0); //force it to build a road
        soc.generateBuilding(10, 0, Building.SETTLEMENT); //force it to build a settlement

        //Upgrade and make sure it did
        soc.upgradeSettlement(10);
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();
        assertEquals(testBuildings[10].isEmpty(), false); //building spot is not empty
        assertEquals(testBuildings[10].getTypeOfBuilding(), Building.CITY); //building is now a city
        assertEquals(testBuildings[10].getPlayer(), 0); //building belongs to player 0
        assertEquals(testHands[0].getTotal(), 45); //resources spent
        assertEquals(testHands[0].getOre(), 7); //rock resources removed
        assertEquals(testHands[0].getWheat(), 8); //wheat resources removed

        //Move to next player
        soc.endTurn();

        //Build a road and a settlement for the player to upgrade
        soc.generateRoad(1, 1); //force it to build a road
        soc.generateBuilding(1, 1, Building.SETTLEMENT); //force it to build a settlement

        //Try to upgrade and make sure it fails since no resources
        soc.upgradeSettlement(1);
        testBuildings = soc.getBuildings();
        testHands = soc.getHands();
        assertEquals(testBuildings[1].isEmpty(), false); //building spot is not empty
        assertEquals(testBuildings[1].getTypeOfBuilding(), Building.SETTLEMENT); //building is now a city
        assertEquals(testBuildings[1].getPlayer(), 1); //building belongs to player 1
        assertEquals(testHands[1].getTotal(), 0); //no resources spent
    }

    @Test
    //Tests the passing of turns to players, uses 4 for this case
    public void testEndTurn() throws Exception {
        //Test turn progression for 4 players
        CatanGameState soc = new CatanGameState();
        soc.setNumPlayers(4);
        soc.setPlayersID(0);

        assertEquals(soc.getPlayersID(), 0);

        soc.endTurn();
        assertEquals(soc.getPlayersID(), 1);

        soc.endTurn();
        assertEquals(soc.getPlayersID(), 2);

        soc.endTurn();
        assertEquals(soc.getPlayersID(), 3);

        soc.endTurn();
        assertEquals(soc.getPlayersID(), 0);

        //Test turn progression for 3 players
        CatanGameState soc1 = new CatanGameState();
        soc1.setNumPlayers(3);
        soc1.setPlayersID(0);

        assertEquals(soc1.getPlayersID(), 0);

        soc1.endTurn();
        assertEquals(soc1.getPlayersID(), 1);

        soc1.endTurn();
        assertEquals(soc1.getPlayersID(), 2);

        soc1.endTurn();
        assertEquals(soc1.getPlayersID(), 0);
    }
}