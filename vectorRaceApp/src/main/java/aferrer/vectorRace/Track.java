package aferrer.vectorRace;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by AFerrer on 19/01/2015.
 */
public class Track {

    //public Bitmap mask;
    public int start_pos_x;
    public int start_pos_y;
    public int pixelsPerSquare;

    //constructor
    public Track(){

        pixelsPerSquare = 12;
        //mask = track_mask;
        analyzeMask();
    }

    public void analyzeMask(){
        /*
        Log.w("[MyFirstApp]", "in analyzeMask = (" + mask.getWidth() + "," + mask.getHeight() + ")");
        start_pos_x = 10;
        start_pos_y = 10;

        int grid_width = mask.getWidth() / pixelsPerSquare;
        int grid_height = mask.getHeight() / pixelsPerSquare;

        for(int i = 0; i < grid_width; i++){
            for(int j = 0; j < grid_height; j++){
                int color = mask.getPixel(i * pixelsPerSquare, j * pixelsPerSquare);
                switch(color){
                    case Color.CYAN :   //startPoint
                        start_pos_x = i;
                        start_pos_y = j;
                        break;
                    case Color.WHITE :  //road
                    case Color.GRAY :   //curb
                    case Color.YELLOW : //gravel
                    case Color.BLUE :   //wall
                    case Color.GREEN :  //checkpoint1
                    case Color.RED :    //checkpoint2
                    case Color.MAGENTA ://checkpoint3
                    case Color.BLACK :  //finish
                        break;
                }
            }
        }
        */
    }
}
