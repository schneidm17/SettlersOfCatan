package edu.up.cs301.catan;

import android.graphics.Color;
import java.util.ArrayList;

/**
 * Created by goldey17 on 10/27/2015.
 * Class to create a tile object which contains all the information relating to tiles.
 */
public class Tile {
    //Instance Variables
    private int numberOfTile;
    private int rollNumber;
    private int resource;
    private byte [] adjacentRoads;
    private byte [] adjacentBuildings;

    //Variables to eliminate use of random integers
    public static final int TOTAL_NUMBER_OF_ROAD_SPOTS = 72;
    public static final int SMALLEST_NUMBER_OF_ROAD_SPOTS = 0;
    public static final int TOTAL_NUMBER_OF_BUILDING_SPOTS = 54;
    public static final int SMALLEST_NUMBER_OF_BUILDING_SPOTS = 0;
    public static final int TOTAL_NUMBER_ROADS_ADJACENT_TO_A_ROAD = 4;
    public static final int SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_ROAD = 0;
    public static final int TOTAL_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD = 2;
    public static final int SMALLEST_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD = 0;
    public static final int TOTAL_NUMBER_OF_TILE_SPOTS = 19;
    public static final int SMALLEST_NUMBER_OF_TILE_SPOTS = 0;
    public static final int LARGEST_ROLL_NUMBER = 12;
    public static final int SMALLEST_ROLL_NUMBER = 2;


    public static final int DESERT = 0;
    public static final int LUMBER = 1;
    public static final int ORE = 2;
    public static final int BRICK = 3;
    public static final int WOOL = 4;
    public static final int WHEAT = 5;

    //Constructor
    public Tile (int newTileNumber, int newRollNumber, int newResource, byte[] newAdjacentBuildings){
        setNumber(newTileNumber);
        setRollNumber(newRollNumber);
        resource = newResource;
        adjacentBuildings = newAdjacentBuildings;
    }

    //Method to return the tile number
    public int getNumber(){
        return numberOfTile;
    }

    //Method to return the roll number on the tile
    public int getRollNumber(){
        return rollNumber;
    }

    //Method to return the type of resource the tile is
    public int getResource() {
        return resource;
    }

    //Method to return the array of adjacent buildings
    public byte[] getAdjacentBuildings(){
        return adjacentBuildings;
    }

    //Method to set the number of the tile
    public void setNumber( int newNumber){
        if (newNumber >= SMALLEST_NUMBER_OF_TILE_SPOTS && newNumber < TOTAL_NUMBER_OF_TILE_SPOTS){
            numberOfTile = newNumber;
        }
    }

    //Method to set the roll number
    public void setRollNumber(int newRollNumber){
        if (newRollNumber <= LARGEST_ROLL_NUMBER && newRollNumber >= SMALLEST_ROLL_NUMBER){
            rollNumber = newRollNumber;
        }
    }

    //Method to set the resource type of the tile
    public void setResource( int newResource){
        if ( newResource == DESERT || newResource == LUMBER || newResource == WHEAT ||
                newResource == WOOL || newResource == ORE || newResource == BRICK){
            resource = newResource;
        }
    }

    //Method to set one of the roads that are adjacent
    public void setAdjacentRoads ( int index, byte number){
        if (index < TOTAL_NUMBER_ROADS_ADJACENT_TO_A_ROAD &&
                index >= SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_ROAD &&
                number < TOTAL_NUMBER_OF_ROAD_SPOTS && number >= SMALLEST_NUMBER_OF_ROAD_SPOTS){
            adjacentRoads[index] = number;
        }
    }

    //Method to set one of the buildings that are adjacent
    public void setAdjacentBuildings ( int index, byte number){
        if (index < TOTAL_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD &&
                index >= SMALLEST_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD &&
                number < TOTAL_NUMBER_OF_BUILDING_SPOTS &&
                number >= SMALLEST_NUMBER_OF_BUILDING_SPOTS){
            adjacentBuildings[index] = number;
        }
    }

    //Method to check if a given road is adjacent. Returns true if adjacent
    public boolean isAdjacentRoad (int roadNumber){
        for(int i = SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_ROAD; i < TOTAL_NUMBER_ROADS_ADJACENT_TO_A_ROAD; i++){
            if (adjacentRoads[i] == roadNumber){
                return true;
            }
        }
        return false;
    }

    //Method to check if a given building is adjacent. Returns true if adjacent
    public boolean isAdjacentBuilding (int buildingNumber){
        for(int i = SMALLEST_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD; i < TOTAL_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD; i++){
            if (adjacentBuildings[i] == buildingNumber){
                return true;
            }
        }
        return false;
    }
}
