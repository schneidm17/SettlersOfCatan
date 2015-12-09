package edu.up.cs301.catan;

import java.io.Serializable;

/**
 * Created by oney18 on 10/27/2015.
 *
 * Class used to keep track of how many cards a player has at any time
 */
public class Hand implements Serializable{
    private int wheat;
    private int wool;
    private int lumber;
    private int brick;
    private int ore;
    private int roadsAvail;
    private int settlementsAvail;
    private int citiesAvail;

    /**
     * Hand
     *
     * Constructor for the Hand class, sets default start of game values
     */
    public Hand()
    {
        wheat = 0;
        wool = 0;
        lumber = 0;
        brick = 0;
        ore = 0;
        roadsAvail = 15;
        settlementsAvail = 5;
        citiesAvail = 4;
    }

    /**
     * getTotal
     *
     * Returns total amount of resources
     *
     * @return the total number of resources
     */
    public int getTotal()
    {
        return wheat + wool + lumber + brick + ore;
    }

    /**
     * getWheat
     *
     * Returns total amount of wheat
     *
     * @return amount of wheat
     */
    public int getWheat()
    {
        return wheat;
    }

    /**
     * getWool
     *
     * Returns total amount of Wool
     *
     * @return amount of Wool
     */
    public int getWool()
    {
        return wool;
    }

    /**
     * getLumber
     *
     * Returns total amount of Lumber
     *
     * @return amount of Lumber
     */
    public int getLumber()
    {
        return lumber;
    }

    /**
     * getBrick
     *
     * Returns total amount of Brick
     *
     * @return amount of Brick
     */
    public int getBrick()
    {
        return brick;
    }

    /**
     * getOre
     *
     * Returns total amount of Ore
     *
     * @return amount of Ore
     */
    public int getOre()
    {
        return ore;
    }

    /**
     * addWheat
     *
     * Adds wheat to the hand
     *
     * @param count amount to increase by
     */
    public void addWheat(int count)
    {
        this.wheat += count;
    }

    /**
     * addWool
     *
     * Adds Wool to the hand
     *
     * @param count amount to increase by
     */
    public void addWool(int count)
    {
        this.wool += count;
    }

    /**
     * addLumber
     *
     * Adds Lumber to the hand
     *
     * @param count amount to increase by
     */
    public void addLumber(int count)
    {
        this.lumber += count;
    }

    /**
     * addLumber
     *
     * Adds Lumber to the hand
     *
     * @param count amount to increase by
     */
    public void addBrick(int count)
    {
        this.brick += count;
    }

    /**
     * addOre
     *
     * Adds Ore to the hand
     *
     * @param count amount to increase by
     */
    public void addOre(int count)
    {
        this.ore += count;
    }

    /**
     * removeWheat
     *
     * Removes wheat from the hand
     *
     * @param count amount to remove
     */
    public void removeWheat(int count)
    {
        this.wheat -= count;
    }

    /**
     * removeWool
     *
     * Removes Wool from the hand
     *
     * @param count amount to remove
     */
    public void removeWool(int count)
    {
        this.wool -= count;
    }

    /**
     * removeLumber
     *
     * Removes Lumber from the hand
     *
     * @param count amount to remove
     */
    public void removeLumber(int count)
    {
        this.lumber -= count;
    }

    /**
     * removeBrick
     *
     * Removes Brick from the hand
     *
     * @param count amount to remove
     */
    public void removeBrick(int count)
    {
        this.brick -= count;
    }

    /**
     * removeOre
     *
     * Removes Ore from the hand
     *
     * @param count amount to remove
     */
    public void removeOre(int count)
    {
        this.ore -= count;
    }

    /**
     * checkIfEmpty
     *
     * Checks to see if the player has any of a given resource
     *
     * @param type the resource type
     * @return if the type amount is <= 0
     */
    public boolean checkIfEmpty(int type)
    {
        switch(type)
        {
            case Tile.LUMBER:
                return lumber <= 0;

            case Tile.ORE:
                return ore <= 0;

            case Tile.BRICK:
                return brick <= 0;

            case Tile.WOOL:
                return wool <= 0;

            case Tile.WHEAT:
                return wheat <= 0;

            default:
                return false;
        }
    }

    /**
     * stealResource
     *
     * Removes one of the given type of resource
     *
     * @param type the type of resource to steal
     * @return returns true if able to steal
     */
    public boolean stealResource(int type)
    {
        switch(type)
        {
            case Tile.LUMBER:
                lumber--;
                return true;

            case Tile.ORE:
                ore--;
                return true;

            case Tile.BRICK:
                brick--;
                return true;

            case Tile.WOOL:
                wool--;
                return true;

            case Tile.WHEAT:
                wheat--;
                return true;

            default:
                return false;
        }
    }

    /**
     * addResource
     *
     * Adds one resource of type to the hand
     *
     * @param type the resource type
     * @return returns true if successful
     */
    public boolean addResource(int type)
    {
        switch(type)
        {
            case Tile.LUMBER:
                lumber++;
                return true;

            case Tile.ORE:
                ore++;
                return true;

            case Tile.BRICK:
                brick++;
                return true;

            case Tile.WOOL:
                wool++;
                return true;

            case Tile.WHEAT:
                wheat++;
                return true;

            default:
                return false;
        }
    }

    /**
     * buildRoad
     *
     * decrements how many roads are available by 1
     */
    public void buildRoad()
    {
        roadsAvail--;
    }

    /**
     * buildSettlement
     *
     * decrements how many settlements are available by 1
     */
    public void buildSettlement()
    {
        settlementsAvail--;
    }

    /**
     * upgradeSettlement
     *
     * decrements how many cities are available by 1
     */
    public void upgradeSettlement()
    {
        settlementsAvail++;
        citiesAvail--;
    }

    /**
     * getRoadsAvail
     *
     * @return amount of roads available
     */
    public int getRoadsAvail()
    {
        return roadsAvail;
    }

    /**
     * getSettlementsAvail
     *
     * @return amount of settlements available
     */
    public int getSettlementsAvail()
    {
        return settlementsAvail;
    }

    /**
     * getCitiesAvail
     *
     * @return amount of cities available
     */
    public int getCitiesAvail()
    {
        return citiesAvail;
    }
}
