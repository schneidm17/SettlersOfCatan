package edu.up.cs301.catan;

import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanBuildSettlementAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanUpgradeSettlementAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * A GUI for a human to play Catan. This default version displays the GUI but is incomplete
 *
 * @author Oney, Goldey, Schneider
 * @version November 2015
 */
public class CatanHumanPlayer extends GameHumanPlayer implements OnClickListener {

	/* instance variables */

    // These variables will reference widgets that will be modified during play
    CatanSurfaceView mySurfaceView;
    Button rotateUpButton;
    Button rotateRightButton;
    Button rotateDownButton;
    Button rotateLeftButton;
    Button buildRoad;
    Button buildSettlement;
    Button buildCity;
    Button endTurn;
    Button done;
    ImageView dice1;
    ImageView dice2;
    TextView numWheat;
    TextView numSheep;
    TextView numWood;
    TextView numBrick;
    TextView numOre;
    ImageView[] wheatCards = new ImageView[10];
    ImageView[] sheepCards = new ImageView[10];
    ImageView[] woodCards = new ImageView[10];
    ImageView[] brickCards = new ImageView[10];
    ImageView[] oreCards = new ImageView[10];

    //Booleans to control what has been clicked
    Boolean buildRoadClicked = false;
    Boolean buildSettlementClicked = false;
    Boolean buildCityClicked = false;

    // the android activity that we are running
    private GameMainActivity myActivity;

    /**
     * constructor does nothing extra
     */
    public CatanHumanPlayer(String name) {
        super(name);
    }

    /**
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view heirarchy
     */
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message
     */
    @Override
    public void receiveInfo(GameInfo info) {
        //TODO Update the canvas
        if (info instanceof CatanGameState) {
            CatanGameState gameState = (CatanGameState) info;

            int die1 = gameState.getDie1();
            int die2 = gameState.getDie2();

            switch (die1){
                case 1:
                    dice1.setBackgroundResource(R.drawable.dice_1_red);
                    break;
                case 2:
                    dice1.setBackgroundResource(R.drawable.dice_2_red);
                    break;
                case 3:
                    dice1.setBackgroundResource(R.drawable.dice_3_red);
                    break;
                case 4:
                    dice1.setBackgroundResource(R.drawable.dice_4_red);
                    break;
                case 5:
                    dice1.setBackgroundResource(R.drawable.dice_5_red);
                    break;
                default:
                    dice1.setBackgroundResource(R.drawable.dice_6_red);
                    break;
            }

            switch (die2){
                case 1:
                    dice2.setBackgroundResource(R.drawable.dice_1_yellow);
                    break;
                case 2:
                    dice2.setBackgroundResource(R.drawable.dice_2_yellow);
                    break;
                case 3:
                    dice2.setBackgroundResource(R.drawable.dice_3_yellow);
                    break;
                case 4:
                    dice2.setBackgroundResource(R.drawable.dice_4_yellow);
                    break;
                case 5:
                    dice2.setBackgroundResource(R.drawable.dice_5_yellow);
                    break;
                default:
                    dice2.setBackgroundResource(R.drawable.dice_6_yellow);
                    break;
            }

           gameState.givePlayerResources(0);

            //Make done button invisible
            done.setVisibility(View.GONE);

            //Make buttons visible if the player has resources
            if (!gameState.playerHasRoadRes()) {
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
            } else {
                buildRoad.setClickable(true);
                buildRoad.setTextColor(Color.BLACK);
            }
            if (!gameState.playerHasCityRes()) {
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
            } else {
                buildCity.setVisibility(View.VISIBLE);
                buildCity.setTextColor(Color.BLACK);
            }
            if (!gameState.playerHasSettlementRes()) {
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
            } else {
                buildSettlement.setVisibility(View.VISIBLE);
                buildSettlement.setTextColor(Color.BLACK);
            }

            //display resource cards for user
            Hand playerHand = gameState.getHand();
            numWheat.setText("" + playerHand.getWheat());
            numSheep.setText("" + playerHand.getWool());
            numWood.setText("" + playerHand.getLumber());
            numBrick.setText("" + playerHand.getBrick());
            numOre.setText("" + playerHand.getOre());

            for (int x = 0; x < 10; x++) {
                if (x < playerHand.getWheat()) {
                    wheatCards[x].setVisibility(View.VISIBLE);
                } else {
                    wheatCards[x].setVisibility(View.INVISIBLE);
                }

                if (x < playerHand.getWool()) {
                    sheepCards[x].setVisibility(View.VISIBLE);
                } else {
                    sheepCards[x].setVisibility(View.INVISIBLE);
                }

                if (x < playerHand.getLumber()) {
                    woodCards[x].setVisibility(View.VISIBLE);
                } else {
                    woodCards[x].setVisibility(View.INVISIBLE);
                }

                if (x < playerHand.getBrick()) {
                    brickCards[x].setVisibility(View.VISIBLE);
                } else {
                    brickCards[x].setVisibility(View.INVISIBLE);
                }

                if (x < playerHand.getOre()) {
                    oreCards[x].setVisibility(View.VISIBLE);
                } else {
                    oreCards[x].setVisibility(View.INVISIBLE);
                }
            }
        }
    }//receiveInfo

