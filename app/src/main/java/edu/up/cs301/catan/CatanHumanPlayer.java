package edu.up.cs301.catan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanBuildSettlementAction;
import edu.up.cs301.catan.actions.CatanRemoveResAction;
import edu.up.cs301.catan.actions.CatanRollAction;
import edu.up.cs301.catan.actions.CatanUpgradeSettlementAction;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.GameOverInfo;

/**
 * A GUI for a human to play Catan. This default version displays the GUI but is incomplete
 *
 * @author Oney, Goldey, Schneider
 * @version November 2015
 */
public class CatanHumanPlayer extends GameHumanPlayer implements OnClickListener, View.OnTouchListener {

	/* instance variables */

    // These variables will reference widgets that will be modified during play
    CatanSurfaceView mySurfaceView;
    CatanGameState myGameState;
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

    //Booleans to control the popups
    public static Boolean popupAlreadyOpen = false;
    public static Boolean statsPopupAlreadyOpen = false;
    public static Boolean discardPopupOpened = false;
    public static Boolean nextTurn = false;

    private Boolean builtSettlement1 = false;
    private Boolean builtRoad1 = false;
    private Boolean builtSettlement2 = false;
    private Boolean builtRoad2 = false;

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
        if (info instanceof CatanGameState) {
            //Save versions of the gamestate to be used
            final CatanGameState GAME_STATE = (CatanGameState) info;
            this.myGameState = (CatanGameState) info;
            mySurfaceView.setGameState(this.myGameState);

            //If the initial round
            if(!builtSettlement1){
                buildSettlementClicked = true;
                updateButtonStates();
                mySurfaceView.waitForSettlementSelection(true);
            }else if(!builtRoad1){
                buildRoadClicked = true;
                updateButtonStates();
                mySurfaceView.waitForRoadSelection(true);
            }else if(!builtSettlement2){
                buildSettlementClicked = true;
                updateButtonStates();
                mySurfaceView.waitForSettlementSelection(true);
            }else if(!builtRoad2){
                buildRoadClicked = true;
                updateButtonStates();
                mySurfaceView.waitForRoadSelection(true);
            }

            //If the player needs to roll, roll
            if(myGameState.getNeedToRoll() && playerNum == myGameState.getPlayersID())
            {
                game.sendAction(new CatanRollAction(this));
            }

            //Get the dice values
            int die1 = GAME_STATE.getDie1();
            int die2 = GAME_STATE.getDie2();

            //If its the players turn set the dice images to the roll value
            if(GAME_STATE.getPlayersID() == playerNum){
                //Red dice
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
                //yellow dice
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

                //Update the buttons based on whhat can be built
                updateButtonStates();

                //Popup to display at the beginning of the players turn to tell them what resources
                //they got during computer players turns
                if(myGameState.getPlayersID() == playerNum && !statsPopupAlreadyOpen && !discardPopupOpened && nextTurn){
                    statsPopupAlreadyOpen = true;
                    nextTurn = false;
                    //Calculate number gained
                    int wheatGained = this.myGameState.getHand(playerNum).getWheat() - Integer.parseInt(numWheat.getText().toString());
                    int rockGained = this.myGameState.getHand(playerNum).getOre() - Integer.parseInt(numOre.getText().toString());
                    int woodGained = this.myGameState.getHand(playerNum).getLumber() - Integer.parseInt(numWood.getText().toString());
                    int brickGained = this.myGameState.getHand(playerNum).getBrick() - Integer.parseInt(numBrick.getText().toString());
                    int sheepGained = this.myGameState.getHand(playerNum).getWool() - Integer.parseInt(numSheep.getText().toString());

                    //If there was more than or less than zero display message
                    String message = "Resources Gained or Lost: ";

                    if (wheatGained != 0){
                        message = message + "\n" + wheatGained + " Wheat";
                    }
                    if (sheepGained != 0){
                        message = message + "\n" + sheepGained + " Sheep";
                    }
                    if (woodGained != 0){
                        message = message + "\n" + woodGained + " Wood";
                    }
                    if (brickGained != 0){
                        message = message + "\n" + brickGained + " Brick";
                    }
                    if (rockGained != 0){
                        message = message + "\n" + rockGained + " Rock";
                    }
                    if (message.equals("Resources Gained or Lost: ")){
                        message = "No Resources gained or lost.";
                    }

                    //Popup to diplay
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(myActivity);
                    builder1.setTitle("What has happened since your last turn:");
                    builder1.setMessage(message);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    CatanHumanPlayer.statsPopupAlreadyOpen = false;
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

                //display resource cards for user
                Hand playerHand = this.myGameState.getHand(this.myGameState.getPlayersID());
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

            //When a seven is rolled and the player has more than seven cards a popup appears for
            //them to discard
            if(GAME_STATE.getRobberWasRolledPlayer() && !popupAlreadyOpen){
                //Popup will only be created if it is the players turn to make a move
                if(GAME_STATE.getHand(playerNum).getTotal() > 7 && playerNum == GAME_STATE.getPlayersID()){
                    discardPopupOpened = true;

                    //If all checks passed a game is started and popup appears saying who won.
                    LayoutInflater layoutInflater = (LayoutInflater) myActivity.getBaseContext().getSystemService(myActivity.LAYOUT_INFLATER_SERVICE);

                    //Opens up the robber popup
                    final View popupView = layoutInflater.inflate(R.layout.popup_select_cards, null);

                    //Opens up the popup at the center of the screen
                    final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    final LinearLayout back_dim_layout = (LinearLayout) myActivity.findViewById(R.id.top_gui_layout);
                    back_dim_layout.setVisibility(View.GONE);

                    popupAlreadyOpen = true;

                    TextView text = (TextView)popupView.findViewById(R.id.cardSelectPopupText);
                    text.setText("A seven has been rolled and you have "+GAME_STATE.getHand(GAME_STATE.getPlayersID()).getTotal()+" cards\n" +
                            "You must discard " + (int) Math.ceil(GAME_STATE.getHand(playerNum).getTotal()*0.5) + " cards.");

                    final CardSelectView selectView = (CardSelectView)popupView.findViewById(R.id.cardSelectionView);
                    selectView.setGameState(GAME_STATE);

                    //Instance of class used for anonymous onClick class
                    final CatanHumanPlayer player = this;

                    //Dismisses the popup when the cancel button is clicked
                    Button btnDismiss = (Button) popupView.findViewById(R.id.cardSelectDoneButton);
                    btnDismiss.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {
                            int[] cardsToLose = selectView.getCardsToRemove();
                            boolean pickedResources = ((cardsToLose[0] + cardsToLose[1]+ cardsToLose[2]+ cardsToLose[3]+ cardsToLose[4]) == Math.ceil(GAME_STATE.getHand(playerNum).getTotal()*0.5));
                            if(pickedResources){
                                popupWindow.dismiss(); //TODO: figure out why this works half the time, make End Turn unclickable while popup is active
                                game.sendAction(new CatanRemoveResAction(player, cardsToLose[0], cardsToLose[1], cardsToLose[2], cardsToLose[3], cardsToLose[4]));
                                //gameState.removeResources(0, wood.getValue(), sheep.getValue(), wheat.getValue(), brick.getValue(), rock.getValue());
                                back_dim_layout.setVisibility(View.VISIBLE);
                                CatanHumanPlayer.popupAlreadyOpen = false;
                            }
                        }
                    });
                }
                else if(playerNum == GAME_STATE.getPlayersID())
                {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 0, 0));
                }
            }

            //If the robber was rolled on the players turn they must move the robber
            if(myGameState.isRolled7() && playerNum == myGameState.getPlayersID()){
                //Todo: Matthew put code here

            }
        }
    }//receiveInfo

    //Method to update the button states based off of what resources the players have
    private void updateButtonStates() {
        if (myGameState == null)
            return;

        if (buildRoadClicked) {
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
        } else if (buildSettlementClicked) {
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
        } else if (buildCityClicked) {
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
        } else {
            //Make end turn button text be cancel
            endTurn.setText("End Turn");
            //Make done button invisible
            done.setVisibility(View.GONE);

            //Make buttons visible if the player has resources
            if (!myGameState.playerHasRoadRes()) {
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
            } else {
                buildRoad.setClickable(true);
                buildRoad.setTextColor(Color.BLACK);
            }
            if (!myGameState.playerHasCityRes()) {
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
            } else {
                buildCity.setClickable(true);
                buildCity.setTextColor(Color.BLACK);
            }
            if (!myGameState.playerHasSettlementRes()) {
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
            } else {
                buildSettlement.setClickable(true);
                buildSettlement.setTextColor(Color.BLACK);
            }
        }
    }

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
            updateButtonStates(); //change button colors
            //Figure out what was clicked on surface view
            mySurfaceView.waitForRoadSelection(true);
        } else if (v.equals(buildSettlement)) {
            //Set build settlement boolean to true
            buildSettlementClicked = true;
            updateButtonStates(); //change button colors
            //Figure out what was clicked on surface view
            mySurfaceView.waitForSettlementSelection(true);
        } else if (v.equals(buildCity)) {
            //Set build settlement boolean to true
            buildCityClicked = true;
            updateButtonStates(); //change button colors
            //Figure out what was clicked on surface view
            mySurfaceView.waitForCitySelection(true);
        } else if (v.equals(endTurn)){
            if (buildRoadClicked) {
                buildRoadClicked = false;
                mySurfaceView.waitForRoadSelection(false);
                updateButtonStates(); //change button colors
            } else if (buildSettlementClicked) {
                buildSettlementClicked = false;
                mySurfaceView.waitForSettlementSelection(false);
                updateButtonStates(); //change button colors
            } else if (buildCityClicked) {
                buildCityClicked = false;
                mySurfaceView.waitForCitySelection(false);
                updateButtonStates(); //change button colors
            } else {
                //if the user pressed "End Turn"
                game.sendAction(new CatanEndTurnAction(this));

                //Make it so stats popup can open again
                discardPopupOpened = false;
                nextTurn = true;
            }

        } else if (v.equals(done)) {
            if (buildRoadClicked) {
                int spot = mySurfaceView.getRoadLastSelected();
                if(spot != -1) {
                    if(!builtRoad1) {
                        builtRoad1 = true;
                    } else if (!builtRoad2) {
                        builtRoad2 = true;
                    }
                    buildRoadClicked=false;
                    mySurfaceView.waitForRoadSelection(false);
                    updateButtonStates();
                    game.sendAction(new CatanBuildRoadAction(this,spot));
                }
            } else if(buildSettlementClicked) {
                int spot = mySurfaceView.getBuildingLastSelected();
                if(spot != -1) {
                    if(!builtSettlement1) {
                        builtSettlement1 = true;
                    } else if (!builtSettlement2) {
                        builtSettlement2 = true;
                    }
                    buildSettlementClicked=false;
                    mySurfaceView.waitForSettlementSelection(false);
                    updateButtonStates();
                    game.sendAction(new CatanBuildSettlementAction(this,spot));
                }
            } else if(buildCityClicked) {
                int spot = mySurfaceView.getBuildingLastSelected();
                if(spot != -1) {
                    buildCityClicked=false;
                    mySurfaceView.waitForCitySelection(false);
                    updateButtonStates();
                    game.sendAction(new CatanUpgradeSettlementAction(this,spot));
                }
            }
        }
    }// onClick

    public boolean onTouch(View v, MotionEvent e) {
        if(v.equals(mySurfaceView)) {
            if (buildRoadClicked) {
                mySurfaceView.selectRoad(e.getX(), e.getY());
                return true;
            } else if(buildSettlementClicked || buildCityClicked) {
                mySurfaceView.selectBuilding(e.getX(), e.getY());
                return true;
            }
        }
        return false;
    }

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

        mySurfaceView.setOnTouchListener(this);
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
        done.setOnClickListener(this);

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

