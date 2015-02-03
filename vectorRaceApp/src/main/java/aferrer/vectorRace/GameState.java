package aferrer.vectorRace;

import android.graphics.Bitmap;

/**
 * Created by AFerrer on 21/01/2015.
 */
public class GameState {
    public Track track;
    public Car car;

    public GameState(Bitmap mask){
        track = new Track(mask);
        car = new Car();

        //posem el cotxe a la sortida
        car.initAtPos(track.start_pos_x, track.start_pos_y);
    }

    public void updateState(int ax, int ay){
        //aqui haurem de gestionar si el cotxe ha sortit de la carretera
        car.move(ax,ay);
    }
}
