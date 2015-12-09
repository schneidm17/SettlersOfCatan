package edu.up.cs301.catan;

import java.io.Serializable;

/**
 * Created by goldey17 on 10/27/2015.
 * Class to create a building object which contains all the information relating to a building.
 */
public class Building implements Serializable {
    //Instance Variables
    private int number;
    private boolean isEmpty;
    private int player;
    private int typeOfBuilding;
    private byte [] adjacentRoads;
    private byte [] adjacentTiles;

    //Variables to eliminate use of random integers
    public static final int EMPTY = -1;
    public static final int SETTLEMENT = 0;
    public static final int CITY = 1;
    public static final int TOTAL_NUMBER_OF_BUILDING_SPOTS = 54;
    public static final int SMALLEST_NUMBER_OF_BUILDING_SPOTS = 0;
    public static final int TOTAL_NUMBER_OF_PLAYERS = 4;
    public static final int SMALLEST_NUMBER_OF_PLAYERS = 0;

    /**
     * Constructor
     * @param newNumber
     * @param newAdjacentRoads
     * @param newAdjacentTiles
     */
    public Building (int newNumber, byte[] newAdjacentRoads, byte[] newAdjacentTiles){
        setNumber(newNumber);
        isEmpty = true;
        player = EMPTY;
        typeOfBuilding = EMPTY;
        adjacentRoads = newAdjacentRoads;
        adjacentTiles = newAdjacentTiles;
    }

    /**
     * Method to return the building number
     * @return building number
     */
    public int getNumber(){
        return number;
    }

    /**
     * Method to return if the building spot is empty
     * @return true if the building is empty, false otherwise
     */
    public boolean isEmpty(){
        return isEmpty;
    }

    /**
     * Method to return the number of the player who has a building built
     * @return number of player who has built on spot
     */
    public int getPlayer(){
        return player;
    }

    /**
     * Method to return the type of building
     * @return type of building on spot
     */
    public int getTypeOfBuilding(){
        return typeOfBuilding;
    }

    /**
     * Method to set the number of the building
     * @param newNumber
     */
    public void setNumber( int newNumber){
        if (newNumber >= SMALLEST_NUMBER_OF_BUILDING_SPOTS && newNumber < TOTAL_NUMBER_OF_BUILDING_SPOTS){
            number = newNumber;
        }
    }

    /**
     * Method to set if the building is empty or full
     * @param empty
     */
    public void setIsEmpty( boolean empty){
        isEmpty = empty;
    }

    /**
     * Method to set the number of the player who owns the building
     * @param newPlayer
     */
    public void setPlayer( int newPlayer){
        if (newPlayer < TOTAL_NUMBER_OF_PLAYERS && newPlayer >= SMALLEST_NUMBER_OF_PLAYERS){
            player = newPlayer;
        }
    }

    /**
     * Method to set the type of building
     * @param type
     */
    public void setTypeOfBuilding (int type){
        if ( type == SETTLEMENT || type == CITY) {
            typeOfBuilding = type;
        }
    }
}