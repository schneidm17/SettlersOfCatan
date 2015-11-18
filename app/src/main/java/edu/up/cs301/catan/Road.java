package edu.up.cs301.catan;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

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
    public static final int TOTAL_NUMBER_OF_BUILDING_SPOTS = 54;
    public static final int SMALLEST_NUMBER_OF_BUILDING_SPOTS = 0;
    public static final int TOTAL_NUMBER_ROADS_ADJACENT_TO_A_ROAD = 4;
    public static final int SMALLEST_NUMBER_ROADS_ADJACENT_TO_A_ROAD = 0;
    public static final int TOTAL_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD = 2;
    public static final int SMALLEST_NUMBER_BUILDINGS_ADJACENT_TO_A_ROAD = 0;

    //Constructor
    public Road (int newNumber, byte[] newAdjacentRoads, byte[] newAdjacentBuildings){
        setNumber(newNumber);
        isEmpty = true;
        player = EMPTY;
        adjacentRoads = newAdjacentRoads;
        adjacentBuildings = newAdjacentBuildings;
    }

    //Method to return the road number
    public int getNumber(){
        return number;
    }

    //Method to return if the road is empty
    public boolean isEmpty(){
        return isEmpty;
    }

    //Method to return the number of the player who has a road built
    public int getPlayer(){
        return player;
    }

    //Method to return the array of adjacent roads
    public byte[] getAdjacentRoads(){
        return adjacentRoads;
    }

    //Method to return the array of adjacent buildings
    public byte[] getAdjacentBuildings(){
        return adjacentBuildings;
    }

    //Method to set the number of the road
    public void setNumber( int newNumber){
        if (newNumber >= SMALLEST_NUMBER_OF_ROAD_SPOTS && newNumber < TOTAL_NUMBER_OF_ROAD_SPOTS){
            number = newNumber;
        }
    }

    //Method to set if the road is empty or full
    public void setIsEmpty( boolean empty){
        isEmpty = empty;
    }

    //Method to set the number of the player who owns the road
    public void setPlayer( int newPlayer){
        if (newPlayer <= TOTAL_NUMBER_OF_PLAYERS && newPlayer >= SMALLEST_NUMBER_OF_PLAYERS){
            player = newPlayer;
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
