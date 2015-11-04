package edu.up.cs301.catan;

import android.graphics.Canvas;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * A GUI for a human to play Catan. This default version displays the GUI but is incomplete
 *
 *
 * @author Andrew M. Nuxoll
 * @version August 2015
 */
public class CatanHumanPlayer extends GameHumanPlayer implements OnClickListener {

	/* instance variables */

    // These variables will reference widgets that will be modified during play
    CatanSurfaceView mySurfaceView;
    Button rotateUpButton;
    Button rotateRightButton;
    Button rotateDownButton;
    Button rotateLeftButton;

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
        //TODO You will implement this method to receive state objects from the game

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

    }//setAsGui
}// class CounterHumanPlayer

