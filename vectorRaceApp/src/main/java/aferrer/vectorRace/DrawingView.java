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
import android.view.MotionEvent;
import android.widget.ImageView;


public class DrawingView extends ImageView {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    private int zoom = 30;

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        //get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int old_w, int old_h) {
        //view given size
        super.onSizeChanged(w, h, old_w, old_h);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    public void drawGameState(GameState gameState){
        //aixo no hauria d'estar aqui!!!
        canvasBitmap = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawTrack(gameState.mTrackId);
        drawGrid();
        for(int i=0; i<gameState.numOfCars; i++) {
            drawCar(gameState.mCars[i]);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        //draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        //drawGameState();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        //detect user touch
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor){
        //set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    private void drawTrack(String trackId){
        /*
        invalidate();
        Bitmap bm = null;
        switch(trackId){
            case "track1":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track1);
                break;
            case "track2":
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.track2);
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

        drawCanvas.drawBitmap(bm, 0, 0, drawPaint);
        */
    }
    private void drawGrid(){
        invalidate();
        setColor("#22000000");
        drawPaint.setStrokeWidth(1);
        for(int i=0; i<200; i++) {
            drawCanvas.drawLine(i*zoom, 0, i*zoom, 1400, drawPaint);
            drawCanvas.drawLine(0, i*zoom, 1400, i*zoom, drawPaint);
        }
    }

    private void drawCar(Car car){
        invalidate();
        setColor("#ffff0000");
        drawPaint.setStrokeWidth(4);

        drawPath.moveTo(car.x.get(0) * zoom, car.y.get(0) * zoom);
        for(int i=1; i<car.getNumOfMovements(); i++){
            drawPath.lineTo(car.x.get(i) * zoom, car.y.get(i) * zoom);
        }
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();

        //current mCar pos
        int currIdx = car.getCurrPosIdx();
        drawCanvas.drawCircle(car.x.get(currIdx)*zoom, car.y.get(currIdx)*zoom, 8, drawPaint);

        //future mCar pos
        drawPaint.setStrokeWidth(1);
        int nextx = car.x.get(currIdx) + car.vx.get(currIdx);
        int nexty = car.y.get(currIdx) + car.vy.get(currIdx);

        drawCanvas.drawCircle((nextx - 1) * zoom, (nexty - 1) * zoom, 5, drawPaint);
        drawCanvas.drawCircle((nextx - 1) * zoom,  nexty * zoom, 5, drawPaint);
        drawCanvas.drawCircle((nextx - 1) * zoom, (nexty + 1) * zoom, 5, drawPaint);

        drawCanvas.drawCircle(nextx * zoom, (nexty - 1) * zoom, 5, drawPaint);
        drawCanvas.drawCircle(nextx * zoom,  nexty * zoom, 5, drawPaint);
        drawCanvas.drawCircle(nextx * zoom, (nexty + 1) * zoom, 5, drawPaint);

        drawCanvas.drawCircle((nextx + 1) * zoom, (nexty - 1) * zoom, 5, drawPaint);
        drawCanvas.drawCircle((nextx + 1) * zoom,  nexty * zoom, 5, drawPaint);
        drawCanvas.drawCircle((nextx + 1) * zoom, (nexty + 1) * zoom, 5, drawPaint);
    }
}
