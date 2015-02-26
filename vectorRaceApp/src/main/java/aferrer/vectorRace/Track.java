package aferrer.vectorRace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by AFerrer on 19/01/2015.
 */
public class Track {

    public enum TypeOfGround {
        START_POINT, ROAD, OUT_OF_ROAD, CURB, GRAVEL, WALL,
        CHECKPOINT_1, CHECKPOINT_2, CHECKPOINT_3, FINISH,
        UNKNOWN
    }

    //world reference
    private Bitmap mMask;
    private int mStartX;
    private int mStartY;

    //image reference
    private int mPixelsPerGridSquare;

    //constructor
    public Track(Bitmap mask){

        // TODO alba: aixo ha d'estar en la definició del track "trackId"
        mMask = mask;
        mPixelsPerGridSquare = 25;
        mStartX = 10;
        mStartY = 10;
    }

    public int getStartX(){return mStartX;}
    public int getStartY(){return mStartY;}

    public int worldToImage(int i){
        return i * mPixelsPerGridSquare;
    }

    public TypeOfGround getTypeOfGround(int posX, int posY){
        int pixelValue = mMask.getPixel(posX * mPixelsPerGridSquare, posY * mPixelsPerGridSquare);
        return colorToTypeOfGround( pixelValue );
    }

    private TypeOfGround colorToTypeOfGround(int color){
        // DKGRAY(0xff444444)
        // LTGRAY(0xffcccccc)
        // TRANSPARENT(0x00000000)

        //Log.d("*** Track ", "colorToTypeOfGround(): argb = " + color + " - (" +
       //         Color.alpha(color) + ", " + Color.red(color) + ", " + Color.green(color) + ", " + Color.blue(color) + ")");

        switch(color){
            case -2004383865 :   //(0xff888888) //TODO alba: fer un editor de pantalles
                return TypeOfGround.ROAD;
            case Color.TRANSPARENT :
                return TypeOfGround.OUT_OF_ROAD;
            case Color.CYAN :   //(0xff00ffff)
                return TypeOfGround.START_POINT;
            case Color.WHITE :  //(0xffffffff)
                return TypeOfGround.CURB;
            case Color.YELLOW : //(0xffffff00)
                return TypeOfGround.GRAVEL;
            case Color.BLUE :   //(0xff0000ff)
                return TypeOfGround.WALL;
            case Color.GREEN :  //(0xff00ff00)
                return TypeOfGround.CHECKPOINT_1;
            case Color.RED :    //(0xffff0000)
                return TypeOfGround.CHECKPOINT_2;
            case Color.MAGENTA ://(0xffff00ff)
                return TypeOfGround.CHECKPOINT_3;
            case Color.BLACK :  //(0xff000000)
                return TypeOfGround.FINISH;

        }
        return TypeOfGround.UNKNOWN;
    }
}






