package edu.up.cs301.catan;

import java.io.Serializable;

/**
 * Created by goldey17 on 10/27/2015.
 * Class to create a tile object which contains all the information relating to tiles.
 */
public class Tile implements Serializable {
    //Instance Variables
    private int numberOfTile;
    private int rollNumber;
    private int resource;
    private byte [] adjacentRoads;
    private byte [] adjacentBuildings;

    //Variables to eliminate use of random integers
    public static final int TOTAL_NUMBER_OF_TILE_SPOTS = 19;
    public static final int SMALLEST_NUMBER_OF_TILE_SPOTS = 0;
    public static final int LARGEST_ROLL_NUMBER = 12;
    public static final int SMALLEST_ROLL_NUMBER = 2;

    //Variables for resources
    public static final int DESERT = 0;
    public static final int LUMBER = 1;
    public static final int ORE = 2;
    public static final int BRICK = 3;
    public static final int WOOL = 4;
    public static final int WHEAT = 5;

    /**
     * Constructor
     * @param newTileNumber
     * @param newRollNumber
     * @param newResource
     * @param newAdjacentBuildings
     */
    public Tile (int newTileNumber, int newRollNumber, int newResource, byte[] newAdjacentBuildings){
        setNumber(newTileNumber);
        setRollNumber(newRollNumber);
        resource = newResource;
        adjacentBuildings = newAdjacentBuildings;
    }

    /**
     * Method to return the tile number
     * @return tile number
     */
    public int getNumber(){
        return numberOfTile;
    }

    /**
     * Method to return the roll number on the tile
     * @return resource number on tile
     */
    public int getRollNumber(){
        return rollNumber;
    }

    /**
     * Method to return the type of resource the tile is
     * @return type of resource the tile is
     */
    public int getResource() {
        return resource;
    }

    /**
     * Method to set the number of the tile
     * @param newNumber
     */
    public void setNumber( int newNumber){
        if (newNumber >= SMALLEST_NUMBER_OF_TILE_SPOTS && newNumber < TOTAL_NUMBER_OF_TILE_SPOTS){
            numberOfTile = newNumber;
        }
    }

    /**
     * Method to set the roll number
     * @param newRollNumber
     */
    public void setRollNumber(int newRollNumber){
        if (newRollNumber <= LARGEST_ROLL_NUMBER && newRollNumber >= SMALLEST_ROLL_NUMBER){
            rollNumber = newRollNumber;
        }
    }
}
