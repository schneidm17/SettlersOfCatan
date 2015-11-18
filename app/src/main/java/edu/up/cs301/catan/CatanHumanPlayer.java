package edu.up.cs301.catan;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * A GUI for a human to play Catan. This default version displays the GUI but is incomplete
 *
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
     * @return
     * 		the top object in the GUI's view heirarchy
     */
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info
     * 		the message
     */
    @Override
    public void receiveInfo(GameInfo info) {
        //TODO Update the canvas
        if( info instanceof CatanGameState){
            CatanGameState gameState = (CatanGameState) info;

           gameState.givePlayerResources(0);

            //Make done button invisible
            done.setVisibility(View.GONE);

            //Make buttons visible if the player has resources
            if(!gameState.playerHasRoadRes()){
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
            }else{
                buildRoad.setClickable(true);
                buildRoad.setTextColor(Color.BLACK);
            }
            if(!gameState.playerHasCityRes()){
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
            }else{
                buildCity.setVisibility(View.VISIBLE);
                buildCity.setTextColor(Color.BLACK);
            }
            if(!gameState.playerHasSettlementRes()){
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
            }else{
                buildSettlement.setVisibility(View.VISIBLE);
                buildSettlement.setTextColor(Color.BLACK);
            }
        }
    }//receiveInfo

    /**
     * this method gets called when the user clicks the '+' or '-' button. It
     * creates a new CounterMoveAction to return to the parent activity.
     *
     * @param v
     * 		the button that was clicked
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
        } else if (v.equals(buildRoad)){
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
        } else if (v.equals(buildSettlement)){
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
        } else if (v.equals(buildCity)){
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
                }else if(buildSettlementClicked){
                    buildSettlementClicked = false;
                }else if(buildCityClicked){
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
        } else if (v.equals(done)){
            if (buildRoadClicked) {
                //Do action
            }else if(buildSettlementClicked){
                //Do action
            }else if(buildCityClicked){
                //Do action
            }
        }
    }// onClick

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity
     * 		the activity under which we are running
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

        buildRoad.setOnClickListener(this);
        buildSettlement.setOnClickListener(this);
        buildCity.setOnClickListener(this);
        endTurn.setOnClickListener(this);
    }//setAsGui

}// class CounterHumanPlayer

