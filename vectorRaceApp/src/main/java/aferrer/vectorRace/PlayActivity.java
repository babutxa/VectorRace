package aferrer.vectorRace;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by AFerrer on 19/01/2015.
 */
public class PlayActivity extends ActionBarActivity {

    private GameState gameState;
    private ImageButton currPaint;
    private DrawingView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        gameState = new GameState(BitmapFactory.decodeResource(getResources(), R.drawable.track1));

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        drawView = (DrawingView)findViewById(R.id.drawing);
        drawView.updateGameState(gameState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void paintClicked(View view){

        String color = view.getTag().toString();

        //use chosen color
        if(view!=currPaint){
            //update color
            ImageButton imgView = (ImageButton)view;
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }

        switch(color){
            case "#FF660000":
                gameState.updateState(-1, -1);
                break;
            case "#FFFF0000":
                gameState.updateState(0, -1);
                break;
            case "#FFFF6600":
                gameState.updateState(1, -1);
                break;
            case "#FFFFCC00":
                gameState.updateState(-1, 0);
                break;
            case "#FF009900":
                gameState.updateState(0, 0);
                break;
            case "#FF009999":
                gameState.updateState(1, 0);
                break;
            case "#FF0000FF":
                gameState.updateState(-1, 1);
                break;
            case "#FF990099":
                gameState.updateState(0, 1);
                break;
            case "#FFFF6666":
                gameState.updateState(1, 1);
                break;
        }

        drawView.updateGameState(gameState); //internally this function render
    }
}
