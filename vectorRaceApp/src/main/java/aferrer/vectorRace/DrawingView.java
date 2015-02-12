package aferrer.vectorRace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;


public class DrawingView extends ImageView {

    private Paint paint;
    private Bitmap canvasBitmap;
    private int zoom = 30;

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
        Log.d("*** DrawingView ", "onDraw(): -------------------------------------");
        if (canvasBitmap == null) {
            return;
        }
        if(mGameState != null) {
            drawGameState(canvas);
        }
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
        Log.d("*** DrawingView ", "drawGameState(): -------------------------------------");

        //scroll
        canvas.translate(mTotalX, mTotalY);

        //redraw state
        drawTrack(canvas);
        drawGrid(canvas);
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
        Bitmap trackBitmap = getTrackBitmap(mGameState.mTrackId);
        canvas.drawBitmap(trackBitmap, 0, 0, paint);
    }

    private void drawGrid(Canvas canvas){
        setColor("#22000000");
        paint.setStrokeWidth(1);
        for(int i=0; i<200; i++) {
            canvas.drawLine(i*zoom, 0, i*zoom, 1400, paint);
            canvas.drawLine(0, i*zoom, 1400, i*zoom, paint);
        }
    }

    private void drawCars(Canvas canvas){
        for(int carIdx = 0; carIdx < mGameState.getNumOfCars(); carIdx++) {
            Car car = mGameState.getCar(carIdx);

            setColor(car.mColor);
            paint.setStrokeWidth(4);

            //draw Path
            Path drawPath = new Path();
            drawPath.moveTo(car.x.get(0) * zoom, car.y.get(0) * zoom);
            for(int i=1; i<car.getNumOfMovements(); i++){
                drawPath.lineTo(car.x.get(i) * zoom, car.y.get(i) * zoom);
            }
            canvas.drawPath(drawPath, paint);

            //current mCar pos
            int currIdx = car.getCurrPosIdx();
            int nextx = car.x.get(currIdx) + car.getVx();
            int nexty = car.y.get(currIdx) + car.getVy();

            //draw current Position
            canvas.drawCircle(car.x.get(currIdx)*zoom, car.y.get(currIdx)*zoom, 8, paint);

            //draw future Position
            paint.setStrokeWidth(1);
            if(car.hasFuturePos()) {
                canvas.drawCircle(car.getFutureX()*zoom, car.getFutureY()*zoom, 8, paint);
            }

            //the current car shows the future options
            if(car.mParticipantId.equals(mGameState.mCurrParticipantId)) {
                canvas.drawCircle((nextx - 1) * zoom, (nexty - 1) * zoom, 5, paint);
                canvas.drawCircle((nextx - 1) * zoom, nexty * zoom, 5, paint);
                canvas.drawCircle((nextx - 1) * zoom, (nexty + 1) * zoom, 5, paint);

                canvas.drawCircle(nextx * zoom, (nexty - 1) * zoom, 5, paint);
                canvas.drawCircle(nextx * zoom, nexty * zoom, 5, paint);
                canvas.drawCircle(nextx * zoom, (nexty + 1) * zoom, 5, paint);

                canvas.drawCircle((nextx + 1) * zoom, (nexty - 1) * zoom, 5, paint);
                canvas.drawCircle((nextx + 1) * zoom, nexty * zoom, 5, paint);
                canvas.drawCircle((nextx + 1) * zoom, (nexty + 1) * zoom, 5, paint);
            }
        }
    }
}
