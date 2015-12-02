package edu.up.cs301.catan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;

import edu.up.cs301.game.R;

/**
 * Created by schneidm17 on 11/3/2015.
 *
 * The gameboard view for Catan.
 */
public class CatanSurfaceView extends SurfaceView {

    /*
     * The camera is stored in spherical coordinates as degrees {d, phi, theta}, which
     * make it easier for the user to rotate the board and prevents rounding errors, but
     * the function that maps the xyz coordinates of the board to the xy of the screen requires
     * cartesian coordinates {a, b, c} that are only calculated once after moving the camera
     */
    private double phi; //angle of the camera from the z axis (in spherical coordinates)
    private double theta; //angle of the camera from the x axis (in spherical coordinates)
    private double a; //the x coordinate of the view plane (in cartesian coordinates)
    private double b; //the y coordinate of the view plane (in cartesian coordinates)
    private double c; //the z coordinate of the view plane (in cartesian coordinates)
    private double k1; //the coefficient for the i unit vector of getX()
    private double k2; //the coefficient for the j unit vector of getX()
    private double k3; //the coefficient for the i unit vector of getY()
    private double k4; //the coefficient for the j unit vector of getY()
    private double k5; //the coefficient for the k unit vector of getY()
    private float cx; //the horizontal center of this SurfaceView
    private float cy; //the vertical center of this SurfaceView

    public final double d = 24; //distance of the camera from the origin (constant)
    public final double p = 18; //distance of the view plane from the origin (constant)
    public final double s = 250; //scale factor on the view plane
    public final double deg = 0.017453292519943295; //conversion factor for deg to rad
    public final double r3 = Math.sqrt(3); //square root of 3

    private CatanGameState gameState; //a copy of the game state used to draw this surface view

    /*
     * These local variables are only used within this class;
     * They are declared here to cut down on runtime.
     */
    Path path; //temporary path (frequently reused);
    Bitmap bitmap; //temporary bitmap (frequently reused);
    Paint temp; //style Paint.Style.FILL (frequently reused);
    Paint outline; //style Paint.Style.STROKE

    /*
     * These ints represent colors that are used in this class
     */
    final int wood = 0xFF466F37;
    final int wheat = 0xFFE7B23E;
    final int brick = 0xFFB16B32;
    final int stone = 0xFF969696;
    final int wool = 0xFF91C14B;
    final int sand = 0xFFE2C581;
    final int[] playerColor = {0xFFFF0000, 0xFFFFFFFF, 0xFF0000FF, 0xFFFF8000};

    /*
     * These variables relate to the selection process for sites, roads, and tiles
     */
    private int roadLastSelected = -1;
    private int buildingLastSelected = -1;
    private int tileLastSelected = -1;
    private boolean waitingForRoadSelection = false;
    private boolean waitingForSettlementSelection = false;
    private boolean waitingForCitySelection = false;
    private boolean waitingForRobberPlacement = false;

    /*
     * These bitmaps represent the numbers on the board
     */
    Bitmap num2, num3, num4, num5, num6, num8, num9, num10, num11, num12;

    /*
     * These are the physical locations on the board of every piece that we might draw
     * We have a numbering system in place so that we know that a settlement at site
     * ten is adjacent to tiles 1, 4 and 5 and roads 12, 13 amd 20;
     */

    public final double tiles[][] = {{-6, 2 * r3}, {-6, 0}, {-6, -2 * r3},
            {-3, 3 * r3}, {-3, r3}, {-3, -r3}, {-3, -3 * r3}, {0, 4 * r3}, {0, 2 * r3},
            {0, 0}, {0, -2 * r3}, {0, -4 * r3}, {3, 3 * r3}, {3, r3}, {3, -r3}, {3, -3 * r3},
            {6, 2 * r3}, {6, 0}, {6, -2 * r3}};

    public final double sites[][] = {{-7, 3 * r3}, {-8, 2 * r3}, {-7, 1 * r3},
            {-8, 0}, {-7, -1 * r3}, {-8, -2 * r3}, {-7, -3 * r3}, {-4, 4 * r3}, {-5, 3 * r3},
            {-4, 2 * r3}, {-5, 1 * r3}, {-4, 0}, {-5, -1 * r3}, {-4, -2 * r3}, {-5, -3 * r3},
            {-4, -4 * r3}, {-1, 5 * r3}, {-2, 4 * r3}, {-1, 3 * r3}, {-2, 2 * r3}, {-1, 1 * r3},
            {-2, 0}, {-1, -1 * r3}, {-2, -2 * r3}, {-1, -3 * r3}, {-2, -4 * r3}, {-1, -5 * r3},
            {1, 5 * r3}, {2, 4 * r3}, {1, 3 * r3}, {2, 2 * r3}, {1, 1 * r3}, {2, 0}, {1, -1 * r3},
            {2, -2 * r3}, {1, -3 * r3}, {2, -4 * r3}, {1, -5 * r3}, {4, 4 * r3}, {5, 3 * r3},
            {4, 2 * r3}, {5, 1 * r3}, {4, 0}, {5, -1 * r3}, {4, -2 * r3}, {5, -3 * r3}, {4, -4 * r3},
            {7, 3 * r3}, {8, 2 * r3}, {7, 1 * r3}, {8, 0}, {7, -1 * r3}, {8, -2 * r3}, {7, -3 * r3}};

