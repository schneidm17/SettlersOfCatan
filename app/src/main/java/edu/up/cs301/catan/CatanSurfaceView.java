package edu.up.cs301.catan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by schneidm17 on 11/3/2015.
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

    public final double d = 30; //distance of the camera from the origin (constant)
    public final double p = 25; //distance of the view plane from the origin (constant)
    public final double s = 350; //scale factor on the view plane
    public final double deg = 0.017453292519943295; //conversion factor for deg to rad
    public final double r3 = Math.sqrt(3); //square root of 3

    /*
     * These local variables are only used within this class;
     * They are declared here to cut down on runtime.
     */
    Path path; //temporary path (frequently reused);
    Paint temp; //style Paint.Style.FILL (frequently reused);
    Paint outline; //style Paint.Style.STROKE

    /*
     * These ints represent colors that are used in this class
     */
    int wood = 0xFF466F37;
    int wheat = 0xFFE7B23E;
    int brick = 0xFFB16B32;
    int stone = 0xFF969696;
    int wool = 0xFF91C14B;
    int sand = 0xFFE2C581;
    int redPlayer = 0xFFFF0000;
    int orangePlayer = 0xFFFF8000;
    int bluePlayer = 0xFF0000FF;
    int whitePlayer = 0xFFFFFFFF;

    /*
     * These are the physical locations on the board of every piece that we might draw
     * We have a numbering system in place so that we know that a settlement at site
     * ten is adjacent to tiles 1, 4 and 5 and roads 12, 13 amd 20;
     */

    public final double tiles[][] = {{-6, 2 * r3}, {-6, 0}, {-6, -2 * r3},
            {-3, 3 * r3}, {-3, r3}, {-3, -r3}, {-3, -3 * r3}, {0, 4 * r3}, {0, 2 * r3},
            {0, 0}, {0, -2 * r3}, {0, -4 * r3}, {3, 3 * r3}, {3, r3}, {3, -r3}, {3, -3 * r3},
            {6, 2 * r3}, {6, 0}, {6, -2 * r3}};

    private int tileColors[] = {wood, wool, wheat, brick, stone, brick, wool, sand, wood, wheat,
            wood, wheat, brick, wool, wool, stone, stone, wheat, wood};

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

    public final double ports[][] = {{-9, 3*r3}, {-9, -r3}, {-6, -4*r3}, {-3, 5*r3},
            {0, -6*r3}, {3, 5*r3}, {6, -4*r3}, {9, -r3}, {9, 3*r3}};

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

        this.phi = 0;
        this.theta = 0;

        path = new Path();
        temp = new Paint();
        temp.setStyle(Paint.Style.FILL);
        outline = new Paint();
        outline.setColor(Color.BLACK);
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateABC();

        drawBoard(canvas);

        //draw some roads
        drawRoad(canvas, redPlayer, 13);
        drawRoad(canvas, redPlayer, 41);
        drawRoad(canvas, redPlayer, 40);
        drawRoad(canvas, redPlayer, 42);
        drawRoad(canvas, whitePlayer, 25);
        drawRoad(canvas, whitePlayer, 37);
        drawRoad(canvas, whitePlayer, 24);
        drawRoad(canvas, bluePlayer, 52);
        drawRoad(canvas, bluePlayer, 56);
        drawRoad(canvas, bluePlayer, 45);
        drawRoad(canvas, orangePlayer, 15);
        drawRoad(canvas, orangePlayer, 58);
        drawRoad(canvas, orangePlayer, 14);

        //draw some settlements
        drawSet(canvas, redPlayer, 10);
        drawSet(canvas, redPlayer, 29);
        drawSet(canvas, whitePlayer, 19);
        drawSet(canvas, whitePlayer, 35);
        drawSet(canvas, bluePlayer, 40);
        drawSet(canvas, bluePlayer, 44);
        drawSet(canvas, orangePlayer, 13);
        drawSet(canvas, orangePlayer, 42);
    }

    public void drawRoad(Canvas canvas, int color, int location) {
        double x = sites[roads[location][0]][0];
        double y = sites[roads[location][0]][1];
        double i = sites[roads[location][1]][0] - x;
        double j = sites[roads[location][1]][1] - y;

        double pts[][] = {
                {x +     i / 6 - j / 16,   y +     j / 5 + i / 12, 0},
                {x +     i / 6 + j / 16,   y +     j / 5 - i / 12, 0},
                {x + 5 * i / 6 + j / 16,   y + 4 * j / 5 - i / 12, 0},
                {x + 5 * i / 6 - j / 16,   y + 4 * j / 5 + i / 12, 0},
                {x + i / 6 - j / 16, y + j / 5 + i / 12, 0.25},
                {x + i / 6 + j / 16, y + j / 5 - i / 12, 0.25},
                {x + 5 * i / 6 + j / 16, y + 4 * j / 5 - i / 12, 0.25},
                {x + 5 * i / 6 - j / 16, y + 4 * j / 5 + i / 12, 0.25}};

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
        double size = 0.25;

        double pts[][] = {
                {x - size, y - size, 0},
                {x + size, y - size, 0},
                {x + size, y + size, 0},
                {x - size, y + size, 0},
                {x - size, y - size, size},
                {x, y - size, 2 * size},
                {x + size, y - size, size},
                {x + size, y + size, size},
                {x, y + size, 2 * size},
                {x - size, y + size, size}};

        double faces[][][] = {
                {pts[0], pts[1], pts[2], pts[3]},
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
        //TODO draw city
    }

    private void drawBoard(Canvas canvas) {
        //draw coastline:
        path.reset();
        path.moveTo(mapX(coastline[0][0], coastline[0][1], 0),
                mapY(coastline[0][0], coastline[0][1], 0));
        for (int i = 1; i < coastline.length; i++) {
            path.lineTo(mapX(coastline[i][0], coastline[i][1], 0),
                    mapY(coastline[i][0], coastline[i][1], 0));
        }
        path.close();
        temp.setColor(sand);
        canvas.drawPath(path, temp);

        for (int i = 0; i < tiles.length; i++) {
            double x = tiles[i][0];
            double y = tiles[i][1];

            path.reset();
            path.moveTo(mapX(x + 2, y, 0),      mapY(x + 2, y, 0));
            path.lineTo(mapX(x + 1, y + r3, 0), mapY(x + 1, y + r3, 0));
            path.lineTo(mapX(x - 1, y + r3, 0), mapY(x - 1, y + r3, 0));
            path.lineTo(mapX(x - 2, y, 0),      mapY(x - 2, y, 0));
            path.lineTo(mapX(x - 1, y - r3, 0), mapY(x - 1, y - r3, 0));
            path.lineTo(mapX(x + 1, y - r3, 0), mapY(x + 1, y - r3, 0));
            path.close();

            temp.setColor(tileColors[i]);
            canvas.drawPath(path, temp);
            canvas.drawPath(path, outline);
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

        a = p * Math.sin(phi * deg) * Math.cos(theta * deg); //x value of view plane axis
        b = p * Math.sin(phi * deg) * Math.sin(theta * deg); //y value of view plane axis
        c = p * Math.cos(phi * deg); //z value of view plane axis (in R3)
        k1 = s * Math.cos(deg * theta);
        k2 = -s * Math.sin(deg * theta);
        k3 = -s * Math.cos(deg * theta) * Math.cos(deg * phi);
        k4 = -s * Math.sin(deg * theta) * Math.cos(deg * phi);
        k5 = s * Math.sin(deg * phi);
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
     * @return the x coordinate on the screen of the point {x,y,x} in 3D space
     */
    public float mapX(double x, double y, double z) {
        double t = (p * p - x * a - y * b - z * c) / (d * p - x * a - y * b - c * z);
        return cx + (float) ((b - y - t * (d * b / p - y)) * k1 + (a - x - t * (d * a / p - x)) * k2);
    }

    /**
     * mapY returns the y coordinate of the point {x,y,z} in 3D space as viewed from
     * the camera at {d, theta, phi} as it would appear on the plane ax+by+cz=p^2
     *
     * @param x the x coordinate of a point in 3D space
     * @param y the y coordinate of a point in 3D space
     * @param z the z coordinate of a point in 3D space
     * @return the y coordinate on the screen of the point {x,y,x} in 3D space
     */
    public float mapY(double x, double y, double z) {
        double t = (p * p - x * a - y * b - z * c) / (d * p - x * a - y * b - c * z);
        return cy + (float) ((a - x - t * (d * a / p - x)) * k3 +
                (b - y - t * (d * b / p - y)) * k4 + (c - z - t * (d * c / p - z)) * k5);
    }

    public void drawFace(Canvas canvas, int color, double[][] pts) {
        double[] v1 = {pts[0][0] - pts[1][0], pts[0][1] - pts[1][1], pts[0][2] - pts[1][2]};
        double[] v2 = {pts[0][0] - pts[2][0], pts[0][1] - pts[2][1], pts[0][2] - pts[2][2]};
        double[] v3 = {v1[1] * v2[2] - v1[2] * v2[1], v1[2] * v2[0] - v1[0] * v2[2], v1[0] * v2[1] - v1[1] * v2[0]};
        double normV3 = Math.sqrt(v3[0]*v3[0] + v3[1]*v3[1] + v3[2]*v3[2]);
        v3[0] /= normV3; v3[1] /= normV3; v3[2] /= normV3; //normalize the vector

        //parameterize the vector from the camera to the face
        double[] cam = {pts[0][0] - a*d/p, pts[0][1] - b*d/p, pts[0][2] - c*d/p};
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
