package edu.up.cs301.catan;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import edu.up.cs301.catan.actions.CatanAddResAction;
import edu.up.cs301.catan.actions.CatanBuildRoadAction;
import edu.up.cs301.catan.actions.CatanBuildSettlementAction;
import edu.up.cs301.catan.actions.CatanEndTurnAction;
import edu.up.cs301.catan.actions.CatanMoveRobberAction;
import edu.up.cs301.catan.actions.CatanRemoveResAction;
import edu.up.cs301.catan.actions.CatanRollAction;
import edu.up.cs301.catan.actions.CatanUpgradeSettlementAction;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * A GUI for a human to play Catan. Contains all the method to make the GUI, send actions, and receives
 * the game state.
 *
 * @author Oney, Goldey, Schneider
 * @version December 2015
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
    Button trade;
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
    boolean buildRoadClicked = false;
    boolean buildSettlementClicked = false;
    boolean buildCityClicked = false;

    //Booleans to control the popups
    public static boolean popupAlreadyOpen = false;
    public static boolean statsPopupAlreadyOpen = false;
    public static boolean discardPopupOpened = false;
    public static boolean nextTurn = false;

    //Booleans to control what buttons have been clicked
    private boolean waitingForSet1 = true;
    private boolean waitingForRoad1 = false;
    private boolean waitingForSet2 = false;
    private boolean waitingForRoad2 = false;
    private boolean waitingForSomething = true;

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
     * @return the top object in the GUI's view hierarchy
     */
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * Method that takes in the game state and decides what to do with it
     *
     * @param info the message
     */
    @Override
    public void receiveInfo(GameInfo info) {
        if (info instanceof CatanGameState) {
            //Save versions of the game state to be used
            final CatanGameState GAME_STATE = (CatanGameState) info;
            this.myGameState = (CatanGameState) info;
            mySurfaceView.setGameState(this.myGameState);

            if (waitingForSet1 || waitingForSet2) {
                buildSettlementClicked = true;
                buildRoadClicked = false;
                mySurfaceView.waitForSettlementSelection(true);
            } else if (waitingForRoad1 || waitingForRoad2) {
                buildSettlementClicked = false;
                buildRoadClicked = true;
                mySurfaceView.waitForRoadSelection(true);
            }
            updateButtonStates();

            Hand playerHand = this.myGameState.getHand(playerNum);
            if (myGameState.getNeedToRoll() && playerNum == myGameState.getPlayersID() && playerHand.getRoadsAvail() < 14) {
                game.sendAction(new CatanRollAction(this));
            }

            //Get the dice values
            int die1 = GAME_STATE.getDie1();
            int die2 = GAME_STATE.getDie2();

            //If its the players turn set the dice images to the roll value
            if (GAME_STATE.getPlayersID() == playerNum) {
                //Red dice
                switch (die1) {
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
                switch (die2) {
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

                //Popup to display at the beginning of the players turn to tell them what resources
                //they got during computer players turns
                if (myGameState.getPlayersID() == playerNum && !statsPopupAlreadyOpen && !discardPopupOpened && nextTurn) {
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

                    if (wheatGained != 0) {
                        message = message + "\n" + wheatGained + " Wheat";
                    }
                    if (sheepGained != 0) {
                        message = message + "\n" + sheepGained + " Sheep";
                    }
                    if (woodGained != 0) {
                        message = message + "\n" + woodGained + " Wood";
                    }
                    if (brickGained != 0) {
                        message = message + "\n" + brickGained + " Brick";
                    }
                    if (rockGained != 0) {
                        message = message + "\n" + rockGained + " Rock";
                    }
                    if (message.equals("Resources Gained or Lost: ")) {
                        message = "No Resources gained or lost.";
                    }

                    //Popup to display
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(myActivity);
                    builder1.setTitle("What has happened since your last turn:");
                    builder1.setMessage(message);
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(R.string.Cancel,
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
                numWheat.setText(Integer.toString(playerHand.getWheat()));
                numSheep.setText(Integer.toString(playerHand.getWool()));
                numWood.setText(Integer.toString(playerHand.getLumber()));
                numBrick.setText(Integer.toString(playerHand.getBrick()));
                numOre.setText(Integer.toString(playerHand.getOre()));

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
            if (GAME_STATE.getRobberWasRolledPlayer() && !popupAlreadyOpen) {
                //Popup will only be created if it is the players turn to make a move
                if (GAME_STATE.getHand(playerNum).getTotal() > 7 && playerNum == GAME_STATE.getPlayersID()) {
                    discardPopupOpened = true;
                    popupAlreadyOpen = true;
                    LayoutInflater layoutInflater = (LayoutInflater) myActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    //Opens up the robber popup
                    final View POPUP_VIEW = layoutInflater.inflate(R.layout.popup_select_cards, null);

                    //Opens up the popup at the center of the screen
                    final PopupWindow POPUP_WINDOW = new PopupWindow(POPUP_VIEW, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    POPUP_WINDOW.showAtLocation(POPUP_VIEW, Gravity.CENTER, 0, 0);

                    //Dims the background
                    final LinearLayout BACK_DIM_LAYOUT = (LinearLayout) myActivity.findViewById(R.id.top_gui_layout);
                    BACK_DIM_LAYOUT.setVisibility(View.GONE);

                    //Text for popup
                    TextView text = (TextView) POPUP_VIEW.findViewById(R.id.cardSelectPopupText);
                    String message = "A seven has been rolled and you have " + GAME_STATE.getHand(GAME_STATE.getPlayersID()).getTotal() + " cards\n" +
                            "You must discard " + (GAME_STATE.getHand(playerNum).getTotal() / 2) + " cards.";
                    text.setText(message);

                    final CardSelectView SELECT_VIEW = (CardSelectView) POPUP_VIEW.findViewById(R.id.cardSelectionView);
                    SELECT_VIEW.setGameState(GAME_STATE);

                    //Instance of class used for anonymous onClick class
                    final CatanHumanPlayer PLAYER = this;

                    //Dismisses the popup when the cancel button is clicked
                    Button btnDismiss = (Button) POPUP_VIEW.findViewById(R.id.cardSelectDoneButton);
                    btnDismiss.setOnClickListener(new Button.OnClickListener() {
                        public void onClick(View v) {
                            int[] cardsToLose = SELECT_VIEW.getCardsToRemove();
                            if (SELECT_VIEW.enoughCardsSelected()) {
                                POPUP_WINDOW.dismiss();
                                game.sendAction(new CatanRemoveResAction(PLAYER, cardsToLose[0], cardsToLose[1], cardsToLose[2], cardsToLose[3], cardsToLose[4]));
                                //gameState.removeResources(0, wood.getValue(), sheep.getValue(), wheat.getValue(), brick.getValue(), rock.getValue());
                                BACK_DIM_LAYOUT.setVisibility(View.VISIBLE);
                                CatanHumanPlayer.popupAlreadyOpen = false;
                                updateButtonStates();
                            }
                        }
                    });
                } else if (playerNum == GAME_STATE.getPlayersID()) {
                    game.sendAction(new CatanRemoveResAction(this, 0, 0, 0, 0, 0));
                }
            }

            //If the robber was rolled on the players turn they must move the robber
            if (myGameState.isRolled7() && playerNum == myGameState.getPlayersID()) {
                mySurfaceView.waitForRobberPlacement(true);
                updateButtonStates();
            }

            //Update the buttons based on what can be built
            updateButtonStates();
        }
    }//receiveInfo

    /**
     * Method to update the button states based off of what resources the players have
     */
    private void updateButtonStates() {
        if (myGameState != null) {
            if (myGameState.isRolled7()) {
                //Show the correct buttons
                endTurn.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);

                //Set other buttons un-clickable
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
                trade.setClickable(false);
                trade.setTextColor(Color.GRAY);
            } else if (buildRoadClicked) {
                //Show the done and cancel buttons, except during initial setup
                if (waitingForRoad1 || waitingForRoad2) {
                    endTurn.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                } else {
                    endTurn.setText(R.string.Cancel);
                    endTurn.setVisibility(View.VISIBLE);
                    done.setVisibility(View.VISIBLE);
                }
                //Set other buttons un-clickable
                buildRoad.setClickable(true);
                buildRoad.setTextColor(Color.BLACK);
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
                trade.setClickable(false);
                trade.setTextColor(Color.GRAY);
            } else if (buildSettlementClicked) {
                //Show the done and cancel buttons, except during initial setup
                if (waitingForSet1 || waitingForSet2) {
                    endTurn.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                } else {
                    endTurn.setText(R.string.Cancel);
                    endTurn.setVisibility(View.VISIBLE);
                    done.setVisibility(View.VISIBLE);
                }
                //Set other buttons un-clickable
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
                buildSettlement.setClickable(true);
                buildSettlement.setTextColor(Color.BLACK);
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
                trade.setClickable(false);
                trade.setTextColor(Color.GRAY);
            } else if (buildCityClicked) {
                //Show the done and cancel buttons
                endTurn.setText(R.string.Cancel);
                endTurn.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);

                //Set other buttons un-clickable
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
                buildCity.setClickable(true);
                buildCity.setTextColor(Color.BLACK);
                trade.setClickable(false);
                trade.setTextColor(Color.GRAY);
            } else if (myGameState.getScores()[myGameState.getPlayersID()] >= CatanGameState.VICTORY_POINTS_TO_WIN) {
                //if the game is over
                endTurn.setVisibility(View.VISIBLE);
                endTurn.setClickable(false);
                endTurn.setText(R.string.GameOver);
                done.setVisibility(View.GONE);

                //Set other buttons un-clickable
                buildRoad.setClickable(false);
                buildRoad.setTextColor(Color.GRAY);
                buildSettlement.setClickable(false);
                buildSettlement.setTextColor(Color.GRAY);
                buildCity.setClickable(false);
                buildCity.setTextColor(Color.GRAY);
                trade.setClickable(false);
                trade.setTextColor(Color.GRAY);
            } else {
                //Make end turn button visible
                endTurn.setText(R.string.EndTurn);
                endTurn.setVisibility(View.VISIBLE);
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
                //Make trade button visible if more than 4 of a resource
                if (Integer.parseInt(numSheep.getText().toString()) >= 4 || Integer.parseInt(numBrick.getText().toString()) >= 4 ||
                        Integer.parseInt(numOre.getText().toString()) >= 4 || Integer.parseInt(numWheat.getText().toString()) >= 4
                        || Integer.parseInt(numWood.getText().toString()) >= 4) {
                    trade.setClickable(true);
                    trade.setTextColor(Color.BLACK);
                } else {
                    trade.setClickable(false);
                    trade.setTextColor(Color.GRAY);
                }
            }
        }
    }

    /**
     * Method to handle when any of the buttons on the GUI are clicked
     *
     * @param v the button that was clicked
     */
    public void onClick(View v) {
        if (myGameState != null) {
            if (v.equals(rotateUpButton)) {//rotates board up
                Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
                mySurfaceView.rotateUp();
                mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
                mySurfaceView.postInvalidate();
            } else if (v.equals(rotateRightButton)) {//rotates board right
                Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
                mySurfaceView.rotateRight();
                mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
                mySurfaceView.postInvalidate();
            } else if (v.equals(rotateDownButton)) {//rotates board down
                Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
                mySurfaceView.rotateDown();
                mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
                mySurfaceView.postInvalidate();
            } else if (v.equals(rotateLeftButton)) {//rotates board left
                Canvas myCanvas = mySurfaceView.getHolder().lockCanvas();
                mySurfaceView.rotateLeft();
                mySurfaceView.getHolder().unlockCanvasAndPost(myCanvas);
                mySurfaceView.postInvalidate();
            } else if (v.equals(buildRoad)) {
                //if the button has already been clicked
                if (buildRoadClicked) {
                    int spot = mySurfaceView.getRoadLastSelected();
                    if (spot != -1) {
                        buildRoadClicked = false;
                        mySurfaceView.waitForRoadSelection(false);
                        if (waitingForSomething) {
                            if (waitingForRoad1) {
                                waitingForRoad1 = false;
                                waitingForSet2 = true;
                                buildSettlementClicked = true;
                                mySurfaceView.waitForSettlementSelection(true);
                            } else if (waitingForRoad2) {
                                waitingForRoad2 = false;
                                waitingForSomething = false;
                            }
                        }
                        updateButtonStates();
                        game.sendAction(new CatanBuildRoadAction(this, spot));
                    }
                } else {
                    //Set build road boolean to true
                    buildRoadClicked = true;
                    updateButtonStates(); //change button colors
                    //Figure out what was clicked on surface view
                    mySurfaceView.waitForRoadSelection(true);
                }
            } else if (v.equals(buildSettlement)) {
                //if the button has already been clicked
                if (buildSettlementClicked) {
                    int spot = mySurfaceView.getBuildingLastSelected();
                    if (spot != -1) {
                        buildSettlementClicked = false;
                        mySurfaceView.waitForSettlementSelection(false);
                        if (waitingForSomething) {
                            if (waitingForSet1) {
                                waitingForSet1 = false;
                                waitingForRoad1 = true;
                                buildRoadClicked = true;
                                mySurfaceView.waitForRoadSelection(true);
                            } else if (waitingForSet2) {
                                waitingForSet2 = false;
                                waitingForRoad2 = true;
                                buildRoadClicked = true;
                                mySurfaceView.waitForRoadSelection(true);
                            }
                        }
                        updateButtonStates();
                        game.sendAction(new CatanBuildSettlementAction(this, spot));
                    }
                } else {
                    //Set build settlement boolean to true
                    buildSettlementClicked = true;
                    updateButtonStates(); //change button colors
                    //Figure out what was clicked on surface view
                    mySurfaceView.waitForSettlementSelection(true);
                }
            } else if (v.equals(buildCity)) {
                //if the button has already been clicked
                if (buildCityClicked) {
                    int spot = mySurfaceView.getBuildingLastSelected();
                    if (spot != -1) {
                        buildCityClicked = false;
                        mySurfaceView.waitForCitySelection(false);
                        updateButtonStates();
                        game.sendAction(new CatanUpgradeSettlementAction(this, spot));
                    }
                } else {
                    //Set build settlement boolean to true
                    buildCityClicked = true;
                    updateButtonStates(); //change button colors
                    //Figure out what was clicked on surface view
                    mySurfaceView.waitForCitySelection(true);
                }
            } else if (v.equals(trade)) {
                LayoutInflater layoutInflater = (LayoutInflater) myActivity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                //Opens up the trade popup
                final View POPUP_VIEW = layoutInflater.inflate(R.layout.trading_popup, null);

                //Opens up the popup at the center of the screen
                final PopupWindow POPUP_WINDOW = new PopupWindow(POPUP_VIEW, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                POPUP_WINDOW.showAtLocation(POPUP_VIEW, Gravity.CENTER, 0, 0);

                //Dims background
                final LinearLayout BACK_DIM_LAYOUT = (LinearLayout) myActivity.findViewById(R.id.top_gui_layout);
                BACK_DIM_LAYOUT.setVisibility(View.GONE);

                //Instance of class used for anonymous onClick class
                final CatanHumanPlayer PLAYER = this;

                //number of resources they can trade
                final NumberPicker WOOD_TO_LOSE = (NumberPicker) POPUP_VIEW.findViewById(R.id.woodNumber);
                final NumberPicker WHEAT_TO_LOSE = (NumberPicker) POPUP_VIEW.findViewById(R.id.wheatNumber);
                final NumberPicker BRICK_TO_LOSE = (NumberPicker) POPUP_VIEW.findViewById(R.id.brickNumber);
                final NumberPicker SHEEP_TO_LOSE = (NumberPicker) POPUP_VIEW.findViewById(R.id.sheepNumber);
                final NumberPicker ROCK_TO_LOSE = (NumberPicker) POPUP_VIEW.findViewById(R.id.rockNumber);

                //number of resources that they can lose
                final NumberPicker WOOD_TO_GAIN = (NumberPicker) POPUP_VIEW.findViewById(R.id.woodWant);
                final NumberPicker WHEAT_TO_GAIN = (NumberPicker) POPUP_VIEW.findViewById(R.id.wheatWant);
                final NumberPicker BRICK_TO_GAIN = (NumberPicker) POPUP_VIEW.findViewById(R.id.brickWant);
                final NumberPicker SHEEP_TO_GAIN = (NumberPicker) POPUP_VIEW.findViewById(R.id.sheepWant);
                final NumberPicker ROCK_TO_GAIN = (NumberPicker) POPUP_VIEW.findViewById(R.id.rockWant);

                //Set values for the number pickers for what they can lose
                int numberOfWood = Integer.parseInt(numWood.getText().toString());
                WOOD_TO_LOSE.setMaxValue(numberOfWood / 4);
                WOOD_TO_LOSE.setMinValue(0);

                int numberOfWheat = Integer.parseInt(numWheat.getText().toString());
                WHEAT_TO_LOSE.setMaxValue(numberOfWheat / 4);
                WHEAT_TO_LOSE.setMinValue(0);

                int numberOfBrick = Integer.parseInt(numBrick.getText().toString());
                BRICK_TO_LOSE.setMaxValue(numberOfBrick / 4);
                BRICK_TO_LOSE.setMinValue(0);

                int numberOfSheep = Integer.parseInt(numSheep.getText().toString());
                SHEEP_TO_LOSE.setMaxValue(numberOfSheep / 4);
                SHEEP_TO_LOSE.setMinValue(0);

                int numberOfRock = Integer.parseInt(numOre.getText().toString());
                ROCK_TO_LOSE.setMaxValue(numberOfRock / 4);
                ROCK_TO_LOSE.setMinValue(0);

                //Set values for the number pickers for what they can gain
                final int MAX_TRADE = numberOfBrick / 4 + numberOfRock / 4 + numberOfSheep / 4 + numberOfWheat / 4 + numberOfWood / 4;
                WOOD_TO_GAIN.setMaxValue(MAX_TRADE);
                WHEAT_TO_GAIN.setMaxValue(MAX_TRADE);
                ROCK_TO_GAIN.setMaxValue(MAX_TRADE);
                SHEEP_TO_GAIN.setMaxValue(MAX_TRADE);
                BRICK_TO_GAIN.setMaxValue(MAX_TRADE);

                WOOD_TO_GAIN.setMinValue(0);
                WHEAT_TO_GAIN.setMinValue(0);
                ROCK_TO_GAIN.setMinValue(0);
                SHEEP_TO_GAIN.setMinValue(0);
                BRICK_TO_GAIN.setMinValue(0);

                //Dismisses the popup when the done button is clicked and send trade actions
                Button btnDismiss = (Button) POPUP_VIEW.findViewById(R.id.bankPopupCancel);
                btnDismiss.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        if (WOOD_TO_GAIN.getValue() + SHEEP_TO_GAIN.getValue() + WHEAT_TO_GAIN.getValue() + BRICK_TO_GAIN.getValue() + ROCK_TO_GAIN.getValue() ==
                                WOOD_TO_LOSE.getValue() + SHEEP_TO_LOSE.getValue() + WHEAT_TO_LOSE.getValue() + BRICK_TO_LOSE.getValue() + ROCK_TO_LOSE.getValue()) {
                            game.sendAction(new CatanRemoveResAction(PLAYER, WOOD_TO_LOSE.getValue() * 4, SHEEP_TO_LOSE.getValue() * 4, WHEAT_TO_LOSE.getValue() * 4, BRICK_TO_LOSE.getValue() * 4, ROCK_TO_LOSE.getValue() * 4));
                            game.sendAction(new CatanAddResAction(PLAYER, WOOD_TO_GAIN.getValue(), SHEEP_TO_GAIN.getValue(), WHEAT_TO_GAIN.getValue(), BRICK_TO_GAIN.getValue(), ROCK_TO_GAIN.getValue()));
                            POPUP_WINDOW.dismiss();
                            BACK_DIM_LAYOUT.setVisibility(View.VISIBLE);
                        }
                    }
                });

                //Dismiss the popup when the cancel button is clicked
                Button btnCancel = (Button) POPUP_VIEW.findViewById(R.id.bankPopupLeave);
                btnCancel.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        POPUP_WINDOW.dismiss();
                        BACK_DIM_LAYOUT.setVisibility(View.VISIBLE);
                    }
                });

            } else if (v.equals(endTurn)) {
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
                if (myGameState.isRolled7()) { //move the robber
                    int spot = mySurfaceView.getTileLastSelected();
                    if (spot != -1) {
                        mySurfaceView.waitForRobberPlacement(false);
                        updateButtonStates();
                        game.sendAction(new CatanMoveRobberAction(this, spot));
                    }
                } else if (buildRoadClicked) { //Build a road
                    int spot = mySurfaceView.getRoadLastSelected();
                    if (spot != -1) {
                        buildRoadClicked = false;
                        mySurfaceView.waitForRoadSelection(false);
                        if (waitingForSomething) {
                            if (waitingForRoad1) {
                                waitingForRoad1 = false;
                                waitingForSet2 = true;
                                buildSettlementClicked = true;
                                mySurfaceView.waitForSettlementSelection(true);
                            } else if (waitingForRoad2) {
                                waitingForRoad2 = false;
                                waitingForSomething = false;
                            }
                        }
                        updateButtonStates();
                        game.sendAction(new CatanBuildRoadAction(this, spot));
                    }
                } else if (buildSettlementClicked) { //build a settlement
                    int spot = mySurfaceView.getBuildingLastSelected();
                    if (spot != -1) {
                        buildSettlementClicked = false;
                        mySurfaceView.waitForSettlementSelection(false);
                        if (waitingForSomething) {
                            if (waitingForSet1) {
                                waitingForSet1 = false;
                                waitingForRoad1 = true;
                                buildRoadClicked = true;
                                mySurfaceView.waitForRoadSelection(true);
                            } else if (waitingForSet2) {
                                waitingForSet2 = false;
                                waitingForRoad2 = true;
                                buildRoadClicked = true;
                                mySurfaceView.waitForRoadSelection(true);
                            }
                        }
                        updateButtonStates();
                        game.sendAction(new CatanBuildSettlementAction(this, spot));
                    }
                } else if (buildCityClicked) { //build a city
                    int spot = mySurfaceView.getBuildingLastSelected();
                    if (spot != -1) {
                        buildCityClicked = false;
                        mySurfaceView.waitForCitySelection(false);
                        updateButtonStates();
                        game.sendAction(new CatanUpgradeSettlementAction(this, spot));
                    }
                }
            }
        }
    }

    /**
     * Method to handle when the surface view is touched
     *
     * @param v the view
     * @param e the motion event
     * @return true if touch was something we car about, false otherwise
     */
    public boolean onTouch(View v, MotionEvent e) {
        if (v.equals(mySurfaceView) && myGameState != null) {
            if (myGameState.isRolled7()) {
                mySurfaceView.selectTile(e.getX(), e.getY());
                updateButtonStates();
                return true;
            } else if (buildRoadClicked) {
                mySurfaceView.selectRoad(e.getX(), e.getY());
                updateButtonStates();
                return true;
            } else if (buildSettlementClicked || buildCityClicked) {
                mySurfaceView.selectBuilding(e.getX(), e.getY());
                updateButtonStates();
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

        //Initialize the buttons and things in the side panel
        buildRoad = (Button) activity.findViewById(R.id.BuildRoad);
        buildSettlement = (Button) activity.findViewById(R.id.BuildSettlement);
        buildCity = (Button) activity.findViewById(R.id.BuildCity);
        endTurn = (Button) activity.findViewById(R.id.EndTurn);
        done = (Button) activity.findViewById(R.id.Done);
        trade = (Button) activity.findViewById(R.id.Trade);
        dice1 = (ImageView) activity.findViewById(R.id.dice1);
        dice2 = (ImageView) activity.findViewById(R.id.dice2);

        buildRoad.setOnClickListener(this);
        buildSettlement.setOnClickListener(this);
        buildCity.setOnClickListener(this);
        endTurn.setOnClickListener(this);
        trade.setOnClickListener(this);
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

        updateButtonStates();
    }//setAsGui

}// class CounterHumanPlayer

