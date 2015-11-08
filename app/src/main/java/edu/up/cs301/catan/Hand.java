package edu.up.cs301.catan;

/**
 * Created by oney18 on 10/27/2015.
 *
 * Class used to keep track of how many cards a player has at any time
 */
public class Hand {
    private int wheat;
    private int wool;
    private int lumber;
    private int brick;
    private int ore;

    //Initializes the hand with no resources in it
    Hand()
    {
        wheat = 0;
        wool = 0;
        lumber = 0;
        brick = 0;
        ore = 0;
    }

    //used for 7 roll GUI
    //Returns the total amount of resources
    public int getTotal()
    {
        return wheat + wool + lumber + brick + ore;
    }

    //Returns the amount of wheat
    public int getWheat()
    {
        return wheat;
    }

    //Returns the amount of wool
    public int getWool()
    {
        return wool;
    }

    //Returns the amount of lumber
    public int getLumber()
    {
        return lumber;
    }

    //Returns the amount of brick
    public int getBrick()
    {
        return brick;
    }

    //Returns the amount of ore
    public int getOre()
    {
        return ore;
    }

    //Adds wheat to the hand
    public void addWheat(int count)
    {
        this.wheat += count;
    }

    //Adds wool to the hand
    public void addWool(int count)
    {
        this.wool += count;
    }

    //Adds lumber to the hand
    public void addLumber(int count)
    {
        this.lumber += count;
    }

    //Adds brick to the hand
    public void addBrick(int count)
    {
        this.brick += count;
    }

    //Adds ore to the hand
    public void addOre(int count)
    {
        this.ore += count;
    }

    //Removes wheat from the hand
    public void removeWheat(int count)
    {
        this.wheat -= count;
    }

    //Removes wool from the hand
    public void removeWool(int count)
    {
        this.wool -= count;
    }

    //Removes lumber from the hand
    public void removeLumber(int count)
    {
        this.lumber -= count;
    }

    //Removes brick from the hand
    public void removeBrick(int count)
    {
        this.brick -= count;
    }

    //Removes ore from the hand
    public void removeOre(int count)
    {
        this.ore -= count;
    }

    //used when a 7 is rolled
    //Checks to see if the hand has any of the inputted resource
    public boolean checkIfEmpty(int type)
    {
        switch(type)
        {
            case Tile.LUMBER:
                return lumber == 0;

            case Tile.ORE:
                return ore == 0;

            case Tile.BRICK:
                return brick == 0;

            case Tile.WOOL:
                return wool == 0;

            case Tile.WHEAT:
                return wheat == 0;

            default:
                return false;
        }
    }

    //used when a 7 is rolled
    //Removes one of the inputted resource
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

    //used when a 7 is rolled
    //Adds one of the inputted resource
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
}
