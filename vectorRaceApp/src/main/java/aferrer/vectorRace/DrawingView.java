package aferrer.vectorRace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class DrawingView extends ImageView {

    private Paint paint;
    private Bitmap canvasBitmap;

    //gameState
    GameState mGameState;

    //scroll stuff
    private float mPrevX = 0;
    private float mPrevY = 0;
    private float mTotalX = 0;
    private float mTotalY = 0;


    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){

        setScaleType(ScaleType.MATRIX);

        //get drawing area setup for interaction
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        //canvasPaint = new Paint(Paint.DITHER_FLAG);

        mGameState = null;
     }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        //view given size
        super.onSizeChanged(w, h, old_w, old_h);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas) {
         drawGameState(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        //detect userTouch
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPrevX = event.getRawX();
                mPrevY = event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();

                // Update how much the touch moved
                //TODO alba : limitar el rang de translacions possibles
                mTotalX += x - mPrevX;
                mTotalY += y - mPrevY;

                mPrevX = x;
                mPrevY = y;

                invalidate();
                break;
         }
        // Consume event
        return true;
    }

    public void updateGameState(GameState gameState){
        mGameState = gameState;
        invalidate();
    }

    private void drawGameState(Canvas canvas){
         if (canvasBitmap == null || mGameState == null ) {
            return;
        }

        //scroll
        canvas.translate(mTotalX, mTotalY);

        //redraw state
        drawTrack(canvas);
        drawCars(canvas);
    }

    private void setColor(String newColor){
        paint.setColor(Color.parseColor(newColor));
    }

    private Bitmap getTrackBitmap(String trackId){
        Bitmap bm = null;
        switch(trackId){
            case "track1":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track1);
                break;
            case "track2":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track);
                break;
            case "track3":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track3);
                break;
            case "track4":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track4);
                break;
            case "track5":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track5);
                break;
            case "track6":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track6);
                break;
        }
        return bm;
    }

    private void drawTrack(Canvas canvas){
        Bitmap trackBitmap = getTrackBitmap(mGameState.getTrackId());
        canvas.drawBitmap(trackBitmap, 0, 0, paint);
        drawGrid(canvas, trackBitmap.getWidth(), trackBitmap.getHeight());
    }

    private void drawGrid(Canvas canvas, int width, int height){
        setColor("#22000000");
        paint.setStrokeWidth(1);
        int pixelsPerGridSquare = mGameState.worldToImage(1);

        //TODO alba: fer que la grilla encaixi perfecte
        for(int i = 0; i < width ; i = i + pixelsPerGridSquare)
            canvas.drawLine(i, 0, i, height, paint);

        for(int i = 0; i < height;  i = i + pixelsPerGridSquare)
            canvas.drawLine(0, i, width, i, paint);
    }

    private void drawCars(Canvas canvas){
        for(int carIdx = 0; carIdx < mGameState.getNumOfCars(); carIdx++) {
            Car car = mGameState.getCar(carIdx);

            setColor(car.mColor);
            paint.setStrokeWidth(4);

            //draw Path
            Path drawPath = new Path();
            drawPath.moveTo(mGameState.worldToImage(car.x.get(0)), mGameState.worldToImage(car.y.get(0)));
            for(int i=1; i<car.getNumOfMovements(); i++){
                drawPath.lineTo(mGameState.worldToImage(car.x.get(i)), mGameState.worldToImage(car.y.get(i)));
            }
            canvas.drawPath(drawPath, paint);

            //current mCar pos
            int currIdx = car.getCurrPosIdx();
            int nextx = car.x.get(currIdx) + car.getVx();
            int nexty = car.y.get(currIdx) + car.getVy();

            //draw current Position
            canvas.drawCircle(mGameState.worldToImage(car.x.get(currIdx)), mGameState.worldToImage(car.y.get(currIdx)), 8, paint);

            //draw future Position
            paint.setStrokeWidth(1);
            if(car.hasFuturePos()) {
                canvas.drawCircle(mGameState.worldToImage(car.getFutureX()), mGameState.worldToImage(car.getFutureY()), 8, paint);
            }

            //the current car shows the future options
            if(car.mParticipantId.equals(mGameState.mCurrParticipantId)) {
                drawOptionPos(canvas, (nextx - 1), (nexty - 1));
                drawOptionPos(canvas, (nextx - 1), (nexty));
                drawOptionPos(canvas, (nextx - 1), (nexty + 1));

                drawOptionPos(canvas, (nextx), (nexty - 1));
                drawOptionPos(canvas, (nextx), (nexty));
                drawOptionPos(canvas, (nextx), (nexty + 1));

                drawOptionPos(canvas, (nextx + 1), (nexty - 1));
                drawOptionPos(canvas, (nextx + 1), (nexty));
                drawOptionPos(canvas, (nextx + 1), (nexty + 1));
            }
        }
    }

    private void drawOptionPos(Canvas canvas, int x, int y){
        if(mGameState.isValidPos(x, y))
            canvas.drawCircle(mGameState.worldToImage(x), mGameState.worldToImage(y), 5, paint);
        else
            canvas.drawText("X", mGameState.worldToImage(x) - 4, mGameState.worldToImage(y) + 4, paint);
    }
}
