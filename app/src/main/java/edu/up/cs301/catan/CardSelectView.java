package edu.up.cs301.catan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import edu.up.cs301.game.R;


/**
 * This view displays cards for the user to select for trading/discarding to the robber
 * Created by Matthew Schneider on 11/23/15.
 */
public class CardSelectView extends SurfaceView implements View.OnTouchListener {
    private CatanGameState gameState;
    private int[] cards = new int[10];
    private Bitmap[] cardImages = new Bitmap[5];
    private double[] colEdges = new double [6];
    private int numCardTypes;

    int fullWidth;
    int cardHeight;

    public CardSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        //wheatCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_wheat);
        //croppedWheatCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_wheat_crop);
        //seepCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_sheep);
        //croppedSheepCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_sheep_crop);
        //woodCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_lumber);
        //croppedWoodCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_lumber_crop);
        //brickCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_brick);
        //croppedBrickCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_brick_crop);
        //oreCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_rock);
        //croppedOreCard = BitmapFactory.decodeResource(getResources(), R.drawable.card_rock_crop);

        cardImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.card_wheat);
        cardImages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.card_sheep);
        cardImages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.card_lumber);
        cardImages[3] = BitmapFactory.decodeResource(getResources(), R.drawable.card_brick);
        cardImages[4] = BitmapFactory.decodeResource(getResources(), R.drawable.card_rock);
        fullWidth = cardImages[0].getWidth();
        cardHeight = cardImages[0].getHeight();

        this.setOnTouchListener(this);
    }

    public void setGameState(CatanGameState catanGameState) {
        if(catanGameState!=null) {
            this.gameState = catanGameState;

            Hand hand = gameState.getHand(gameState.getPlayersID());
            cards[0] = hand.getWheat();
            cards[1] = hand.getWool();
            cards[2] = hand.getLumber();
            cards[3] = hand.getBrick();
            cards[4] = hand.getOre();
            cards[5] = 0;
            cards[6] = 0;
            cards[7] = 0;
            cards[8] = 0;
            cards[9] = 0;

            if(cards[0]>0) {
                numCardTypes =1;
            } else {
                numCardTypes =0;
            } if(cards[1]>0) {
                numCardTypes++;
            } if(cards[2]>0) {
                numCardTypes++;
            } if(cards[3]>0) {
                numCardTypes++;
            } if(cards[4]>0) {
                numCardTypes++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameState == null) {
            return;
        }


        /*
        Paint temp = new Paint();
        temp.setColor(Color.BLACK);
        temp.setStyle(Paint.Style.STROKE);
        canvas.drawText("numCardTypes = " + numCardTypes, 0, 320, temp);
        canvas.drawText("totalCards = "+gameState.getHand(gameState.getPlayersID()).getTotal(), 0,340,temp);
        canvas.drawText("wheat = "+gameState.getHand(gameState.getPlayersID()).getWheat(), 0,400,temp);
        canvas.drawText("sheep = "+gameState.getHand(gameState.getPlayersID()).getWool(), 0,420,temp);
        canvas.drawText("wood = "+gameState.getHand(gameState.getPlayersID()).getLumber(), 0,440,temp);
        canvas.drawText("brick = "+gameState.getHand(gameState.getPlayersID()).getBrick(), 0,460, temp);
        canvas.drawText("ore = "+gameState.getHand(gameState.getPlayersID()).getOre(), 0,480,temp);
        */

        int col = 0;
        colEdges[5] = canvas.getWidth()-20;
        for (int x = 0; x < 5; x++) {
            double colSize = (canvas.getWidth() - 120.0) / numCardTypes;
            colEdges[x] = 20 + (colSize + 20) * col; //left edge of this column

            //if there is none of that resource, don't draw this column
            if(cards[x]+cards[x+5] == 0) {
                continue;
            }

            //otherwise, increment the column count
            col++;

            //draw the bottom row of cards
            if(cards[x]==0) {
                //if this row has no cards in it, do nothing
            } else if(cards[x] == 1) {
                canvas.drawBitmap(cardImages[x], (int)(colEdges[x] + (colSize-fullWidth)*0.5), canvas.getHeight()-cardHeight, null);
            } else {
                double cropWidth = Math.min(fullWidth,(colSize - fullWidth) / (cards[x] - 1.0));
                double edge = 0.5*(colSize-fullWidth-(cards[x]-1)*cropWidth);
                for (int j = cards[x] - 1; j >= 0; j--) {
                    canvas.drawBitmap(cardImages[x], (int)(colEdges[x]+cropWidth*j+edge), canvas.getHeight()-cardHeight, null);
                }
            }

            //draw the top row of cards
            if(cards[x+5]==0) {
                //if this row has no cards in it, do nothing
            } else if(cards[x+5] == 1) {
                canvas.drawBitmap(cardImages[x], (int)(colEdges[x] + (colSize-fullWidth)*0.5), 0, null);
            } else {
                double cropWidth = Math.min(fullWidth,(colSize - fullWidth) / (cards[x+5] - 1.0));
                double edge = 0.5*(colSize-fullWidth-(cards[x+5]-1)*cropWidth);
                for (int j = cards[x+5] - 1; j >= 0; j--) {
                    canvas.drawBitmap(cardImages[x], (int)(colEdges[x]+cropWidth*j+edge), 0, null);
                }
            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN && v==this) {
            float x = event.getX();
            float y = event.getY();

            if(x<0 || x>=v.getWidth() || y<0 || y>v.getHeight())
                return false;

            //find which column the user pressed
            for(int i=0; i<5; i++) {
                //if the user pressed this column
                if(colEdges[i]<=x && x<=colEdges[i+1]) {
                    if(y<v.getHeight()*0.4 && cards[i+5]>0) {
                        cards[i+5]--;
                        cards[i]++;
                        this.postInvalidate();
                        return true;
                    } else if (y>v.getHeight()*0.6 && cards[i]>0) {
                        cards[i]--;
                        cards[i+5]++;
                        this.postInvalidate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int[] getCardsToRemove() {
        //woodToLose, sheepToLose, wheatToLose, brickToLose, rockToLose));
        int[] cardsToRemove = {cards[7],cards[6],cards[5],cards[8], cards[9]};
        return cardsToRemove;

    }
}