    /**
     * this method gets called when the user clicks the '+' or '-' button. It
     * creates a new CounterMoveAction to return to the parent activity.
     *
     * @param v the button that was clicked
     */
    public void onClick(View v) {
        if (v.equals(rotateUpButton)) {
            Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
            mySurfaceView.rotateUp();
            mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
            mySurfaceView.postInvalidate();
        } else if (v.equals(rotateRightButton)) {
            Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
            mySurfaceView.rotateRight();
            mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
            mySurfaceView.postInvalidate();
        } else if (v.equals(rotateDownButton)) {
            Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
            mySurfaceView.rotateDown();
            mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
            mySurfaceView.postInvalidate();
        } else if (v.equals(rotateLeftButton)) {
            Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
            mySurfaceView.rotateLeft();
            mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
            mySurfaceView.postInvalidate();
        } else if (v.equals(buildRoad)) {
            //Set build road boolean to true
            buildRoadClicked = true;

            //Make end turn button text be cancel
            endTurn.setText("Cancel");

            //Turn on done button
            done.setVisibility(View.VISIBLE);

            //Set other buttons un-clickable
            buildRoad.setClickable(false);
            buildSettlement.setClickable(false);
            buildSettlement.setTextColor(Color.GRAY);
            buildCity.setClickable(false);
            buildCity.setTextColor(Color.GRAY);

            //TODO:Figure out what was clicked on surface view
        } else if (v.equals(buildSettlement)) {
            //Set build settlement boolean to true
            buildSettlementClicked = true;

            //Make end turn button text be cancel
            endTurn.setText("Cancel");

            //Turn on done button
            done.setVisibility(View.VISIBLE);

            //Set other buttons un-clickable
            buildSettlement.setClickable(false);
            buildRoad.setClickable(false);
            buildRoad.setTextColor(Color.GRAY);
            buildCity.setClickable(false);
            buildCity.setTextColor(Color.GRAY);

            //TODO:Figure out what was clicked on surface view
        } else if (v.equals(buildCity)) {
            //Set build settlement boolean to true
            buildCityClicked = true;

            //Make end turn button text be cancel
            endTurn.setText("Cancel");

            //Turn on done button
            done.setVisibility(View.VISIBLE);

            //Set other buttons un-clickable
            buildCity.setClickable(false);
            buildSettlement.setClickable(false);
            buildSettlement.setTextColor(Color.GRAY);
            buildRoad.setClickable(false);
            buildRoad.setTextColor(Color.GRAY);

            //TODO:Figure out what was clicked on surface view
        } else if (v.equals(endTurn)){
            if (endTurn.getText().equals("End Turn")){
                game.sendAction(new CatanEndTurnAction(this));
            }else if (endTurn.getText().equals("Cancel")){
                if (buildRoadClicked) {
                    buildRoadClicked = false;
                } else if (buildSettlementClicked) {
                    buildSettlementClicked = false;
                } else if (buildCityClicked) {
                    buildCityClicked = false;
                }
                //Reset Buttons
                buildRoad.setClickable(true);
                buildRoad.setTextColor(Color.BLACK);
                buildSettlement.setClickable(true);
                buildSettlement.setTextColor(Color.BLACK);
                buildCity.setClickable(true);
                buildCity.setTextColor(Color.BLACK);

                //Make end turn button text be cancel
                endTurn.setText("End Turn");

                //Turn on done button
                done.setVisibility(View.GONE);
            }
        } else if (v.equals(done)) {
            if (buildRoadClicked) {
                //game.sendAction(new CatanBuildRoadAction(this,spot));
            }else if(buildSettlementClicked){
                //game.sendAction(new CatanBuildSettlementAction(this,spot));
            }else if(buildCityClicked){
                //game.sendAction(new CatanUpgradeSettlementAction(this,spot));
            }
        }
    }// onClick

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui(GameMainActivity activity) {

        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.catan_layout);

        //Initialize the widget reference member variables
        mySurfaceView = (CatanSurfaceView) activity.findViewById(R.id.gameMainSurfaceView);
        rotateUpButton = (Button) activity.findViewById(R.id.goUpButton);
        rotateRightButton = (Button) activity.findViewById(R.id.goRightButton);
        rotateDownButton = (Button) activity.findViewById(R.id.goDownButton);
        rotateLeftButton = (Button) activity.findViewById(R.id.goLeftButton);

        rotateUpButton.setOnClickListener(this);
        rotateRightButton.setOnClickListener(this);
        rotateDownButton.setOnClickListener(this);
        rotateLeftButton.setOnClickListener(this);

