package edu.up.cs301.catan;

import android.graphics.Color;
import java.util.ArrayList;

/**
 * Created by goldey17 on 10/27/2015.
 * Class to create a building object which contains all the information relating to a building.
 */
public class Building {
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
    public static final int TOTAL_NUMBER_OF_TILE_SPOTS = 19;
    public static final int SMALLEST_NUMBER_OF_TILE_SPOTS = 0;
    public static final int TOTAL_NUMBER_OF_ROAD_SPOTS = 72;
    public static final int SMALLEST_NUMBER_OF_ROAD_SPOTS = 0;
    public static final int TOTAL_NUMBER_OF_PLAYERS = 4;
    public static final int SMALLEST_NUMBER_OF_PLAYERS = 0;
    public static final int TOTAL_NUMBER_TILES_ADJACENT_TO_A_BUILDING = 3;
    public static final int SMALLEST_NUMBER_TILES_ADJACENT_TO_A_BUILDING = 0;
    public static final int TOTAL_NUMBER_ROADS_ADJACENT_TO_A_BUILDING = 3;
    public static final int SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_BUILDING = 0;

    //Constructor
    public Building (int newNumber, byte[] newAdjacentRoads, byte[] newAdjacentTiles){
        setNumber(newNumber);
        isEmpty = true;
        player = EMPTY;
        typeOfBuilding = EMPTY;
        adjacentRoads = newAdjacentRoads;
        adjacentTiles = newAdjacentTiles;
    }

    //Method to return the building number
    public int getNumber(){
        return number;
    }

    //Method to return if the building spot is empty
    public boolean isEmpty(){
        return isEmpty;
    }

    //Method to return the number of the player who has a building built
    public int getPlayer(){
        return player;
    }

    //Method to return the type of building
    public int getTypeOfBuilding(){
        return typeOfBuilding;
    }

    //Method to return the array of adjacent roads
    public byte[] getAdjacentRoads(){
        return adjacentRoads;
    }

    //Method to return the array of adjacent buildings
    public byte[] getAdjacentTiles(){
        return adjacentTiles;
    }


    //Method to set the number of the building
    public void setNumber( int newNumber){
        if (newNumber >= SMALLEST_NUMBER_OF_BUILDING_SPOTS && newNumber < TOTAL_NUMBER_OF_BUILDING_SPOTS){
            number = newNumber;
        }
    }

    //Method to set if the building is empty or full
    public void setIsEmpty( boolean empty){
        isEmpty = empty;
    }

    //Method to set the number of the player who owns the building
    public void setPlayer( int newPlayer){
        if (newPlayer < TOTAL_NUMBER_OF_PLAYERS && newPlayer >= SMALLEST_NUMBER_OF_PLAYERS){
            player = newPlayer;
        }
    }

    //Method to set the type of building
    public void setTypeOfBuilding (int type){
        if ( type == SETTLEMENT || type == CITY) {
            typeOfBuilding = type;
        }
    }

    //Method to set one of the roads that are adjacent
    public void setAdjacentRoads ( int index, byte number){
        if (index < TOTAL_NUMBER_ROADS_ADJACENT_TO_A_BUILDING && index >= SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_BUILDING && number < TOTAL_NUMBER_OF_ROAD_SPOTS && number >= SMALLEST_NUMBER_OF_ROAD_SPOTS){
            adjacentRoads[index] = number;
        }
    }

    //Method to set one of the buildings that are adjacent
    public void setAdjacentTiles ( int index, byte number){
        if (index < TOTAL_NUMBER_TILES_ADJACENT_TO_A_BUILDING && index >=
                SMALLEST_NUMBER_TILES_ADJACENT_TO_A_BUILDING && number < TOTAL_NUMBER_OF_TILE_SPOTS
                && number >= SMALLEST_NUMBER_OF_TILE_SPOTS){
            adjacentTiles[index] = number;
        }
    }

    //Method to check if a given road is adjacent. Returns true if adjacent
    public boolean isAdjacentRoad (int roadNumber){
        for(int i = SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_BUILDING; i < TOTAL_NUMBER_ROADS_ADJACENT_TO_A_BUILDING; i++){
            if (adjacentRoads[i] == roadNumber){
                return true;
            }
        }
        return false;
    }

    //Method to check if a given tile is adjacent. Returns true if adjacent
    public boolean isAdjacentTile (int tileNumber){
        for(int i = SMALLEST_NUMBER_TILES_ADJACENT_TO_A_BUILDING; i < TOTAL_NUMBER_TILES_ADJACENT_TO_A_BUILDING; i++){
            if (adjacentTiles[i] == tileNumber){
                return true;
            }
        }
        return false;
    }
}