package aferrer.vectorRace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by AFerrer on 21/01/2015.
 */
public class GameState {

    public static final String TAG = "EBTurn";

    public int turnCounter;
    public String data = "";
    public Track track;
    public Car car;

    public GameState(){
        track = new Track();
        car = new Car();

        //posem el cotxe a la sortida
        car.initAtPos(track.start_pos_x, track.start_pos_y);
    }

    public void updateState(int ax, int ay){
        //aqui haurem de gestionar si el cotxe ha sortit de la carretera
        car.move(ax,ay);
    }

    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();
        try {
            retVal.put("data", data);
            retVal.put("turnCounter", turnCounter);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String st = retVal.toString();
        Log.d(TAG, "==== PERSISTING\n" + st);
        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of GameState.
    static public GameState unpersist(byte[] byteArray) {
        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new GameState();
        }
        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }
        Log.d(TAG, "====UNPERSIST \n" + st);
        GameState retVal = new GameState();
        try {
            JSONObject obj = new JSONObject(st);
            if (obj.has("data")) {
                retVal.data = obj.getString("data");
            }
            if (obj.has("turnCounter")) {
                retVal.turnCounter = obj.getInt("turnCounter");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retVal;
    }
}