    public final byte roads[][] = {{0, 1}, {2, 1}, {2, 3}, {4, 3}, {4, 5}, {6, 5},
            {8, 0}, {10, 2}, {12, 4}, {14, 6}, {7, 8}, {9, 8}, {9, 10}, {11, 10}, {11, 12},
            {13, 12}, {13, 14}, {15, 14}, {17, 7}, {19, 9}, {21, 11}, {23, 13}, {25, 15},
            {16, 17}, {18, 17}, {18, 19}, {20, 19}, {20, 21}, {22, 21}, {22, 23}, {24, 23},
            {24, 25}, {26, 25}, {27, 16}, {29, 18}, {31, 20}, {33, 22}, {35, 24}, {37, 26},
            {28, 27}, {28, 29}, {30, 29}, {30, 31}, {32, 31}, {32, 33}, {34, 33}, {34, 35},
            {36, 35}, {36, 37}, {38, 28}, {40, 30}, {42, 32}, {44, 34}, {46, 36}, {39, 38},
            {39, 40}, {41, 40}, {41, 42}, {43, 42}, {43, 44}, {45, 44}, {45, 46}, {47, 39},
            {49, 41}, {51, 43}, {53, 45}, {48, 47}, {48, 49}, {50, 49}, {50, 51}, {52, 51},
            {52, 53}};

    public final double ports[][] = {{-9, 3 * r3}, {-9, -r3}, {-6, -4 * r3}, {-3, 5 * r3},
            {0, -6 * r3}, {3, 5 * r3}, {6, -4 * r3}, {9, -r3}, {9, 3 * r3}};

    public final double coastline[][] = {{-8, 3.2 * r3}, {-8, 3 * r3}, {-7.4, 3 * r3},
            {-8.2, 2.2 * r3}, {-8.5, 2.5 * r3}, {-8.8, 2.4 * r3}, {-7.4, 1 * r3}, {-8.8, -0.4 * r3},
            {-8.5, -0.5 * r3}, {-8.2, -0.2 * r3}, {-7.5, -0.9 * r3}, {-8.1, -0.9 * r3},
            {-8.1, -1.1 * r3}, {-7.5, -1.1 * r3}, {-8.4, -2 * r3}, {-7.2, -3.2 * r3},
            {-5.4, -3.2 * r3}, {-5.7, -3.5 * r3}, {-5.4, -3.6 * r3}, {-5.1, -3.3 * r3},
            {-4.4, -4 * r3}, {-5, -4 * r3}, {-5, -4.2 * r3}, {-2.2, -4.2 * r3}, {-1.2, -5.2 * r3},
            {-0.8, -5.6 * r3}, {-0.5, -5.5 * r3}, {-0.8, -5.2 * r3}, {0.8, -5.2 * r3},
            {0.5, -5.5 * r3}, {0.8, -5.6 * r3}, {1.2, -5.2 * r3}, {2.2, -4.2 * r3}, {5, -4.2 * r3},
            {5, -4 * r3}, {4.4, -4 * r3}, {5.1, -3.3 * r3}, {5.4, -3.6 * r3}, {5.7, -3.5 * r3},
            {5.4, -3.2 * r3}, {7.2, -3.2 * r3}, {8.4, -2 * r3}, {7.5, -1.1 * r3}, {8.1, -1.1 * r3},
            {8.1, -0.9 * r3}, {7.5, -0.9 * r3}, {8.2, -0.2 * r3}, {8.5, -0.5 * r3}, {8.8, -0.4 * r3},
            {7.4, 1 * r3}, {8.8, 2.4 * r3}, {8.5, 2.5 * r3}, {8.2, 2.2 * r3}, {7.4, 3 * r3}, {8, 3 * r3},
            {8, 3.2 * r3}, {5.2, 3.2 * r3}, {3.8, 4.6 * r3}, {3.5, 4.5 * r3}, {3.8, 4.2 * r3},
            {2.4, 4.2 * r3}, {2.7, 4.5 * r3}, {2.4, 4.6 * r3}, {2.1, 4.3 * r3}, {1.2, 5.2 * r3},
            {-1.2, 5.2 * r3}, {-2.1, 4.3 * r3}, {-2.4, 4.6 * r3}, {-2.7, 4.5 * r3}, {-2.4, 4.2 * r3},
            {-3.8, 4.2 * r3}, {-3.5, 4.5 * r3}, {-3.8, 4.6 * r3}, {-5.2, 3.2 * r3}};

