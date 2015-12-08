package edu.up.cs301.catan;

import java.io.Serializable;

/**
 * Created by goldey17 on 10/27/2015.
 * Class to create a road object which contains all the information relating to roads.
 */
public class Road implements Serializable {
    //Instance Variables
    private int number;
    private boolean isEmpty;
    private int player;
    private byte [] adjacentRoads;
    private byte [] adjacentBuildings;

    //Variables to eliminate use of random integers
    public static final int EMPTY = -1;
    public static final int TOTAL_NUMBER_OF_ROAD_SPOTS = 72;
    public static final int SMALLEST_NUMBER_OF_ROAD_SPOTS = 0;
    public static final int TOTAL_NUMBER_OF_PLAYERS = 4;
    public static final int SMALLEST_NUMBER_OF_PLAYERS = 0;

    /**
     * Constructor
     * @param newNumber
     * @param newAdjacentRoads
     * @param newAdjacentBuildings
     */
    public Road (int newNumber, byte[] newAdjacentRoads, byte[] newAdjacentBuildings){
        setNumber(newNumber);
        isEmpty = true;
        player = EMPTY;
        adjacentRoads = newAdjacentRoads;
        adjacentBuildings = newAdjacentBuildings;
    }

    /**
     * Method to return the road number
     * @return road number
     */
    public int getNumber(){
        return number;
    }

    /**
     * Method to return if the road is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty(){
        return isEmpty;
    }

    /**
     * Method to return the number of the player who has a road built
     * @return player who has built on the spot
     */
    public int getPlayer(){
        return player;
    }

    /**
     * Method to set the number of the road
     * @param newNumber
     */
    public void setNumber( int newNumber){
        if (newNumber >= SMALLEST_NUMBER_OF_ROAD_SPOTS && newNumber < TOTAL_NUMBER_OF_ROAD_SPOTS){
            number = newNumber;
        }
    }

    /**
     * Method to set if the road is empty or full
     * @param empty
     */
    public void setIsEmpty( boolean empty){
        isEmpty = empty;
    }

    /**
     * Method to set the number of the player who owns the road
     * @param newPlayer
     */
    public void setPlayer( int newPlayer){
        if (newPlayer <= TOTAL_NUMBER_OF_PLAYERS && newPlayer >= SMALLEST_NUMBER_OF_PLAYERS){
            player = newPlayer;
        }
    }
}
