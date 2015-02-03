package aferrer.vectorRace;

import android.content.Context;
import android.graphics.Bitmap;
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
    //gameState
    private GameState gameState;
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
        drawGameState();
    }

    private void drawGameState(){
        //qixo no hauria d'estar aqui!!!
        canvasBitmap = Bitmap.createBitmap(gameState.track.mask.getWidth(), gameState.track.mask.getHeight(), Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

        drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawTrack(gameState.track);
        drawGrid();
        drawCar(gameState.car);
    }

    public void updateGameState(GameState newGameState){
        gameState = newGameState;
        drawGameState();
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

    private void drawTrack(Track track){
        invalidate();
        drawCanvas.drawBitmap(track.mask, 0, 0, drawPaint);
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
        setColor(car.color);
        drawPaint.setStrokeWidth(4);

        drawPath.moveTo(car.x.get(0) * zoom, car.y.get(0) * zoom);
        for(int i=1; i<car.getNumOfMovements(); i++){
            drawPath.lineTo(car.x.get(i) * zoom, car.y.get(i) * zoom);
        }
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();

        //current car pos
        int currIdx = car.getCurrPosIdx();
        drawCanvas.drawCircle(car.x.get(currIdx)*zoom, car.y.get(currIdx)*zoom, 8, drawPaint);

        //future car pos
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