    public CatanSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        this.phi = 40;
        this.theta = -60;

        path = new Path();
        temp = new Paint();
        temp.setStyle(Paint.Style.FILL);
        outline = new Paint();
        outline.setColor(Color.BLACK);
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(1);

        num2 = BitmapFactory.decodeResource(getResources(), R.drawable.num_2);
        num3 = BitmapFactory.decodeResource(getResources(), R.drawable.num_3);
        num4 = BitmapFactory.decodeResource(getResources(), R.drawable.num_4);
        num5 = BitmapFactory.decodeResource(getResources(), R.drawable.num_5);
        num6 = BitmapFactory.decodeResource(getResources(), R.drawable.num_6);
        num8 = BitmapFactory.decodeResource(getResources(), R.drawable.num_8);
        num9 = BitmapFactory.decodeResource(getResources(), R.drawable.num_9);
        num10 = BitmapFactory.decodeResource(getResources(), R.drawable.num_10);
        num11 = BitmapFactory.decodeResource(getResources(), R.drawable.num_11);
        num12 = BitmapFactory.decodeResource(getResources(), R.drawable.num_12);
    }

    public void setGameState(CatanGameState catanGameState) {
        if(catanGameState!=null) {
            this.gameState = catanGameState;
            this.postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateABC();

        //display certain debugging features on the surfaceView
        boolean DEBUG=false;

        drawBoard(canvas);
        if(gameState==null) {
            temp.setColor(0xB0FFFFFF);
            canvas.drawPaint(temp);
            Paint message = new Paint();
            message.setColor(Color.BLACK);
            message.setTextSize(100);
            message.setTextAlign(Paint.Align.CENTER);
            message.setTypeface(Typeface.SERIF);
            canvas.drawText("No GameState loaded", cx, cy, message);
            return;
        }

        Road[] myRoads = gameState.getRoads();
        Building[] myBuildings = gameState.getBuildings();

        for(Road road : myRoads) {
            if(!road.isEmpty()) {
                drawRoad(canvas, playerColor[road.getPlayer()], road.getNumber());
            }
        }

        if(waitingForRobberPlacement) {
            drawRobber(canvas, 0xC0808080, gameState.getRobber());
        } else {
            drawRobber(canvas, 0xFF606060, gameState.getRobber());
        }

        if(waitingForSettlementSelection || waitingForCitySelection) {
            for (int x = 0; x < sites.length; x++) {
                drawSelectedBuilding(canvas, x);
            }
        }

        for(Building building : myBuildings) {
            if(!building.isEmpty()) {
                if (building.getTypeOfBuilding() == Building.SETTLEMENT)
                    drawSet(canvas, playerColor[building.getPlayer()], building.getNumber());
                else if (building.getTypeOfBuilding() == Building.CITY)
                    drawCity(canvas, playerColor[building.getPlayer()], building.getNumber());
            }
        }

        if(waitingForRoadSelection) {
            for (int x = 0; x < roads.length; x++) {
                drawSelectedRoad(canvas, x);
            }
        }

        if(waitingForRobberPlacement && tileLastSelected != -1) {
            drawRobber(canvas, 0xFFFFFF00, gameState.getRobber());
        }

        if(DEBUG) {
            temp.setColor(Color.BLACK);
            temp.setTextSize(28);
            canvas.drawText("Current player: "+gameState.getPlayersID(), 20, 50, temp);

            Hand[] hands = gameState.getHands();
            for(int i=0; i<hands.length; i++) {
                canvas.drawText("Player "+i+" res:",          20, 100+200*i, temp);
                canvas.drawText(hands[i].getWheat()+" Wheat", 80, 130+200*i, temp);
                canvas.drawText(hands[i].getWool()+" Sheep",  80, 160+200*i, temp);
                canvas.drawText(hands[i].getLumber()+" Wood", 80, 190+200*i, temp);
                canvas.drawText(hands[i].getBrick()+" Brick", 80, 220+200*i, temp);
                canvas.drawText(hands[i].getOre()+" Ore",     80, 250+200*i, temp);
            }

        }
    }

    public void drawRoad(Canvas canvas, int color, int location) {
        double x = sites[roads[location][0]][0];
        double y = sites[roads[location][0]][1];
        double i = sites[roads[location][1]][0] - x;
        double j = sites[roads[location][1]][1] - y;

        double pts[][] = {
                {x + 0.25 * i - 0.05 * j, y + 0.25 * j + 0.05 * i, 0},
                {x + 0.25 * i + 0.05 * j, y + 0.25 * j - 0.05 * i, 0},
                {x + 0.75 * i + 0.05 * j, y + 0.75 * j - 0.05 * i, 0},
                {x + 0.75 * i - 0.05 * j, y + 0.75 * j + 0.05 * i, 0},
                {x + 0.25 * i - 0.05 * j, y + 0.25 * j + 0.05 * i, 0.2},
                {x + 0.25 * i + 0.05 * j, y + 0.25 * j - 0.05 * i, 0.2},
                {x + 0.75 * i + 0.05 * j, y + 0.75 * j - 0.05 * i, 0.2},
                {x + 0.75 * i - 0.05 * j, y + 0.75 * j + 0.05 * i, 0.2}};

        double faces[][][] = {
                {pts[1], pts[5], pts[6], pts[2]},
                {pts[2], pts[6], pts[7], pts[3]},
                {pts[0], pts[3], pts[7], pts[4]},
                {pts[0], pts[4], pts[5], pts[1]},
                {pts[7], pts[6], pts[5], pts[4]}};

        for (double[][] face : faces) {
            drawFace(canvas, color, face);
        }
    }

    public void drawSet(Canvas canvas, int color, int location) {
        double x = sites[location][0];
        double y = sites[location][1];

        double pts[][] = {
                {x - 0.25, y - 0.25, 0},
                {x + 0.25, y - 0.25, 0},
                {x + 0.25, y + 0.25, 0},
                {x - 0.25, y + 0.25, 0},
                {x - 0.25, y - 0.25, 0.25},
                {x, y - 0.25, 0.45},
                {x + 0.25, y - 0.25, 0.25},
                {x + 0.25, y + 0.25, 0.25},
                {x, y + 0.25, 0.45},
                {x - 0.25, y + 0.25, 0.25}};

        double faces[][][] = {
                //{pts[0], pts[1], pts[2], pts[3]},//base of the settlement
                {pts[1], pts[6], pts[7], pts[2]},
                {pts[2], pts[7], pts[8], pts[9], pts[3]},
                {pts[0], pts[3], pts[9], pts[4]},
                {pts[0], pts[4], pts[5], pts[6], pts[1]},
                {pts[8], pts[7], pts[6], pts[5]},
                {pts[4], pts[9], pts[8], pts[5]}};

        for (double[][] face : faces) {
            drawFace(canvas, color, face);
        }
    }

    public void drawCity(Canvas canvas, int color, int location) {
        double x = sites[location][0];
        double y = sites[location][1];

        double pts[][] = {
                {x - 0.25, y - 0.25, 0},
                {x + 0.75, y - 0.25, 0},
                {x + 0.75, y + 0.25, 0},
                {x - 0.25, y + 0.25, 0},
                {x + 0.25, y - 0.25, 0.25},
                {x + 0.75, y - 0.25, 0.25},
                {x + 0.75, y + 0.25, 0.25},
                {x + 0.25, y + 0.25, 0.25},
                {x - 0.25, y - 0.25, 0.5},
                {x,        y - 0.25, 0.75},
                {x + 0.25, y - 0.25, 0.5},
                {x + 0.25, y + 0.25, 0.5},
                {x,        y + 0.25, 0.75},
                {x - 0.25, y + 0.25, 0.5}};

        double faces[][][] = {
                {pts[4], pts[7], pts[6], pts[5]},
                {pts[1], pts[5], pts[6], pts[2]},
                {pts[4], pts[10], pts[11], pts[7]},
                {pts[0], pts[3], pts[13], pts[8]},
                {pts[2], pts[6], pts[7], pts[11], pts[12], pts[13], pts[3]},
                {pts[0], pts[8], pts[9], pts[10], pts[4], pts[5], pts[1]},
                {pts[12], pts[11], pts[10], pts[9]},
                {pts[8], pts[13], pts[12], pts[9]}
        };

        for (double[][] face : faces) {
            drawFace(canvas, color, face);
        }
    }

    public void drawSelectedRoad(Canvas canvas, int location) {
        //if the user cannot build at a particular spot, don't draw it
        if(!gameState.canBuildRoad(location)) {
            return;
        }

        //otherwise, draw the spot
        float x = (float)sites[roads[location][0]][0];
        float y = (float)sites[roads[location][0]][1];
        float i = (float)sites[roads[location][1]][0] - x;
        float j = (float)sites[roads[location][1]][1] - y;

        double pts[][] = {
                {x + 0.2f * i - 0.1f * j, y + 0.2f * j + 0.1f * i},
                {x + 0.2f * i + 0.1f * j, y + 0.2f * j - 0.1f * i},
                {x + 0.8f * i + 0.1f * j, y + 0.8f * j - 0.1f * i},
                {x + 0.8f * i - 0.1f * j, y + 0.8f * j + 0.1f * i}};

        path.reset();
        path.moveTo(mapX(pts[0][0], pts[0][1]), mapY(pts[0][0], pts[0][1]));
        path.lineTo(mapX(pts[1][0], pts[1][1]), mapY(pts[1][0], pts[1][1]));
        path.lineTo(mapX(pts[2][0], pts[2][1]), mapY(pts[2][0], pts[2][1]));
        path.lineTo(mapX(pts[3][0], pts[3][1]), mapY(pts[3][0], pts[3][1]));

        //select the color for the spot
        if(location==roadLastSelected) {
            temp.setColor(0xFFFFFF00);
        } else {
            temp.setColor(0xC0808080);
        }
        canvas.drawPath(path, temp);
    }

    public void drawSelectedBuilding(Canvas canvas, int location) {
        //if the user cannot build at a particular spot, don't draw it
        if(waitingForSettlementSelection && !gameState.canBuildSettlement(location) ||
                waitingForCitySelection && !gameState.canUpgradeSettlement(location)) {
            return;
        }

        float x = (float)sites[location][0];
        float y = (float)sites[location][1];

        int width;
        int height;

        if(waitingForSettlementSelection) {
            width=(int)(1500/distance(x,y,0));
            height=(int)(1500*Math.cos(phi*deg)/distance(x,y, 0));
        } else {
            width=(int)(2000/distance(x,y,0));
            height=(int)(2000*Math.cos(phi*deg)/distance(x,y, 0));
        }

        //select the color for the spot
        if(location==buildingLastSelected) {
            temp.setColor(0xFFFFFF00);
        } else {
            temp.setColor(0xC0808080);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawOval(mapX(x, y) - width / 2, mapY(x, y) - height / 2, mapX(x, y) + width / 2, mapY(x, y) + height / 2, temp);
        } else {
            canvas.drawCircle(mapX(x, y), mapY(x, y), width, temp);
        }
    }

    public void drawRobber(Canvas canvas, int color, int location) {
        double x = tiles[location][0];
        double y = tiles[location][1];
        float xPos = mapX(x,y);
        float yPos = mapY(x,y);
        int size = (int)(2200/distance(x,y,0));

        path.reset();
        path.moveTo(xPos, yPos - 0.3f * size);
        path.lineTo(xPos + 0.3f * size, yPos + 0.3f * size);
        path.lineTo(xPos + 0.3f * size, yPos + 0.5f * size);
        path.lineTo(xPos - 0.3f * size, yPos + 0.5f * size);
        path.lineTo(xPos - 0.3f * size, yPos + 0.3f * size);
        path.close();

        temp.setColor(color);
        canvas.drawPath(path, temp);
        canvas.drawCircle(xPos, yPos - 0.3f * size, 0.2f * size, temp);
    }

    public void waitForRoadSelection(boolean set) {
        roadLastSelected = -1;
        waitingForRoadSelection = set;
        waitingForSettlementSelection = false;
        waitingForCitySelection = false;
        waitingForRobberPlacement = false;
        this.postInvalidate();
    }

    public void waitForSettlementSelection(boolean set) {
        buildingLastSelected = -1;
        waitingForRoadSelection = false;
        waitingForSettlementSelection = set;
        waitingForCitySelection = false;
        waitingForRobberPlacement = false;
        this.postInvalidate();
    }

    public void waitForCitySelection(boolean set) {
        buildingLastSelected = -1;
        waitingForRoadSelection = false;
        waitingForSettlementSelection = false;
        waitingForCitySelection = set;
        waitingForRobberPlacement = false;
        this.postInvalidate();
    }

    public void waitForRobberPlacement(boolean set) {
        tileLastSelected = -1;
        waitingForRoadSelection = false;
        waitingForSettlementSelection = false;
        waitingForCitySelection = false;
        waitingForRobberPlacement = set;
        this.postInvalidate();
    }

    public int getRoadLastSelected() {
        return (roadLastSelected<0 || roadLastSelected>71) ? -1 : roadLastSelected;
    }

    public int getBuildingLastSelected() {
        return (buildingLastSelected<0 || buildingLastSelected>53) ? -1 : buildingLastSelected;
    }

    public int getTileLastSelected() {
        return (tileLastSelected<0 || tileLastSelected>18) ? -1 : tileLastSelected;
    }

    public void selectRoad(double xPos, double yPos) {
        if(gameState==null)
            return;

        double x = inverseMapX(xPos, yPos); //x coordinate on the board of the last user touch
        double y = inverseMapY(xPos, yPos); //y coordinate on the board of the last user touch
        double closest = Integer.MAX_VALUE; //initially set the largest distance to be infinite
        int indexOfClosest = -1;

        for(int i=0; i<roads.length; i++) {
            //if the user can build a road at site i;
            if(gameState.canBuildRoad(i)) {
                double rx = (sites[roads[i][0]][0] + sites[roads[i][1]][0])/2.0;
                double ry = (sites[roads[i][0]][1] + sites[roads[i][1]][1])/2.0;
                double distance = Math.hypot(x-rx, y-ry);
                if(distance < closest) {
                    closest = distance;
                    indexOfClosest = i;
                }
            }
        }
        roadLastSelected = indexOfClosest;
        this.postInvalidate();
    }

    public void selectBuilding(double xPos, double yPos) {
        if (gameState == null)
            return;

        double x = inverseMapX(xPos, yPos); //x coordinate on the board of the last user touch
        double y = inverseMapY(xPos, yPos); //y coordinate on the board of the last user touch
        double closest = Integer.MAX_VALUE; //initially set the largest distance to be infinite
        int indexOfClosest = -1;

        for(int i=0; i<sites.length; i++) {
            //if the user can build a building at site i;
            if(waitingForSettlementSelection && gameState.canBuildSettlement(i) ||
                    waitingForCitySelection && gameState.canUpgradeSettlement(i)) {
                double distance = Math.hypot(x-sites[i][0], y-sites[i][1]);
                if(distance < closest) {
                    closest = distance;
                    indexOfClosest = i;
                }
            }
        }
        buildingLastSelected = indexOfClosest;
        this.postInvalidate();
    }

    public void selectTile(double xPos, double yPos) {
        if (gameState == null)
            return;

        double x = inverseMapX(xPos, yPos); //x coordinate on the board of the last user touch
        double y = inverseMapY(xPos, yPos); //y coordinate on the board of the last user touch
        double closest = Integer.MAX_VALUE; //initially set the largest distance to be infinite
        int indexOfClosest = -1;

        for(int i=0; i<tiles.length; i++) {
            //if the tile is not the tile the robber is currently on;
            if(i!=gameState.getRobber()) {
                double distance = Math.hypot(x-tiles[i][0], y-tiles[i][1]);
                if(distance < closest) {
                    closest = distance;
                    indexOfClosest = i;
                }
            }
        }
        tileLastSelected = indexOfClosest;
        this.postInvalidate();
    }

    private void drawBoard(Canvas canvas) {
        //draw coastline:
        path.reset();
        path.moveTo(mapX(coastline[0][0], coastline[0][1]),
                mapY(coastline[0][0], coastline[0][1]));
        for (int i = 1; i < coastline.length; i++) {
            path.lineTo(mapX(coastline[i][0], coastline[i][1]),
                    mapY(coastline[i][0], coastline[i][1]));
        }
        path.close();
        temp.setColor(sand);
        canvas.drawPath(path, temp);

        for (int i = 0; i < tiles.length; i++) {
            double x = tiles[i][0];
            double y = tiles[i][1];

            path.reset();
            path.moveTo(mapX(x + 2, y), mapY(x + 2, y));
            path.lineTo(mapX(x + 1, y + r3), mapY(x + 1, y + r3));
            path.lineTo(mapX(x - 1, y + r3), mapY(x - 1, y + r3));
            path.lineTo(mapX(x - 2, y),      mapY(x - 2, y));
            path.lineTo(mapX(x - 1, y - r3), mapY(x - 1, y - r3));
            path.lineTo(mapX(x + 1, y - r3), mapY(x + 1, y - r3));
            path.close();

            switch (CatanGameState.tileTypes[i]) {
                case Tile.BRICK:
                    temp.setColor(brick);
                    break;
                case Tile.WHEAT:
                    temp.setColor(wheat);
                    break;
                case Tile.WOOL:
                    temp.setColor(wool);
                    break;
                case Tile.LUMBER:
                    temp.setColor(wood);
                    break;
                case Tile.ORE:
                    temp.setColor(stone);
                    break;
                default:
                    temp.setColor(sand);
                    break;
            }
            canvas.drawPath(path, temp);
            canvas.drawPath(path, outline);
            int numWidth=(int)(2000/distance(x,y,0));
            int numHeight=(int)(2000*Math.cos(phi*deg)/distance(x,y, 0));

            switch (CatanGameState.rollNums[i]) {
                case 2:
                    bitmap = Bitmap.createScaledBitmap(num2, numWidth, numHeight, true);
                    break;
                case 3:
                    bitmap = Bitmap.createScaledBitmap(num3, numWidth, numHeight, true);
                    break;
                case 4:
                    bitmap = Bitmap.createScaledBitmap(num4, numWidth, numHeight, true);
                    break;
                case 5:
                    bitmap = Bitmap.createScaledBitmap(num5, numWidth, numHeight, true);
                    break;
                case 6:
                    bitmap = Bitmap.createScaledBitmap(num6, numWidth, numHeight, true);
                    break;
                case 8:
                    bitmap = Bitmap.createScaledBitmap(num8, numWidth, numHeight, true);
                    break;
                case 9:
                    bitmap = Bitmap.createScaledBitmap(num9, numWidth, numHeight, true);
                    break;
                case 10:
                    bitmap = Bitmap.createScaledBitmap(num10, numWidth, numHeight, true);
                    break;
                case 11:
                    bitmap = Bitmap.createScaledBitmap(num11, numWidth, numHeight, true);
                    break;
                case 12:
                    bitmap = Bitmap.createScaledBitmap(num12, numWidth, numHeight, true);
                    break;
                default:
                    bitmap = null;
                    break;
            }
            if(bitmap!=null)
                canvas.drawBitmap(bitmap, mapX(x, y)-numWidth/2, mapY(x, y)-numHeight/2, null);
        }
    }

    /**
     * updateABC updates the x,y,z coordinates of intersection of the vector form the camera
     * to the origin with the view plane. Must be called before redrawing the surface view
     */
    private void updateABC() {
        if (phi <= 0)
            phi = 0;
        else if (phi > 75)
            phi = 75;
        if (theta <= -180)
            theta += 360;
        else if (theta > 180)
            theta -= 360;

        a = Math.sin(phi*deg)*Math.cos(theta * deg);
        b = Math.sin(phi*deg)*Math.sin(theta*deg);
        c = Math.cos(phi*deg);
        k1 = -s*(d-p)*Math.sin(theta * deg);
        k2 = s*(d-p)*Math.cos(theta * deg);
        k3 = -s*(d-p)*Math.cos(phi*deg)*Math.cos(theta*deg);
        k4 = -s*(d-p)*Math.cos(phi * deg)*Math.sin(theta * deg);
        k5 = s*(d-p)*Math.sin(phi*deg);
        cy = this.getHeight() / 2.0f;
        cx = this.getWidth() / 2.0f;
    }

    /**
     * mapX returns the x coordinate of the point {x,y,z} in 3D space as viewed from
     * the camera at {d, theta, phi} as it would appear on the plane ax+by+cz=p^2
     *
     * @param x the x coordinate of a point in 3D space
     * @param y the y coordinate of a point in 3D space
     * @param z the z coordinate of a point in 3D space
     * @return the x coordinate on the screen of the point {x,y,z} in 3D space
     */
    public float mapX(double x, double y, double z) {
        return cx+(float)((x*k1 + y*k2)/(a*x + b*y + c*z - d));
    }

    /**
     * mapX returns the x coordinate of the point {x,y,0} in 3D space as viewed from
     * the camera at {d, theta, phi} as it would appear on the plane ax+by+cz=p^2
     *
     * @param x the x coordinate of a point in 3D space
     * @param y the y coordinate of a point in 3D space
     * @return the x coordinate on the screen of the point {x,y,0} in 3D space
     */
    public float mapX(double x, double y) {
        return cx+(float)((x*k1 + y*k2)/(a*x + b*y - d));
    }

    /**
     * mapY returns the y coordinate of the point {x,y,z} in 3D space as viewed from
     * the camera at {d, theta, phi} as it would appear on the plane ax+by+cz=p^2
     *
     * @param x the x coordinate of a point in 3D space
     * @param y the y coordinate of a point in 3D space
     * @param z the z coordinate of a point in 3D space
     * @return the y coordinate on the screen of the point {x,y,z} in 3D space
     */
    public float mapY(double x, double y, double z) {
        return cy+(int)((x*k3 + y*k4 + z*k5)/(a*x + b*y + c*z - d));
    }

    /**
     * mapY returns the y coordinate of the point {x,y,0} in 3D space as viewed from
     * the camera at {d, theta, phi} as it would appear on the plane ax+by+cz=p^2
     *
     * @param x the x coordinate of a point in 3D space
     * @param y the y coordinate of a point in 3D space
     * @return the y coordinate on the screen of the point {x,y,0} in 3D space
     */
    public float mapY(double x, double y) {
        return cy+(int)((x*k3 + y*k4)/(a*x + b*y - d));
    }

    /**
     * inverseMapX returns the x coordinate of the point {x,y,0} in 3D space
     * that is mapped to the point {x,y} on the screen with mapX() and mapY()
     *
     * @param x the x coordinate of a point on the screen
     * @param y the y coordinate of a point on the screen
     * @return the x coordinate of the point {x,y,0} in 3D space
     */
    public double inverseMapX(double x, double y) {
        return d*(k4*(x-cx)-k2*(y-cy))/((a*k4-b*k3)*(x-cx)+(b*k1-a*k2)*(y-cy)+k2*k3-k1*k4);
    }

    /**
     * inverseMapY returns the y coordinate of the point {x,y,0} in 3D space
     * that is mapped to the point {x,y} on the screen with mapX() and mapY()
     *
     * @param x the x coordinate of a point on the screen
     * @param y the y coordinate of a point on the screen
     * @return the y coordinate of the point {x,y,0} in 3D space
     */
    public double inverseMapY(double x, double y) {
        return d*(k3*(x-cx)-k1*(y-cy))/((b*k3-a*k4)*(x-cx)+(a*k2-b*k1)*(y-cy)+k1*k4-k2*k3);
    }

    /**
     * distance returns the distance from the camera at {d, theta, phi} to the point {x,y,z}
     *
     * @param x the x coordinate of a point in 3D space
     * @param y the y coordinate of a point in 3D space
     * @param z the z coordinate of a point in 3D space
     * @return the distance from the camera to the point {x,y,z} in 3D space
     */
    public double distance(double x, double y, double z) {
        return Math.sqrt((d*a - x) * (d*a - x) + (d*b - y) * (d*b - y) + (d*c - z) * (d*c - z));
    }


    public void drawFace(Canvas canvas, int color, double[][] pts) {
        double[] v1 = {pts[0][0] - pts[1][0], pts[0][1] - pts[1][1], pts[0][2] - pts[1][2]};
        double[] v2 = {pts[0][0] - pts[2][0], pts[0][1] - pts[2][1], pts[0][2] - pts[2][2]};
        double[] v3 = {v1[1] * v2[2] - v1[2] * v2[1], v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0]};
        double normV3 = Math.sqrt(v3[0]*v3[0] + v3[1]*v3[1] + v3[2]*v3[2]);
        v3[0] /= normV3; v3[1] /= normV3; v3[2] /= normV3; //normalize the vector

        //parameterize the vector from the camera to the face
        double[] cam = {pts[0][0] - a*d, pts[0][1] - b*d, pts[0][2] - c*d};
        double normCam = Math.sqrt(cam[0]*cam[0] + cam[1]*cam[1] + cam[2]*cam[2]);
        cam[0] /= normCam; cam[1] /= normCam; cam[2] /= normCam;

        //dot the normal vector with the camera vector
        double dot = cam[0]*v3[0] + cam[1]*v3[1] + cam[2]*v3[2];

        if(dot>0) {
            path.reset();
            path.moveTo(
                    mapX(pts[0][0], pts[0][1], pts[0][2]),
                    mapY(pts[0][0], pts[0][1], pts[0][2]));
            for(int i=1; i<pts.length; i++) {
                path.lineTo(
                        mapX(pts[i][0], pts[i][1], pts[i][2]),
                        mapY(pts[i][0], pts[i][1], pts[i][2]));
            }
            path.close();

            double factor = Math.min(1, Math.max(0, 0.4 + 0.6 * dot));
            temp.setColor(Color.rgb(
                    (int) (Color.red(color) * factor),
                    (int) (Color.green(color) * factor),
                    (int) (Color.blue(color) * factor)));

            canvas.drawPath(path, temp);
        }
    }


    /**
     * rotate the game board 10 degrees to the right
     */
    public void rotateRight() {
        theta = 10 * Math.round(theta / 10) + 10;
    }

    /**
     * rotate the game board 10 degrees to the left
     */
    public void rotateLeft() {
        theta = 10 * Math.round(theta / 10) - 10;
    }

    /**
     * rotate the camera 5 degrees up
     */
    public void rotateUp() {
        phi = 5 * Math.round(phi / 5) - 5;
    }

    /**
     * rotate the camera 5 degrees down
     */
    public void rotateDown() {
        phi = 5 * Math.round(phi / 5) + 5;
    }

    /**
     * get the value of phi
     *
     * @return this.phi
     */
    public double getPhi() {
        return phi;
    }

    /**
     * get the value of theta
     *
     * @return this.theta
     */
    public double getTheta() {
        return theta;
    }

    /**
     * change the value of phi based on user touch
     *
     * @param angle the new value of phi
     */
    public void setPhi(double angle) {
        this.phi = angle % 360;
    }

    /**
     * change the value of theta based on user touch
     *
     * @param angle the new value of theta
     */
    public void setTheta(double angle) {
        this.theta = angle % 360;
    }
}