        //Initialize the buttons in the side panel
        //TODO:Change button ids
        buildRoad = (Button)activity.findViewById(R.id.button);
        buildSettlement = (Button)activity.findViewById(R.id.button2);
        buildCity = (Button)activity.findViewById(R.id.button3);
        endTurn = (Button)activity.findViewById(R.id.button4);
        done = (Button)activity.findViewById(R.id.button5);
        dice1 = (ImageView)activity.findViewById(R.id.dice1);
        dice2 = (ImageView)activity.findViewById(R.id.dice2);

        buildRoad.setOnClickListener(this);
        buildSettlement.setOnClickListener(this);
        buildCity.setOnClickListener(this);
        endTurn.setOnClickListener(this);

        numWheat = (TextView) activity.findViewById(R.id.numWheat);
        numSheep = (TextView) activity.findViewById(R.id.numSheep);
        numWood = (TextView) activity.findViewById(R.id.numWood);
        numBrick = (TextView) activity.findViewById(R.id.numBrick);
        numOre = (TextView) activity.findViewById(R.id.numOre);

        wheatCards[0] = (ImageView) activity.findViewById(R.id.wheat1);
        wheatCards[1] = (ImageView) activity.findViewById(R.id.wheat2);
        wheatCards[2] = (ImageView) activity.findViewById(R.id.wheat3);
        wheatCards[3] = (ImageView) activity.findViewById(R.id.wheat4);
        wheatCards[4] = (ImageView) activity.findViewById(R.id.wheat5);
        wheatCards[5] = (ImageView) activity.findViewById(R.id.wheat6);
        wheatCards[6] = (ImageView) activity.findViewById(R.id.wheat7);
        wheatCards[7] = (ImageView) activity.findViewById(R.id.wheat8);
        wheatCards[8] = (ImageView) activity.findViewById(R.id.wheat9);
        wheatCards[9] = (ImageView) activity.findViewById(R.id.wheat10);

        sheepCards[0] = (ImageView) activity.findViewById(R.id.sheep1);
        sheepCards[1] = (ImageView) activity.findViewById(R.id.sheep2);
        sheepCards[2] = (ImageView) activity.findViewById(R.id.sheep3);
        sheepCards[3] = (ImageView) activity.findViewById(R.id.sheep4);
        sheepCards[4] = (ImageView) activity.findViewById(R.id.sheep5);
        sheepCards[5] = (ImageView) activity.findViewById(R.id.sheep6);
        sheepCards[6] = (ImageView) activity.findViewById(R.id.sheep7);
        sheepCards[7] = (ImageView) activity.findViewById(R.id.sheep8);
        sheepCards[8] = (ImageView) activity.findViewById(R.id.sheep9);
        sheepCards[9] = (ImageView) activity.findViewById(R.id.sheep10);

        woodCards[0] = (ImageView) activity.findViewById(R.id.wood1);
        woodCards[1] = (ImageView) activity.findViewById(R.id.wood2);
        woodCards[2] = (ImageView) activity.findViewById(R.id.wood3);
        woodCards[3] = (ImageView) activity.findViewById(R.id.wood4);
        woodCards[4] = (ImageView) activity.findViewById(R.id.wood5);
        woodCards[5] = (ImageView) activity.findViewById(R.id.wood6);
        woodCards[6] = (ImageView) activity.findViewById(R.id.wood7);
        woodCards[7] = (ImageView) activity.findViewById(R.id.wood8);
        woodCards[8] = (ImageView) activity.findViewById(R.id.wood9);
        woodCards[9] = (ImageView) activity.findViewById(R.id.wood10);

        brickCards[0] = (ImageView) activity.findViewById(R.id.brick1);
        brickCards[1] = (ImageView) activity.findViewById(R.id.brick2);
        brickCards[2] = (ImageView) activity.findViewById(R.id.brick3);
        brickCards[3] = (ImageView) activity.findViewById(R.id.brick4);
        brickCards[4] = (ImageView) activity.findViewById(R.id.brick5);
        brickCards[5] = (ImageView) activity.findViewById(R.id.brick6);
        brickCards[6] = (ImageView) activity.findViewById(R.id.brick7);
        brickCards[7] = (ImageView) activity.findViewById(R.id.brick8);
        brickCards[8] = (ImageView) activity.findViewById(R.id.brick9);
        brickCards[9] = (ImageView) activity.findViewById(R.id.brick10);

        oreCards[0] = (ImageView) activity.findViewById(R.id.ore1);
        oreCards[1] = (ImageView) activity.findViewById(R.id.ore2);
        oreCards[2] = (ImageView) activity.findViewById(R.id.ore3);
        oreCards[3] = (ImageView) activity.findViewById(R.id.ore4);
        oreCards[4] = (ImageView) activity.findViewById(R.id.ore5);
        oreCards[5] = (ImageView) activity.findViewById(R.id.ore6);
        oreCards[6] = (ImageView) activity.findViewById(R.id.ore7);
        oreCards[7] = (ImageView) activity.findViewById(R.id.ore8);
        oreCards[8] = (ImageView) activity.findViewById(R.id.ore9);
        oreCards[9] = (ImageView) activity.findViewById(R.id.ore10);
    }//setAsGui

}// class CounterHumanPlayer

